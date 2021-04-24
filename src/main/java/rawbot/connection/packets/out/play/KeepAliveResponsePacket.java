package rawbot.connection.packets.out.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;

public class KeepAliveResponsePacket implements PacketOut {

  private long id;

  public KeepAliveResponsePacket(long id) {
    this.id = id;
  }

  public void write(WriteBuffer buf) throws IOException {
    buf.writeLong(this.id);
  }

  public int getId(EnumConnectionState state) {
    return 11;
  }

}
