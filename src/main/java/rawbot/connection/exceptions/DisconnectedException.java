package rawbot.connection.exceptions;

import java.io.IOException;

public class DisconnectedException extends IOException {

  public DisconnectedException(String msg) {
    super(msg);
  }
}
