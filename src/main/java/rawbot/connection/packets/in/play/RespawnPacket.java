package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class RespawnPacket implements PacketIn {

  private JoinGamePacket.Dimension dimension;
  private ServerDifficultyPacket.Difficulty difficulty;
  private JoinGamePacket.Gamemode gameType;
  private String worldType;

  public void read(ReadBuffer buf) throws IOException {
    this.dimension = JoinGamePacket.Dimension.getById(buf.readInt());
    this.difficulty = ServerDifficultyPacket.Difficulty.getById(buf.readByte());
    this.gameType = JoinGamePacket.Gamemode.getById(buf.readByte());
    this.worldType = buf.readString();
    if(this.worldType == null) this.worldType = "default";
  }

  public int getId(EnumConnectionState state) {
    return 53;
  }

  public JoinGamePacket.Dimension getDimension() {
    return this.dimension;
  }

  public ServerDifficultyPacket.Difficulty getDifficulty() {
    return this.difficulty;
  }

  public JoinGamePacket.Gamemode getGameType() {
    return this.gameType;
  }

  public String getWorldType() {
    return this.worldType;
  }
}
