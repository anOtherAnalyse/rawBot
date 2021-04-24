package rawbot.connection.packets.out.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;

public class ClientStatusPacket implements PacketOut {
    private State state;

    public ClientStatusPacket(State state)
    {
        this.state = state;
    }

    public void write(WriteBuffer buf) throws IOException {
      buf.writeVarInt(this.state.ordinal());
    }

    public int getId(EnumConnectionState state) {
      return 3;
    }

    public static enum State {
        PERFORM_RESPAWN,
        REQUEST_STATS;
    }
}
