package rawbot.game.text;

public class TextComponentScore extends TextComponentBase {
  private final String name;
  private final String objective;

  /** The value displayed instead of the real score (may be null) */
  private String value = "";

  public TextComponentScore(String name, String objective) {
    super();
    this.name = name;
    this.objective = objective;
  }

  public void setValue(String valueIn) {
    this.value = valueIn;
  }

  public String toString() {
    return "[Score:" + this.name +  ", " + this.objective + ", " + this.value + "]";
  }
}
