package actions.keystore;

import java.awt.event.ActionEvent;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import gui.EnterPasswordDialog;
import gui.MainWindow;
import security.KeyStoreUtils;

/**
 * Opens a file choosing dialog to select the path which keystore will be opened.
 *
 */
public class ActionOpen extends AbstractAction {
	private static final long serialVersionUID = 340823143919984037L;
	public ActionOpen() {
		putValue(NAME, "Open");
		putValue(SHORT_DESCRIPTION, "Open a keystore file");
	}
	public void actionPerformed(ActionEvent e) {
		JTable keypairTable = MainWindow.getInstance().getKeypairTable();
		
		// Set default file chooser directory. Create the dialog.
		String workingDir = System.getProperty("user.dir");
		workingDir = Paths.get(workingDir, "certificates").toString();
		JFileChooser chooser = new JFileChooser(workingDir);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Java keystore files", "jks");
	    chooser.setFileFilter(filter);
	    
	    String path = "";
	    		    
	    // User gave up.
	    int returnVal = chooser.showOpenDialog(MainWindow.getInstance());
	    if (returnVal == JFileChooser.CANCEL_OPTION) {
	    	return;
	    }
	    
	    // User approved.
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	// Enter password.
	    	EnterPasswordDialog ksd = new EnterPasswordDialog();
			ksd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			ksd.setVisible(true);
			
			// After returning from the modal dialog.
			path = chooser.getSelectedFile().getAbsolutePath();
	    	KeyStore loaded = KeyStoreUtils.loadKeyStore(path, ksd.getPassword());
	    	MainWindow.getInstance().setCurrentKeystore(loaded);
	    	
	    	MainWindow.getInstance().setCurrentPath(path);
	    	MainWindow.getInstance().getTxtCurrentKeystore().setText(path);
	    	// Clean up.
	    	Arrays.fill(ksd.getPassword(), '0');
	    	ksd.dispose();
	    	
	    	// Populate the table.
	    	Enumeration<String> aliases;
			try {
				aliases = loaded.aliases();					
				System.out.println("Aliases:");
				while(aliases.hasMoreElements()) {
					String a = aliases.nextElement();
					System.out.println(a);
					int rows = ((DefaultTableModel)keypairTable.getModel()).getRowCount();
					((DefaultTableModel)keypairTable.getModel()).addRow(new Object[]{rows+1, a});
				}
				
			} catch (KeyStoreException e1) {
				e1.printStackTrace();
			}
	    }
	    
	}
}
