package rawbot.connection.packets.in.play;

import java.io.IOException;
import java.lang.Class;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Optional;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;
import rawbot.game.nbt.NBTTagCompound;
import rawbot.game.position.BlockPos;
import rawbot.game.position.Vec3f;

public class EntityMetaDataPacket implements PacketIn {

  private int entityId;
  private Map<Byte, Object> entries;

  public void read(ReadBuffer buf) throws IOException {
    this.entityId = buf.readVarInt();
    this.entries = EntityMetaDataPacket.readEntries(buf);
  }

  public static Map<Byte, Object> readEntries(ReadBuffer buf) throws IOException {
    Map<Byte, Object> out = new HashMap<Byte, Object>();
    int i;
    while((i = (((int)buf.readByte()) & 255)) != 255) {
      int id = buf.readVarInt();
      out.put((byte)i, EntityMetaDataPacket.readObject(id, buf));
    }
    return out;
  }

  private static Object readObject(int id, ReadBuffer buf) throws IOException {
    switch(id) {
      case 0:
        return Byte.valueOf(buf.readByte());
      case 1:
        return Integer.valueOf(buf.readVarInt());
      case 2:
        return Float.valueOf(buf.readFloat());
      case 3:
        return buf.readString();
      case 4:
        try {
          return buf.readTextComponent();
        } catch (java.io.IOException e) {
          return null;
        }
      case 5:
        return buf.readItemStack();
      case 6:
        return buf.readBoolean();
      case 7:
        return new Vec3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
      case 8:
        return BlockPos.fromLong(buf.readLong());
      case 9:
        return !buf.readBoolean() ? Optional.absent() : Optional.of(BlockPos.fromLong(buf.readLong()));
      case 10: // not handled
        return new String("Facing: " + Integer.toString(buf.readVarInt()));
      case 11:
        return !buf.readBoolean() ? Optional.absent() : Optional.of(new UUID(buf.readLong(), buf.readLong()));
      case 12: // not handled
        return new String("BlockState: " + Integer.toString(buf.readVarInt()));
      case 13:
        return buf.readCompoundTag();
    }
    throw new RuntimeException("Unknown metadata of id " + Integer.toString(id));
  }

  public int getId(EnumConnectionState state) {
    return 60;
  }

  public int getEntityId() {
    return this.entityId;
  }

  public Map<Byte, Object> getMetaDatas() {
    return this.entries;
  }

  public Object getMetaData(Byte id) {
    return this.entries.get(id);
  }

  /* Meaning for player metadatas */
  public static enum PlayerMetaData {
    FLAGS(0, Byte.class),
    AIR(1, Integer.class),
    CUSTOM_NAME(2, String.class),
    CUSTOM_NAME_VISIBLE(3, String.class),
    SILENT(4, Boolean.class),
    NO_GRAVITY(5, Boolean.class),
    HAND_STATES(6, Byte.class),
    HEALTH(7, Float.class),
    POTION_EFFECTS(8, Integer.class),
    HIDE_PARTICLES(9, Boolean.class),
    ARROW_COUNT_IN_ENTITY(10, Integer.class),
    ABSORPTION(11, Float.class),
    PLAYER_SCORE(12, Integer.class),
    PLAYER_MODEL_FLAG(13, Byte.class),
    MAIN_HAND(14, Byte.class),
    LEFT_SHOULDER_ENTITY(15, NBTTagCompound.class),
    RIGHT_SHOULDER_ENTITY(16, NBTTagCompound.class),
    UNKNOWN(17, Object.class);

    private int id;
    private Class<?> cl;

    private PlayerMetaData(int id, Class<?> cl) {
      this.id = id;
      this.cl = cl;
    }

    public int getId() {
      return this.id;
    }

    public Class<?> getObjectType() {
      return this.cl;
    }

    public static PlayerMetaData getPlayerMetaData(int id) {
      if(id < 0 || id > 16) return PlayerMetaData.UNKNOWN;
      return PlayerMetaData.values()[id];
    }
  }
}
