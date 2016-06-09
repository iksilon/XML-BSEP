package controllers;

import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;

import org.w3c.dom.Document;

import play.mvc.Controller;
import play.mvc.With;
import play.mvc.results.Ok;
import play.mvc.results.Result;
import utils.MarkLogicUtils;
import utils.SecurityUtils;
import utils.XMLUtils;

public class Acts extends AppController {
	
	public static Result newAct() {
		return new Ok();
	}
	
	public static void submitProposition() {
		
	}
	
	public static void submitAmendment() {
		
	}
	
	public static void submitFinal() {
		
	}
	
	public static void submitArchive() {
		
	}
	
	public static void submitXML() {
		// TODO: XML will be sent from the editor.
		org.apache.xml.security.Init.init();
		
		System.out.println("Beginning signature demo:");
		String workingDir = System.getProperty("user.dir");
		workingDir = Paths.get(workingDir, "public").toString();
		
		System.out.println("> Files located at: " + workingDir);
		
		String sorcPath = Paths.get(workingDir, "placeholder1.xml").toString();
		
		Document xmlDoc = XMLUtils.loadDocument(sorcPath);
		
		System.out.println("> Loaded placehoder1.xml");
		
		// TODO: User will be extracted.
		String kp = "odbornik1";
		KeyStore ks = SecurityUtils.getKeyStore("odbornik1.jks", kp.toCharArray());
		
		System.out.println("> Loaded default user odbornik1");
		
		try {
			PrivateKey pk = (PrivateKey) ks.getKey(kp, kp.toCharArray());
			Certificate cert = ks.getCertificate(kp);
			
			Document signedDoc = SecurityUtils.signDocument(xmlDoc, pk, cert);
			
			System.out.println("> Document signed.");
			
			// Inserting into database:
			MarkLogicUtils.insertDocument(signedDoc, MarkLogicUtils.ACT_PROPOSAL);
			
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}
}
