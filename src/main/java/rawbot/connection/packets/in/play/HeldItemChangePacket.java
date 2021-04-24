package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class HeldItemChangePacket implements PacketIn {

  private int heldItemHotbarIndex;

  public void read(ReadBuffer buf) throws IOException {
    int heldItemHotbarIndex = ((int)(buf.readByte())) & 255;
  }

  public int getId(EnumConnectionState state) {
    return 58;
  }

  public int getHeldItem() {
    return this.heldItemHotbarIndex;
  }
}
