package controllers;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import play.mvc.Controller;
import play.mvc.results.Ok;
import play.mvc.results.Result;
import utils.Utils;
public class Acts extends Controller {
	
	public static Result newAct() {
		return new Ok();
	}
	
	/**
	 * Method for transforming given digest into a signature.
	 * The returned {@link SignedObject} contains both the original message and the signature.
	 * Right now is hardcoded to one private key.
	 * 
	 * @param data - {@link String} presumably representing the document hash.
	 * @return {@link SignedObject}
	 */
	private static SignedObject signTextDocument(String data) {
		try {
			// TODO: Signing: Hardcoded keystore and private key for now. 
			
			String ksName = "odbornik1.jks";
			String ksPass = "odbornik1";
			
			KeyStore keystore = Utils.getKeyStore(ksName, ksPass);
			
			PrivateKey pk = (PrivateKey) keystore.getKey(ksPass, ksPass.toCharArray());
			
			Signature sig = Signature.getInstance("SHA1withRSA");
			SignedObject so = new SignedObject(data, pk, sig);
			
			return so;
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			return null;
		} catch (KeyStoreException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return null;
		} catch (SignatureException e) {
			e.printStackTrace();
			return null;
		}
	}
}
