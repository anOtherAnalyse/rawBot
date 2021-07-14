package rawbot.game.position;

public class Vec3 {
  public int x, y, z;

  public Vec3(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public boolean equals(Object i) {
    if(i == null || !(i instanceof Vec3)) return false;
    return this.x == ((Vec3)i).x && this.y == ((Vec3)i).y && this.z == ((Vec3)i).z;
  }
}
