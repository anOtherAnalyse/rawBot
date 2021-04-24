package rawbot.connection;

import java.lang.Class;
import java.lang.NumberFormatException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.crypto.SecretKey;

import rawbot.connection.exceptions.DisconnectedException;
import rawbot.connection.packets.Packets;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.packets.in.ping.PingAnswer;
import rawbot.connection.packets.in.login.DisconnectPacket;
import rawbot.connection.packets.out.PacketOut;
import rawbot.utils.CipherUtil;
import rawbot.utils.DeflateUtil;

/* Socket connection with server */

public class Connection {

  private String host;
  private int port;

  private Socket socket;
  private OutputStream output;
  private InputStream input;

  /* Connection state */
  private EnumConnectionState coState;
  private Packets packetsRecord;

  /* Compression parameters */
  private int compressionThreshold;
  private DeflateUtil deflator;

  /* Encryption parameters */
  private boolean cipher_enabled;
  private CipherUtil cipher;

  /* New connection to server */
  public Connection(String host, int port) throws IOException, UnknownHostException {
    this.socket = new Socket(host, port);
    this.output = this.socket.getOutputStream();
    this.input = this.socket.getInputStream();

    this.host = host;
    this.port = port;

    this.cipher_enabled = false;
    this.compressionThreshold = 0;

    this.packetsRecord = new Packets();
    this.setConnectionState(EnumConnectionState.HANDSHAKING);
  }

  public String getHost() {
    return this.host;
  }

  public int getPort() {
    return this.port;
  }

  public void setConnectionState(EnumConnectionState state) {
    this.coState = state;
    this.packetsRecord.setConnexionState(state);
  }

  public EnumConnectionState getConnectionState() {
    return this.coState;
  }

  public void enableCompression(int threshold) {
    this.deflator = new DeflateUtil(threshold);
    this.compressionThreshold = threshold;
  }

  public void enableEncryption(SecretKey secret) {
    this.cipher = new CipherUtil(secret);
    this.cipher_enabled = true;
  }

  /* Read next frame from socket */
  private synchronized ReadBuffer readData() throws IOException {

    /* Read length varInt header */
    int length = 0, c, i = 0;
    byte[] head = new byte[2];

    do {
      c = this.input.read();

      if(c == -1) throw new IOException("Socket closed by server");

      if(this.cipher_enabled) { // Decrypt length header
        head[0] = (byte)c;
        this.cipher.decipher(head, 0, head, 1, 1);
        c = ((int)head[1]);
      }

      length |= (c & 127) << ((i++) * 7);
    } while((c & 128) != 0);

    /* Read payload */
    byte[] buff = new byte[length];
    i = 0;
    while(i < length) {
      i += this.input.read(buff, i, length - i);
    }

    /* decrypt if needed */
    byte[] clear;
    if(this.cipher_enabled) {
      clear = new byte[length];
      this.cipher.decipher(buff, 0, clear, 0, length);
    } else clear = buff;

    /* inflate if needed */
    if(this.compressionThreshold > 0) {
      clear = this.deflator.inflate(clear);
    }

    return new ReadBuffer(clear);
  }

  /* Read next packet from socket */
  public PacketIn readPacket() throws IOException {
    ReadBuffer buf = this.readData();
    int pid = buf.readVarInt();

    Class<? extends PacketIn> out_class = this.packetsRecord.getById(pid);
    if(out_class == null) return null; // Packet has not been implemented yet

    PacketIn out = null;
    try {
      out = out_class.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Bad class instantiation: " + e.getMessage());
    }

    out.read(buf);

    if(out instanceof DisconnectPacket) {
      throw DisconnectedException.Serializer.jsonToComponent(((DisconnectPacket)out).getMessage());
    }

    if(buf.left() > 0) {
      throw new RuntimeException("Exceeding data for packet id " + Integer.toString(pid) + ", by " + Integer.toString(buf.left()) + " bytes");
    }

    return out;
  }

  /* Write packet to socket */
  public synchronized void writePacket(PacketOut p) throws IOException {

    // payload
    int pid = p.getId(this.coState);
    WriteBuffer payload = new WriteBuffer();
    payload.writeVarInt(pid);
    p.write(payload);

    // handle compression
    byte[] raw_payload = null;
    if(this.compressionThreshold > 0) {
      raw_payload = this.deflator.deflate(payload.getBuff());
    } else raw_payload = payload.getBuff();

    // add length header
    WriteBuffer packet = new WriteBuffer();
    packet.writeVarInt(raw_payload.length);
    packet.writeBytes(raw_payload, raw_payload.length);

    // handle encryption
    try {
      if(this.cipher_enabled) {
        output.write(this.cipher.cipher(packet.getBuff()));
      } else {
        output.write(packet.getBuff());
      }
    } catch (IOException e) {
      throw new IOException("Socket closed by server");
    }
  }

  /* Close the connection */
  public void close() throws IOException {
    if(! socket.isClosed()) {
      //output.close();
      //input.close();
      socket.close();
    }
  }

  /* Legacy ping request */
  public PingAnswer ping() throws IOException {

    if(this.coState != EnumConnectionState.HANDSHAKING) return null;

    byte[] ping = {-2, 1};
    output.write(ping);

    int c = input.read();

    if(c != 255) {
      throw new IOException("Ping answer header byte is not 255");
    }

    c = input.read();
    int length = (c & 255) << 8;
    c = input.read();
    length = (length | (c & 255)) * 2;

    byte[] response = new byte[length];
    int i = 0;
    while(i < length) {
        i += input.read(response, i, length - i);
    }

    this.close();

    Pattern p = Pattern.compile("^\u00a71\u0000([^\u0000]+)\u0000([^\u0000]+)\u0000([^\u0000]+)\u0000([^\u0000]+)\u0000([\\s\\S]+)$");
    Matcher m = p.matcher(new String(response, StandardCharsets.UTF_16));

    if(m.find()) {
      if(! m.group(1).equals("127")) throw new IOException("Ping answer header different from 127");

      int count = 0, max = 0;
      try {
        count = Integer.parseInt(m.group(4));
        max = Integer.parseInt(m.group(5));
      } catch (NumberFormatException e) {
        throw new IOException(String.format("Wrong integer format in ping answer: %s", e.getMessage()));
      }

      return new PingAnswer(this.host, m.group(2), m.group(3), count, max);
    }

    throw new IOException("Ping answer: wrong format");
  }
}
