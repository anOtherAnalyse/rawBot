package scanbot;

import rawbot.game.position.BlockPos;
import rawbot.game.position.ChunkPos;

// Rectangle area to be scan
public class ScanTargetArea extends ScanTarget {

  public static final int PLAYER_RENDER_DISTANCE = 7; // 9b render distance

  private ChunkPos corner;

  private int width_x, width_z, render_radius;

  public ScanTargetArea(int block_center_x, int block_center_z, int block_radius) {
    this.render_radius = ScanTargetArea.PLAYER_RENDER_DISTANCE;

    int chunk_radius = (int) Math.ceil((double) block_radius / 16d) - (this.render_radius / 2);
    if(chunk_radius < 0) chunk_radius = 0;

    int effective_radius = (int) Math.ceil((double) chunk_radius / (double) this.render_radius);

    this.width_x = (effective_radius * 2) + 1;
    this.width_z = this.width_x;

    this.corner = new ChunkPos((block_center_x / 16) - (effective_radius * this.render_radius), (block_center_z / 16) - (effective_radius * this.render_radius));
    this.total = this.width_x * this.width_z;
  }

  public ScanTargetArea(int x, int z, int wx, int wz) {
    this.corner = new ChunkPos(x, z);
    this.width_x = wx;
    this.width_z = wz;
    this.render_radius = ScanTargetArea.PLAYER_RENDER_DISTANCE; // 9b render distance
    this.total = this.width_x * this.width_z;
  }

  public BlockPos getPositionForIndex(int index) {
    if(index < 0 || index >= this.total) return null;

    ChunkPos next = new ChunkPos(this.corner.x + ((index % this.width_x)) * this.render_radius, this.corner.z + ((index / this.width_x) * this.render_radius));
    return new BlockPos(next.x << 4, -16, next.z << 4);
  }

  public void setRenderRadius(int radius) {
    this.render_radius = radius;
  }
}
