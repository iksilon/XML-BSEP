package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import play.mvc.Controller;
import play.mvc.With;
import play.mvc.results.BadRequest;
import play.mvc.results.Ok;
import play.mvc.results.Result;
import utils.MarkLogicUtils;
import utils.SecurityUtils;
import utils.XMLUtils;

public class Acts extends AppController {
	
	public static Result newAct() {
		return new Ok();
	}
	
	private static Result handleSubmission(int type) {
		
		System.out.println("\n------------- Starting PROPOSITION submission ----------------");
		
		// Extract the document.
		InputStream is = request.body;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(is);
			System.out.println(">> Document parsed.");
			/*
			// TODO: XML will be sent from the editor.
			org.apache.xml.security.Init.init();
			
			System.out.println("Beginning signature demo:");
			String workingDir = System.getProperty("user.dir");
			workingDir = Paths.get(workingDir, "public").toString();
			
			System.out.println("> Files located at: " + workingDir);
			
			String sorcPath = Paths.get(workingDir, "placeholder1.xml").toString();
			
			Document xmlDoc = XMLUtils.loadDocument(sorcPath);
			
			System.out.println("> Loaded placehoder1.xml");
			*/
			
			// TODO: User will be extracted.
			String kp = "odbornik1";
			KeyStore ks = SecurityUtils.getKeyStore("odbornik1.jks", kp.toCharArray());
			System.out.println(">> Loaded default user odbornik1");
			
			PrivateKey pk = (PrivateKey) ks.getKey(kp, kp.toCharArray());
			Certificate cert = ks.getCertificate(kp);
			
			Document signedDoc = SecurityUtils.signDocument(doc, pk, cert);
			
			System.out.println(">> Document signed.");
			
			// Inserting into database:
			MarkLogicUtils.insertDocument(signedDoc, type);
			
			System.out.println(">> Document inserted into database.");
			
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			return new BadRequest("XML parser setup failure");
		} catch (SAXException e) {
			e.printStackTrace();
			return new BadRequest("Bad XML data");
		} catch (IOException e) {
			e.printStackTrace();
			return new BadRequest("Bad XML data");
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			return new BadRequest("");
		} catch (KeyStoreException e) {
			e.printStackTrace();
			return new BadRequest("");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new BadRequest("");
		}		
		
		System.out.println("\n------------- PROPOSITION submission FINISHED ----------------");
		return new Ok();
		
	}
	
	public static void submitProposition() {
		handleSubmission(MarkLogicUtils.ACT_PROPOSAL);
	}
	
	public static void submitAmendment() {
		handleSubmission(MarkLogicUtils.AMENDMENT);
	}
	
	public static void submitFinal() {
		handleSubmission(MarkLogicUtils.ACT_FINAL);
	}
	
	public static Result submitArchive() {
		return new BadRequest("Do not use this any more.");
	}
	
	public static void submitXML() {
		org.apache.xml.security.Init.init();
		handleSubmission(MarkLogicUtils.ACT_PROPOSAL);
	}
}
