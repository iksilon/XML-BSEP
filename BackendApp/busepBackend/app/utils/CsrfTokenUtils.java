package utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonObject;

@Deprecated
public class CsrfTokenUtils {

	private static SecureRandom srand;
	private static ConcurrentHashMap<String, String> userToken = new ConcurrentHashMap<String, String>();
	private static ConcurrentHashMap<String, Long> userTokenTime = new ConcurrentHashMap<String, Long>();
	
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
		System.out.println("\tUSER FOR TOKEN GEN: " + user);
		
		String token = GeneralUtils.getHexHash(bytes.toString());
		userToken.put(user, token);
		userTokenTime.put(token, System.currentTimeMillis());
		
		return token;
	}
	
	public static String generateJsonToken(String user) {
		byte[] bytes = new byte[512];
		srand.nextBytes(bytes);
		System.out.println("\tUSER FOR TOKEN GEN: " + user);
		
		String token = GeneralUtils.getHexHash(bytes.toString());
		userToken.put(user, token);
		userTokenTime.put(token, System.currentTimeMillis());
		
		JsonObject jo = new JsonObject();
		jo.addProperty("token", token);
		return jo.toString();
	}
	
	public static boolean checkToken(String user, String token) {
		long timeNow = System.currentTimeMillis();
		String stored = userToken.get(user);
		System.out.println("\tUSER FOR TOKEN CHECK: " + user + " TOKEN: " + token);
		if(stored == null) {
			return false;
		}
		
		if(stored.equals(token)) {
			System.out.println("\tUSER " + user + " TOKEN PASS EQUALITY CHECK");
			long timeCreated = userTokenTime.get(token);
			long timeDelta = timeNow - timeCreated;
			
			long tenMinutesMilli = 10L * 60000L; // 10 * 60 * 1000
			if(timeDelta < tenMinutesMilli) {
				System.out.println("\t\t\t\tUSER " + user + " TOKEN PASS TIME CHECK");
				userToken.remove(user);
				userTokenTime.remove(token);
				return true;
			}
			
			System.out.println("\tUSER " + user + " TOKEN FAILED TIME CHECK");
		}

		System.out.println("\tUSER " + user + " TOKEN FAILED EQUALITY CHECK");
		return false;
	}
}
