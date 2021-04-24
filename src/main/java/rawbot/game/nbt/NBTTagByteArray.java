package rawbot.game.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NBTTagByteArray extends NBTBase {

  private static byte[] toArray(List<Byte> list) {
    byte[] abyte = new byte[list.size()];

    for(int i = 0; i < list.size(); ++i) {
        Byte obyte = (Byte)list.get(i);
        abyte[i] = (obyte == null ? 0 : obyte.byteValue());
    }

    return abyte;
  }

  private byte[] data;

  public NBTTagByteArray() {}

  public NBTTagByteArray(byte[] data) {
    this.data = data;
  }

  public NBTTagByteArray(List<Byte> list) {
    this(NBTTagByteArray.toArray(list));
  }

  public void write(DataOutput output) throws IOException {
    output.writeInt(this.data.length);
    output.write(this.data);
  }

  public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
    sizeTracker.read(192L);
    int i = input.readInt();
    sizeTracker.read((long)(8 * i));
    this.data = new byte[i];
    input.readFully(this.data);
  }

  public byte getId() {
    return 7;
  }

  public String toString() {
    StringBuilder stringbuilder = new StringBuilder("[B;");

    for (int i = 0; i < this.data.length; ++i) {

        if (i != 0) stringbuilder.append(',');

        stringbuilder.append(this.data[i]).append('B');
    }

    return stringbuilder.append(']').toString();
  }

  public NBTBase copy() {
    byte[] abyte = new byte[this.data.length];
    System.arraycopy(this.data, 0, abyte, 0, this.data.length);
    return new NBTTagByteArray(abyte);
  }

  public boolean equals(Object obj) {
    return super.equals(obj) && Arrays.equals(this.data, ((NBTTagByteArray)obj).data);
  }

  public int hashCode() {
    return super.hashCode() ^ Arrays.hashCode(this.data);
  }

  public byte[] getByteArray() {
    return this.data;
  }
}
