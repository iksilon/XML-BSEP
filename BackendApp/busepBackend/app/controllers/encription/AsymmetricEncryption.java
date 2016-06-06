package controllers.encription;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

/**
 * Created by Nemanja on 6/6/2016.
 */
public class AsymmetricEncryption {
	public AsymmetricEncryption() {
		//Postavljamo providera, jer treba za RSA Enkripciji/Dekripciju
		Security.addProvider(new BouncyCastleProvider());
	}

	public byte[] encrypt(String data, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		//kriptovanje poruke javnim kljucem
		Cipher rsaCipherEnc = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
		//inicijalizacija za kriptovanje
		rsaCipherEnc.init(Cipher.ENCRYPT_MODE, key);

		//kriptovanje
		return rsaCipherEnc.doFinal(data.getBytes());
	}

	public String decrypt(byte[] cipheredData, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher rsaCipherDec = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");

		//inicijalizacija za dekriptovanje
		rsaCipherDec.init(Cipher.DECRYPT_MODE, key);

		//dekriptovanje
		byte[] receivedTxt = rsaCipherDec.doFinal(cipheredData);

		return new String(receivedTxt);
	}
}
