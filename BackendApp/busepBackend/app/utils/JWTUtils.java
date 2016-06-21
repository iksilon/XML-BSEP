package utils;

import java.util.concurrent.ConcurrentHashMap;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import models.User;

public class JWTUtils {

	private static ConcurrentHashMap<String, RsaJsonWebKey> userJwk = new ConcurrentHashMap<String, RsaJsonWebKey>();
	
	public static String generateJWT(String username) {
		try {
			User user = User.find("byUsername", username).first();
			if(user == null) {
			    System.out.println("Token generation fail 1");
				return null;
			}
			
			RsaJsonWebKey jwk = RsaJwkGenerator.generateJwk(2048);			
			jwk.setKeyId(user.id.toString());
			
			userJwk.put(user.username, jwk);

		    System.out.println("Token generation start");
			JwtClaims claims = new JwtClaims();
			claims.setIssuer("SGNS");
			claims.setAudience(user.username);
			claims.setExpirationTimeMinutesInTheFuture(10);
			claims.setGeneratedJwtId();
		    claims.setNotBeforeMinutesInThePast(2);
		    claims.setSubject(user.username + "_form");
		    claims.setClaim("name",user.name);
		    claims.setClaim("lastname",user.lastName);
		    claims.setIssuedAtToNow();
		    

		    
		    System.out.println("Token generation ok 1");
		    JsonWebSignature jws = new JsonWebSignature();
		    jws.setPayload(claims.toJson());
		    jws.setKey(jwk.getPrivateKey());
		    jws.setKeyIdHeaderValue(jwk.getKeyId());
		    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

		    System.out.println("Token generation ok 2");
		    String jwt = jws.getCompactSerialization();
		    
		    System.out.println("Token succesfuly generated");
		    System.out.println("Token expirty: " + claims.getExpirationTime());
		    System.out.println("Token issued at: " + claims.getIssuedAt());
		    
		    return jwt;
		} catch (JoseException e) {
			e.printStackTrace();
		} catch (MalformedClaimException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    System.out.println("Token generation failed");
		return null;
	}
	
	public static String getAudience(String jwt, User user) {
//		User user = User.find("byUsername", username).first();
		System.out.println("JWT validation start! User " + user.username);
		
		if(user == null) {
			System.out.println("JWT validation fail 1! User " + user.username);
			return null;
		}

		System.out.println("JWT validation ok 1! User " + user.username);
		JwtConsumer jwtConsumer = getConsumer(user);
		if(jwtConsumer == null) {
			System.out.println("JWT validation fail 2! User " + user.username);
			return null;
		}

		System.out.println("JWT validation ok 2! User " + user.username);
		try
	    {
			JwtClaims claims = jwtConsumer.processToClaims(jwt);
	        try {
	        	NumericDate now = NumericDate.now();
	        	System.out.println("NOW: " + now);
	        	System.out.println("TOKEN ISSUED AT: " + claims.getIssuedAt());
	        	System.out.println("ISSUED AT OK: " + !claims.getIssuedAt().isOnOrAfter(now));
	        	System.out.println("TOKEN EXPIRY: " + claims.getExpirationTime());
	        	System.out.println("EXPIRY OK: " + claims.getExpirationTime().isOnOrAfter(now));
	        	boolean asd = claims.getIssuedAt().isOnOrAfter(now) && !claims.getExpirationTime().isOnOrAfter(now);
	        	System.out.println("SHIT BOOL: " + asd);
	        	if(claims.getIssuedAt().isOnOrAfter(now) && !claims.getExpirationTime().isOnOrAfter(now)) {
					System.out.println("JWT validation fail 3! User " + user.username);
	        		return null;
	        	}

				System.out.println("JWT validation ok 3! User " + user.username);
				
				if(!(claims.getAudience().get(0).equals(user.username)
					&& claims.getIssuer().equals("SGNS")
					&& claims.getSubject().equals(user.username + "_form")
					&& claims.getClaimValue("name").equals(user.name)
					&& claims.getClaimValue("lastname").equals(user.lastName))) {
					System.out.println("JWT validation fail 4! User " + user.username);
					return null;
				}

				System.out.println("JWT validation succeeded! User " + user.username);
				return claims.getAudience().get(0);
			} catch (MalformedClaimException e) {
		        System.out.println("Invalid Claim! " + e);
			}
	    }
	    catch (InvalidJwtException e)
	    {
	        System.out.println("Invalid JWT! " + e);
	    }
		catch(Exception e) {
	        System.out.println("JWT validation error! " + e);
		}
		
		return "";
	}
	
	public static boolean checkJWT(String jwt, User user) {
//		User user = User.find("byUsername", username).first();
		if(user == null) {
			return false;
		}
		
		JwtConsumer jwtConsumer = getConsumer(user);
		if(jwtConsumer == null) {
			return false;
		}
		
		try
	    {
	        JwtClaims claims = jwtConsumer.processToClaims(jwt);
	        try {
	        	NumericDate now = NumericDate.now();
	        	if(claims.getIssuedAt().isOnOrAfter(now) && !claims.getExpirationTime().isOnOrAfter(now)) {
	        		return false;
	        	}
	        	
				if(!(claims.getAudience().get(0).equals(user.username)
					&& claims.getIssuer().equals("SGNS")
					&& claims.getSubject().equals(user.username + "_form")
					&& claims.getClaimValue("name").equals(user.name)
					&& claims.getClaimValue("lastname").equals(user.lastName))) {
					return false;
				}

				System.out.println("JWT validation succeeded! User " + user.username);
				return true;
			} catch (MalformedClaimException e) {
		        System.out.println("Invalid Claim! " + e);
			}
	    }
	    catch (InvalidJwtException e)
	    {
	        System.out.println("Invalid JWT! " + e);
	    }
		catch(Exception e) {
	        System.out.println("JWT validation error! " + e);
		}
		
		return false;
	}
	
	private static JwtConsumer getConsumer(User user) {
		RsaJsonWebKey jwk = userJwk.get(user.username);
		
		if(jwk == null) {
			return null;
		}
		
		JwtConsumer jwtConsumer = new JwtConsumerBuilder()
				.setRequireExpirationTime()
				.setMaxFutureValidityInMinutes(300)
				.setAllowedClockSkewInSeconds(30)
				.setRequireSubject()
				.setExpectedIssuer("SGNS")
				.setExpectedAudience(user.username)
				.setVerificationKey(jwk.getKey())
				.build();
		
		return jwtConsumer;
	}
}
