package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class DestroyEntitiesPacket implements PacketIn {

  private int[] entities;

  public void read(ReadBuffer buf) throws IOException {
    int length = buf.readVarInt();
    this.entities = new int[length];
    for(int i = 0; i < this.entities.length; ++i) {
      this.entities[i] = buf.readVarInt();
    }
  }

  public int getId(EnumConnectionState state) {
    return 50;
  }

  public int[] getEntities() {
    return this.entities;
  }
}
