package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;
import rawbot.game.items.ItemStack;

public class SetSlotPacket implements PacketIn {

  private int windowId;
  private int slot;
  private ItemStack item = ItemStack.EMPTY;

  public void read(ReadBuffer buf) throws IOException {
    this.windowId = buf.readByte();
    this.slot = buf.readShort();
    this.item = buf.readItemStack();
  }

  public int getId(EnumConnectionState state) {
    return 22;
  }

  public int getWindowId() {
    return this.windowId;
  }

  public int getSlot() {
    return this.slot;
  }

  public ItemStack getStack() {
    return this.item;
  }
}
