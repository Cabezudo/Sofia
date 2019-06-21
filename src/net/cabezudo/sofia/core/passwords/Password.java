package net.cabezudo.sofia.core.passwords;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import net.cabezudo.sofia.core.configuration.Configuration;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.18
 */
public class Password {

  public static final int MIN_LENGTH = 4;
  public static final int MAX_LENGTH = 50;

  private final String plainPassword;
  private String base64Password;
  private final byte[] bytePassword;

  private enum Type {
    PLAIN, BASE_64
  }

  public static Password createFromBase64(String base64Password) {
    return new Password(base64Password, Type.BASE_64);
  }

  public static Password createFromPlain(String password) {
    return new Password(password, Type.PLAIN);
  }

  private Password(String password, Type type) {
    if (type == Type.PLAIN) {
      this.plainPassword = password;
      this.base64Password = DatatypeConverter.printBase64Binary(password.getBytes());
    } else {
      byte[] decodedValue = DatatypeConverter.parseBase64Binary(password);
      this.plainPassword = new String(decodedValue, StandardCharsets.UTF_8);
      this.base64Password = password;
    }

    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-512");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    md.update(plainPassword.getBytes(Configuration.getInstance().getEncoding()));

    bytePassword = md.digest();

  }

  public String toBase64() {
    return base64Password;
  }

  public byte[] getBytes() {
    return bytePassword;
  }

  @Override
  public String toString() {
    return plainPassword + " : " + this.toBase64();
  }

  public boolean isEmpty() {
    return plainPassword.isEmpty();
  }

  public int length() {
    return plainPassword.length();
  }
}
