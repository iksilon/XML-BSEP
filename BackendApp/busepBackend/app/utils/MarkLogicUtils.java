package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.util.EditableNamespaceContext;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * Utilities for using the MarkLogic XML database.
 * 
 * @author Isidora
 *
 */
public class MarkLogicUtils {
	
	public static final int ACT_PROPOSAL = 0;
	public static final int AMENDMENT = 1;
	public static final int ACT_FINAL = 2;
	public static final int ARCHIVE = 3;
	
	private static final String COLL_PROPOSAL = "tim27/proposals";
	private static final String COLL_AMENDMENT = "tim27/amendments";
	private static final String COLL_FINAL = "tim27/finals";
	private static final String COLL_ARCHIVE = "tim27/archive";
	
	//---------------------------------------------------------------------------------------------------
	// XQuery handling
	//---------------------------------------------------------------------------------------------------
	
	public static void exequteXQuery(String path) {
		
		
		ConnectionProperties props;
		try {
			// Read the file contents into a string object
			String query = readFile(path, StandardCharsets.UTF_8);
			
			props = loadProperties();
			DatabaseClient client = DatabaseClientFactory.newClient(props.host, props.port, props.user, props.password, props.authType);
			
			QueryManager queryMgr = client.newQueryManager();
			StringQueryDefinition qd = queryMgr.newStringDefinition();
			
			qd.setCriteria(query);
			SearchHandle results = queryMgr.search(qd, new SearchHandle());
			MatchDocumentSummary[] summaries = results.getMatchResults();
			for (MatchDocumentSummary summary : summaries ) {
				System.out.println("-");
			    System.out.println(summary.getUri());
			}
			
			
			/*
			// Initialize XQuery invoker object
			ServerEvaluationCall invoker = client.newServerEval();
			
			// Read the file contents into a string object
			String query = readFile(path, StandardCharsets.UTF_8);
			
			// Invoke the query
			invoker.xquery(query);
			
			// Interpret the results
			EvalResultIterator response = invoker.eval();
			
			return response;
			*/
		} catch (IOException e) {
			e.printStackTrace();
			//return null;
		}
		
	}
	
