package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;
import rawbot.game.position.BlockPos;
import rawbot.game.world.Block;

public class BlockChangePacket implements PacketIn {

  private BlockPos position;
  private int blockState;

  public void read(ReadBuffer buf) throws IOException {
    this.position = BlockPos.fromLong(buf.readLong());
    this.blockState = buf.readVarInt();
  }

  public int getId(EnumConnectionState state) {
    return 11;
  }

  public BlockPos getPosition() {
    return this.position;
  }

  public int getBlockState() {
    return this.blockState;
  }

  public Block getBlock() {
    return new Block(this.blockState >>> 4);
  }
}
