package rawbot.connection.packets.out.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;
import rawbot.game.position.BlockPos;
import rawbot.game.utils.EnumFacing;

public class PlayerDiggingPacket implements PacketOut {

  private BlockPos position;
  private EnumFacing facing;
  private Action action;

  public PlayerDiggingPacket(BlockPos position, EnumFacing facing, Action action) {
    this.position = position;
    this.facing = facing;
    this.action = action;
  }

  public void write(WriteBuffer buf) throws IOException {
    buf.writeVarInt(this.action.ordinal());
    buf.writeLong(BlockPos.toLong(this.position));
    buf.writeByte((byte) this.facing.ordinal());
  }

  public int getId(EnumConnectionState state) {
    return 20;
  }

  public static enum Action {
    START_DESTROY_BLOCK,
    ABORT_DESTROY_BLOCK,
    STOP_DESTROY_BLOCK,
    DROP_ALL_ITEMS,
    DROP_ITEM,
    RELEASE_USE_ITEM,
    SWAP_HELD_ITEMS;
  }
}
