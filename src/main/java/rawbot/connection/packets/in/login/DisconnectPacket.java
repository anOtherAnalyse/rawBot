package rawbot.connection.packets.in.login;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class DisconnectPacket implements PacketIn {

  private String message;

  public void read(ReadBuffer buf) throws IOException {
    this.message = buf.readString();
  }

  public int getId(EnumConnectionState state) {
    if(state == EnumConnectionState.PLAY) return 26;
    return 0;
  }

  public String getMessage() {
    return this.message;
  }
}
