package rawbot.connection.packets.out.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;

public class ChatSendPacket implements PacketOut {

  private String message;

  public ChatSendPacket(String message) {
    this.message = message;
  }

  public void write(WriteBuffer buf) throws IOException {
    buf.writeString(this.message);
  }

  public int getId(EnumConnectionState state) {
    return 2;
  }

}
