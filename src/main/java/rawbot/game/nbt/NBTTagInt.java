package rawbot.game.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBTPrimitive {

  private int data;

  public NBTTagInt() {}

  public NBTTagInt(int data) {
    this.data = data;
  }

  public void write(DataOutput output) throws IOException {
    output.writeInt(this.data);
  }

  public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
    sizeTracker.read(96L);
    this.data = input.readInt();
  }

  public byte getId() {
    return 3;
  }

  public String toString() {
    return String.valueOf(this.data);
  }

  public NBTTagInt copy() {
    return new NBTTagInt(this.data);
  }

  public boolean equals(Object obj) {
    return super.equals(obj) && this.data == ((NBTTagInt)obj).data;
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
    return (short)(this.data & 65535);
  }

  public byte getByte() {
    return (byte)(this.data & 255);
  }

  public double getDouble() {
    return (double)this.data;
  }

  public float getFloat() {
    return (float)this.data;
  }
}
