package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;
import rawbot.game.text.TextComponentBase;
import rawbot.game.text.TextComponentString;

public class ChatReceivePacket implements PacketIn {

  private TextComponentBase message;
  private Type type;

  public void read(ReadBuffer buf) throws IOException {
    try {
      this.message = buf.readTextComponent();
    } catch (java.io.IOException e) {
      this.message = new TextComponentString("IO Error: " + e.getMessage());
    }
    byte type = buf.readByte();
    if(type < 0 || type > 2) this.type = Type.UNKNOWN;
    else this.type = Type.values()[type];
  }

  public int getId(EnumConnectionState state) {
    return 15;
  }

  public TextComponentBase getMessage() {
    return this.message;
  }

  public Type getType() {
    return this.type;
  }

  public static enum Type {CHAT, SYSTEM, GAME_INFO, UNKNOWN};
}
