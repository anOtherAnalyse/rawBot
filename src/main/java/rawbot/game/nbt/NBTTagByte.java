package rawbot.game.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTPrimitive {

  private byte data;

  public NBTTagByte() {}

  public NBTTagByte(byte data) {
    this.data = data;
  }

  public void write(DataOutput output) throws IOException {
    output.writeByte(this.data);
  }

  public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
    sizeTracker.read(72L);
    this.data = input.readByte();
  }

  public byte getId() {
    return 1;
  }

  public String toString() {
    return this.data + "b";
  }

  public NBTTagByte copy() {
    return new NBTTagByte(this.data);
  }

  public boolean equals(Object obj) {
    return super.equals(obj) && this.data == ((NBTTagByte)obj).data;
  }

  public int hashCode() {
    return super.hashCode() ^ this.data;
  }

  public long getLong() {
    return (long)this.data;
  }

  public int getInt() {
    return this.data;
  }

  public short getShort() {
    return (short)this.data;
  }

  public byte getByte() {
    return this.data;
  }

  public double getDouble() {
    return (double)this.data;
  }

  public float getFloat() {
    return (float)this.data;
  }
}
