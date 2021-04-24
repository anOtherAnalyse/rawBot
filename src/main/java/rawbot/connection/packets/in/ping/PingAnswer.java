package rawbot.connection.packets.in.ping;

public class PingAnswer {

  private String host;
  private String version;
  private String motd;
  private int players_count;
  private int players_max;

  public PingAnswer(String host, String version, String motd, int players_count, int players_max) {
    this.host = host;
    this.version = version;
    this.motd = motd;
    this.players_count = players_count;
    this.players_max = players_max;
  }

  public String getHost() {
    return this.host;
  }

  public String getVersion() {
    return this.version;
  }

  public String getMOTD() {
    return this.motd;
  }

  public int getPlayersCount() {
    return this.players_count;
  }

  public int getMaxPlayers() {
    return this.players_max;
  }
}
