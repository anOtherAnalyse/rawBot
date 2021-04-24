package rawbot.authlib.formats.out;

public class ValidateRequest {
  private String clientToken;
  private String accessToken;

  public ValidateRequest(String clientToken, String accessToken) {
    this.clientToken = clientToken;
    this.accessToken = accessToken;
  }

}
