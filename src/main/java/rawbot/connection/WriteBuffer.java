package rawbot.connection;

import java.io.IOException;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import rawbot.game.items.ItemStack;
import rawbot.game.nbt.NBTTagCompound;
import rawbot.game.nbt.CompressedStreamTools;

public class WriteBuffer {

  private ByteArrayOutputStream stream;

  public WriteBuffer() {
    this.stream = new ByteArrayOutputStream();
  }

  /* Getters */

  public byte[] getBuff() {
    return this.stream.toByteArray();
  }

  /* Basic writes */

  public void writeByte(byte b) {
    this.stream.write(b);
  }

  public void writeBytes(byte[] buff, int size) {
    this.stream.write(buff, 0, size);
  }

  /* Type specific writes */

  public void writeVarInt(int i) {
    do {
      byte c = (byte)(i & 127);
      i = i >> 7;
      if(i != 0) c |= 128;
      this.writeByte(c);
    } while(i != 0);
  }

  public void writeShort(short i) {
    for(int j = 1; j >= 0; j --) {
      byte c = (byte)(i >> (8 * j));
      this.writeByte(c);
    }
  }

  public void writeInt(int i) {
    for(int j = 3; j >= 0; j --) {
      byte c = (byte)(i >> (8 * j));
      this.writeByte(c);
    }
  }

  public void writeLong(long i) {
    for(int j = 7; j >= 0; j --) {
      byte c = (byte)(i >> (8 * j));
      this.writeByte(c);
    }
  }

  public void writeFloat(float f) {
    int conv = Float.floatToIntBits(f);
    this.writeInt(conv);
  }

  public void writeDouble(double d) {
    long conv = Double.doubleToLongBits(d);
    this.writeLong(conv);
  }

  public void writeBoolean(boolean b) {
    if(b) this.writeByte((byte)1);
    else this.writeByte((byte)0);
  }

  public void writeUnsignedShort(int s) {
    this.writeByte((byte)((s >> 8) & 255));
    this.writeByte((byte)(s & 255));
  }

  public void writeByteArray(byte[] buff, int size) {
    this.writeVarInt(size);
    this.writeBytes(buff, size);
  }

  public void writeString(String s) {
    byte[] b = s.getBytes(StandardCharsets.UTF_8);
    this.writeVarInt(b.length);
    this.writeBytes(b, b.length);
  }

  public void writeItemStack(ItemStack stack) throws IOException {
    if(stack.getSize() <= 0) {
            this.writeShort((short)-1);
    } else {
      this.writeShort((short)stack.getItem().getId());
      this.writeByte((byte)stack.getSize());
      this.writeShort((short)stack.getDamage());
      NBTTagCompound nbttagcompound = stack.getTagCompound();
      this.writeCompoundTag(nbttagcompound);
    }
  }

  public void writeCompoundTag(NBTTagCompound nbt) throws IOException {
    if(nbt == null) {
      this.writeByte((byte)0);
    } else {
      DataOutputStream dataStr = new DataOutputStream(this.stream);
      CompressedStreamTools.write(nbt, dataStr);
    }
  }
}
