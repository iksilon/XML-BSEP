package actions.keystore;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.MainWindow;
import gui.SetPasswordDialog;
import security.KeyStoreUtils;

/**
 * Opens a file choosing dialog to select the path where the keystore will be saved.
 *
 */
public class ActionSaveAs extends AbstractAction {
	private static final long serialVersionUID = 3925404848236570471L;
	public ActionSaveAs() {
		putValue(NAME, "Save As");
		putValue(SHORT_DESCRIPTION, "Save keystore to a file");
	}
	public void actionPerformed(ActionEvent e) {			
		// Is there a keystore at all?
		if(MainWindow.getInstance().getCurrentKeystore() == null) {
			JOptionPane.showMessageDialog(MainWindow.getInstance(),
					"There is no active keystore to be saved. Please create or open a keystore first.");
			return;
		}
		
		// Set default file chooser directory. Create the dialog.
		String workingDir = System.getProperty("user.dir");
		workingDir = Paths.get(workingDir, "keystores").toString();
		JFileChooser chooser = new JFileChooser(workingDir);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Java keystore files", "jks");
	    chooser.setFileFilter(filter);
	    
	    String path = "";
	    		    
	    // User gave up.
	    int returnVal = chooser.showSaveDialog(MainWindow.getInstance());
	    if (returnVal == JFileChooser.CANCEL_OPTION) {
	    	return;
	    }
	    
	    // User approved.
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	// TODO: Z:Minor: Prevent unsupported chars in filename by making a key press listener.
	    	
	    	// Check if user forgot file extension or got it wrong.
	        path = chooser.getSelectedFile().getAbsolutePath();
	        if(!path.endsWith(".jks")) {
	        	path = path.concat(".jks");
	        }
	        
	        // Does this file already exist? Overwrite it?
		    File f = new File(path);
		    if(f.isFile()) {
		    	String ObjButtons[] = {"Yes","No"};
		        int PromptResult = 
		        		JOptionPane.showOptionDialog(null,
		        									"Selected file already exists, would you like to overwrite existing file?",
		        									"Overwrite existing file",
		        									JOptionPane.DEFAULT_OPTION,
		        									JOptionPane.WARNING_MESSAGE,
		        									null,
		        									ObjButtons,
		        									ObjButtons[1]);
		        // Yep, overwrite.
		        if(PromptResult == JOptionPane.YES_OPTION)
		        {
		        	// Set password for keystore.
			    	SetPasswordDialog ksd = new SetPasswordDialog();
					ksd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
					ksd.setVisible(true);
					
					// After returning from the modal dialog.					
					KeyStoreUtils.saveKeyStore(MainWindow.getInstance().getCurrentKeystore(), path, ksd.getPassword());
					MainWindow.getInstance().setCurrentPath(path);
			    	MainWindow.getInstance().getTxtCurrentKeystore().setText(path);
			    	MainWindow.getInstance().getLblCurrentKeystore().setText("Current keystore:");
			    	// Clean up.
			    	Arrays.fill(ksd.getPassword(), '0');
					ksd.dispose();
		        }
		        
		    }
		    // This is a new file.
		    else {
		    	// Set password for keystore.
		    	SetPasswordDialog ksd = new SetPasswordDialog();
				ksd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
				ksd.setVisible(true);
				
				// After returning from the modal dialog.					
				KeyStoreUtils.saveKeyStore(MainWindow.getInstance().getCurrentKeystore(), path, ksd.getPassword());
				MainWindow.getInstance().setCurrentPath(path);
				MainWindow.getInstance().getTxtCurrentKeystore().setText(path);
		    	MainWindow.getInstance().getLblCurrentKeystore().setText("Current keystore:");
		    	// Clean up.
		    	Arrays.fill(ksd.getPassword(), '0');
				ksd.dispose();
		    }
	    }
	    
	    
	}
}
