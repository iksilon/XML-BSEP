package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;

import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import models.User;
import play.cache.Cache;
import play.libs.WS;
import play.libs.WS.WSRequest;
import play.mvc.Http.Header;
import play.mvc.results.BadRequest;
import play.mvc.results.Ok;
import play.mvc.results.RenderJson;
import play.mvc.results.RenderText;
import play.mvc.results.Result;
import utils.*;
import utils.xmlEncryption.EncryptXML;

import static org.bouncycastle.asn1.iana.IANAObjectIdentifiers.security;

public class Acts extends AppController {

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
			Header hUsername = request.headers.get("username");
			if(hUsername == null) {
				return new BadRequest("Invalid issuer user data");
			}
			String username = hUsername.value();
			User loggedUser = User.find("byUsername", username).first();
			Cache.set(loggedUser.username, loggedUser);			
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

			System.out.println("\n------------- PROPOSITION submission FINISHED ----------------");		
			
			String jwt = JWTUtils.generateJWT(loggedUser);
			String json = "{\"token\": \"" + jwt + "\"}";
			return new RenderText(json);

//			return new Ok();
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
		} catch (Exception e) {
			e.printStackTrace();
			return new BadRequest("");
		}
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
		
		String docURI = "timestamp-test.xml";
		Document doc = MarkLogicUtils.readDocument(docURI);		
		//TODO: Encrypt here
		EncryptXML encryptXMLutil = new EncryptXML();
		CertificateUtils certificateUtils = new CertificateUtils();


		SecretKey secretKey = encryptXMLutil.generateDataEncryptionKey();
		Certificate cert = certificateUtils.openDERfile("./app/keystores/arhiv.der");

		doc = encryptXMLutil.encrypt(doc, secretKey, cert, "Akt");   //TODO Akt (naziv taga koji enkriptujemo)
		//---------------------------------------------------------

		Document encrypted = doc;
		
		try {
			InputStream is = MarkLogicUtils.createInputStream(encrypted, true);
			
			WSRequest req = WS.url("https://localhost:9090/xml/submit");
			req = req.setHeader("Content-Type", "application/xml");
			req = req.body(is);
			req.post();
			
			//http://localhost:9090/xml/submit
			return new Ok();
		} catch (IOException e) {
			e.printStackTrace();
			return new BadRequest("Submission failed.");
		}
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

	public static Result latestDocuments(int count) {
		/*
		String workingDir = System.getProperty("user.dir");
		workingDir = Paths.get(workingDir, "query", "latest.xqy").toString();
		MarkLogicUtils.exequteXQuery(workingDir);
		// Do the thing.
		/*
		EvalResultIterator it = MarkLogicUtils.exequteXQuery(workingDir);
		if (it.hasNext()) {
			for (EvalResult result : it) {
				System.out.println("\n" + result.getString());
			}
		} else { 		
			System.out.println("your query returned an empty sequence.");
		}*/
		
		
		//TODO: Izlistati poslednje izmene.
		/*
		Ovo vraca URI-je od poslednja tri uneta u zadatu kolekciju.
		Videti XQueryInvokerExample9 klasu iz vezbe 4. XQuery sa XML vezbi
		
		for $doc in fn:collection("team27/proposals")[1 to 3]
		let $uri := xdmp:node-uri($doc)
		let $updated-date := xdmp:document-get-properties($uri, fn:QName("http://marklogic.com/cpf", "last-updated"))
		order by $updated-date/text()
		return $uri
		
		 */

		return new RenderJson(""); //ili sta vec bude trebalo
	}
}
