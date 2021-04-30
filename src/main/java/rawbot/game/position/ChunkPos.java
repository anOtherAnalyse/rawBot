package rawbot.game.position;

public class ChunkPos {

  public int x, z;

  public ChunkPos(int x, int z) {
    this.x = x;
    this.z = z;
  }

  public int hashCode() {
    return (this.x & 0xffffffff) | (this.z << 16);
  }

  public boolean equals(Object obj) {
    if(obj == null || !(obj instanceof ChunkPos)) return false;
    return this.hashCode() == obj.hashCode();
  }
}
