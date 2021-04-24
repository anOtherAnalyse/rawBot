package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class JoinGamePacket implements PacketIn {

  private int entityId;
  private Gamemode gameType;
  private boolean isHarcore;
  private Dimension dimension;
  private ServerDifficultyPacket.Difficulty difficulty;
  private int maxPlayers;
  private String worldType;
  private boolean reduceDebugInfo;

  public void read(ReadBuffer buf) throws IOException {
    this.entityId = buf.readInt();

    byte c = buf.readByte();
    this.gameType = Gamemode.getById((byte) (c & 7));
    this.isHarcore = ((c & 8) != 0);

    this.dimension = Dimension.getById(buf.readInt());
    this.difficulty = ServerDifficultyPacket.Difficulty.getById(buf.readByte());
    this.maxPlayers = ((int)buf.readByte()) & 255;
    this.worldType = buf.readString();
    if(this.worldType == null) this.worldType = "default";
    this.reduceDebugInfo = buf.readBoolean();
  }

  public int getId(EnumConnectionState state) {
    return 35;
  }

  public int getPlayerId() {
    return this.entityId;
  }

  public Gamemode getGameType() {
    return this.gameType;
  }

  public Dimension getDimension() {
    return this.dimension;
  }

  public ServerDifficultyPacket.Difficulty getDifficulty() {
    return this.difficulty;
  }

  public boolean isHarcore() {
    return this.isHarcore;
  }

  public boolean isReduceDebugInfo() {
    return this.reduceDebugInfo;
  }

  public int getMaxPlayers() {
    return this.maxPlayers;
  }

  public String getWorldType() {
    return this.worldType;
  }

  public static enum Gamemode {
    NOT_SET((byte) -1),
    SURVIVAL((byte) 0),
    CREATIVE((byte) 1),
    ADVENTURE((byte) 2),
    SPECTATOR((byte) 3),
    UNKNOWN((byte) 42);

    private byte type;

    Gamemode(byte type) {
      this.type = type;
    }

    public byte getType() {
      return this.type;
    }

    public static Gamemode getById(byte id) {
      if(id < -1 || id > 3) return Gamemode.UNKNOWN;
      return Gamemode.values()[id + 1];
    }
  }

  public static enum Dimension {
    NETHER((byte) -1),
    OVERWORLD((byte) 0),
    END((byte) 1),
    UNKNOWN((byte) 42);

    private byte id;

    Dimension(byte id) {
      this.id = id;
    }

    public int getId() {
      return this.id;
    }

    public static Dimension getById(int id) {
      if(id < -1 || id > 1) return Dimension.UNKNOWN;
      return Dimension.values()[id + 1];
    }
  }
}
