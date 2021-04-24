package rawbot.connection.packets.out.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;

public class CustomPayloadPacket implements PacketOut {

  protected String channel;
  protected byte[] data; // payload max is 32767 bytes

  public CustomPayloadPacket(String channel) {
    this.channel = channel;
    this.data = null;
  }

  public CustomPayloadPacket(String channel, byte[] data) {
    this.channel = channel;
    this.data = data;
  }

  public void write(WriteBuffer buf) throws IOException {
    buf.writeString(this.channel);
    buf.writeBytes(this.data, this.data.length);
  }

  public int getId(EnumConnectionState state) {
    return 9;
  }

}
