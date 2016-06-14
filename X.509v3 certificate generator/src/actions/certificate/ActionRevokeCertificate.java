package actions.certificate;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import javax.security.auth.x500.X500Principal;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import gui.CAChooseDialog;
import gui.MainWindow;
import security.CRLUtils;

/**
 * Revokes the certificate selected in the table.
 */
public class ActionRevokeCertificate extends AbstractAction {
	private static final long serialVersionUID = -1212925295287886365L;
	public ActionRevokeCertificate() {
		putValue(NAME, "RevokeCertificate");
		putValue(SHORT_DESCRIPTION, "Revoke selected certificate");
	}
	public void actionPerformed(ActionEvent e) {
		JTable keypairTable = MainWindow.getInstance().getKeypairTable();
		KeyStore currentKeystore = MainWindow.getInstance().getCurrentKeystore();
		
		if(keypairTable.getSelectedRow() == -1) {
			JOptionPane.showMessageDialog(MainWindow.getInstance(), "No certificate is selected. Please select a certificate and try again.");
			return;
		}
		
		try {
			// Get revoked certificate
			String alias = (String) keypairTable.getValueAt(keypairTable.getSelectedRow(), 1);
			X509Certificate cert = (X509Certificate) currentKeystore.getCertificate(alias);
			
			// Revoke to which CRL?
			String workingDir = System.getProperty("user.dir");
			workingDir = Paths.get(workingDir, "crls").toString();
			JFileChooser chooser = new JFileChooser(workingDir);
		    FileNameExtensionFilter filterDef = new FileNameExtensionFilter("Certificate Revokation List files", "crl");
		    chooser.setFileFilter(filterDef);
		    
		    // User gave up.
		    int returnVal = chooser.showOpenDialog(MainWindow.getInstance());
		    if (returnVal == JFileChooser.CANCEL_OPTION) {
		    	return;
		    }
		    
		    // User approved.
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		    	String path = chooser.getSelectedFile().getAbsolutePath();
		    	
		    	if(!path.endsWith(".crl")) {
		    		JOptionPane.showMessageDialog(MainWindow.getInstance(), "Selected file is not a Certificate Revocation List (.crl) file.");
		    		return;
		    	}
		    	
		    	// Old CRL to be updated
		    	X509CRL chosenCRL = CRLUtils.openFromFile(path);
		    	
		    	// Select CA which will sign.
		    	CAChooseDialog cacd = new CAChooseDialog();
		    	cacd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		    	cacd.setVisible(true);
				PrivateKey pk = cacd.getIssuerPK();
				X509Certificate CAcert = (X509Certificate) cacd.getIssuerCertificate();
				
				// Is it the right CA?
				if(!CAcert.getSubjectX500Principal().equals(chosenCRL.getIssuerX500Principal())) {
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "The issuer you selected is not the original issuer. Please try again.");
		    		return;
				}
				
				// Issuer name
				X500Principal prnc = chosenCRL.getIssuerX500Principal();
				X500Name CA = X500Name.getInstance(prnc.getEncoded());
				
				// Signed by our CA
				JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
				builder.setProvider("BC");
				ContentSigner contentSigner = builder.build(pk);
				
				// Valid from
				Date today = Calendar.getInstance().getTime();
				
				// Create the CRL which will replace old
				X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(CA, today);
		    	crlBuilder.addCRL(new X509CRLHolder(chosenCRL.getEncoded()));
		    	crlBuilder.addCRLEntry(cert.getSerialNumber(), today, CRLReason.unspecified);
		    	X509CRLHolder holder = crlBuilder.build(contentSigner);
				JcaX509CRLConverter cnv = new JcaX509CRLConverter();
				cnv.setProvider("BC");
				X509CRL crl = cnv.getCRL(holder);					
				
				CRLUtils.saveCRLfile(chooser.getSelectedFile().getAbsolutePath(), crl);
		    }
			 
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
		} catch (OperatorCreationException e1) {
			e1.printStackTrace();
		} catch (CRLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
