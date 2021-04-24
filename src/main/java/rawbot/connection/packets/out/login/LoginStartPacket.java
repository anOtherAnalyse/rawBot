package rawbot.connection.packets.out.login;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;

public class LoginStartPacket implements PacketOut {

  // Username max length: 64 bytes
  private String username;

  public LoginStartPacket(String username) {
    this.username = username;
  }

  public void write(WriteBuffer buf) throws IOException {
    buf.writeString(this.username);
  }

  public int getId(EnumConnectionState state) {
    return 0;
  }
}
