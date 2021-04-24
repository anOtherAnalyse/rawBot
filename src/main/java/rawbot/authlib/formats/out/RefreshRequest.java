package rawbot.authlib.formats.out;

import rawbot.authlib.commons.GameProfile;

public class RefreshRequest {
  private String clientToken;
  private String accessToken;
  private GameProfile selectedProfile;
  private boolean requestUser;

  public RefreshRequest(String clientToken, String accessToken) {
    this.clientToken = clientToken;
    this.accessToken = accessToken;
    this.selectedProfile = null;
    this.requestUser = true;
  }

}
