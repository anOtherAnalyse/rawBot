package rawbot.game.position;

public class BlockPos extends Vec3 {

  public static BlockPos fromLong(long l) {
    int x = (int) (l >> 38);
    int y = (int) ((l << 26) >> 52);
    int z = (int) ((l << 38) >> 38);
    return new BlockPos(x, y, z);
  }

  public static long toLong(BlockPos p) {
    return (((long) p.x) << 38) | (((long) p.y & 0xfff) << 26) | ((long) p.z & 0x3ffffff);
  }

  public BlockPos(int x, int y, int z) {
    super(x, y, z);
  }

  public BlockPos(Vec3 vec) {
    super(vec.x, vec.y, vec.z);
  }

  public BlockPos modulo(int i) {
    return new BlockPos(this.x % i, this.y % i, this.z % i);
  }
}
