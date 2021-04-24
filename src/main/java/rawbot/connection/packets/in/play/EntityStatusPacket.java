package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class EntityStatusPacket implements PacketIn {

  private int entityId;
  private StatusOpCode status;

  public void read(ReadBuffer buf) throws IOException {
    this.entityId = buf.readInt();
    this.status = StatusOpCode.getStatus(buf.readByte());
  }

  public int getId(EnumConnectionState state) {
    return 27;
  }

  public int getEntity() {
    return this.entityId;
  }

  public StatusOpCode getStatus() {
    return this.status;
  }

  public static enum StatusOpCode {
    DEATH(3), THORNS_HIT(33), GENERIC_HIT(2), DROWN_HIT(36),
    FIRE_HIT(37), SHIELD_BREAK(30), SHIELD_BLOCK(29),
    PERMISSION_LEVEL_0(24), PERMISSION_LEVEL_1(25),
    PERMISSION_LEVEL_2(26), PERMISSION_LEVEL_3(27),
    PERMISSION_LEVEL_4(28),ITEM_USE_FINISH(9), REDUCED_DEBUG_OFF(23),
    REDUCED_DEBUG_ON(22), UNKNOWN(420);

    private byte opCode;

    private StatusOpCode(int opCode) {
      this.opCode = (byte)opCode;
    }

    public byte getOpCode() {
      return this.opCode;
    }

    public static StatusOpCode getStatus(byte opCode) {
      for(StatusOpCode s : StatusOpCode.values()) {
        if(s.getOpCode() == opCode) return s;
      }
      return StatusOpCode.UNKNOWN;
    }
  }
}
