package rawbot.connection;

import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import rawbot.game.items.Item;
import rawbot.game.items.ItemStack;
import rawbot.game.nbt.NBTTagCompound;
import rawbot.game.nbt.CompressedStreamTools;
import rawbot.game.nbt.NBTSizeTracker;
import rawbot.game.text.TextComponentBase;

public class ReadBuffer {

  private byte[] wrap;
  private int cursor;

  public ReadBuffer(byte[] wrap) {
    this.wrap = wrap;
    this.cursor = 0;
  }

  /* Getters / Setters */

  public byte[] getWrapped() {
    return this.wrap;
  }

  public int getCursor() {
    return this.cursor;
  }

  public int left() {
    return this.wrap.length - this.cursor;
  }

  public void setCursor(int cursor) {
    this.cursor = cursor;
  }

  public byte[] getRest() {
    int left = this.wrap.length - this.cursor;
    if(left <= 0) return null;

    byte[] out = new byte[left];
    System.arraycopy(this.wrap, this.cursor, out, 0, left);
    this.cursor = this.wrap.length;
    return out;
  }

  public void end() {
    this.cursor = this.wrap.length;
  }

  /* Basic reads */

  public byte readByte() throws IOException {
    if(this.cursor >= this.wrap.length)
      throw new IOException("ReadBuffer out of bound");
    return this.wrap[this.cursor++];
  }

  public void readBytes(byte[] buff, int size) throws IOException {
    if(size > (this.wrap.length - this.cursor))
      throw new IOException("ReadBuffer out of bound");
    System.arraycopy(this.wrap, this.cursor, buff, 0, size);
    this.cursor += size;
  }

  /* Type specific reads */

  public int readVarInt() throws IOException {
    int i = 0, j = 0;
    byte c;
    do {
      c = this.readByte();
      i |= (c & 127) << (7 * (j++));
    } while((c & 128) != 0);
    return i;
  }

  public int readInt() throws IOException {
    int i = 0;
    byte c;
    for(int j = 3; j >= 0; j --) {
      c = this.readByte();
      i |= (((int)c) & 255) << (j * 8);
    }
    return i;
  }

  public short readShort() throws IOException {
    short i = 0;
    byte c;
    for(int j = 1; j >= 0; j --) {
      c = this.readByte();
      i |= (((short)c) & 255) << (j * 8);
    }
    return i;
  }

  public long readLong() throws IOException {
    long i = 0;
    byte c;
    for(int j = 7; j >= 0; j --) {
      c = this.readByte();
      i |= (((long)c) & 255) << (j * 8);
    }
    return i;
  }

  public void readLongArray(long[] array) throws IOException {
    int length = this.readVarInt();
    if(array == null || array.length != length) array = new long[length];
    for(int j = 0; j < array.length; ++j) {
      array[j] = this.readLong();
    }
  }

  public float readFloat() throws IOException {
    return Float.intBitsToFloat(this.readInt());
  }

  public double readDouble() throws IOException {
    return Double.longBitsToDouble(this.readLong());
  }

  public boolean readBoolean() throws IOException {
    return (this.readByte() != 0);
  }

  public byte[] readByteArray() throws IOException {
    int length = this.readVarInt();
    byte[] rcv = new byte[length];
    this.readBytes(rcv, length);
    return rcv;
  }

  public String readString() throws IOException {
    int length = this.readVarInt();
    if(length > 32767) {
      throw new IOException("Received string exceed limit size of 32767, was " + Integer.toString(length));
    }

    byte[] str = new byte[length];
    this.readBytes(str, length);
    return new String(str, StandardCharsets.UTF_8);
  }

  public TextComponentBase readTextComponent() throws IOException {
    return TextComponentBase.Serializer.jsonToComponent(this.readString(), false);
  }

  public ItemStack readItemStack() throws IOException {
    int itemId = this.readShort();

    if(itemId < 0) {
      return ItemStack.EMPTY;
    }

    ItemStack i = new ItemStack(new Item(itemId), this.readByte(), this.readShort());
    i.setTagCompound(this.readCompoundTag());
    return i;
  }

  public NBTTagCompound readCompoundTag() throws IOException {
    int index = this.getCursor();
    byte b0 = this.readByte();
    if(b0 == 0) return null;

    this.setCursor(index);
    ByteArrayInputStream input = new ByteArrayInputStream(this.wrap, this.cursor, this.wrap.length - this.cursor);
    DataInputStream d = new DataInputStream(input);
    NBTTagCompound out = CompressedStreamTools.read(d, new NBTSizeTracker(2097152L));
    this.setCursor(this.wrap.length - input.available());
    return out;
  }
}
