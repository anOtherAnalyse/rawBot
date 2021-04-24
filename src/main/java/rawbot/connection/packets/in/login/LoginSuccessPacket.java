package rawbot.connection.packets.in.login;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class LoginSuccessPacket implements PacketIn {

  private String UUID;
  private String username;

  public void read(ReadBuffer buf) throws IOException {
    this.UUID = buf.readString();
    this.username = buf.readString();
  }

  public int getId(EnumConnectionState state) {
    return 2;
  }

  public String getUsername() {
    return this.username;
  }

  public String getUUID() {
    return this.UUID;
  }

}
