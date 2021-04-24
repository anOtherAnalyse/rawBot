package rawbot.connection.packets.out.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;

public class EntityActionPacket implements PacketOut {

  private int entityID;
  private Action action;
  private int auxData;

  public EntityActionPacket(int entityIn, Action actionIn, int auxDataIn) {
      this.entityID = entityIn;
      this.action = actionIn;
      this.auxData = auxDataIn;
  }

  public EntityActionPacket(int entityIn, Action actionIn) {
      this(entityIn, actionIn, 0);
  }

  public void write(WriteBuffer buf) throws IOException {
    buf.writeVarInt(this.entityID);
    buf.writeVarInt(this.action.ordinal());
    buf.writeVarInt(this.auxData);
  }

  public int getId(EnumConnectionState state) {
    return 21;
  }

  public static enum Action {
      START_SNEAKING,
      STOP_SNEAKING,
      STOP_SLEEPING,
      START_SPRINTING,
      STOP_SPRINTING,
      START_RIDING_JUMP,
      STOP_RIDING_JUMP,
      OPEN_INVENTORY,
      START_FALL_FLYING;
  }
}
