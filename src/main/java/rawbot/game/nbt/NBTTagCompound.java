package rawbot.game.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

public class NBTTagCompound extends NBTBase {

  private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");

  private final Map<String, NBTBase> tagMap = Maps.<String, NBTBase>newHashMap();

  public NBTTagCompound() {}

  public Set<String> getKeySet() {
    return this.tagMap.keySet();
  }

  public byte getId() {
    return 10;
  }

  public int getSize() {
    return this.tagMap.size();
  }

  public void write(DataOutput output) throws IOException {
    for (String s : this.tagMap.keySet())
    {
        NBTBase nbtbase = (NBTBase)this.tagMap.get(s);
        writeEntry(s, nbtbase, output);
    }

    output.writeByte(0);
  }

  public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
    sizeTracker.read(384L);

    if (depth > 512) throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");

    this.tagMap.clear();

    byte b0;
    while ((b0 = readType(input, sizeTracker)) != 0) {
      String s = readKey(input, sizeTracker);
      sizeTracker.read((long)(224 + 16 * s.length()));
      NBTBase nbtbase = NBTTagCompound.readNBT(b0, s, input, depth + 1, sizeTracker);

      if (this.tagMap.put(s, nbtbase) != null) {
          sizeTracker.read(288L);
      }
    }
  }

  /* Setters */

  public void setTag(String key, NBTBase value) {
    this.tagMap.put(key, value);
  }

  public void setByte(String key, byte value) {
    this.tagMap.put(key, new NBTTagByte(value));
  }

  public void setShort(String key, short value) {
    this.tagMap.put(key, new NBTTagShort(value));
  }

  public void setInteger(String key, int value) {
    this.tagMap.put(key, new NBTTagInt(value));
  }

  public void setLong(String key, long value) {
    this.tagMap.put(key, new NBTTagLong(value));
  }

  public void setUniqueId(String key, UUID value) {
    this.setLong(key + "Most", value.getMostSignificantBits());
    this.setLong(key + "Least", value.getLeastSignificantBits());
  }

  public void setFloat(String key, float value) {
    this.tagMap.put(key, new NBTTagFloat(value));
  }

  public void setDouble(String key, double value) {
    this.tagMap.put(key, new NBTTagDouble(value));
  }

  public void setString(String key, String value) {
    this.tagMap.put(key, new NBTTagString(value));
  }

  public void setByteArray(String key, byte[] value) {
    this.tagMap.put(key, new NBTTagByteArray(value));
  }

  public void setIntArray(String key, int[] value) {
    this.tagMap.put(key, new NBTTagIntArray(value));
  }

  public void setBoolean(String key, boolean value) {
    this.setByte(key, (byte)(value ? 1 : 0));
  }

  /* Getters */

  public NBTBase getTag(String key) {
    return (NBTBase)this.tagMap.get(key);
  }

  public byte getTagId(String key) {
    NBTBase nbtbase = (NBTBase)this.tagMap.get(key);
    return nbtbase == null ? 0 : nbtbase.getId();
  }

  public boolean hasKey(String key) {
    return this.tagMap.containsKey(key);
  }

  // type to 99 for any primitive type
  public boolean hasKey(String key, int type) {
    int i = this.getTagId(key);
    if (i == type || (type == 99 && i > 0 && i < 7)) return true;
    return false;
  }

  public byte getByte(String key) {
    try {
      if(this.hasKey(key, 99)) {
        return ((NBTPrimitive)this.tagMap.get(key)).getByte();
      }
    } catch(ClassCastException e) {}

    return 0;
  }

  public short getShort(String key) {
    try {
      if(this.hasKey(key, 99)) {
        return ((NBTPrimitive)this.tagMap.get(key)).getShort();
      }
    } catch(ClassCastException e) {}

    return 0;
  }

  public int getInteger(String key) {
    try {
      if(this.hasKey(key, 99)) {
        return ((NBTPrimitive)this.tagMap.get(key)).getInt();
      }
    } catch(ClassCastException e) {}

    return 0;
  }

  public long getLong(String key) {
    try {
      if(this.hasKey(key, 99)) {
        return ((NBTPrimitive)this.tagMap.get(key)).getLong();
      }
    } catch(ClassCastException e) {}

    return 0;
  }

  public float getFloat(String key) {
    try {
      if(this.hasKey(key, 99)) {
        return ((NBTPrimitive)this.tagMap.get(key)).getFloat();
      }
    } catch(ClassCastException e) {}

    return 0f;
  }

  public double getDouble(String key) {
    try {
      if(this.hasKey(key, 99)) {
        return ((NBTPrimitive)this.tagMap.get(key)).getDouble();
      }
    } catch(ClassCastException e) {}

    return 0d;
  }

  public UUID getUniqueId(String key) {
    return new UUID(this.getLong(key + "Most"), this.getLong(key + "Least"));
  }

  public boolean hasUniqueId(String key) {
    return this.hasKey(key + "Most", 99) && this.hasKey(key + "Least", 99);
  }

  public String getString(String key) {
    try {
      if(this.hasKey(key, 8)) {
        return ((NBTBase)this.tagMap.get(key)).getString();
      }
    } catch(ClassCastException e) {}

    return "";
  }

  public byte[] getByteArray(String key) {
    try {
      if(this.hasKey(key, 7)) {
        return ((NBTTagByteArray)this.tagMap.get(key)).getByteArray();
      }
    } catch(ClassCastException e) {}

    return new byte[0];
  }

  public int[] getIntArray(String key) {
    try {
      if(this.hasKey(key, 11)) {
        return ((NBTTagIntArray)this.tagMap.get(key)).getIntArray();
      }
    } catch(ClassCastException e) {}

    return new int[0];
  }

  public NBTTagCompound getCompoundTag(String key) {
    try {
      if(this.hasKey(key, 10)) {
        return (NBTTagCompound)this.tagMap.get(key);
      }
    } catch(ClassCastException e) {}

    return new NBTTagCompound();
  }

  public NBTTagList getTagList(String key, int type) {
    try {
      if(this.getTagId(key) == 9) {
        NBTTagList nbttaglist = (NBTTagList)this.tagMap.get(key);

        if(!nbttaglist.isEmpty() && nbttaglist.getTagType() != type) {
          return new NBTTagList();
        }

        return nbttaglist;
      }
    } catch(ClassCastException e) {}

    return new NBTTagList();
  }

  public boolean getBoolean(String key) {
    return this.getByte(key) != 0;
  }

  public void removeTag(String key) {
    this.tagMap.remove(key);
  }

  public String toString() {
    StringBuilder stringbuilder = new StringBuilder("{");
    Collection<String> collection = this.tagMap.keySet();

    for(String s : collection) {
      if(stringbuilder.length() != 1) {
          stringbuilder.append(',');
      }

      stringbuilder.append(handleEscape(s)).append(':').append(this.tagMap.get(s));
    }

    return stringbuilder.append('}').toString();
  }

  public boolean isEmpty() {
    return this.tagMap.isEmpty();
  }

  public NBTTagCompound copy() {
    NBTTagCompound nbttagcompound = new NBTTagCompound();

    for(String s : this.tagMap.keySet()) {
      nbttagcompound.setTag(s, ((NBTBase)this.tagMap.get(s)).copy());
    }

    return nbttagcompound;
  }

  public boolean equals(Object obj) {
    return super.equals(obj) && Objects.equals(this.tagMap.entrySet(), ((NBTTagCompound)obj).tagMap.entrySet());
  }

  public int hashCode() {
    return super.hashCode() ^ this.tagMap.hashCode();
  }

  private static void writeEntry(String name, NBTBase data, DataOutput output) throws IOException {
    output.writeByte(data.getId());

    if (data.getId() != 0) {
      output.writeUTF(name);
      data.write(output);
    }
  }

  private static byte readType(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
    return input.readByte();
  }

  private static String readKey(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
    return input.readUTF();
  }

  private static NBTBase readNBT(byte id, String key, DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
    NBTBase nbtbase = NBTBase.create(id);

    nbtbase.read(input, depth, sizeTracker);
    return nbtbase;
  }

  private static String handleEscape(String str) {
    return SIMPLE_VALUE.matcher(str).matches() ? str : NBTTagString.escape(str);
  }
}
