package rawbot.connection.packets.in.login;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.ReadBuffer;

public class EncryptionRequestPacket implements PacketIn {

  private String hashedServerId;
  private PublicKey publicKey;
  private byte[] verifyToken;

  public void read(ReadBuffer buf) throws IOException {
    // hash Server id
    this.hashedServerId = buf.readString();

    // decode server public key
    EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(buf.readByteArray());
    KeyFactory keyfactory = null;
    try {
      keyfactory = KeyFactory.getInstance("RSA");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e.getMessage());
    }
    try {
      this.publicKey = keyfactory.generatePublic(encodedkeyspec);
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException(e.getMessage());
    }

    // verify token
    this.verifyToken = buf.readByteArray();
  }

  public int getId(EnumConnectionState state) {
    return 1;
  }

  public PublicKey getPublicKey() {
    return this.publicKey;
  }

  public byte[] getVerifyToken() {
    return this.verifyToken;
  }

  public String getServerId() {
    return this.hashedServerId;
  }
}
