package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class ServerDifficultyPacket implements PacketIn {

  private Difficulty difficulty;

  public void read(ReadBuffer buf) throws IOException {
    this.difficulty = Difficulty.getById(buf.readByte());
  }

  public int getId(EnumConnectionState state) {
    return 13;
  }

  public Difficulty getDifficulty() {
    return this.difficulty;
  }

  public static enum Difficulty {
    PEACEFULL((byte) 0),
    EASY((byte) 1),
    NORMAL((byte) 2),
    HARD((byte) 3),
    UNKNOWN((byte) 42);

    private byte id;

    Difficulty(byte id) {
      this.id = id;
    }

    public byte getId() {
      return this.id;
    }

    public static Difficulty getById(byte id) {
      if(id < 0 || id > 3) return Difficulty.UNKNOWN;
      return Difficulty.values()[id];
    }
  }
}
