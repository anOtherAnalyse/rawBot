package rawbot.game.text;

public class TextComponentSelector extends TextComponentBase {
  private String selector;

  public TextComponentSelector(String selector) {
    super();
    this.selector = selector;
  }

  public String toString() {
    return "[Selector: " + this.selector + "]";
  }
}
