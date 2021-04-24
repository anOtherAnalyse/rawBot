package rawbot.game.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class NBTBase {

  public abstract void write(DataOutput output) throws IOException;

  public abstract void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException;

  public abstract String toString();

  public abstract byte getId();

  public abstract NBTBase copy();

  protected static NBTBase create(byte id) {
    switch(id) {
        case 0:
            return new NBTTagEnd();
        case 1:
            return new NBTTagByte();
        case 2:
            return new NBTTagShort();
        case 3:
            return new NBTTagInt();
        case 4:
            return new NBTTagLong();
        case 5:
            return new NBTTagFloat();
        case 6:
            return new NBTTagDouble();
        case 7:
            return new NBTTagByteArray();
        case 8:
            return new NBTTagString();
        case 9:
            return new NBTTagList();
        case 10:
            return new NBTTagCompound();
        case 11:
            return new NBTTagIntArray();
        case 12:
            return new NBTTagLongArray();
        default:
            return null;
    }
  }

  public boolean isEmpty() {
    return false;
  }

  public boolean equals(Object obj) {
    return (obj instanceof NBTBase) && (this.getId() == ((NBTBase)obj).getId());
  }

  public int hashCode() {
    return this.getId();
  }

  protected String getString() {
    return this.toString();
  }
}
