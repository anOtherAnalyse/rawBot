package scanbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;

import rawbot.game.position.ChunkPos;
import rawbot.game.position.BlockPos;

// Defined positions to be scanned
public class ScanTargetDefined extends ScanTarget {

  private static final String TARGETS_FILE = "targets.txt";

  private List<ChunkPos> targets;

  public ScanTargetDefined() {
    this.targets = new ArrayList<ChunkPos>();

    Scanner reader = null;
    try {
      reader = new Scanner(new File(ScanTargetDefined.TARGETS_FILE));
    } catch (FileNotFoundException e) {
      throw new RuntimeException("No file named " + ScanTargetDefined.TARGETS_FILE + " defining targets");
    }

    Pattern pat = Pattern.compile("^.*\\((-?[0-9]+),\\s*(-?[0-9]+)\\).*$");
    while(reader.hasNextLine()) {
      String data = reader.nextLine();
      Matcher match = pat.matcher(data);
      if(match.matches()) {
        ChunkPos position = null;
        try {
          int x = Integer.parseInt(match.group(1));
          int z = Integer.parseInt(match.group(2));
          position = new ChunkPos(x >> 4, z >> 4);
        } catch(NumberFormatException e) {
            continue;
        }
        if((Math.abs(position.x) > 35 || Math.abs(position.z) > 35) && !(this.targets.contains(position))) { // Avoid spawn
          this.targets.add(position);
        }
      }
    }
    reader.close();

    if(this.targets.size() == 0) {
      throw new RuntimeException("No targets defined in file");
    }

    this.total = this.targets.size();
  }

  public BlockPos getPositionForIndex(int index) {
    if(index < 0 || index >= this.total) return null;
    ChunkPos chunk = this.targets.get(index);
    return new BlockPos(chunk.x << 4, -16, chunk.z << 4);
  }
}
