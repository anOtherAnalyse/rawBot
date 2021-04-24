package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class ScoreboardObjectivePacket implements PacketIn {

  private String name, value, renderType;
  private int action;

  public void read(ReadBuffer buf) throws IOException {
    this.name = buf.readString();
    this.action = ((int)(buf.readByte())) & 255;
    if (this.action == 0 || this.action == 2) {
      this.value = buf.readString();
      this.renderType = buf.readString();
    }
  }

  public int getId(EnumConnectionState state) {
    return 66;
  }

  public String getName() {
    return this.name;
  }

  public String getValue() {
    return this.value;
  }

  public String getType() {
    return this.renderType;
  }

  public int getAction() {
    return this.action;
  }
}
