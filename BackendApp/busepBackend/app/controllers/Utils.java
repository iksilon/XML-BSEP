package controllers;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import controllers.encryption.AsymmetricEncryption;
import models.Document;
import models.User;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.results.Ok;
import play.mvc.results.RenderJson;
import play.mvc.results.Result;
import utils.SecurityUtils;

public class Utils extends AppController {
	
	private static int msgNum = 0;
	
	// VEROVATNO NECE TREBATI
	public static String responseTimestamp(String jsonResponse) {
		jsonResponse = jsonResponse.substring(0, jsonResponse.length() - 1);
		// https://docs.angularjs.org/api/ng/service/$http#json-vulnerability-protection
		StringBuilder sb = new StringBuilder(")]}',\n");
		sb.append(jsonResponse);
		long time = System.currentTimeMillis();//Long.parseLong(request.headers.get("timestamp").value());
		
		String ksPass = "odbornik1";
		KeyStore ks = SecurityUtils.getKeyStore("odbornik1.jks", ksPass.toCharArray());
		String encryTime;
		String encryMsgNum;
		try {
			Key key = ks.getKey(ksPass, ksPass.toCharArray());
			AsymmetricEncryption ae = new AsymmetricEncryption();
			encryTime = ae.encrypt(String.valueOf(time), key).toString();
			encryMsgNum = ae.encrypt(String.valueOf(msgNum++), key).toString();
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException 
				| InvalidKeyException | NoSuchPaddingException | NoSuchProviderException 
				| BadPaddingException | IllegalBlockSizeException e) 
		{
			e.printStackTrace();
			System.out.println("Failed to get private key for TIMESTAMP");
			return null;
		}
		
		sb.append(", \"timestamp\":\"");		
		sb.append(encryTime);
		sb.append("\", \"msgNum\":\"");
		sb.append(encryMsgNum);
		sb.append("\"}");

		System.out.println(sb);
		return sb.toString();
	}

	public static int checkTimestamp(String tsString, String tsHashString) {
		try { // check timestamp hash
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(tsString.getBytes(StandardCharsets.UTF_8));
		    String hexCharsHash = Utils.byteToHex(hashBytes);
//			System.out.println(hexCharsHash);

			byte[] asd = tsHashString.getBytes(StandardCharsets.UTF_8);			
//			System.out.println(new String(asd));
			
			String hexHash = new String(hexCharsHash).toUpperCase();
			String hexTsHash = new String(asd).toUpperCase();
			if(!hexHash.equals(hexTsHash)) {
				return 1;
			}
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
			return 2;
		}
		
		Long timestamp = Long.parseLong(tsString);
		Long serverTimestamp = System.currentTimeMillis();
		Long timestampDiff = serverTimestamp - timestamp;
		if(timestampDiff <= 0 || timestampDiff > 5000) { // 5 sekundi. mozda treba manje?
			return 3;
		}
		
		return 0;
	}

	public static String byteToHex(byte[] bytes) {
		char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars= new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    
	    return new String(hexChars);
	}
	
	// prosledi role 0 za predsednika, 1 za odbornika itd
	public static void usersByRole(long role) {
		List<User> users = User.find("byRole", role).fetch();
		if(users.isEmpty()) {
			users = User.findAll();
		}
		
		Cache.set("users", users);
	}

	public static void latestDocuments(int count) {
		List<Document> latestDocuments = Document.findAll();
		if(count <= 0) {
			Cache.set("documents", latestDocuments);
		}
		
		latestDocuments = new ArrayList<Document>(latestDocuments.subList(latestDocuments.size() - count, latestDocuments.size()));
		Cache.set("documents", latestDocuments);
	}
	
	public static Result getPanelIds(String uname) {
		int panelDeoId = (int) Cache.get(uname + "DeoId");
		int panelClanId = (int) Cache.get(uname + "ClanId");
		
		StringBuilder sb = new StringBuilder("{\"panelDeoId\":");
		sb.append(panelDeoId);
		sb.append(", \"panelClanId\":");
		sb.append(panelClanId);
		sb.append("}");
		
		return new RenderJson(sb.toString());
	}
	
	public static Result setDeoId(String uname) {
		String keyDeo = uname + "DeoId";
		int panelDeoId = (int) Cache.get(keyDeo);
		Cache.set(keyDeo, ++panelDeoId);
		return new Ok();
	}
	
	public static Result setClanId(String uname) {
		String keyClan = uname + "ClanId";
		int panelClanId = (int) Cache.get(keyClan);		
		Cache.set(keyClan, ++panelClanId);
		return new Ok();
	}
}
