package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;
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
	
	//---------------------------------------------------------------------------------------------------
	// Document handling
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * Inserts a given XML {@link Document} into the XML database.
	 * Database is specified in a property file.
	 * 
	 * @param doc - {@link Document} object representing the XML document
	 * @param collection - one of three possible collections specified in the static fields
	 */
	public static void insertDocument(Document doc, int collection) {
		
		try {
			System.out.println("Beginning database insert:");
			
			// Collection
			String collectionID = "";
			
			switch (collection) {
			case ACT_PROPOSAL:
				collectionID = "team27/proposals";
				break;
			case AMENDMENT:
				collectionID = "team27/amendments";
				break;
			case ACT_FINAL:
				collectionID = "team27/finals";
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
			
			//TODO: Document ID is hardcoded, needs schema revision.
			// Document section
			String documentID = "test/test1.xml";
			InputStreamHandle ish = new InputStreamHandle(createInputStream(doc, false));
			
			xmlManager.write(documentID, metadata, ish);
			client.release();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
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
