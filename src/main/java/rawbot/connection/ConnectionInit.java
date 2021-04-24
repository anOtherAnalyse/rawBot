package rawbot.connection;

import java.io.IOException;
import java.net.UnknownHostException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import rawbot.authlib.Authenticator;
import rawbot.authlib.AuthenticationException;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.packets.in.login.*;
import rawbot.connection.packets.in.ping.PingAnswer;
import rawbot.connection.packets.in.status.*;
import rawbot.connection.packets.out.handshake.HandshakePacket;
import rawbot.connection.packets.out.login.*;
import rawbot.connection.packets.out.status.*;

/* Handles the initialization of a connection with a server */

public class ConnectionInit {

  private Connection co; // Underlying connection

  private Authenticator auth;

  private boolean authenticate; // Do we authenticate to mojang

  public ConnectionInit(String host, int port) throws IOException, UnknownHostException {
    this(host, port, true);
  }

  public ConnectionInit(String host, int port, boolean authenticate) throws IOException, UnknownHostException {
    this.co = new Connection(host, port);
    this.auth = new Authenticator();
    this.authenticate = authenticate;
  }

  public Connection getConnection() {
    return this.co;
  }

  /* handshake with the server */
  private void handshake(EnumConnectionState requestedState) throws IOException {

    if(this.co.getConnectionState() != EnumConnectionState.HANDSHAKING) throw new IOException("Handshake with server was already proceeded");

    HandshakePacket hsk = new HandshakePacket(requestedState, this.co.getHost(), this.co.getPort());
    this.co.writePacket(hsk);
    this.co.setConnectionState(requestedState);
  }

  /* Log into the server */
  public void login() throws IOException, AuthenticationException {

    // Authenticate
    if(this.authenticate) {
      this.auth.authenticate();
    } else this.auth.setUsername("Meursault"); // Offline username

    // Handshake with server
    this.handshake(EnumConnectionState.LOGIN);

    // Start login
    this.co.writePacket(new LoginStartPacket(this.auth.getUsername()));

    // Get throught login process
    PacketIn pin;
    do {
      pin = this.co.readPacket();

      if(pin == null) throw new IOException("Received bad packet during login, packet id: " + Integer.toString(pin.getId(EnumConnectionState.LOGIN)));

      switch(pin.getId(EnumConnectionState.LOGIN)) {
        case 3:
          EnableCompressionPacket enableComp = (EnableCompressionPacket) pin;
          this.co.enableCompression(enableComp.getThreshold());
          break;
        case 2:
          LoginSuccessPacket logsuc = (LoginSuccessPacket) pin;
          System.out.println(String.format("Connected to %s under username \"%s\"", this.co.getHost(), logsuc.getUsername()));
          break;
        case 1:
          EncryptionRequestPacket encreq = (EncryptionRequestPacket)pin;
          this.handleEncryptionRequest(encreq);
      }
    } while(pin.getId(EnumConnectionState.LOGIN) != 2);

    // we are now playing
    this.co.setConnectionState(EnumConnectionState.PLAY);
  }

  // Start secured channel mode
  private void handleEncryptionRequest(EncryptionRequestPacket encreq) throws IOException, AuthenticationException {

    // Generate symetric encryption key
    KeyGenerator keyGen = null;
    try {
      keyGen = KeyGenerator.getInstance("AES");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e.getMessage());
    }
    keyGen.init(128);
    SecretKey secretKey = keyGen.generateKey();

    // Tell Mojang servers we connect to the server
    MessageDigest digest = null;
    try {
      digest = MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("generate digest: " + e.getMessage());
    }
    digest.update(encreq.getServerId().getBytes("ISO_8859_1"));
    digest.update(secretKey.getEncoded());
    digest.update(encreq.getPublicKey().getEncoded());
    String s1 = (new BigInteger(digest.digest())).toString(16);
    this.auth.joinServer(s1);

    // send encryption init answer
    this.co.writePacket(new EncryptionResponsePacket(secretKey, encreq.getPublicKey(), encreq.getVerifyToken()));

    // Enable encryption
    this.co.enableEncryption(secretKey);
  }

  /* Server status request - no auth required */
  public StatusAnswerPacket status() throws IOException {
    this.handshake(EnumConnectionState.STATUS);
    this.co.writePacket(new StatusQueryPacket());
    StatusAnswerPacket ans = (StatusAnswerPacket)this.co.readPacket();
    this.co.close();
    return ans;
  }

  /* Legacy ping request - no auth required */
  public PingAnswer legacyPing() throws IOException {
    return this.co.ping();
  }
}
