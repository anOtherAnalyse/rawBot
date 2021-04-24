package rawbot.game.world;

import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

import rawbot.connection.ReadBuffer;
import rawbot.game.position.ChunkPos;
import rawbot.game.position.BlockPos;
import rawbot.game.nbt.NBTTagCompound;

// Can be used to store chunks sent in ChunkDataPacket
public class Chunk {

  private ChunkPos position;

  private ExtendedBlockStorage[] storage;

  private List<NBTTagCompound> tileTag;

  private boolean hasSkyLight;

  // 16x16 mapping on the X/Z plane of the biome ID to which each colum belongs.
  private final byte[] blockBiomeArray;

  public Chunk(int x, int z, boolean hasSkyLight) {
    this.position = new ChunkPos(x, z);
    this.storage = new ExtendedBlockStorage[16];
    this.blockBiomeArray = new byte[256];
    this.tileTag = new LinkedList<NBTTagCompound>();
    this.hasSkyLight = hasSkyLight;
  }

  public void setBlockState(BlockPos position, int state) {
    int index_y = position.y / 16;
    if(index_y > 15 || this.storage[index_y] == null) return;
    this.storage[index_y].setBlockState(position.modulo(16), state);
  }

  public int getBlockState(BlockPos position) {
    int index_y = position.y / 16;
    if(index_y > 15 || this.storage[index_y] == null) return 0;
    return this.storage[index_y].getBlockState(position.modulo(16));
  }

  public Block getBlock(BlockPos position) {
    int index_y = position.y / 16;
    if(index_y > 15 || this.storage[index_y] == null) return null;
    return this.storage[index_y].getBlock(position.modulo(16));
  }

  public void addTiles(List<NBTTagCompound> tiles) {
    this.tileTag.addAll(tiles);
  }

  public void addTile(NBTTagCompound tile) {
    this.tileTag.add(tile);
  }

  public List<NBTTagCompound> getTiles() {
    return this.tileTag;
  }

  public ChunkPos getPosition() {
    return this.position;
  }

  // Retrieve chunk data from buffer - can be call multiple times for update
  public void read(ReadBuffer buf, int availableSections, boolean groundUpContinuous) throws IOException {
    for(int i = 0; i < this.storage.length; i ++) {
      if(((1 << i) & availableSections) == 0) {
        this.storage[i] = null;
      } else {
        if(this.storage[i] == null) this.storage[i] = new ExtendedBlockStorage(this.hasSkyLight);
        this.storage[i].read(buf);
      }
    }

    if(groundUpContinuous) {
      buf.readBytes(this.blockBiomeArray, 256);
    }
  }
}
