package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;
import rawbot.game.position.ChunkPos;

public class UnloadChunkPacket implements PacketIn {

  private ChunkPos position;

  public void read(ReadBuffer buf) throws IOException {
    this.position = new ChunkPos(buf.readInt(), buf.readInt());
  }

  public int getId(EnumConnectionState state) {
    return 29;
  }

  public ChunkPos getPosition() {
    return this.position;
  }
}
