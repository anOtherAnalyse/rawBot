package rawbot.game.position;

public class Vec3 {

  public static Vec3 fromLong(long l) {
    int x = (int)(l >> 39);
    int y = (int)((l >> 25) & 0x3fff);
    int z = (int)(l & 0x1ffffff);
    return new Vec3(x, y, z);
  }

  public static long toLong(Vec3 p) {
    return (((long)p.x) << 39) | (((long)p.y) << 25) | ((long)p.z);
  }

  public int x, y, z;

  public Vec3(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
