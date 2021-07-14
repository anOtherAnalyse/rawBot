package scanbot;

import rawbot.game.position.BlockPos;

/* Defines the scan's target */
public abstract class ScanTarget {

  protected int index, total;

  public ScanTarget() {
    this.index = 0;
  }

  public abstract BlockPos getPositionForIndex(int index);

  public RequestsWave getNextWave(int size, int ack_number) {
    if(this.index >= this.total) return null;

    int start = this.index;

    this.index += size;
    if(this.index > this.total) this.index = this.total;

    return new RequestsWave(start, this.index, ack_number);
  }

  public void reset() {
    this.index = 0;
  }

  public int getAdvancement() {
    return (int) (((double) this.index / (double) this.total) * 100);
  }

  public int bestCaseRemainingTime(int wave_size, int tick_rate) {
    int chunks_left = this.total - this.index;
    int packets_left = chunks_left + (int) Math.ceil((double) chunks_left / (double) wave_size);
    return (int) Math.ceil((double) packets_left / (double) (tick_rate * 20));
  }
}
