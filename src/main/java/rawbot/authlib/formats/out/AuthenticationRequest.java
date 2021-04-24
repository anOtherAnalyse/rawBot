package rawbot.authlib.formats.out;

import rawbot.authlib.commons.Agent;

public class AuthenticationRequest {
  private Agent agent;
  private String username;
  private String password;
  private String clientToken;
  private boolean requestUser;

  public AuthenticationRequest(String username, String password) {
    this.agent = new Agent("Minecraft", 1);
    this.username = username;
    this.clientToken = null;
    this.password = password;
    this.requestUser = true;
  }
}
