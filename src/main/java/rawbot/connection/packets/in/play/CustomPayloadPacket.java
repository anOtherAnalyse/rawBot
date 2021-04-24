package rawbot.connection.packets.in.play;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class CustomPayloadPacket implements PacketIn {

  private String channel;
  private byte[] data;

  public void read(ReadBuffer buf) throws IOException {
    this.channel = buf.readString();
    this.data = buf.getRest();
  }

  public int getId(EnumConnectionState state) {
    return 24;
  }

  public String getChannel() {
    return this.channel;
  }

  public byte[] getData() {
    return this.data;
  }
}
