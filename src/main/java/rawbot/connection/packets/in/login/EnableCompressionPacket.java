package rawbot.connection.packets.in.login;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class EnableCompressionPacket implements PacketIn {

  private int compressionThreshold;

  public void read(ReadBuffer buf) throws IOException {
    this.compressionThreshold = buf.readVarInt();
  }

  public int getId(EnumConnectionState state) {
    return 3;
  }

  public int getThreshold() {
    return this.compressionThreshold;
  }
}
