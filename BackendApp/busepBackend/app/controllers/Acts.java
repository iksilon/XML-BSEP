package controllers;

import java.io.IOException;
import java.io.InputStream;
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

import models.User;
import play.libs.WS;
import play.libs.WS.WSRequest;
import play.mvc.results.BadRequest;
import play.mvc.results.Ok;
import play.mvc.results.RenderJson;
import play.mvc.results.Result;
import utils.KeystoreUtils;
import utils.MarkLogicUtils;
import utils.SecurityUtils;

public class Acts extends AppController {
	
	public static Result newAct() {
		return new Ok();
	}
	
	/**
	 * Takes over the specific document type submission and handles it.
	 * This includes checking is the user has a valid certificate, 
	 * signing the created document and pushing the document to XML database.
	 * 
	 * @param type - What kind of document is to be submitted.
	 * Takes one of the static values from {@link MarkLogicUtils}.
	 * @return {@link Result} back to the frontend.
	 */
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
			
			// Get user data.
			//TODO:? Check if it's really them
			String username = request.headers.get("username").value();
			User loggedUser = User.find("byUsername", username).first();
			String pass = loggedUser.password;
			
			// Load user data.
			KeyStore ks = KeystoreUtils.getKeyStore(username+".jks", pass.toCharArray());
			System.out.println(">> Loaded user: " + username);
			PrivateKey pk = (PrivateKey) ks.getKey(username, pass.toCharArray());
			Certificate cert = ks.getCertificate(username);
			
			// Check if certificate still valid.
			if(SecurityUtils.isCertificateRevoked(cert)) {
				return new BadRequest("Your certificate is revoked.");
			}
			
			Document signedDoc = SecurityUtils.signDocument(doc, pk, cert);
			
			System.out.println(">> Document signed.");
			
			// Inserting into database:
			MarkLogicUtils.insertDocument(signedDoc, type, username);
			
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

	/**
	 * Route used to submit propositions. Sends the request off for handling.
	 */
	public static void submitProposition() { handleSubmission(MarkLogicUtils.ACT_PROPOSAL);	}
	/**
	 * Route used to submit amendments. Sends the request off for handling.
	 */
	public static void submitAmendment() { handleSubmission(MarkLogicUtils.AMENDMENT); }
	/**
	 * Route used to submit final document versions. Sends the request off for handling.
	 */
	public static void submitFinal() { handleSubmission(MarkLogicUtils.ACT_FINAL); }
	
	
	public static Result submitArchive() {
		System.out.println("Archive submission requested, commencing");
		
		WSRequest req = WS.url("https://localhost:9090/xml/submit");
		req.get();
		
		//http://localhost:9090/xml/submit
		return new Ok();
	}
	@Deprecated
	public static void submitXML() {
		org.apache.xml.security.Init.init();
		handleSubmission(MarkLogicUtils.ACT_PROPOSAL);
	}
	
	public static Result inProcedure() {
		//TODO: Uzeti akte iz baze koji jos nisu usvojeni i vratiti u JSON/XML-tekst formatu
		
		return new RenderJson(""); //ili sta vec bude trebalo
	}

	@Deprecated
	public static Result latestDocuments(int count) {
		//TODO: Uzeti COUNT akata iz baze koji su usvojeni i vratiti listu u JSON formatu
		//Ovo zajebi.

		return new RenderJson(""); //ili sta vec bude trebalo
	}
}
