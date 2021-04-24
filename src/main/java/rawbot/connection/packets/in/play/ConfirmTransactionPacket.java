package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class ConfirmTransactionPacket implements PacketIn {

  private int windowId;
  private short actionNumber;
  private boolean accepted;

  public void read(ReadBuffer buf) throws IOException {
    this.windowId = 255 & ((int)buf.readByte());
    this.actionNumber = buf.readShort();
    this.accepted = buf.readBoolean();
  }

  public int getId(EnumConnectionState state) {
    return 17;
  }

  public int getWindowId() {
    return this.windowId;
  }

  public short getAction() {
    return this.actionNumber;
  }

  public boolean accepted() {
    return this.accepted;
  }
}
