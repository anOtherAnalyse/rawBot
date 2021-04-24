package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class SetPassengersPacket implements PacketIn {

  private int entityId;
  private int[] passengerIds;

  public void read(ReadBuffer buf) throws IOException {
    this.entityId = buf.readVarInt();
    int length = buf.readVarInt();
    this.passengerIds = new int[length];
    for(int i = 0; i < length; i ++) {
      this.passengerIds[i] = buf.readVarInt();
    }
  }

  public int getId(EnumConnectionState state) {
    return 67;
  }

  private int getVehicle() {
    return this.entityId;
  }

  private int[] getPassengers() {
    return this.passengerIds;
  }
}
