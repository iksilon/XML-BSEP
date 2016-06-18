package controllers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;

import javax.crypto.SecretKey;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import models.User;
import play.cache.Cache;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Http.Header;
import play.mvc.results.BadRequest;
import play.mvc.results.NotFound;
import play.mvc.results.NotModified;
import play.mvc.results.Ok;
import play.mvc.results.RenderBinary;
import play.mvc.results.RenderJson;
import play.mvc.results.RenderText;
import play.mvc.results.Result;
import utils.Constants;
import utils.CsrfTokenUtils;
import utils.GeneralUtils;
import utils.KeystoreUtils;
import utils.MarkLogicUtils;
import utils.SecurityUtils;
import utils.XMLUtils;
import utils.xmlEncryption.EncryptXML;

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

		// Add schema for validation

		if (type < 2) {
			try {
				SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = null;
				if (type == 1) {
					schema = schemaFactory.newSchema(new File("./xml-schema/Amandman.xsd"));
				}
				if (type == 0) {
					schema = schemaFactory.newSchema(new File("./xml-schema/Propis.xsd"));
				}
				dbf.setSchema(schema);
				dbf.setNamespaceAware(true);
				//dbf.setXIncludeAware(true);
				//dbf.setValidating(true);
			} catch (SAXException e) {
				e.printStackTrace();
			}
		}

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			//System.out.println(">> Da li ce puci pre parsiranja?");
			db.setErrorHandler(new ErrorHandler() {
				@Override
				public void warning(SAXParseException exception) throws SAXException {
					System.out.println(">>warning");
				}

				@Override
				public void error(SAXParseException exception) throws SAXException {
					System.out.println(">>error");
					System.out.println(">>Validation failed!");
					exception.printStackTrace();
					throw new SAXException();
				}

				@Override
				public void fatalError(SAXParseException exception) throws SAXException {
					System.out.println(">>fatalError");
				}
			});

			doc = db.parse(is);
			System.out.println(">> Document parsed.");

			//TODO Proveriti da li je validan po šemi
			
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
			MarkLogicUtils.insertDocument(signedDoc, type, username, false);
			
			System.out.println(">> Document inserted into database.");

			System.out.println("\n------------- PROPOSITION submission FINISHED ----------------");		
			
			String user = session.get("user");
			String token = CsrfTokenUtils.generateToken(user);
			String json = "{\"token\": \"" + token + "\"}";
			return new RenderText(json);

