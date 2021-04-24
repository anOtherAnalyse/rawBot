package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class MapPacket implements PacketIn {

  private int mapId;
  private byte mapScale;
  private boolean trackingPosition;
  private Decoration[] icons;
  private int minX;
  private int minZ;
  private int columns;
  private int rows;
  private byte[] mapDataBytes;

  public void read(ReadBuffer buf) throws IOException {
    this.mapId = buf.readVarInt();
    this.mapScale = buf.readByte();
    this.trackingPosition = buf.readBoolean();

    int length = buf.readVarInt();
    this.icons = new Decoration[length];
    for(int i = 0; i < length; i ++) {
      byte s = buf.readByte();
      byte x = buf.readByte();
      byte y = buf.readByte();
      this.icons[i] = new Decoration(((int)(s >> 4)) & 15, x, y, (byte)((int)s & 15));
    }

    this.columns = ((int)buf.readByte()) & 255;
    if (this.columns > 0) {
      this.rows = ((int)buf.readByte()) & 255;
      this.minX = ((int)buf.readByte()) & 255;
      this.minZ = ((int)buf.readByte()) & 255;
      this.mapDataBytes = buf.readByteArray();
    }
  }

  public int getId(EnumConnectionState state) {
    return 36;
  }

  public int getMapId() {
    return this.mapId;
  }

  public byte getScale() {
    return this.mapScale;
  }

  public boolean isTrackingPosition() {
    return this.trackingPosition;
  }

  public Decoration[] getIcons() {
    return this.icons;
  }

  public int getMinX() {
    return this.minX;
  }

  public int getMinZ() {
    return this.minZ;
  }

  public int getColumns() {
    return this.columns;
  }

  public int getRows() {
    return this.rows;
  }

  public byte[] getContent() {
    return this.mapDataBytes;
  }

  public static class Decoration {

    public Type type;
    public byte x;
    public byte y;
    public byte rotation;

    public Decoration(int type, byte x, byte y, byte rotation) {
      if(type < 0 || type > 9) this.type = Type.UNKNOWN;
      else this.type = Type.values()[type];
      this.x = x;
      this.y = y;
      this.rotation = rotation;
    }

    public static enum Type {
      PLAYER, FRAME, RED_MARKER, BLUE_MARKER,
      TARGET_X, TARGET_POINT, PLAYER_OFF_MAP,
      PLAYER_OFF_LIMITS, MANSION, MONUMENT, UNKNOWN;
    }
  }

}
