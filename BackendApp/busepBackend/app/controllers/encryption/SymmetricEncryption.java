package controllers.encryption;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Nemanja on 6/6/2016.
 */
public class SymmetricEncryption {

	public SymmetricEncryption(){

	}

	public SecretKey generateKey() throws NoSuchAlgorithmException {

		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		return keygen.generateKey();
	}

	public byte[] encrypt(String data, SecretKey secretKey) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
		Cipher rsaCipherEnc = Cipher.getInstance("AES/ECB/PKCS5Padding");
		//inicijalizacija za sifrovanje,
		rsaCipherEnc.init(Cipher.ENCRYPT_MODE, secretKey);

		//sifrovanje
		byte[] ciphertext = rsaCipherEnc.doFinal(data.getBytes());

		return ciphertext;
	}

	public String decrypt(byte[] cipheredData, SecretKey secretKey) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
		Cipher rsaCipherDec = Cipher.getInstance("AES/ECB/PKCS5Padding");
		//inicijalizacija za dekriptovanje
		rsaCipherDec.init(Cipher.DECRYPT_MODE, secretKey);

		//dekriptovanje
		byte[] receivedTxt = rsaCipherDec.doFinal(cipheredData);
		return  new String(receivedTxt);
	}
}
