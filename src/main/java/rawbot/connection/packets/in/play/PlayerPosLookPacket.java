package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class PlayerPosLookPacket implements PacketIn {

  private static final int FX = 1;
  private static final int FY = 2;
  private static final int FZ = 4;
  private static final int FYAW = 8;
  private static final int FPITCH = 16;

  private double x;
  private double y;
  private double z;
  private float yaw;
  private float pitch;
  private byte flags;
  private int teleportId;

  public void read(ReadBuffer buf) throws IOException {
    this.x = buf.readDouble();
    this.y = buf.readDouble();
    this.z = buf.readDouble();
    this.yaw = buf.readFloat();
    this.pitch = buf.readFloat();
    this.flags = buf.readByte();
    this.teleportId = buf.readVarInt();
  }

  public int getId(EnumConnectionState state) {
    return 47;
  }

  public double getPlayerX(double x) {
    return ((this.flags & PlayerPosLookPacket.FX) == 0) ? this.x : x + this.x;
  }

  public double getPlayerY(double y) {
    return ((this.flags & PlayerPosLookPacket.FY) == 0) ? this.y : y + this.y;
  }

  public double getPlayerZ(double Z) {
    return ((this.flags & PlayerPosLookPacket.FZ) == 0) ? this.z : z + this.z;
  }

  public float getPlayerPitch(float pitch) {
    return ((this.flags & PlayerPosLookPacket.FPITCH) == 0) ? this.pitch : pitch + this.pitch;
  }

  public float getPlayerYaw(float yaw) {
    return ((this.flags & PlayerPosLookPacket.FYAW) == 0) ? this.yaw : yaw + this.yaw;
  }

  public int getTeleportId() {
    return this.teleportId;
  }
}
