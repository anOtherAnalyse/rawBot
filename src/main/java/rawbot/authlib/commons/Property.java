 package rawbot.authlib.commons;

 import java.security.InvalidKeyException;
 import java.security.NoSuchAlgorithmException;
 import java.security.PublicKey;
 import java.security.Signature;
 import java.security.SignatureException;
 import java.util.Base64;

 public class Property {
   private final String name;
   private final String value;
   private final String signature;

   public Property(String value, String name) {
     this(value, name, null);
   }

   public Property(String name, String value, String signature) {
     this.name = name;
     this.value = value;
     this.signature = signature;
   }

   public String getName() {
     return this.name;
   }

   public String getValue() {
     return this.value;
   }

   public String getSignature() {
     return this.signature;
   }

   public boolean hasSignature() {
     return (this.signature != null);
   }

   public boolean isSignatureValid(PublicKey publicKey) {
     try {
       Signature signature = Signature.getInstance("SHA1withRSA");
       signature.initVerify(publicKey);
       signature.update(this.value.getBytes());
       return signature.verify(Base64.getDecoder().decode(this.signature));
     } catch (NoSuchAlgorithmException e) {
       e.printStackTrace();
     } catch (InvalidKeyException e) {
       e.printStackTrace();
     } catch (SignatureException e) {
       e.printStackTrace();
     }
     return false;
   }
 }
