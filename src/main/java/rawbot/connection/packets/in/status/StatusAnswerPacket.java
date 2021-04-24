package rawbot.connection.packets.in.status;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class StatusAnswerPacket implements PacketIn {

  private String json;

  public void read(ReadBuffer buf) throws IOException {
    this.json = buf.readString();
  }

  public int getId(EnumConnectionState state) {
    return 0;
  }

  public String getStatus() {
    return this.json;
  }
}
