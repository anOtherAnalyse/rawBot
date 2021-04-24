package rawbot.connection.packets.out.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;
import rawbot.game.items.ItemStack;

public class ClickWindowPacket implements PacketOut {

  /** The id of the window which was clicked. 0 for player inventory. */
  private int windowId;

  /** Id of the clicked slot */
  private int slotId;

  /** Button used */
  private int packedClickData;

  /** A unique number for the action, used for transaction handling */
  private short actionNumber;

  /** Expected stack result */
  private ItemStack clickedItem = ItemStack.EMPTY;

  /** Inventory operation type */
  private ClickType mode;

  public ClickWindowPacket(int windowId, int slot, int packedClickData, ItemStack item, ClickType mode, short actionNumber)
  {
    this.windowId = windowId;
    this.slotId = slot;
    this.clickedItem = item;
    this.packedClickData = packedClickData;
    this.actionNumber = actionNumber;
    this.mode = mode;
  }

  public void write(WriteBuffer buf) throws IOException {
    buf.writeByte((byte)this.windowId);
    buf.writeShort((short)this.slotId);
    buf.writeByte((byte)this.packedClickData);
    buf.writeShort(this.actionNumber);
    buf.writeVarInt(this.mode.ordinal());
    buf.writeItemStack(this.clickedItem);
  }

  public int getId(EnumConnectionState state) {
    return 7;
  }

  public static enum ClickType {
    PICKUP,
    QUICK_MOVE,
    SWAP,
    CLONE,
    THROW,
    QUICK_CRAFT,
    PICKUP_ALL;
  }
}
