package actions.certificate;

import java.awt.event.ActionEvent;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import javax.security.auth.x500.X500Principal;
import javax.swing.AbstractAction;
import javax.swing.JDialog;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import gui.EnterPasswordDialog;
import gui.MainWindow;
import security.CRLUtils;
import security.KeyStoreUtils;

/**
 * Creates a dialog for new Certificate revocation list (CRL)
 */
public class ActionCreateCRL extends AbstractAction {
	private static final long serialVersionUID = -4655131886494842553L;
	public ActionCreateCRL() {
		putValue(NAME, "SwingAction");
		putValue(SHORT_DESCRIPTION, "Some short description");
	}
	public void actionPerformed(ActionEvent e) {
		KeyStore currentKeystore = MainWindow.getInstance().getCurrentKeystore();
		
		// Select CA which will sign.
		String alias = KeyStoreUtils.selectCA(currentKeystore);
		
		try {
			
			// Valid from
			Date today = Calendar.getInstance().getTime();
			
			// Extract the certificate and CA data from the keystore.
			EnterPasswordDialog epd = new EnterPasswordDialog();
			epd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			epd.setVisible(true);
			PrivateKey pk = (PrivateKey) currentKeystore.getKey(alias, epd.getPassword());
			X509Certificate cert = (X509Certificate) currentKeystore.getCertificate(alias);
			
			// Issuer name
			X500Principal prnc = cert.getIssuerX500Principal();
			X500Name CA = X500Name.getInstance(prnc.getEncoded());

			// Signed by our CA
			JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			builder.setProvider("BC");
			ContentSigner contentSigner = builder.build(pk);
			
			// Create the CRL
			X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(CA, today);
			X509CRLHolder holder = crlBuilder.build(contentSigner);
			JcaX509CRLConverter cnv = new JcaX509CRLConverter();
			cnv.setProvider("BC");
			X509CRL crl = cnv.getCRL(holder);
			
			// Set the path
			String path = System.getProperty("user.dir");
			path = Paths.get(path, "crls").toString();
			path = Paths.get(path, alias+".crl").toString();
			
			CRLUtils.saveCRLfile(path, crl);
			
		} catch (UnrecoverableKeyException e1) {
			e1.printStackTrace();
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (OperatorCreationException e1) {
			e1.printStackTrace();
		} catch (CRLException e1) {
			e1.printStackTrace();
		}
		
	}
}
