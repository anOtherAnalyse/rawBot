package scanbot;

// A bunch of requests, ended by an ACK request
public class RequestsWave {
  private int start, end, index; // end excluded in wave
  private int ack_number;
  private long time;

  public RequestsWave(int start, int end, int ack) {
    this.start = start;
    this.end = end;
    this.ack_number = ack;
    this.index = this.start;
  }

  public int nextIndex() {
    if(this.index >= this.end) return -1;
    return this.index++;
  }

  public void reset(int ack) {
    this.index = this.start;
    this.ack_number = ack;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public long getTime() {
    return this.time;
  }

  public int getACK() {
    return this.ack_number;
  }
}
