package rawbot.connection.packets.in.play;

import java.io.IOException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;
import rawbot.game.position.Vec3;

public class CustomSoundPacket implements PacketIn {

  private String soundName;
  private SoundCategory category;
  private Vec3 position;
  private float volume;
  private float pitch;

  public void read(ReadBuffer buf) throws IOException {
    this.soundName = buf.readString();

    int cat = buf.readVarInt();
    if(cat < 0 || cat > 9) this.category = SoundCategory.UNKNOWN;
    this.category = SoundCategory.values()[cat];

    this.position = new Vec3(buf.readInt(), buf.readInt(), buf.readInt());
    this.volume = buf.readFloat();
    this.pitch = buf.readFloat();
  }

  public int getId(EnumConnectionState state) {
    return 25;
  }

  public String getName() {
    return this.soundName;
  }

  public SoundCategory getCategory() {
    return this.category;
  }

  public Vec3 getPosition() {
    return this.position;
  }

  public float getVolume() {
    return this.volume;
  }

  public float getPitch() {
    return this.pitch;
  }

  public static enum SoundCategory {
    MASTER, MUSIC, RECORDS, WEATHER, BLOCKS,
    HOSTILE, NEUTRAL, PLAYERS, AMBIENT, VOICE, UNKNOWN
  }
}
