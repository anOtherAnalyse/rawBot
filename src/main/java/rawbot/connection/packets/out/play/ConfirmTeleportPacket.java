package rawbot.connection.packets.out.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;

public class ConfirmTeleportPacket implements PacketOut {
    private int telportId;

    public ConfirmTeleportPacket(int teleportIdIn)
    {
        this.telportId = teleportIdIn;
    }

    public void write(WriteBuffer buf) throws IOException {
      buf.writeVarInt(this.telportId);
    }

    public int getId(EnumConnectionState state) {
      return 0;
    }

}
