package rawbot.game.world;

import java.io.IOException;
import java.lang.Math;

import rawbot.connection.ReadBuffer;
import rawbot.game.position.BlockPos;

// 16 by 16 by 16 section of a chunk
public class ExtendedBlockStorage {

  public static BlockPos indexToPosition(int index) {
    return new BlockPos(index & 15, (index >> 8) & 15, (index >> 4) & 15);
  }

  public static int positionToIndex(BlockPos position) {
    return (position.y & 15) << 8 | (position.z & 15) << 4 | (position.x & 15);
  }

  // Blockstates palette
  private int[] blocks;
  private int length;

  // Actual chunk data, array of index in the palette
  private long[] bits;
  private int bit; // how many bits per entry in the bitmap

  private byte[] blockLight;
  private byte[] skyLight;

  public ExtendedBlockStorage(boolean storeSkylight) {
    this.bit = -1;
    this.length = 0;
    this.blockLight = new byte[2048];

    if(storeSkylight) this.skyLight = new byte[2048];
    else this.skyLight = null;
  }

  public int getBlockState(BlockPos position) {
    return this.get(ExtendedBlockStorage.positionToIndex(position));
  }

  public Block getBlock(BlockPos position) {
    return new Block(this.getBlockState(position) >>> 4);
  }

  public void setBlockState(BlockPos position, int state) {
    int index = -1;
    for(int i = 0; i < this.blocks.length; i ++) {
      if(this.blocks[i] == state) {
        index = i;
        break;
      }
    }
    if(index < 0) { // Need to add blockstate to the palette
      if(this.blocks.length >= (1 << this.bit)) { // need to extend the bitmap
        // save old bitmap
        int bit = this.bit;
        long[] save = this.bits;

        // create new bitmap
        this.bit ++;
        this.bits = new long[(int)Math.ceil((4096.0 * (float)this.bit) / 64.0)];

        // copy old bitmap
        for(int i = 0; i < 4096; i ++) {
          int j = ExtendedBlockStorage.getAt(i, save, bit);
          this.setAt(i, j);
        }
      }
      int[] palette = new int[this.blocks.length + 1];
      System.arraycopy(this.blocks, 0, palette, 0, this.blocks.length);
      palette[this.blocks.length] = state;
      index = this.blocks.length;
      this.blocks = palette;
    }
    int offset = ExtendedBlockStorage.positionToIndex(position);
    this.setAt(offset, index);
  }

  // Get palette index for given bitmap index
  public int getAt(int index) {
    return ExtendedBlockStorage.getAt(index, this.bits, this.bit);
  }

  public static int getAt(int index, long[] bits, int bit) {
    int offset = index * bit;
    int i = offset / 64;
    int j = (offset + bit - 1) / 64;
    int shift = offset % 64;
    if(i == j) {
      return (int)(bits[i] >> shift) & ((1 << bit) - 1);
    } else {
      return (int)((bits[i] >>> shift) | (bits[j] << (64 - shift))) & ((1 << bit) - 1);
    }
  }

  // Set given palette_index at given index in bitmap
  public void setAt(int index, int palette_index) {
    int offset = index * this.bit;
    int i = offset / 64;
    int j = (offset + this.bit - 1) / 64;
    int shift = offset % 64;
    long mask = ~(((1 << this.bit) - 1) << shift);
    this.bits[i] = (this.bits[i] & mask) | ((long)palette_index << shift);
    if(i != j) {
      int rest = this.bit - 64 + shift;
      mask = ~((1 << rest) - 1);
      this.bits[j] = (this.bits[j] & mask) | (palette_index >> (this.bit - rest));
    }
  }

  // Get block state at given index
  public int get(int index) {
    return this.blocks[this.getAt(index)];
  }

  // Read data from buffer - can be called multiple times in case of update
  public void read(ReadBuffer buf) throws IOException {
    // read nb bit per entry
    int bit = (int)buf.readByte();

    if(bit > 8) {
      bit = (int)(Math.log(bit) / Math.log(2)) + 1;
    }

    if(bit != this.bit) {
      this.bit = bit;
      this.blocks = new int[1 << bit];
      this.bits = new long[(int)Math.ceil((4096.0 * (float)bit) / 64.0)];
    }

    // read palette
    this.length = buf.readVarInt();
    if(this.length > this.blocks.length)
      throw new RuntimeException(String.format("Blocks palette of size %d (bits per entry = %d) can no contain transmitted palette of size %d", this.blocks.length, this.bit, this.length));

    for (int i = 0; i < this.length; ++i) {
      this.blocks[i] = buf.readVarInt();
    }

    // read bit array
    buf.readLongArray(this.bits);

    // read block light
    buf.readBytes(this.blockLight, this.blockLight.length);

    // read sky light (opt)
    if(this.skyLight != null) {
      buf.readBytes(this.skyLight, this.skyLight.length);
    }
  }
}
