package rawbot.connection.packets.out.login;

import java.io.IOException;
import java.security.PublicKey;
import javax.crypto.SecretKey;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.WriteBuffer;
import rawbot.connection.packets.out.PacketOut;
import rawbot.utils.CipherUtil;

public class EncryptionResponsePacket implements PacketOut {

  private byte[] secretKeyEncrypted;
  private byte[] verifyTokenEncrypted;

  public EncryptionResponsePacket(SecretKey secret, PublicKey key, byte[] verifyToken)
  {
      this.secretKeyEncrypted = CipherUtil.oneTimeEncryptData(key, secret.getEncoded());
      this.verifyTokenEncrypted = CipherUtil.oneTimeEncryptData(key, verifyToken);
  }

  public void write(WriteBuffer buf) throws IOException {
    buf.writeByteArray(this.secretKeyEncrypted, this.secretKeyEncrypted.length);
    buf.writeByteArray(this.verifyTokenEncrypted, this.verifyTokenEncrypted.length);
  }

  public int getId(EnumConnectionState state) {
    return 1;
  }
}
