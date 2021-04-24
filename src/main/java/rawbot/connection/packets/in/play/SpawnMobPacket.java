package rawbot.connection.packets.in.play;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;
import rawbot.game.position.Vec3;
import rawbot.game.position.Vec3d;

public class SpawnMobPacket implements PacketIn {

  private int entityId;
  private UUID uniqueId;
  private EntityId type;
  private Vec3d position;
  private Vec3 velocity;
  private byte yaw;
  private byte pitch;
  private byte headPitch;
  private Map<Byte, Object> dataEntries;

  public void read(ReadBuffer buf) throws IOException {
    this.entityId = buf.readVarInt();
    this.uniqueId = new UUID(buf.readLong(), buf.readLong());
    this.type = EntityId.getEntityById(buf.readVarInt());
    this.position = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    this.yaw = buf.readByte();
    this.pitch = buf.readByte();
    this.headPitch = buf.readByte();
    this.velocity = new Vec3((int)buf.readShort(), (int)buf.readShort(), (int)buf.readShort());
    this.dataEntries = EntityMetaDataPacket.readEntries(buf);
  }

  public int getId(EnumConnectionState state) {
    return 3;
  }

  public int getEntityId() {
    return this.entityId;
  }

  public UUID getUUID() {
    return this.uniqueId;
  }

  public EntityId getType() {
    return this.type;
  }

  public Vec3d getPosition() {
    return this.position;
  }

  public Vec3 getVelocity() {
    return this.velocity;
  }

  public byte getYaw() {
    return this.yaw;
  }

  public byte getPitch() {
    return this.pitch;
  }

  public byte getHeadPitch() {
    return this.headPitch;
  }

  public Map<Byte, Object> getMetaDatas() {
    return this.dataEntries;
  }

  public static enum EntityId {
    ITEM(1),
    XP_ORB(2),
    AREA_EFFECT_CLOUD(3),
    ELDER_GUARDIAN(4),
    WITHER_SKELETON(5),
    STRAY(6),
    EGG(7),
    LEASH_KNOT(8),
    PAINTING(9),
    ARROW(10),
    SNOWBALL(11),
    FIREBALL(12),
    SMALL_FIREBALL(13),
    ENDER_PEARL(14),
    EYE_OF_ENDER_SIGNAL(15),
    POTION(16),
    XP_BOTTLE(17),
    ITEM_FRAME(18),
    WITHER_SKULL(19),
    TNT(20),
    FALLING_BLOCK(21),
    FIREWORKS_ROCKET(22),
    HUSK(23),
    SPECTRAL_ARROW(24),
    SHULKER_BULLET(25),
    DRAGON_FIREBALL(26),
    ZOMBIE_VILLAGER(27),
    SKELETON_HORSE(28),
    ZOMBIE_HORSE(29),
    ARMOR_STAND(30),
    DONKEY(31),
    MULE(32),
    EVOCATION_FANGS(33),
    EVOCATION_ILLAGER(34),
    VEX(35),
    VINDICATION_ILLAGER(36),
    ILLUSION_ILLAGER(37),
    COMMANDBLOCK_MINECART(40),
    BOAT(41),
    MINECART(42),
    CHEST_MINECART(43),
    FURNACE_MINECART(44),
    TNT_MINECART(45),
    HOPPER_MINECART(46),
    SPAWNER_MINECART(47),
    CREEPER(50),
    SKELETON(51),
    SPIDER(52),
    GIANT(53),
    ZOMBIE(54),
    SLIME(55),
    GHAST(56),
    ZOMBIE_PIGMAN(57),
    ENDERMAN(58),
    CAVE_SPIDER(59),
    SILVERFISH(60),
    BLAZE(61),
    MAGMA_CUBE(62),
    ENDER_DRAGON(63),
    WITHER(64),
    BAT(65),
    WITCH(66),
    ENDERMITE(67),
    GUARDIAN(68),
    SHULKER(69),
    PIG(90),
    SHEEP(91),
    COW(92),
    CHICKEN(93),
    SQUID(94),
    WOLF(95),
    MOOSHROOM(96),
    SNOWMAN(97),
    OCELOT(98),
    VILLAGER_GOLEM(99),
    HORSE(100),
    RABBIT(101),
    POLAR_BEAR(102),
    LLAMA(103),
    LLAMA_SPIT(104),
    PARROT(105),
    VILLAGER(120),
    ENDER_CRYSTAL(200),
    PLAYER(666),
    UNKNOWN(420);

    private int id;

    private EntityId(int id) {
      this.id = id;
    }

    public int getId() {
      return this.id;
    }

    private static Map<Integer, EntityId> mapping = new HashMap<Integer, EntityId>();
    static {
      for(EntityId e : EntityId.values()) {
        mapping.put(e.getId(), e);
      }
    }

    public static EntityId getEntityById(int id) {
      EntityId out = EntityId.mapping.get(id);
      if(out == null) return EntityId.UNKNOWN;
      return out;
    }
  }
}
