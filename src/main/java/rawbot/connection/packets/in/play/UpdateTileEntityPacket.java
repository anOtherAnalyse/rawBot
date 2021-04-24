package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;
import rawbot.game.nbt.NBTTagCompound;
import rawbot.game.position.BlockPos;

public class UpdateTileEntityPacket implements PacketIn {

  private BlockPos position;
  private NBTTagCompound tag;
  private int type;

  public void read(ReadBuffer buf) throws IOException {
    this.position = BlockPos.fromLong(buf.readLong());
    this.type = ((int)buf.readByte()) & 255;
    this.tag = buf.readCompoundTag();
  }

  public int getId(EnumConnectionState state) {
    return 9;
  }

  public BlockPos getPosition() {
    return this.position;
  }

  public NBTTagCompound getTag() {
    return this.tag;
  }

  public int getType() {
    return this.type;
  }
}
