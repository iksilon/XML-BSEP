package utils.hashAndSaltUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import models.User;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Util koji služi kako bismo implementirali helper metode.
 *
 * Kod redovnih helper metoda bismo imali problem zbog toga što nisu thread safe. Ovako dobijamo i thread safety i
 * preglednost koda, which is nice.
 *
 * Created by Nemanja on 5/27/2016.
 */
public class HashAndSaltUtil {
	public HashAndSaltUtil(){
		//nema tu šta da se konstruiše
	}

	public byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String algorithm = "PBKDF2WithHmacSHA1";	//koristimo vrstu SHA1 algoritma
		int derivedKeyLength = 160;	//duzina sha1 hasha u bitima
		int iterations = 32450;  //neka se sete ovog broja :)

		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

		return f.generateSecret(spec).getEncoded();
	}

	public byte[] generateSalt() throws NoSuchAlgorithmException {
		int saltLength = 8;
		String saltAlgorithm = "SHA1PRNG";
		SecureRandom random = SecureRandom.getInstance(saltAlgorithm);
		byte[] salt = new byte[saltLength];
		random.nextBytes(salt);

		return salt;
	}

	public String base64Encode(byte[] data) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);
	}

	public byte[] base64Decode(String base64Data) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(base64Data);
	}

	public boolean authenticate(String inputPasswd, User user)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{   //nema potrebe da proveravamo username, jer smo ga vec nasli

		byte[] hashedAttemptedPassword = hashPassword(inputPasswd, base64Decode(user.salt));
		return Arrays.equals(base64Decode(user.password), hashedAttemptedPassword);

	}

}
