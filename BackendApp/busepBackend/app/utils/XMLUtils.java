package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import static org.joox.JOOX.*;

public class XMLUtils {
	
	/**
	 * Opens a XML document from file.
	 * 
	 * @param file - {@link String} of the full path to the XML file
	 * @return {@link Document} DOM respresentation of the XML file 
	 */
	public static Document loadDocument(String file) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new File(file));

			return document;
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Saves a XML file. 
	 * 
	 * @param doc - {@link Document} DOM representation
	 * @param fileName - {@link String} filepath
	 */
	public static void saveDocument(Document doc, String fileName) {
		try {
			File outFile = new File(fileName);
			FileOutputStream f = new FileOutputStream(outFile);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(f);
			
			transformer.transform(source, result);

			f.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Transforms a given {@link Document} into XHTML in an {@link OutputStream}. 
	 * 
	 * @param doc - {@link Document} to transform
	 * @param out - {@link OutputStream} with the XHTML
	 */
	public static void transformHTML(Document doc, OutputStream out) {
		String xslFilepath = System.getProperty("user.dir");
		xslFilepath = Paths.get(xslFilepath, "xslt", "propisHTML.xsl").toString();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		// Add xpath to every element.
		NodeList nodeList = doc.getElementsByTagName("*");
	    for (int i = 0; i < nodeList.getLength(); i++) {
	        Node node = nodeList.item(i);
	        if (node.getNodeType() == Node.ELEMENT_NODE) {
	            Element element = (Element)node;
	            String xpath = $(element).xpath();
	            element.setAttribute("element_path", xpath);
	        }
	    }
		
		
		DOMSource source = new DOMSource(doc);
		
		File xsltFile = new File(xslFilepath);
		StreamSource transformSource = new StreamSource(xsltFile);
		Transformer xslTransformer;
		try {
			
			xslTransformer = transformerFactory.newTransformer(transformSource);
			StreamResult result = new StreamResult(out);
			xslTransformer.transform(source, result);
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
	}
}
