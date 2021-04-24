package rawbot.game.nbt;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

/* NBT tags serialization helper */
public class CompressedStreamTools {

    public static void write(NBTBase tag, DataOutput output) throws IOException {
      output.writeByte(tag.getId());

      if (tag.getId() != 0) {
        output.writeUTF("");
        tag.write(output);
      }
    }

    public static NBTTagCompound read(DataInputStream input) throws IOException {
      return CompressedStreamTools.read(input, NBTSizeTracker.INFINITE);
    }

    public static NBTTagCompound read(DataInput input, NBTSizeTracker accounter) throws IOException {
      NBTBase nbtbase = CompressedStreamTools.read(input, 0, accounter);

      if (nbtbase instanceof NBTTagCompound) {
        return (NBTTagCompound) nbtbase;
      } else {
        throw new IOException("Received root tag was not a NBTTagCompound");
      }
    }

    private static NBTBase read(DataInput input, int depth, NBTSizeTracker accounter) throws IOException {
      byte b0 = input.readByte();

      if (b0 == 0) return new NBTTagEnd();

      input.readUTF();

      NBTBase nbtbase = NBTBase.create(b0);
      nbtbase.read(input, depth, accounter);

      return nbtbase;
    }
}
