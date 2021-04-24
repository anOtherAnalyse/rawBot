package rawbot.authlib.formats.in;

import java.util.UUID;

import rawbot.authlib.commons.PropertyMap;

public class HasJoinedServerResponse extends Response {
  private UUID id;
  private PropertyMap properties;

  public UUID getId() {
   return this.id;
  }

  public PropertyMap getProperties() {
   return this.properties;
  }
}
