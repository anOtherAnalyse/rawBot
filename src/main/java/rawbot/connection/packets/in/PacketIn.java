package rawbot.connection.packets.in;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.ReadBuffer;

public interface PacketIn {

  public void read(ReadBuffer buf) throws IOException;

  public int getId(EnumConnectionState state);

}
