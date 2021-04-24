package rawbot.connection.packets.in.play;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class ChangeGameStatePacket implements PacketIn {

  private GameState state;
  private float value;

  public void read(ReadBuffer buf) throws IOException {
    this.state = GameState.getStateByValue(255 & ((int)buf.readByte()));
    this.value = buf.readFloat();
  }

  public int getId(EnumConnectionState state) {
    return 30;
  }

  public GameState getState() {
    return this.state;
  }

  public float getValue() {
    return this.value;
  }

  public static enum GameState {
    RAIN_START(1), RAIN_STOP(2), GAMEMODE_CHANGE(3), DEATH(4), GAME_SETTINGS_CHANGE(5),
    ARROW_HIT(6), RAIN_STENGTH(7), THUNDER_STENGTH(8), GUARDIAN_CURSE(10),
    UNKNOWN(420);

    private int state;

    private GameState(int state) {
      this.state = state;
    }

    public int getState() {
      return this.state;
    }

    public static GameState getStateByValue(int value) {
      for(GameState state : GameState.values()) {
        if(state.getState() == value) return state;
      }
      return GameState.UNKNOWN;
    }
  }
}
