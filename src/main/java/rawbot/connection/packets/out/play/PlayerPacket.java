package rawbot.connection.packets.out.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;

public class PlayerPacket implements PacketOut {

  protected double x;
  protected double y;
  protected double z;
  protected float yaw;
  protected float pitch;
  protected boolean onGround;

  public PlayerPacket(boolean onGround) {
    this.onGround = onGround;
  }

  public void write(WriteBuffer buf) throws IOException {
    buf.writeByte(this.onGround ? (byte)1 : (byte)0);
  }

  public int getId(EnumConnectionState state) {
    return 12;
  }

  public static class Position extends PlayerPacket {

    public Position(double x, double y, double z, boolean onGround) {
      super(onGround);
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public Position(double x, double y, double z) {
      this(x, y, z, true);
    }

    public int getWriteLength() {
      return 25;
    }

    public void write(WriteBuffer buf) throws IOException {
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      super.write(buf);
    }

    public int getId(EnumConnectionState state) {
      return 13;
    }
  }

  public static class PositionRotation extends PlayerPacket.Position {

    public PositionRotation(double xIn, double yIn, double zIn, float yawIn, float pitchIn, boolean onGroundIn) {
      super(xIn, yIn, zIn, onGroundIn);
      this.yaw = yawIn;
      this.pitch = pitchIn;
    }

    public int getWriteLength() {
      return 33;
    }

    public void write(WriteBuffer buf) throws IOException {
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.pitch);
      super.write(buf);
    }

    public int getId(EnumConnectionState state) {
      return 14;
    }
  }

  public static class Rotation extends PlayerPacket {

    public Rotation(float yawIn, float pitchIn) {
      this(yawIn, pitchIn, true);
    }

    public Rotation(float yawIn, float pitchIn, boolean onGroundIn) {
      super(onGroundIn);
      this.yaw = yawIn;
      this.pitch = pitchIn;
    }

    public int getWriteLength() {
      return 9;
    }

    public void write(WriteBuffer buf) throws IOException {
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.pitch);
      super.write(buf);
    }

    public int getId(EnumConnectionState state) {
      return 15;
    }
  }

}
