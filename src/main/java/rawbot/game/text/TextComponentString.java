package rawbot.game.text;

public class TextComponentString extends TextComponentBase {
  private String msg;

  public TextComponentString(String msg) {
    super();
    this.msg = msg;
  }

  public String toString() {
    return this.msg;
  }
}
