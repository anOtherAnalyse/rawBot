package rawbot.authlib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.StringBuilder;
import java.lang.RuntimeException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import rawbot.authlib.formats.in.*;
import rawbot.authlib.formats.out.*;
import rawbot.authlib.commons.UUIDTypeAdapter;
import rawbot.authlib.commons.PropertyMap;
import rawbot.authlib.commons.GameProfile;

public class Authenticator {

  private static String token_save_file = "token.txt"; // Where to save tokens for next use

  private Gson gson; // Json serializer

  private UUID id;
  private String username;
  private String authToken;
  private String clientToken;

  private boolean isAuth;

  public Authenticator() {
    GsonBuilder builder = new GsonBuilder();

    builder.registerTypeAdapter(UUID.class, new UUIDTypeAdapter());
    builder.registerTypeAdapter(GameProfile.class, new GameProfile.Serializer());
    builder.registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer());

    this.gson = builder.create();
    this.isAuth = false;
    this.authToken = null;
    this.clientToken = null;
    this.id = null;
    this.username = null;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public boolean isAuthenticated() {
    return this.isAuth;
  }

  /* Join a server */
  public void joinServer(String serverId) throws IOException, AuthenticationException {

    if(! this.isAuth) return;

    JoinServerRequest request = new JoinServerRequest();
    request.accessToken = this.authToken;
    request.selectedProfile = this.id;
    request.serverId = serverId;

    URL joinURL = null;
    try {
      joinURL = new URL("https://sessionserver.mojang.com/session/minecraft/join");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e.getMessage());
    }

