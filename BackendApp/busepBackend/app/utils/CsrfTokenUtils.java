package utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonObject;

public class CsrfTokenUtils {

	private static SecureRandom srand;
	private static ConcurrentHashMap<String, String> userToken = new ConcurrentHashMap<String, String>();
	
	static {
		try {
			srand = SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			srand = new SecureRandom();
			System.err.println("Could not get strong instance of SecureRandom. Instantiating new one.");
			e.printStackTrace();
		}
	}
	
	public static String generateToken(String user) {
		byte[] bytes = new byte[512];
		srand.nextBytes(bytes);
		
		String token = GeneralUtils.getHexHash(bytes.toString());
		userToken.put(user, token);
		return token;
	}
	
	public static String generateJsonToken(String user) {
		byte[] bytes = new byte[512];
		srand.nextBytes(bytes);
		
		String token = GeneralUtils.getHexHash(bytes.toString());
		userToken.put(user, token);
		JsonObject jo = new JsonObject();
		jo.addProperty("token", token);
		return jo.toString();
	}
	
	public static boolean checkToken(String user, String token) {
		String stored = userToken.get(user);
		if(stored == null) {
			return false;
		}
		
		if(stored.equals(token)) {
			userToken.remove(user);
			return true;
		}
		
		return false;
	}
}
