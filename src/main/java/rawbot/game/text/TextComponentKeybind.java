package rawbot.game.text;

public class TextComponentKeybind extends TextComponentBase {
  private String keybind;

  public TextComponentKeybind(String keybind) {
    super();
    this.keybind = keybind;
  }

  public String toString() {
    return "[Keybind: " + this.keybind + "]";
  }
}
