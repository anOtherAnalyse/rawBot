package rawbot.connection.packets.in.login;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;
import rawbot.game.text.TextComponentBase;

public class DisconnectPacket implements PacketIn {

  private TextComponentBase reason;

  public void read(ReadBuffer buf) throws IOException {
    this.reason = TextComponentBase.Serializer.jsonToComponent(buf.readString(), true);
  }

  public int getId(EnumConnectionState state) {
    if(state == EnumConnectionState.PLAY) return 26;
    return 0;
  }

  public TextComponentBase getReason() {
    return this.reason;
  }
}
