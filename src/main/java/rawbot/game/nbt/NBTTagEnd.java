package rawbot.game.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd extends NBTBase {

  public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
    sizeTracker.read(64L);
  }

  public void write(DataOutput output) throws IOException {}

  public byte getId() {
    return 0;
  }

  public String toString() {
    return "END";
  }

  public NBTTagEnd copy() {
    return new NBTTagEnd();
  }
}
