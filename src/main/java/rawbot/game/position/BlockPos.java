package rawbot.game.position;

public class BlockPos extends Vec3 {

  public static BlockPos fromLong(long serialized) {
    return new BlockPos(Vec3.fromLong(serialized));
  }

  public static long toLong(BlockPos p) {
    return Vec3.toLong(p);
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
