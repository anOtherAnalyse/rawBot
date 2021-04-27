package rawbot.game.text;

import java.lang.reflect.Type;
import java.io.StringReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.common.collect.Lists;

public abstract class TextComponentBase {

  private static final Pattern FORMAT_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");

  public static String getString(JsonElement json, String memberName) {
    if(json.isJsonPrimitive()) {
      return json.getAsString();
    } else {
      throw new JsonSyntaxException("Expected " + memberName + " to be a string");
    }
  }

  public static String getString(JsonObject json, String memberName) {
    if(json.has(memberName)) {
      return getString(json.get(memberName), memberName);
    } else {
      throw new JsonSyntaxException("Missing " + memberName + ", expected to find a string");
    }
  }

  protected List<TextComponentBase> siblings = Lists.<TextComponentBase>newArrayList();
  protected Style style;

  public TextComponentBase() {
    this.style = new Style();
  }

  public void appendSibling(TextComponentBase component) {
    this.siblings.add(component);
  }

  public List<TextComponentBase> getSiblings() {
    return this.siblings;
  }

  public void setStyle(Style style) {
    this.style = style;
    for(TextComponentBase component : this.siblings) {
      component.getStyle().setParentStyle(this.style);
    }
  }

  public Style getStyle() {
    return this.style;
  }

  public abstract String toString();

  /* Get formatted text to output into Unix terminal (uses special Unix color codes)*/
  public String getFormattedText() {
    StringBuilder stringbuilder = new StringBuilder();

    String s = this.toString();
    if(! s.isEmpty()) {
      stringbuilder.append(this.style.getFormatHead());
      stringbuilder.append(s);
      stringbuilder.append(this.style.getFormatTail());
    }

    for(TextComponentBase c : this.siblings) {
      s = c.getFormattedText();
      if(! s.isEmpty()) {
        stringbuilder.append(c.getStyle().getFormatHead());
        stringbuilder.append(s);
        stringbuilder.append(c.getStyle().getFormatTail());
      }
    }

    return stringbuilder.toString();
  }

  // Component text without format
  public String getUnformattedText() {
    StringBuilder stringbuilder = new StringBuilder();

    String s = this.toString();
    if(! s.isEmpty()) {
      stringbuilder.append(s);
    }

    for(TextComponentBase c : this.siblings) {
      s = c.getUnformattedText();
      if(! s.isEmpty()) {
        stringbuilder.append(s);
      }
    }

    return TextComponentBase.FORMAT_PATTERN.matcher(stringbuilder.toString()).replaceAll("");
  }

  public static class Serializer implements JsonDeserializer<TextComponentBase> {

    private static Gson GSON;

    public TextComponentBase deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {

        if (element.isJsonPrimitive()) return new TextComponentString(element.getAsString());

        if (! element.isJsonObject()) {
          if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();

            TextComponentBase textComponent = null;
            for (JsonElement jsonelement : array) {
              TextComponentBase current = this.deserialize(jsonelement, jsonelement.getClass(), context);
              if (textComponent == null) textComponent = current;
              else textComponent.appendSibling(current);
            }
            return textComponent;
          }

          throw new JsonParseException("Don't know how to turn " + element + " into a Component");
        }

        JsonObject jsonobject = element.getAsJsonObject();
        TextComponentBase itextcomponent;

        if (jsonobject.has("text")) {
            itextcomponent = new TextComponentString(jsonobject.get("text").getAsString());
        } else if(jsonobject.has("translate")) {
          String s = jsonobject.get("translate").getAsString();

          // ignore WITH element

          itextcomponent = new TextComponentTranslation(s);
        } else if(jsonobject.has("score")) {
          JsonObject jsonobject1 = jsonobject.getAsJsonObject("score");

          if(!jsonobject1.has("name") || !jsonobject1.has("objective")) {
            throw new JsonParseException("A score component needs a least a name and an objective");
          }

          itextcomponent = new TextComponentScore(TextComponentBase.getString(jsonobject1, "name"), TextComponentBase.getString(jsonobject1, "objective"));

          if(jsonobject1.has("value")) {
              ((TextComponentScore)itextcomponent).setValue(TextComponentBase.getString(jsonobject1, "value"));
          }
        } else if(jsonobject.has("selector")) {
            itextcomponent = new TextComponentSelector(TextComponentBase.getString(jsonobject, "selector"));
        } else {
          if(!jsonobject.has("keybind")) {
            throw new JsonParseException("Don't know how to turn " + element + " into a Component");
          }

          itextcomponent = new TextComponentKeybind(TextComponentBase.getString(jsonobject, "keybind"));
        }

        if (jsonobject.has("extra")) {
          JsonArray extra = jsonobject.getAsJsonArray("extra");

          if(extra.size() <= 0) {
            throw new JsonParseException("Unexpected empty array for extra");
          }

          for(int j = 0; j < extra.size(); ++j) {
            itextcomponent.appendSibling(this.deserialize(extra.get(j), type, context));
          }
        }

        itextcomponent.setStyle((Style)context.deserialize(element, Style.class));

        return itextcomponent;
    }

    public static TextComponentBase jsonToComponent(String json, boolean lenient) {
      try {
        JsonReader jsonreader = new JsonReader(new StringReader(json));
        jsonreader.setLenient(lenient);
        return (TextComponentBase) Serializer.GSON.getAdapter(TextComponentBase.class).read(jsonreader);
      } catch (IOException ioexception) {
        throw new JsonParseException(ioexception);
      }
    }

    static {
      GsonBuilder gsonbuilder = new GsonBuilder();
      gsonbuilder.registerTypeHierarchyAdapter(TextComponentBase.class, new TextComponentBase.Serializer());
      gsonbuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
      Serializer.GSON = gsonbuilder.create();
    }
  }
}
