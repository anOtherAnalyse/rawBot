package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class TimeUpdatePacket implements PacketIn {

  private long totalWorldTime;
  private long worldTime;

  public void read(ReadBuffer buf) throws IOException {
    this.totalWorldTime = buf.readLong();
    this.worldTime = buf.readLong();
  }

  public int getId(EnumConnectionState state) {
    return 71;
  }

  public long getTotalTime() {
    return this.totalWorldTime;
  }

  public long getWorldTime() {
    return this.worldTime;
  }
}
