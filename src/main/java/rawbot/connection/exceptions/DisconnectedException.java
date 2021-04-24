package rawbot.connection.exceptions;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;

public class DisconnectedException extends IOException {

  public DisconnectedException(String msg) {
    super(msg);
  }

  public static class Serializer implements JsonDeserializer<DisconnectedException> {

    private static Gson GSON;

    public DisconnectedException deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {

      if (element.isJsonPrimitive()) {
        return new DisconnectedException(element.getAsString());
      }

      if(element.isJsonObject()) {
        JsonObject jsonobject = element.getAsJsonObject();
        if (jsonobject.has("Error")) {
          JsonObject error = jsonobject.get("Error").getAsJsonObject();
          if(error.has("text")) {
            return new DisconnectedException(error.get("text").getAsString());
          }
          return new DisconnectedException(error.toString());
        } else if(jsonobject.has("translate")) {
          return new DisconnectedException(jsonobject.get("translate").getAsString());
        }
      }
      return new DisconnectedException(element.toString());
    }

    public static DisconnectedException jsonToComponent(String json) {
      try {
        StringReader str_reader = new StringReader(json);
        JsonReader jsonreader = new JsonReader(str_reader);
        jsonreader.setLenient(true);

        return (DisconnectedException)GSON.getAdapter(DisconnectedException.class).read(jsonreader);
        
      } catch (IOException except) {
        return new DisconnectedException(json);
      } catch (JsonParseException except) {
        return new DisconnectedException(json);
      }
    }

    static {
      GsonBuilder gsonbuilder = new GsonBuilder();
      gsonbuilder.registerTypeHierarchyAdapter(DisconnectedException.class, new DisconnectedException.Serializer());
      GSON = gsonbuilder.create();
    }
  }
}
