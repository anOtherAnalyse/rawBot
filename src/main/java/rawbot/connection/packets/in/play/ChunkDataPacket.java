package rawbot.connection.packets.in.play;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;
import rawbot.game.nbt.NBTTagCompound;
import rawbot.game.position.ChunkPos;

public class ChunkDataPacket implements PacketIn {

  private ChunkPos position;
  private boolean fullChunk;
  private int availableSections;
  private List<NBTTagCompound> tileEntityTags;

  private byte[] data;

  public void read(ReadBuffer buf) throws IOException {
    this.position = new ChunkPos(buf.readInt(), buf.readInt());
    this.fullChunk = buf.readBoolean();
    this.availableSections = buf.readVarInt();
    int i = buf.readVarInt();

    if (i > 2097152) {
      throw new RuntimeException("Chunk Packet trying to allocate too much memory on read");
    }

    this.data = new byte[i];
    buf.readBytes(this.data, i);

    int j = buf.readVarInt();
    this.tileEntityTags = new ArrayList<NBTTagCompound>(j);
    for (int k = 0; k < j; ++k) {
        this.tileEntityTags.add(buf.readCompoundTag());
    }
  }

  public int getId(EnumConnectionState state) {
    return 32;
  }

  public ReadBuffer getData() {
    return new ReadBuffer(this.data);
  }

  public ChunkPos getPosition() {
    return this.position;
  }

  public boolean isFull() {
    return this.fullChunk;
  }

  public int getAvailableSections() {
    return this.availableSections;
  }

  public List<NBTTagCompound> getTilesEntities() {
    return this.tileEntityTags;
  }
}
