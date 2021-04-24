package rawbot.game.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagFloat extends NBTPrimitive {

  private float data;

  public NBTTagFloat() {}

  public NBTTagFloat(float data) {
    this.data = data;
  }

  public void write(DataOutput output) throws IOException {
    output.writeFloat(this.data);
  }

  public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
    sizeTracker.read(96L);
    this.data = input.readFloat();
  }

  public byte getId() {
    return 5;
  }

  public String toString() {
    return this.data + "f";
  }

  public NBTTagFloat copy() {
    return new NBTTagFloat(this.data);
  }

  public boolean equals(Object obj) {
    return super.equals(obj) && this.data == ((NBTTagFloat)obj).data;
  }

  public int hashCode() {
    return super.hashCode() ^ Float.floatToIntBits(this.data);
  }

  public long getLong() {
    return (long)this.data;
  }

  public int getInt() {
    int i = (int)this.data;
    return this.data < (float)i ? i - 1 : i;
  }

  public short getShort() {
    int i = (int)this.data;
    return (short)((this.data < (float)i ? i - 1 : i) & 65535);
  }

  public byte getByte() {
    int i = (int)this.data;
    return (byte)((this.data < (float)i ? i - 1 : i) & 255);
  }

  public double getDouble() {
    return (double)this.data;
  }

  public float getFloat() {
    return this.data;
  }
}
