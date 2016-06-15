package controllers;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import play.mvc.Controller;
import utils.MarkLogicUtils;

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

}