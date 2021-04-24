package rawbot.game.text;

import java.lang.StringBuilder;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/* Text style */

public class Style {

  public String insertion;
  public Boolean bold, italic, underlined, strikethrough, obfuscated;
  public String color;
  private Style parent;

  public Style() {}

  public void setParentStyle(Style style) {
    this.parent = style;
  }

  public boolean isEmpty() {
    return this.bold == null && this.italic == null && this.strikethrough == null && this.underlined == null && this.obfuscated == null && this.color == null && this.insertion == null;
  }

  /* Special prefix to do format in Unix terminal */
  public String getFormatHead() {
    if(this.isEmpty()) {
      if(this.parent != null) return this.parent.getFormatHead();
      else return "";
    }
    StringBuilder build = new StringBuilder("\033[");

    if(this.bold != null && this.bold) {
      build.append(";1");
    }

    if(this.underlined != null && this.underlined) {
      build.append(";4");
    }

    if(this.obfuscated != null && this.obfuscated) {
      build.append(";2");
    }

    if(this.italic != null && this.italic) {
      build.append(";3");
    }

    if(this.strikethrough != null && this.strikethrough) {
      build.append(";9");
    }

    if(this.color != null) {
      switch(this.color.toUpperCase()) {
        case "BLACK": build.append(";30"); break;
        case "DARK_BLUE": build.append(";38;5;17"); break;
        case "DARK_GREEN": build.append(";38;5;28"); break;
        case "DARK_AQUA": build.append(";38;5;25"); break;
        case "DARK_RED": build.append(";38;5;88"); break;
        case "DARK_PURPLE": build.append(";38;5;21"); break;
        case "GOLD": build.append(";38;5;222"); break;
        case "GRAY": build.append(";38;5;241"); break;
        case "DARK_GRAY": build.append(";38;5;236"); break;
        case "BLUE": build.append(";34"); break;
        case "GREEN": build.append(";32"); break;
        case "AQUA": build.append(";38;5;116"); break;
        case "RED": build.append(";31"); break;
        case "LIGHT_PURPLE": build.append(";38;5;92"); break;
        case "YELLOW": build.append(";38;5;226"); break;
        case "WHITE": build.append(";38;5;231"); break;
        case "OBFUSCATED": build.append(";2"); break;
        case "BOLD": build.append(";1"); break;
        case "UNDERLINE": build.append(";4"); break;
        case "ITALIC": build.append(";3"); break;
        case "RESET": build.append(";0"); break;
      }
    }
    build.append("m");
    return build.toString();
  }

  /* Unix reset format special code */
  public String getFormatTail() {
    return "\033[0m";
  }

  public static class Serializer implements JsonDeserializer<Style> {

    public Style deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
      if(element.isJsonObject()) {
        Style style = new Style();

        JsonObject jsonobject = element.getAsJsonObject();

        if (jsonobject == null) return null;

        if(jsonobject.has("bold")) {
          style.bold = jsonobject.get("bold").getAsBoolean();
        }

        if (jsonobject.has("italic")) {
          style.italic = jsonobject.get("italic").getAsBoolean();
        }

        if (jsonobject.has("underlined")) {
          style.underlined = jsonobject.get("underlined").getAsBoolean();
        }

        if (jsonobject.has("strikethrough")) {
          style.strikethrough = jsonobject.get("strikethrough").getAsBoolean();
        }

        if (jsonobject.has("obfuscated")) {
          style.obfuscated = jsonobject.get("obfuscated").getAsBoolean();
        }

        if (jsonobject.has("color")) {
          style.color = jsonobject.getAsJsonPrimitive("color").getAsString();
        }

        if (jsonobject.has("insertion")) {
          style.insertion = jsonobject.get("insertion").getAsString();
        }

        // clickEvent & hoverEvent not handled

        return style;
      } else return null;
    }
  }
}
