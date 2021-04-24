package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class EntityAttachPacket implements PacketIn {

  private int holderId;
  private int leashedId;

  public void read(ReadBuffer buf) throws IOException {
    this.leashedId = buf.readInt();
    this.holderId = buf.readInt();
  }

  public int getId(EnumConnectionState state) {
    return 61;
  }

  public int getHolder() {
    return this.holderId;
  }

  public int getLeashed() {
    return this.leashedId;
  }
}
