package actions.certificate;

import java.awt.event.ActionEvent;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.MainWindow;
import security.CertificateUtils;

/**
 * 
 * Exports the certificate into specified file and encoding.
 *
 */
public class ActionExportCertificate extends AbstractAction {
	private static final long serialVersionUID = -1698079888963949279L;
	public ActionExportCertificate() {
		putValue(NAME, "Export Certificate");
		putValue(SHORT_DESCRIPTION, "Export keypair to a certificate file");
	}
	
	/**
	 * Saves the given {@link Certificate} to the specified {@code path} based on the type of the file (file extension {@code ex}).
	 * If the encoding is not specified (.cer or .crt), user will be prompted to choose encoding type.
	 * 
	 * @param path {@link String}
	 * @param cert {@link Certificate}
	 * @param ex {@link String}
	 */
	private void saveFile(String path, Certificate cert, String ex) {
		System.out.println(ex);
		
		switch (ex) {
		case "cer":
		case "crt":
			// Ask which encoding is to be used. A misuse of Y/N dialog, I know, I don't care.
			Object[] options = {"PEM", "DER"};
			int n = JOptionPane.showOptionDialog(MainWindow.getInstance(),
			    "Which encoding you want to use?",
			    "Certificate encoding",
			    JOptionPane.YES_NO_OPTION,
			    JOptionPane.QUESTION_MESSAGE,
			    null,
			    options,
			    options[0]);
			
			if(n == 0) {
				CertificateUtils.savePEMfile(path, cert);
			}
			else {
				CertificateUtils.saveDERfile(path, cert);
			}
			
			break;
		case "der":
			CertificateUtils.saveDERfile(path, cert);
			break;
		case "pem":
			CertificateUtils.savePEMfile(path, cert);
			break;
		default:
			break;
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		JTable keypairTable = MainWindow.getInstance().getKeypairTable();
		KeyStore currentKeystore = MainWindow.getInstance().getCurrentKeystore();
		
		// Is anything selected?
		if(keypairTable.getSelectedRow() == -1) {
			JOptionPane.showMessageDialog(MainWindow.getInstance(),
					"No row in table is selected, certificate cannot be exported."
					+ "Please select the certificate first and try again.");
			return;
		}
		// Yes, it is.
		else {
			String alias = keypairTable.getValueAt(keypairTable.getSelectedRow(), 1).toString();
			try {
				Certificate cert = currentKeystore.getCertificate(alias);
				
				// Set default file chooser directory. Create the dialog.
				String workingDir = System.getProperty("user.dir");
				workingDir = Paths.get(workingDir, "certificates").toString();
				JFileChooser chooser = new JFileChooser(workingDir);
			    FileNameExtensionFilter filterDef = new FileNameExtensionFilter("Certificate files", "cer", "crt");
			    chooser.setFileFilter(filterDef);
			    FileNameExtensionFilter filterPEM = new FileNameExtensionFilter("PEM encoded certificate files", "pem");
			    FileNameExtensionFilter filterDER = new FileNameExtensionFilter("DER encoded certificate files", "der");
			    chooser.addChoosableFileFilter(filterPEM);
			    chooser.addChoosableFileFilter(filterDER);
			    
			    // User gave up.
			    int returnVal = chooser.showSaveDialog(MainWindow.getInstance());
			    if (returnVal == JFileChooser.CANCEL_OPTION) {
			    	return;
			    }
			    
			    // User approved.
			    if (returnVal == JFileChooser.APPROVE_OPTION) {
			    	FileNameExtensionFilter ff = (FileNameExtensionFilter) chooser.getFileFilter();
			    	String path = chooser.getSelectedFile().getAbsolutePath();
			    	String[] exts = ff.getExtensions();
			    	
			    	// Find extension match
			    	for (String ex : exts) {
						if(path.endsWith("." + ex)) {
							// Match found, save file.
							saveFile(path, cert, ex);
							return;
						}
					}
			    	
			    	// No match, add the extension and save file.
			    	path = path.concat("." + exts[0]);
			    	saveFile(path, cert, exts[0]);
			    }
				
			} catch (KeyStoreException e1) {
				e1.printStackTrace();
			}				
		}
		
	}
}
