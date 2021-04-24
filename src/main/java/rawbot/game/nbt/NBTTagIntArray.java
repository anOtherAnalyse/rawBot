package rawbot.game.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NBTTagIntArray extends NBTBase {

  private static int[] toArray(List<Integer> list) {
    int[] aint = new int[list.size()];

    for(int i = 0; i < list.size(); ++i) {
      Integer integer = (Integer)list.get(i);
      aint[i] = integer == null ? 0 : integer.intValue();
    }

    return aint;
  }

  private int[] intArray;

  public NBTTagIntArray() {}

  public NBTTagIntArray(int[] array) {
    this.intArray = array;
  }

  public NBTTagIntArray(List<Integer> list) {
    this(NBTTagIntArray.toArray(list));
  }

  public void write(DataOutput output) throws IOException {
    output.writeInt(this.intArray.length);

    for(int i : this.intArray) {
        output.writeInt(i);
    }
  }

  public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
    sizeTracker.read(192L);
    int i = input.readInt();
    sizeTracker.read((long)(32 * i));
    this.intArray = new int[i];

    for(int j = 0; j < i; ++j) {
      this.intArray[j] = input.readInt();
    }
  }

  public byte getId() {
    return 11;
  }

  public String toString() {
    StringBuilder stringbuilder = new StringBuilder("[I;");

    for(int i = 0; i < this.intArray.length; ++i) {
      if (i != 0) {
        stringbuilder.append(',');
      }

      stringbuilder.append(this.intArray[i]);
    }

    return stringbuilder.append(']').toString();
  }

  public NBTTagIntArray copy() {
    int[] aint = new int[this.intArray.length];
    System.arraycopy(this.intArray, 0, aint, 0, this.intArray.length);
    return new NBTTagIntArray(aint);
  }

  public boolean equals(Object obj) {
    return super.equals(obj) && Arrays.equals(this.intArray, ((NBTTagIntArray)obj).intArray);
  }

  public int hashCode() {
    return super.hashCode() ^ Arrays.hashCode(this.intArray);
  }

  public int[] getIntArray() {
    return this.intArray;
  }
}
