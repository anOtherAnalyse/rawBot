package rawbot.connection.packets.out;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;

public interface PacketOut {

  public abstract void write(WriteBuffer buf) throws IOException;

  public abstract int getId(EnumConnectionState state);

}
