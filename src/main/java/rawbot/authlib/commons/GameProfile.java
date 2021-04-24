package rawbot.authlib.commons;

import java.util.UUID;
import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GameProfile {
  private final UUID id;
  private final String name;
  private final PropertyMap properties = new PropertyMap();
  private boolean legacy;

  public GameProfile(UUID id, String name) {
    if (id == null && name.equals("")) {
      throw new IllegalArgumentException("Name and ID cannot both be blank");
    }
    this.id = id;
    this.name = name;
  }

  public UUID getId() {
   return this.id;
  }

  public String getName() {
   return this.name;
  }

  public PropertyMap getProperties() {
    return this.properties;
  }

  public boolean isComplete() {
    return (this.id != null && !getName().equals(""));
  }


  public int hashCode() {
    int result = (this.id != null) ? this.id.hashCode() : 0;
    result = 31 * result + ((this.name != null) ? this.name.hashCode() : 0);
    return result;
  }

  public String toString() {
    return "id: " + this.id + ", name: " + this.name + ", properties: " + this.properties.toString() + ", legagy: " + this.legacy;
  }

  public boolean isLegacy() {
    return this.legacy;
  }

  public static class Serializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {

      public GameProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = (JsonObject)json;
        UUID id = object.has("id") ? (UUID)context.deserialize(object.get("id"), UUID.class) : null;
        String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
        return new GameProfile(id, name);
      }

      public JsonElement serialize(GameProfile src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        if (src.getId() != null) {
        result.add("id", context.serialize(src.getId()));
        }
        if (src.getName() != null) {
        result.addProperty("name", src.getName());
        }
        return (JsonElement)result;
      }
  }

 }
