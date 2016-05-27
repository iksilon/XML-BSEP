package controllers;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.List;

import models.User;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.results.BadRequest;
import play.mvc.results.Ok;
import play.mvc.results.Result;
import play.mvc.results.Unauthorized;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

public class Login extends Controller {
	
	public static Result logIn(String uname, String pwd) {
		List<User> user = User.find("byUsernameAndPassword", uname, pwd).fetch();
		if(user.isEmpty()) {
			return new BadRequest("Invalid login");
		}
		
		User loggedUser = user.get(0);
		// Proslediti uvek username kad je neka akcija koja zavisi od korisnika (npr predlaganje amandmana)
		//potrebno radi pronalazenja korisnika u Cache, jer je kljuc njegov username
		//takodje, korisnika ukloniti iz Cache kad se izloguje
		Cache.set(loggedUser.username, loggedUser);
		Cache.set(loggedUser.username + "DeoId", 1);
		Cache.set(loggedUser.username + "ClanId", 1);
		Cache.set("loggedUserTest", loggedUser);
		return new Ok();
	}
	
	public static Result logOut(String uname) {
		Cache.delete(uname);
		Cache.delete(uname + "DeoId");
		Cache.delete(uname + "ClanId");
		System.out.println(uname + " LOGGED OUT");
		return new Ok();
	}

	//helper methods
	private byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String algorithm = "PBKDF2WithHmacSHA1";	//koristimo vrstu SHA1 algoritma
		int derivedKeyLength = 160;	//duzina sha1 hasha u bitima
		int iterations = 32450;  //neka se sete ovog broja :)

		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

		return f.generateSecret(spec).getEncoded();
	}

	private byte[] generateSalt() throws NoSuchAlgorithmException {
		int saltLength = 8;
		String saltAlgorithm = "SHA1PRNG";
		SecureRandom random = SecureRandom.getInstance(saltAlgorithm);
		byte[] salt = new byte[saltLength];
		random.nextBytes(salt);

		return salt;
	}

	private String base64Encode(byte[] data) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);
	}

	private byte[] base64Decode(String base64Data) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(base64Data);
	}
}