    this.<Response>makeRequest(joinURL, request, Response.class);
  }

  /* Has joined server */
  public boolean hashJoinedServer(String serverId) throws IOException, AuthenticationException {

    if(! this.isAuth) return false;

    String url = null;
    try {
      url = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + URLEncoder.encode(this.username, "UTF-8")
        + "&serverId=" + URLEncoder.encode(serverId, "UTF-8");
    } catch (java.io.UnsupportedEncodingException e) {
      throw new RuntimeException(e.getMessage());
    }

    HasJoinedServerResponse response;
    try {
      response = this.<HasJoinedServerResponse>makeRequest(new URL(url), null, HasJoinedServerResponse.class);
    } catch (MalformedURLException e) {
      throw new RuntimeException("Bad url " + e.getMessage());
    }

    if(response == null) return false;

    return true;
  }

  /* Authenticate to mojang, etheir with saved tokens or with login & password */
  public void authenticate() throws IOException, AuthenticationException {

    // Verify that we are connected to internet / can reach the DNS server with a 1.5 sec timeout
    if(! DNSResolver.resolverUp("authserver.mojang.com")) {
      throw new IOException("Cannot resolve domain name \"authserver.mojang.com\"");
    }

    boolean tryToken = true; // Use previously saved auth tokens
    try {
      this.loadToken();
    } catch (IOException e) {
      tryToken = false;
    }

    if(this.authToken == null || this.clientToken == null) tryToken = false;

    // auth with token
    if(tryToken) {
      boolean success = false;
      try {
        this.authenticateWithToken();
        success = true;
      } catch (AuthenticationException e) {}

      if(success) {
        this.saveToken();
        return;
      }
    }

    // auth with password - get it from stdin for now
    Scanner input = new Scanner(System.in);

    System.out.print("Account name: ");
    System.out.flush();
    String username = input.nextLine();

    System.out.print("Password: ");
    System.out.flush();
    String password = input.nextLine();

    this.authenticateWithPassword(username, password);

    this.saveToken();
  }

  /* Authenticate and get account parameters & auth token */
  public void authenticateWithPassword(String username, String password) throws IOException, AuthenticationException {
    AuthenticationRequest request = new AuthenticationRequest(username, password);

    URL authURL = null;
    try {
      authURL = new URL("https://authserver.mojang.com/authenticate");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e.getMessage());
    }

    AuthenticationResponse response = this.<AuthenticationResponse>makeRequest(authURL, request, AuthenticationResponse.class);

    if(response == null) {
      throw new RuntimeException("Received null answer from gson deserializer");
    }

    this.authToken = response.getAccessToken();
    this.clientToken = response.getClientToken();
    this.username = response.getSelectedProfile().getName();
    this.id = response.getSelectedProfile().getId();

    this.isAuth = true;
  }

  /* Authenticate with current tokens */
  public void authenticateWithToken() throws IOException, AuthenticationException {

    if(this.clientToken == null || this.authToken == null)
      throw new AuthenticationException("No auth/client token loaded");

    if(this.checkTokenValidity()) {
      this.isAuth = true;
      return;
    }

    /* Refresh our tokens */

    URL refreshURL = null;
    try {
      refreshURL = new URL("https://authserver.mojang.com/refresh");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e.getMessage());
    }

    RefreshRequest request = new RefreshRequest(this.clientToken, this.authToken);
    AuthenticationResponse response = this.<AuthenticationResponse>makeRequest(refreshURL, request, AuthenticationResponse.class);

    if (!response.getClientToken().equals(this.clientToken)) {
      throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
    }

    this.authToken = response.getAccessToken();
    this.clientToken = response.getClientToken();
    this.username = response.getSelectedProfile().getName();
    this.id = response.getSelectedProfile().getId();

    this.isAuth = true;
  }

  /* Our tokens are still valid */
  public boolean checkTokenValidity() throws IOException {

    if(this.clientToken == null || this.authToken == null) return false;

    ValidateRequest request = new ValidateRequest(this.clientToken, this.authToken);

    URL validateURL = null;
    try {
      validateURL = new URL("https://authserver.mojang.com/validate");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e.getMessage());
    }

    try {
      this.<Response>makeRequest(validateURL, request, Response.class);
      return true;
    } catch (AuthenticationException e) {
      return false;
    }
  }

  // save tokens to file
  public void saveToken() throws IOException {
    FileWriter file = new FileWriter(Authenticator.token_save_file);
    file.write("clientToken = " + this.clientToken + "\n");
    file.write("accessToken = " + this.authToken + "\n");
    file.write("username = " + this.username + "\n");
    file.write("uuid = " + this.id.toString() + "\n");
    file.close();
  }

  // load tokens from file
  public void loadToken() throws IOException {
    Scanner reader = null;
    try {
      reader = new Scanner(new File(Authenticator.token_save_file));
    } catch (java.io.FileNotFoundException e) {
      return;
    }
    Pattern pat = Pattern.compile("^([^=]*\\S)\\s*=\\s*(.+)$");
    while (reader.hasNextLine()) {
        String data = reader.nextLine();
        Matcher match = pat.matcher(data);
        if(match.matches()) {
          switch(match.group(1)) {
            case "clientToken":
              this.clientToken = match.group(2); break;
            case "accessToken":
              this.authToken = match.group(2); break;
            case "username":
              this.username = match.group(2); break;
            case "uuid":
              this.id = UUID.fromString(match.group(2)); break;
            default:
              throw new IOException("Invalid format for " + Authenticator.token_save_file);
          }
        }
    }
    reader.close();
    if(this.clientToken == null || this.authToken == null || this.username == null || this.id == null)
      throw new IOException("No tokens found in " + Authenticator.token_save_file);
  }

  /* Create a json post request from object input and receive json answer of class output_class */
  private <T extends Response> T makeRequest(URL url, Object input, Class<T> output_class) throws IOException, AuthenticationException {
    String jsonResult = null;

    if(input == null) jsonResult = this.getRequest(url);
    else jsonResult = this.postRequest(url, this.gson.toJson(input), "application/json");

    Response response = null;
    try {
      response = (Response)this.gson.fromJson(jsonResult, output_class);
    } catch (JsonSyntaxException e) {
      throw new IOException("Malformated json answer from " + url.toString() + ": " + e.getMessage());
    }

    if(response != null && response.getError() != null) {
      throw new AuthenticationException(response.getError());
    }

    return (T)response;
  }

  /* Send http get request; no keepalive */
  private String getRequest(URL url) throws IOException, AuthenticationException {

    // Create new connection
    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
    connection.setConnectTimeout(5000);
    connection.setReadTimeout(5000);
    connection.setUseCaches(false);

    // Read answer
    InputStream input = null;
    try {
      input = connection.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
      StringBuilder build = new StringBuilder();
      String s;
      while((s = reader.readLine()) != null) {
        build.append(s);
      }

      return build.toString();
    } catch (IOException e) {
      if(connection.getResponseCode() == 403) {
        throw new AuthenticationException("Forbidden access");
      }

      input = connection.getErrorStream();
      if(input == null) throw e;
      BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
      StringBuilder build = new StringBuilder();
      String s;
      while((s = reader.readLine()) != null) {
        build.append(s);
      }

      throw new IOException("Request error: " + build.toString());
    }
  }

  /* Send http post request, no keepalive */
  private String postRequest(URL url, String content, String contentType) throws IOException, AuthenticationException {

    // Create new connection
    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
    connection.setConnectTimeout(5000);
    connection.setReadTimeout(5000);
    connection.setUseCaches(false);

    byte[] postAsBytes = content.getBytes(StandardCharsets.UTF_8);

    // Set http request headers
    connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");
    connection.setRequestProperty("Content-Length", "" + postAsBytes.length);
    connection.setDoOutput(true);

    // Write Request
    OutputStream output = connection.getOutputStream();
    output.write(postAsBytes);
    output.close();

    // Read answer
    InputStream input = null;
    try {
      input = connection.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
      StringBuilder build = new StringBuilder();
      String s;
      while((s = reader.readLine()) != null) {
        build.append(s);
      }

      return build.toString();
    } catch (IOException e) {

      if(connection.getResponseCode() == 403) {
        throw new AuthenticationException("Forbidden access");
      }

      input = connection.getErrorStream();
      if(input == null) throw e;
      BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
      StringBuilder build = new StringBuilder();
      String s;
      while((s = reader.readLine()) != null) {
        build.append(s);
      }

      throw new IOException("Request error: " + build.toString());
    }
  }
}
