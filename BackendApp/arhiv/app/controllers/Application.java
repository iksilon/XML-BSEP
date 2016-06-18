package controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import play.mvc.Controller;
import play.mvc.results.BadRequest;
import play.mvc.results.RenderBinary;
import play.mvc.results.Result;
import utils.KeyStoreUtils;
import utils.MarkLogicUtils;
import utils.xmlEncryption.DecryptXML;

public class Application extends Controller {
	
    public static void submitToArchive() {
    	System.out.println("-------------Submission received, commencing parse-----------");
    	
    	// Extract the document.
		InputStream is = request.body;
		System.out.println(is);
		
		DOMParser dp = new DOMParser();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				Document doc = db.parse(is);
				System.out.println(">> Document parsed.");
				
				TransformerFactory tranFactory = TransformerFactory.newInstance();
			    Transformer aTransformer = tranFactory.newTransformer();
			    Source src = new DOMSource(doc);
			    javax.xml.transform.Result dest = new StreamResult(System.out);
			    aTransformer.transform(src, dest);
			
				DecryptXML decryptXMLutil = new DecryptXML();
				
				String filepath = System.getProperty("user.dir");
				// certificate.jks -> crypt.jks
				filepath = Paths.get(filepath, "conf", "certificate.jks").toString();
				
				// Ook!Ook! -> crypt | localhost -> crypt | Pogledaj i metodu getCertificat()
				KeyStore ks = KeyStoreUtils.loadKeyStore(filepath, "Ook!Ook!".toCharArray());
				PrivateKey pk = (PrivateKey) ks.getKey("localhost", "Ook!Ook!".toCharArray());
				
				System.out.println(pk);
				doc = decryptXMLutil.decrypt(doc, pk);
				//------------------------
				MarkLogicUtils.insertDocument(doc, MarkLogicUtils.ARCHIVE, "arhiv-robo");
				
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnrecoverableKeyException e) {
				e.printStackTrace();
			} catch (KeyStoreException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
			
			
    	
    	System.out.println("-------------Submission stored-----------");
    }

	public static Result getCertificat() {
		String certpath = System.getProperty("user.dir");
		// arhiv.pem -> crypt.pem
		certpath = Paths.get(certpath, "conf", "arhiv.pem").toString();
		
		FileInputStream fis;
		try {
			fis = new FileInputStream(certpath);
			return new RenderBinary(fis, "path");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new BadRequest("Certificate not available.");
		}
	}

}