package rawbot.connection.packets.in.play;

import java.io.IOException;
import java.util.UUID;
import java.util.Map;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;
import rawbot.game.position.Vec3d;

public class SpawnPlayerPacket implements PacketIn {

  private int entityId;
  private UUID uniqueId;
  private Vec3d position;
  private byte yaw;
  private byte pitch;
  private Map<Byte, Object> dataEntries;

  public void read(ReadBuffer buf) throws IOException {
    this.entityId = buf.readVarInt();
    this.uniqueId = new UUID(buf.readLong(), buf.readLong());
    this.position = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    this.yaw = buf.readByte();
    this.pitch = buf.readByte();
    this.dataEntries = EntityMetaDataPacket.readEntries(buf);
  }

  public int getId(EnumConnectionState state) {
    return 5;
  }

  public int getEntityId() {
    return this.entityId;
  }

  public UUID getUUID() {
    return this.uniqueId;
  }

  public Vec3d getPosition() {
    return this.position;
  }

  public byte getYaw() {
    return this.yaw;
  }

  public byte getPitch() {
    return this.pitch;
  }

  public Map<Byte, Object> getMetaDatas() {
    return this.dataEntries;
  }
}