	/**
	 * Convenience method for reading file contents into a string.
	 */
	private static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	
	//---------------------------------------------------------------------------------------------------
	// Document handling
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * Inserts a given XML {@link Document} into the XML database.
	 * Database is specified in the {@code connection.properties} file.
	 * 
	 * @param doc - {@link Document} object representing the XML document
	 * @param collection - one of four possible collections specified in the static fields
	 */
	public static void insertDocument(Document doc, int collection, String user) {
		
		try {
			System.out.println("Beginning database insert:");
			
			// Collection
			String collectionID = "";
			
			switch (collection) {
			case ACT_PROPOSAL:
				collectionID = COLL_PROPOSAL;
				break;
			case AMENDMENT:
				collectionID = COLL_AMENDMENT;
				break;
			case ACT_FINAL:
				collectionID = COLL_FINAL;
				break;
			case ARCHIVE:
				collectionID = COLL_ARCHIVE;
				break;
			default:
				System.out.println(">> ERROR: Bad collection ID <<\n giving up\n");
				return;
			}
			
			// Connection parameters for the database.
			System.out.println("> Loading connection properties.");
			ConnectionProperties cn = loadProperties();
			DatabaseClient client = DatabaseClientFactory.newClient(cn.host, cn.port, cn.database, cn.user, cn.password, cn.authType);
			
			// Create a document manager to work with XML files.
			XMLDocumentManager xmlManager = client.newXMLDocumentManager();
			
			DocumentMetadataHandle metadata = new DocumentMetadataHandle();
			metadata.getCollections().add(collectionID);
			
			// Document section
			String documentID = doc.getDocumentElement().getAttribute("Naziv");
			if(documentID.equals("") || documentID == null) {
				documentID = documentID.concat(user).concat(String.valueOf(Calendar.getInstance().getTimeInMillis()));
			}
			doc.getDocumentElement().setAttribute("Naziv", documentID);
			documentID = documentID.concat(".xml");
			System.out.println("Inserting: " + documentID);
			InputStreamHandle ish = new InputStreamHandle(createInputStream(doc, false));
			
			xmlManager.write(documentID, metadata, ish);
			client.release();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Reads a XML entry into a {@link Document} from the MarkLogic database specified in the {@code connection.properties}.
	 * 
	 * @param documentID - Full URI of the entry
	 * @return {@link Document}
	 */
	public static Document readDocument(String documentID) {
		
		try {
			// Connection parameters for the database.
			System.out.println("> Loading connection properties.");
			ConnectionProperties cn = loadProperties();
			DatabaseClient client = DatabaseClientFactory.newClient(cn.host, cn.port, cn.database, cn.user, cn.password, cn.authType);
			
			// Create a document manager to work with XML files.
			XMLDocumentManager xmlManager = client.newXMLDocumentManager();
			
			// Handles
			DOMHandle content = new DOMHandle();
			DocumentMetadataHandle metadata = new DocumentMetadataHandle();
			
			// Document section
			System.out.println("Reading: " + documentID);
			xmlManager.read(documentID, metadata, content);
			Document doc = content.get();
			client.release();
			
			return doc;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns all documents from the specified collection.
	 * @param coll - static int fields
	 */
	public static void getDocumentsFromCollection(int coll) {
		
	}
	
	public static void updateDocument(String documentID, Document amendment) {
		try {
			// Connection parameters for the database.
			System.out.println("> Loading connection properties.");
			ConnectionProperties cn = loadProperties();
			
			DatabaseClient client = DatabaseClientFactory.newClient(cn.host, cn.port, cn.database, cn.user, cn.password, cn.authType);
			
			// Create a document manager to work with XML files.
			XMLDocumentManager xmlManager = client.newXMLDocumentManager();
			
			// Defining namespace mappings
			System.out.println("> Namespace " + amendment.getDocumentElement().getPrefix() + " : " + amendment.getDocumentElement().getNamespaceURI());
			EditableNamespaceContext namespaces = new EditableNamespaceContext();
			namespaces.put(amendment.getDocumentElement().getPrefix(), amendment.getDocumentElement().getNamespaceURI());
			namespaces.put("fn", "http://www.w3.org/2005/xpath-functions");
			
			// Assigning namespaces to patch builder
			DocumentPatchBuilder patchBuilder = xmlManager.newPatchBuilder();
			patchBuilder.setNamespaces(namespaces);
			
			// Data
			Element predlogResenja = (Element)( amendment.getElementsByTagName("Predlog_resenja").item(0));
			String tipPredloga = predlogResenja.getAttribute("tippredloga");
			// Type of amendment
			switch (tipPredloga) {
			case "izmena": {
				String tipElementa = predlogResenja.getAttribute("tipElementa");			
				Element izmena = (Element)( predlogResenja.getElementsByTagName(tipElementa).item(0));
				
				String xpath = ((Element)(amendment.getElementsByTagName("Odredba").item(0))).getTextContent();
				
				patchBuilder.replaceFragment(xpath, izmena);
				
				DocumentPatchHandle patchHandle = patchBuilder.build();
				xmlManager.patch(documentID, patchHandle);
				client.release();
				
				break;
			}
			case "dopuna": {
				String tipElementa = predlogResenja.getAttribute("tipElementa");			
				Element izmena = (Element)( predlogResenja.getElementsByTagName(tipElementa).item(0));
				
				String xpath = ((Element)(amendment.getElementsByTagName("Odredba").item(0))).getTextContent();
				
				patchBuilder.insertFragment(xpath, Position.BEFORE, izmena);
				
				DocumentPatchHandle patchHandle = patchBuilder.build();
				xmlManager.patch(documentID, patchHandle);
				client.release();
				
				break;
			}
			case "brisanje": {				
				String xpath = ((Element)(amendment.getElementsByTagName("Odredba").item(0))).getTextContent();
				
				patchBuilder.delete(xpath);
				
				DocumentPatchHandle patchHandle = patchBuilder.build();
				xmlManager.patch(documentID, patchHandle);
				client.release();
				break;
			}
			default:
				System.out.println("ERROR: Wrong amendment type.");
				return;
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Streams ----------------------------------------------------------------------
	
	 /**
	  * Creates an {@link InputStream} from the given {@link Document}.
	  * 
	  * @param document the document to convert
	  * @param prettyPrint prettyPrinted if true
	  * @return An input stream of the document
	  * @throws IOException
	  */
	public static InputStream createInputStream(Document document, boolean prettyPrint) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		OutputFormat outputFormat = new OutputFormat(document);
		if (prettyPrint) {
			outputFormat.setIndenting(true);
			outputFormat.setIndent(2);
			outputFormat.setLineWidth(65);
			outputFormat.setPreserveSpace(false);
		}
		XMLSerializer serializer = new XMLSerializer(outputStream, outputFormat);
		serializer.serialize(document);
		return new ByteArrayInputStream(outputStream.toByteArray());
	}
	
	/**
	 * Creates an {@link OutputStream} from the given {@link Node}.
	 * This can then be used to print in the console or some other use.
	 *
	 * @param node - a node to be serialized, can also be a {@link Document}
	 * @param out - an output stream to write the serialized DOM representation to
	 * 
	 */
	public static void createOutputStream(Node node, OutputStream out) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			DOMSource source = new DOMSource(node);
 
			StreamResult result = new StreamResult(out);

			transformer.transform(source, result);
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	
	//---------------------------------------------------------------------------------------------------
	// Connection handling
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * Represents the connection parameters specified in a property file
	 */
	static public class ConnectionProperties {

		public String host;
		public int port = -1;
		public String user;
		public String password;
		public String database;
		public Authentication authType;

		/**
		 * Creates connection properties object from {@link Properties}.
		 * @param props {@link Properties}
		 */
		public ConnectionProperties(Properties props) {
			super();
			host = props.getProperty("conn.host").trim();
			port = Integer.parseInt(props.getProperty("conn.port"));
			user = props.getProperty("conn.user").trim();
			password = props.getProperty("conn.password").trim();
			database = props.getProperty("conn.database").trim();
			authType = Authentication.valueOf(props.getProperty("conn.authentication_type").toUpperCase().trim());
		}
	}

	/**
	 * Loads the connection properties from the property file for the specified MarkLogic database.
	 * 
	 * @return {@link ConnectionProperties}
	 */
	public static ConnectionProperties loadProperties() throws IOException {
		String propsName = "connection.properties";

		InputStream propsStream = openStream(propsName);
		if (propsStream == null) {
			throw new IOException("Could not read properties " + propsName);
		}

		Properties props = new Properties();
		props.load(propsStream);

		return new ConnectionProperties(props);
	}

	/**
	 * Read a resource for an example.
	 * 
	 * @param fileName
	 *            the name of the resource
	 * @return an input stream for the resource
	 * @throws IOException
	 */
	public static InputStream openStream(String fileName) throws IOException {
		return MarkLogicUtils.class.getClassLoader().getResourceAsStream(fileName);
	}
	
	

}
