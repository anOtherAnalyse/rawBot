package rawbot.game.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NBTTagLongArray extends NBTBase {

  private long[] data;

  private static long[] toArray(List<Long> list) {
    long[] along = new long[list.size()];

    for(int i = 0; i < list.size(); ++i) {
      Long olong = list.get(i);
      along[i] = olong == null ? 0L : olong.longValue();
    }

    return along;
  }

  public NBTTagLongArray() {}

  public NBTTagLongArray(long[] array) {
    this.data = array;
  }

  public NBTTagLongArray(List<Long> list) {
    this(toArray(list));
  }

  public void write(DataOutput output) throws IOException {
    output.writeInt(this.data.length);

    for (long i : this.data) {
      output.writeLong(i);
    }
  }

  public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
    sizeTracker.read(192L);
    int i = input.readInt();
    sizeTracker.read((long)(64 * i));
    this.data = new long[i];

    for(int j = 0; j < i; ++j) {
      this.data[j] = input.readLong();
    }
  }

  public byte getId() {
    return 12;
  }

  public String toString() {
    StringBuilder stringbuilder = new StringBuilder("[L;");

    for(int i = 0; i < this.data.length; ++i) {
      if (i != 0) stringbuilder.append(',');
      stringbuilder.append(this.data[i]).append('L');
    }

    return stringbuilder.append(']').toString();
  }

  public NBTTagLongArray copy() {
    long[] along = new long[this.data.length];
    System.arraycopy(this.data, 0, along, 0, this.data.length);
    return new NBTTagLongArray(along);
  }

  public boolean equals(Object obj) {
    return super.equals(obj) && Arrays.equals(this.data, ((NBTTagLongArray)obj).data);
  }

  public int hashCode() {
    return super.hashCode() ^ Arrays.hashCode(this.data);
  }
}
