package rawbot.connection.packets.in.play;

import java.io.IOException;
import java.util.UUID;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;
import rawbot.authlib.commons.GameProfile;
import rawbot.authlib.commons.Property;
import rawbot.authlib.commons.PropertyMap;
import rawbot.game.text.TextComponentBase;

public class PlayerListItemPacket implements PacketIn {

  private Action action;
  private AddPlayerData[] players;

  public void read(ReadBuffer buf) throws IOException {
    int action = buf.readVarInt();

    if(action < 0 || action > 4) throw new IOException("Unknown action for PlayerListItemPacket");

    this.action = Action.values()[action];

    int length = buf.readVarInt();
    this.players = new AddPlayerData[length];
    for(int i = 0; i < length; i ++) {
      GameProfile profile = null;
      int ping = 0, gametype = 0;
      TextComponentBase displayName = null;
      switch(this.action) {
        case ADD_PLAYER:
          {
            profile = new GameProfile(new UUID(buf.readLong(), buf.readLong()), buf.readString());
            int l = buf.readVarInt();
            for(int k = 0; k < l; k ++) {
              String key = buf.readString();
              String value = buf.readString();
              if(buf.readBoolean()) { // with signature
                profile.getProperties().put(key, new Property(key, value, buf.readString()));
              } else { // without signature
                profile.getProperties().put(key, new Property(key, value));
              }
            }
            gametype = buf.readVarInt();
            ping = buf.readVarInt();
            if (buf.readBoolean()) {
              try {
                displayName = buf.readTextComponent();
              } catch(java.io.IOException e) {}
            }
          }
          break;
        case UPDATE_GAME_MODE:
          profile = new GameProfile(new UUID(buf.readLong(), buf.readLong()), null);
          gametype = buf.readVarInt();
          break;
        case UPDATE_LATENCY:
          profile = new GameProfile(new UUID(buf.readLong(), buf.readLong()), null);
          ping = buf.readVarInt();
          break;
        case UPDATE_DISPLAY_NAME:
          profile = new GameProfile(new UUID(buf.readLong(), buf.readLong()), null);
          if (buf.readBoolean()) {
            try {
              displayName = buf.readTextComponent();
            } catch(java.io.IOException e) {}
          }
          break;
        case REMOVE_PLAYER:
          profile = new GameProfile(new UUID(buf.readLong(), buf.readLong()), null);
          break;
      }
      this.players[i] = new AddPlayerData(profile, ping, gametype, displayName);
    }
  }

  public int getId(EnumConnectionState state) {
    return 46;
  }

  public Action getAction() {
    return this.action;
  }

  public AddPlayerData[] getPlayers() {
    return this.players;
  }

  public static enum Action {
    ADD_PLAYER,
    UPDATE_GAME_MODE,
    UPDATE_LATENCY,
    UPDATE_DISPLAY_NAME,
    REMOVE_PLAYER;
  }

  public static class AddPlayerData {
    public int ping;
    public int gamemode;
    public GameProfile profile;
    public TextComponentBase displayName;

    public AddPlayerData(GameProfile profileIn, int latencyIn, int gameModeIn, TextComponentBase displayNameIn)
    {
        this.profile = profileIn;
        this.ping = latencyIn;
        this.gamemode = gameModeIn;
        this.displayName = displayNameIn;
    }
  }
}
