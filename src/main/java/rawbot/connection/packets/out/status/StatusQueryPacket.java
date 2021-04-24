package rawbot.connection.packets.out.status;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;

public class StatusQueryPacket implements PacketOut {

  public void write(WriteBuffer buf) throws IOException {}

  public int getId(EnumConnectionState state) {
    return 0;
  }
}
