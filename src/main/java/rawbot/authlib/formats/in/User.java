package rawbot.authlib.formats.in;

import rawbot.authlib.commons.PropertyMap;

public class User {
  private String id;
  private PropertyMap properties;

  public String getId() {
    return this.id;
  }

  public PropertyMap getProperties() {
      return this.properties;
    }
}
