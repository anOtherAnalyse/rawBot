package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class KeepAliveRequestPacket implements PacketIn {

  private long id;

  public void read(ReadBuffer buf) throws IOException {
    this.id = buf.readLong();
  }

  public int getId(EnumConnectionState state) {
    return 31;
  }

  public long getNumber() {
    return this.id;
  }
}
