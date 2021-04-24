package rawbot.connection;

public enum EnumConnectionState {
  HANDSHAKING(-1), PLAY(0), STATUS(1), LOGIN(2);

  private int id;

  private EnumConnectionState(int id) {
    this.id = id;
  }

  public int getId() {
    return this.id;
  }
}
