package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class TeamPacket implements PacketIn {

  private String name, displayName, prefix, suffix, nameTagVisibility, collisionRule;
  private int action;
  private boolean allowFriendlyFire, seeFriendlyInvisibleEnabled;
  private byte color;
  private String[] players;

  public void read(ReadBuffer buf) throws IOException {
    this.name = buf.readString();
    this.action = ((int)(buf.readByte())) & 255;

    if(this.action == 0 || this.action == 2) {
      this.displayName = buf.readString();
      this.prefix = buf.readString();
      this.suffix = buf.readString();
      byte c = buf.readByte();
      this.allowFriendlyFire = (c & 1) == 1;
      this.seeFriendlyInvisibleEnabled = (c & 2) == 2;
      this.nameTagVisibility = buf.readString();
      this.collisionRule = buf.readString();
      this.color = buf.readByte();
    }

    if (this.action == 0 || this.action == 3 || this.action == 4) {
      int players_size = buf.readVarInt();
      this.players = new String[players_size];
      for(int i = 0; i < players_size; i ++) {
        this.players[i] = buf.readString();
      }
    }
  }

  public int getId(EnumConnectionState state) {
    return 68;
  }

  public String getPlayerName() {
    return this.name;
  }

  public String getPlayerDisplayName() {
    return this.displayName;
  }

  public String getPlayerPrefix() {
    return this.prefix;
  }

  public String getPlayerSuffix() {
    return this.suffix;
  }

  public String getTagVisibility() {
    return this.nameTagVisibility;
  }

  public String getCollisionRule() {
    return this.collisionRule;
  }

  public int getAction() {
    return this.action;
  }

  public boolean allowFriendlyFire() {
    return this.allowFriendlyFire;
  }

  public boolean seeFriendlyInvisibleEnabled() {
    return this.seeFriendlyInvisibleEnabled;
  }

  public byte getColor() {
    return this.color;
  }

  public String[] GetPlayers() {
    return this.players;
  }
}
