package rawbot.connection.packets.out.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;
import rawbot.game.position.BlockPos;

public class UpdateSignPacket implements PacketOut {

  private BlockPos position;
  private String[] lines;

  public UpdateSignPacket(int x, int y, int z, String[] lines) {
    this.position = new BlockPos(x, y, z);
    this.lines = lines;
  }

  public void write(WriteBuffer buf) throws IOException {
    buf.writeLong(BlockPos.toLong(this.position));
    for(int i = 0; i < 4; i ++) {
      if(i < this.lines.length)
        buf.writeString(this.lines[i]);
      else buf.writeString("");
    }
  }

  public int getId(EnumConnectionState state) {
    return 28;
  }
}
