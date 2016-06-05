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
import javax.swing.table.DefaultTableModel;

import gui.MainWindow;
import security.CertificateUtils;

/**
 * Opens a certificate from a file and adds it to the current keystore.
 * File chooser gives possible file extension filters but this is only for UX,
 * extension and encoding are checked nevertheless. 
 *
 */
public class ActionImportCertificate extends AbstractAction {
	private static final long serialVersionUID = 4384732596786044097L;
	public ActionImportCertificate() {
		putValue(NAME, "Export All");
		putValue(SHORT_DESCRIPTION, "Export all certificates to specified folder.");
	}
	
	public void actionPerformed(ActionEvent e) {
		KeyStore currentKeystore = MainWindow.getInstance().getCurrentKeystore();
		JTable keypairTable = MainWindow.getInstance().getKeypairTable();
		
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
	    int returnVal = chooser.showOpenDialog(MainWindow.getInstance());
	    if (returnVal == JFileChooser.CANCEL_OPTION) {
	    	return;
	    }
	    
	    // User approved.
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	String path = chooser.getSelectedFile().getAbsolutePath();
	    	String[] exts = {".cer", ".crt", ".der", ".pem"};
	    	
	    	// Find extension match
	    	for (String ex : exts) {
				if(path.endsWith(ex)) {
					// Match found, set alias.
					String alias = JOptionPane.showInputDialog(MainWindow.getInstance(), 
				    		"Please enter certificate alias:");
				    if(alias == null || alias.equals("")) {
				    	JOptionPane.showMessageDialog(MainWindow.getInstance(), 
				    			"That is not a valid alias.");
				    	return;
				    }
					
					// Open file.
					Certificate c = CertificateUtils.openFile(path, ex);
					if(c == null) {
						return;
					}
					
					// Yay, let's present this mofo.
					try {
						currentKeystore.setCertificateEntry(alias, c);
						int rows = ((DefaultTableModel)keypairTable.getModel()).getRowCount();
						((DefaultTableModel)keypairTable.getModel()).addRow(new Object[]{rows+1, alias});
						//TODO: Z:Minor: Have some kind of check in the table whether certificate has a private key available.
						MainWindow.getInstance().getLblCurrentKeystore().setText("*Current keystore:");
					} catch (KeyStoreException e1) {
						e1.printStackTrace();
					}
					
					return;
				}
			}
	    	
	    	// No match, alert the user.
	    	JOptionPane.showMessageDialog(MainWindow.getInstance(),
	    			"Selected file type is not supported.");
	    }
	}
}
