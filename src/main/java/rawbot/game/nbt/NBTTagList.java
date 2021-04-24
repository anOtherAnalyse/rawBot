package rawbot.game.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

public class NBTTagList extends NBTBase {

  private List<NBTBase> tagList = Lists.<NBTBase>newArrayList();

  private byte tagType = 0;

  public NBTTagList() {}

  public NBTTagList(byte type) {
    this.tagType = type;
  }

  public void addTag(NBTBase tag) {
    this.tagList.add(tag);
  }

  public void write(DataOutput output) throws IOException {
    if(this.tagList.isEmpty()) this.tagType = 0;
    else this.tagType = this.tagList.get(0).getId();

    output.writeByte(this.tagType);
    output.writeInt(this.tagList.size());

    for(int i = 0; i < this.tagList.size(); ++i) {
      this.tagList.get(i).write(output);
    }
  }

  public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
    sizeTracker.read(296L);

    if(depth > 512) throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");

    this.tagType = input.readByte();
    int i = input.readInt();

    if(this.tagType == 0 && i > 0) throw new RuntimeException("Missing type on ListTag");

    sizeTracker.read(32L * (long)i);
    this.tagList = Lists.<NBTBase>newArrayListWithCapacity(i);

    for(int j = 0; j < i; ++j) {
      NBTBase nbtbase = NBTBase.create(this.tagType);
      nbtbase.read(input, depth + 1, sizeTracker);
      this.tagList.add(nbtbase);
    }
  }

  public byte getId() {
    return 9;
  }

  public String toString() {
    StringBuilder stringbuilder = new StringBuilder("[");

    for(int i = 0; i < this.tagList.size(); ++i) {
      if(i != 0) stringbuilder.append(',');
      stringbuilder.append(this.tagList.get(i));
    }

    return stringbuilder.append(']').toString();
  }

  public NBTBase removeAtIndex(int index) {
    return this.tagList.remove(index);
  }

  public boolean isEmpty() {
    return this.tagList.isEmpty();
  }

  public NBTTagCompound getCompoundTagAt(int i) {
    if(i >= 0 && i < this.tagList.size()) {
      NBTBase nbtbase = (NBTBase)this.tagList.get(i);

      if(nbtbase.getId() == 10) {
        return (NBTTagCompound) nbtbase;
      }
    }
    return new NBTTagCompound();
  }

  public int getIntAt(int index) {
    if(index >= 0 && index < this.tagList.size()) {
        NBTBase nbtbase = (NBTBase)this.tagList.get(index);

        if(nbtbase.getId() == 3) {
          return ((NBTTagInt)nbtbase).getInt();
        }
    }
    return 0;
  }

  public int[] getIntArrayAt(int i) {
    if(i >= 0 && i < this.tagList.size()) {
      NBTBase nbtbase = (NBTBase)this.tagList.get(i);

      if(nbtbase.getId() == 11) {
        return ((NBTTagIntArray)nbtbase).getIntArray();
      }
    }
    return new int[0];
  }

  public double getDoubleAt(int i) {
    if(i >= 0 && i < this.tagList.size()) {
      NBTBase nbtbase = (NBTBase)this.tagList.get(i);

      if(nbtbase.getId() == 6) {
        return ((NBTTagDouble)nbtbase).getDouble();
      }
    }
    return 0.0D;
  }

  public float getFloatAt(int i) {
    if(i >= 0 && i < this.tagList.size()) {
      NBTBase nbtbase = (NBTBase)this.tagList.get(i);

      if(nbtbase.getId() == 5) {
        return ((NBTTagFloat)nbtbase).getFloat();
      }
    }
    return 0.0F;
  }

  public String getStringTagAt(int i) {
    if(i >= 0 && i < this.tagList.size()) {
      NBTBase nbtbase = (NBTBase)this.tagList.get(i);
      return nbtbase.getId() == 8 ? nbtbase.getString() : nbtbase.toString();
    }
    return "";
  }

  public NBTBase getTagAt(int index) {
    return (NBTBase)(index >= 0 && index < this.tagList.size() ? this.tagList.get(index) : new NBTTagEnd());
  }

  public int size() {
    return this.tagList.size();
  }

  public NBTTagList copy() {
    NBTTagList nbttaglist = new NBTTagList();
    nbttaglist.tagType = this.tagType;

    for(NBTBase nbtbase : this.tagList) {
      NBTBase cpy = nbtbase.copy();
      nbttaglist.tagList.add(cpy);
    }

    return nbttaglist;
  }

  public boolean equals(Object obj) {
    if(! super.equals(obj)) return false;

    NBTTagList nbttaglist = (NBTTagList)obj;
    return this.tagType == nbttaglist.tagType && Objects.equals(this.tagList, nbttaglist.tagList);
  }

  public int hashCode() {
    return super.hashCode() ^ this.tagList.hashCode();
  }

  public int getTagType() {
    return this.tagType;
  }
}
