package utils.xmlEncryption;

import java.security.PrivateKey;
import java.security.Security;

import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.utils.EncryptionConstants;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * Created by Nemanja on 8/6/2016.
 */
public class DecryptXML {
	static {
		//staticka inicijalizacija
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
	}

	/**
	 * Kriptuje sadrzaj prvog elementa odsek
	 */
	public Document decrypt(Document doc, PrivateKey privateKey) {

		try {
			//cipher za dekritpovanje XML-a
			XMLCipher xmlCipher = XMLCipher.getInstance();
			//inicijalizacija za dekriptovanje
			xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
			//postavlja se kljuc za dekriptovanje tajnog kljuca
			xmlCipher.setKEK(privateKey);

			//trazi se prvi EncryptedData element
			//getElementsByTagNameNS(String namespaceURI, String localName)
			//Returns a NodeList of all the Elements with a given local name and namespace URI in a document order
			
			Element r = doc.getDocumentElement();
			System.out.println("Root is " + r.getTagName());
			NodeList children = r.getChildNodes();
			for(int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i); 
				if(child instanceof Element) {
					System.out.println("Found element: " + child.getNodeName());
				}
				else {
					System.out.println("Found not element: " + child.getNodeName());
				}
			}
			
			NodeList encDataList = r.getElementsByTagName("xenc:EncryptedData");
			
			//NodeList encDataList = doc.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "xenc:EncryptedData");
			System.out.println("List no " + encDataList.getLength());
			Element encData = (Element) encDataList.item(0);
			
			System.out.println(doc.getNamespaceURI());
			encData.setPrefix("xenc");
			
			
			NodeList dataElements = 
	                encData.getElementsByTagNameNS(
	                    EncryptionConstants.EncryptionSpecNS, EncryptionConstants._TAG_CIPHERDATA);
			
			System.out.println("data elems " + dataElements.getLength());

			//dekriptuje se
			//pri cemu se prvo dekriptuje tajni kljuc, pa onda njime podaci
			xmlCipher.doFinal(doc, encData);

			return doc;
		} catch (XMLEncryptionException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
