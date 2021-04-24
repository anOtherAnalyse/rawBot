package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class PlayerAbilitiesPacket implements PacketIn {

  private boolean invulnerable;
  private boolean isFlying;
  private boolean allowFlying;
  private boolean isCreativeMode;
  private float flySpeed;
  private float walkSpeed;

  public void read(ReadBuffer buf) throws IOException {
    byte b = buf.readByte();

    this.invulnerable = (b & 1) != 0;
    this.isFlying = (b & 2) != 0;
    this.allowFlying = (b & 4) != 0;
    this.isCreativeMode = (b & 8) != 0;

    this.flySpeed = buf.readFloat();
    this.walkSpeed = buf.readFloat();
  }

  public int getId(EnumConnectionState state) {
    return 44;
  }

  public boolean isInvulnerable() {
    return this.invulnerable;
  }

  public boolean isFlying() {
    return this.isFlying;
  }

  public boolean allowFlying() {
    return this.allowFlying;
  }

  public boolean isCreativeMode() {
    return this.isCreativeMode;
  }

  public float getFlySpeed() {
    return this.flySpeed;
  }

  public float getWalkSpeed() {
    return this.walkSpeed;
  }
}
