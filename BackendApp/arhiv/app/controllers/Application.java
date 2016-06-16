package controllers;

import java.io.*;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import play.mvc.Controller;

import play.mvc.results.Ok;
import play.mvc.results.Result;
import play.test.FunctionalTest;
import utils.KeystoreUtils;
import utils.MarkLogicUtils;
import utils.xmlEncryption.DecryptXML;

public class Application extends Controller {

    public static void index() {
        render();
    }
    
    public static void submitToArchive() {
    	System.out.println("-------------Submission received, commencing parse-----------");
    	
    	// Extract the document.
		InputStream is = request.body;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = null;
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				doc = db.parse(is);
				System.out.println(">> Document parsed.");
				//TODO ovde dekripcija
				/*KeystoreUtils keystoreUtils = new KeystoreUtils();
				DecryptXML decryptXMLutil = new DecryptXML();

				PrivateKey pk = keystoreUtils.readPrivateKey("./conf/certificate.jks", "localhost", "Ook!Ook!");
				doc = decryptXMLutil.decrypt(doc, pk);*/
				//------------------------
				MarkLogicUtils.insertDocument(doc, MarkLogicUtils.ARCHIVE, "arhiv-robo");
				
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
    	
    	System.out.println("-------------Submission stored-----------");
    }

	/*public static Result getCertificat() {
		KeystoreUtils keystoreUtils = new KeystoreUtils();

		Certificate cert = keystoreUtils.readCertificate("./app/conf/certificate.jks", "localhost", "Ook!Ook!");


		FUCK THIS SHIT!     ...for now...


		response.out =

		return new Ok();
	}*/

}