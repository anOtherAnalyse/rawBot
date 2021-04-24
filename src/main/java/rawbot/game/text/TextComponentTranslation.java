package rawbot.game.text;

// Not implemented yet
public class TextComponentTranslation extends TextComponentBase {

  private String key;
  //private Object[] formatArgs;

  public TextComponentTranslation(String key) { //, Object... args) {
    super();
    this.key = key;
    //this.formatArgs = args;
  }

  public String toString() {
    return "[Translate: " + this.key + "]";
  }
}
