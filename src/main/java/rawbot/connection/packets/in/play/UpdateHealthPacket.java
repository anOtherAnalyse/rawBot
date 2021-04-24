package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class UpdateHealthPacket implements PacketIn {

  private float health;
  private int foodLevel;
  private float saturationLevel;

  public void read(ReadBuffer buf) throws IOException {
    this.health = buf.readFloat();
    this.foodLevel = buf.readVarInt();
    this.saturationLevel = buf.readFloat();
  }

  public int getId(EnumConnectionState state) {
    return 65;
  }

  public float getHealth() {
    return this.health;
  }

  public int getFood() {
    return this.foodLevel;
  }

  public float getSaturation() {
    return this.saturationLevel;
  }
}
