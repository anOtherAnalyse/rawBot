package rawbot.connection.packets.out.handshake;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;

public class HandshakePacket implements PacketOut {

  private int protocolVersion;
  private String ip;
  private int port;
  private int requestedState;

  public HandshakePacket(EnumConnectionState requestedState, String domain, int port) {
    this.requestedState = requestedState.getId();
    this.port = port;
    this.ip = domain;
    this.protocolVersion = 340; // 1.12.2
  }

  public void write(WriteBuffer buf) throws IOException {
    buf.writeVarInt(this.protocolVersion);
    buf.writeString(this.ip);
    buf.writeUnsignedShort(this.port);
    buf.writeVarInt(this.requestedState);
  }

  public int getId(EnumConnectionState state) {
    return 0;
  }
}
