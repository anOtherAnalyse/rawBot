package rawbot.connection.packets.out.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;
import rawbot.game.position.Vec3d;

public class UseEntityPacket implements PacketOut {

  private int entityId;
  private Action action;
  private Vec3d hitVec;
  private EnumHand hand;

  public UseEntityPacket(int id, Action action) {
    this.entityId = id;
    this.action = action;
  }

  public UseEntityPacket(int id, Action action, EnumHand hand) {
    this.entityId = id;
    this.action = action;
    this.hand = hand;
  }

  public UseEntityPacket(int id, Action action, Vec3d vect) {
    this.entityId = id;
    this.action = action;
    this.hitVec = vect;
  }

  public void write(WriteBuffer buf) throws IOException {
    buf.writeVarInt(this.entityId);
    buf.writeVarInt(this.action.ordinal());

    if (this.action == Action.INTERACT_AT) {
      buf.writeFloat((float)this.hitVec.x);
      buf.writeFloat((float)this.hitVec.y);
      buf.writeFloat((float)this.hitVec.z);
    }

    if (this.action == Action.INTERACT || this.action == Action.INTERACT_AT) {
      buf.writeVarInt(this.hand.ordinal());
    }
  }

  public int getId(EnumConnectionState state) {
    return 10;
  }

  public static enum EnumHand {
    MAIN_HAND,
    OFF_HAND;
  }

  public static enum Action {
    INTERACT,
    ATTACK,
    INTERACT_AT;
  }
}
