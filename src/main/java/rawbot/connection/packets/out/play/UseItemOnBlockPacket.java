package rawbot.connection.packets.out.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;
import rawbot.game.position.BlockPos;
import rawbot.game.utils.EnumHand;
import rawbot.game.utils.EnumFacing;

public class UseItemOnBlockPacket implements PacketOut {

  private BlockPos position;
  private EnumFacing placedBlockDirection;
  private EnumHand hand;
  private float facingX;
  private float facingY;
  private float facingZ;

  public UseItemOnBlockPacket(BlockPos position) {
    this(position, EnumFacing.UP, EnumHand.MAIN_HAND, 0f, 0f, 0f);
  }

  public UseItemOnBlockPacket(BlockPos position, EnumFacing placedBlockDirection, EnumHand hand, float fx, float fy, float fz) {
    this.position = position;
    this.placedBlockDirection = placedBlockDirection;
    this.hand = hand;
    this.facingX = fx;
    this.facingY = fy;
    this.facingZ = fz;
  }

  public void write(WriteBuffer buf) throws IOException {
    buf.writeBlockPos(this.position);
    buf.writeByte((byte) this.placedBlockDirection.ordinal());
    buf.writeByte((byte) this.hand.ordinal());
    buf.writeFloat(this.facingX);
    buf.writeFloat(this.facingY);
    buf.writeFloat(this.facingZ);
  }

  public int getId(EnumConnectionState state) {
    return 31;
  }
}
