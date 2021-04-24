package rawbot.game.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTPrimitive {

  private short data;

  public NBTTagShort() {}

  public NBTTagShort(short data) {
    this.data = data;
  }

  public void write(DataOutput output) throws IOException {
    output.writeShort(this.data);
  }

  public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
    sizeTracker.read(80L);
    this.data = input.readShort();
  }

  public byte getId() {
    return 2;
  }

  public String toString() {
    return this.data + "s";
  }

  public NBTTagShort copy() {
    return new NBTTagShort(this.data);
  }

  public boolean equals(Object obj) {
    return super.equals(obj) && this.data == ((NBTTagShort)obj).data;
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
    return this.data;
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