//			return new Ok();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			return new BadRequest("XML parser setup failure");
		} catch (SAXException e) {
			e.printStackTrace();
			System.out.println("<< Invalid XML");
			return new BadRequest("Bad XML data or not valid");
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
	public static void submitProposition() {
		handleSubmission(MarkLogicUtils.ACT_PROPOSAL);
	}
	/**
	 * Route used to submit amendments. Sends the request off for handling.
	 */
	public static void submitAmendment() {
		handleSubmission(MarkLogicUtils.AMENDMENT);
	}
	/**
	 * Route used to submit final document versions. Sends the request off for handling.
	 */
	public static Result submitFinal(String body) {
		ArrayList<String> data = new Gson().fromJson(body, ArrayList.class);
		if(data == null || data.size() != 10) {
			return new BadRequest("Invalid payload data");
		}
		/*[$scope.votes['for'], $scope.votes.against, percentFor, percentAgainst,
			                           shaFor, shaAgainst, shaPercentFor, shaPercentAgainst,
			                           doc.uri, doc.uriHash]*/
		String votesFor = data.get(0);
		String votesAgainst = data.get(1);
		String percentFor = data.get(2);
		String percentAgainst = data.get(3);
		String shaFor = data.get(4);
		String shaAgainst = data.get(5);
		String shaPercentFor = data.get(6);
		String shaPercentAgainst = data.get(7);
		String uri = data.get(8);
		String uriHash = data.get(9);
		if(votesFor == null || votesFor.trim().equals("")
				|| votesAgainst == null || votesAgainst.trim().equals("")
				|| percentFor == null || percentFor.trim().equals("")
				|| percentAgainst == null || percentAgainst.trim().equals("")
				|| shaFor == null || shaFor.trim().equals("")
				|| shaAgainst == null || shaAgainst.trim().equals("")
				|| shaPercentFor == null || shaPercentFor.trim().equals("")
				|| shaPercentAgainst == null || shaPercentAgainst.trim().equals("")
				|| uri == null || uri.trim().equals("")
				|| uriHash == null || uriHash.trim().equals(""))
		{
			return new BadRequest("Invalid payload data");
		}

//		try {
//			MessageDigest digest = MessageDigest.getInstance("SHA-256");
//			byte[] uriHashBytes = digest.digest(uri.getBytes(StandardCharsets.UTF_8));
			String hexCharsUriHash = GeneralUtils.getHexHash(uri);
			
			if(hexCharsUriHash == null) {
				return new play.mvc.results.Error("Unable to validate uri");
			}
			
			if(!hexCharsUriHash.equals(uriHash)) {
				return new BadRequest("Invalid uri");
			}
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		handleSubmission(MarkLogicUtils.ACT_FINAL);
		
		String user = session.get("user");
		String token = CsrfTokenUtils.generateToken(user);
		String json = "{\"token\": \"" + token + "\"}";
		return new RenderText(json);
	}
	
	public static Result refuseDoc() {
		//refuse stuff
		
		String user = session.get("user");
		String token = CsrfTokenUtils.generateToken(user);
		String json = "{\"token\": \"" + token + "\"}";
		return new RenderText(json);
	}
	
	public static Result submitArchive() {
		System.out.println("Archive submission requested, commencing");
		
		Document doc = MarkLogicUtils.readDocument("poceo-je-aktotizam-to-je-nova-umetnost.xml");
		
		//TODO: Encrypt here
		EncryptXML encryptXMLutil = new EncryptXML();
		SecretKey secretKey = encryptXMLutil.generateDataEncryptionKey();
		
		WSRequest certreq = WS.url("https://localhost:9090/certificate/request");
		HttpResponse resp = certreq.get();
		
		InputStream cis = resp.getStream();
		BufferedInputStream bis = new BufferedInputStream(cis);			
		CertificateFactory cf;
		try {
			cf = CertificateFactory.getInstance("X.509");
			Certificate cert = cf.generateCertificate(bis);
			System.out.println(cert.toString());
			bis.close();
			doc = encryptXMLutil.encrypt(doc, secretKey, cert, "Propis");
			
		} catch (CertificateException e1) {
			e1.printStackTrace();
			return new play.mvc.results.Error("Bad certificate.");
		} catch (IOException e) {
			e.printStackTrace();
			return new play.mvc.results.Error("Bad certificate.");
		} 
		
		//---------------------------------------------------------
		
		Document encrypted = doc;
		
		try {			
			InputStream is = MarkLogicUtils.createInputStream(encrypted, true);
			System.out.println(encrypted);
			WSRequest req = WS.url("https://localhost:9090/xml/submit");
			req = req.setHeader("Content-Type", "application/xml");
			req = req.body(is);
			req.post();
			
			String user = session.get("user");
			String token = CsrfTokenUtils.generateToken(user);
			String json = "{\"token\": \"" + token + "\"}";
			return new RenderText(json);
		} catch (IOException e) {
			e.printStackTrace();
			return new BadRequest("Submission failed.");
		}
	}
	
	
	
	public static Result inProcedure() {
		
		ArrayList<JsonObject> uris = MarkLogicUtils.getAllProposalsFromDB();
		
		String user = session.get("user");
		String token = CsrfTokenUtils.generateToken(user);
		
		JsonObject jo = new JsonObject();
		jo.addProperty("uris", new Gson().toJson(uris));
		jo.addProperty("token", token);
		
		return new RenderJson(jo);
	}
	
	public static Result inAmendments() {
		
		ArrayList<JsonObject> uris = MarkLogicUtils.getAllAmendmentsFromDB();
		
		String user = session.get("user");
		String token = CsrfTokenUtils.generateToken(user);
		
		JsonObject jo = new JsonObject();
		jo.addProperty("uris", new Gson().toJson(uris));
		jo.addProperty("token", token);
		
		return new RenderJson(jo);
	}
	
	public static Result inFinals() {
		ArrayList<JsonObject> uris = MarkLogicUtils.getAllFinalsFromDB();
		
		String user = session.get("user");
		String token = CsrfTokenUtils.generateToken(user);
		
		JsonObject jo = new JsonObject();
		jo.addProperty("uris", new Gson().toJson(uris));
		jo.addProperty("token", token);
		
		return new RenderJson(jo);
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

		return new NotFound("Not implemented");
	}
	
	public static Result getActPdf(String body) {
		ArrayList<String> data = new Gson().fromJson(body, ArrayList.class);
		if(data == null || data.size() == 0) {
			return new BadRequest("No payload data");
		}
		
		String uri = data.get(0);
		String uriHash = data.get(1);
		if(uri == null || uri.trim().equals("")
				|| uriHash == null || uriHash.trim().equals(""))
		{
			return new BadRequest("Invalid payload data");
		}
		
		String uriHashCheck = GeneralUtils.getHexHash(uri);
		if(!uriHash.equals(uriHashCheck)) {
			return new BadRequest("Invalid uri");
		}
		
		// Do the thing.
		String name = uri.replace(".xml", ".pdf");
		File file = new File(name);
		
		
		
		return new RenderBinary(file, name);
	}
	
	public static Result getAct(String body) {
		ArrayList<String> data = new Gson().fromJson(body, ArrayList.class);
		if(data == null || data.size() == 0) {
			return new BadRequest("No payload data");
		}
		
		String uri = data.get(0);
		String uriHash = data.get(1);
		if(uri == null || uri.trim().equals("")
				|| uriHash == null || uriHash.trim().equals(""))
		{
			return new BadRequest("Invalid payload data");
		}
		
		String uriHashCheck = GeneralUtils.getHexHash(uri);
		if(!uriHash.equals(uriHashCheck)) {
			return new BadRequest("Invalid uri");
		}
		
		try {
			String fileName = Constants.FOLDER_PUBLIC + Constants.FOLDER_XSLT_HTMLS + uri.substring(0, uri.length() - 4) + Constants.FILE_HTML;
			File f = new File(fileName);
			
			if(!f.exists()) {
				Document d = MarkLogicUtils.readDocument(uri);
		
				FileOutputStream os;
				os = new FileOutputStream(f);
				XMLUtils.transformHTML(d, os);
				os.flush();
				os.close();
			}
			
			String user = session.get("user");
			if(user != null) {
				String token = CsrfTokenUtils.generateToken(user);
				JsonObject jo = new JsonObject();
				jo.addProperty("token", token);
				jo.addProperty("path", fileName.substring(Constants.FOLDER_PUBLIC.length()));
				return new RenderJson(jo);
			}

			JsonObject jo = new JsonObject();
			jo.addProperty("path", fileName.substring(Constants.FOLDER_PUBLIC.length()));
			return new RenderJson(jo);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new play.mvc.results.Error("Unable to get document in html");
	}
	
	public static Result cancelProcedure(String body) {
		ArrayList<String> data = new Gson().fromJson(body, ArrayList.class);
		if(data == null || data.size() == 0) {
			return new BadRequest("No payload data");
		}
		
		String uri = data.get(0);
		String uriHash = data.get(1);
		if(uri == null || uri.trim().equals("")
				|| uriHash == null || uriHash.trim().equals(""))
		{
			return new BadRequest("Invalid payload data");
		}
		
		String uriHashCheck = GeneralUtils.getHexHash(uri);
		if(!uriHash.equals(uriHashCheck)) {
			return new BadRequest("Invalid uri");
		}
		
		if(MarkLogicUtils.removeDocument(uri)) {
			String user = session.get("user");
			String token = CsrfTokenUtils.generateToken(user);
			String json = "{\"token\": \"" + token + "\"}";
			return new RenderText(json);
		}
		
		return new NotModified("Could not find document with given URI");
	}
}
