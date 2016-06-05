package actions.keystore;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import gui.MainWindow;
import gui.SetPasswordDialog;
import security.KeyStoreUtils;

/**
 * Saves the current keystore into file.
 * If it's a new keystore, {@link ActionSaveAs} is called.
 *
 */
public class ActionSave extends AbstractAction {
	private static final long serialVersionUID = -4641089031850059072L;
	public ActionSave() {
		putValue(NAME, "Save");
		putValue(SHORT_DESCRIPTION, "Save keystore");
	}
	public void actionPerformed(ActionEvent e) {
		// Is there a keystore at all?
		if(MainWindow.getInstance().getCurrentKeystore() == null) {
			JOptionPane.showMessageDialog(MainWindow.getInstance(),
					"There is no active keystore to be saved. Please create or open a keystore first.");
			return;
		}
		
		// This is a new keystore - call SaveAs.
		if(MainWindow.getInstance().getCurrentPath().equals("")) {
			MainWindow.getInstance().getActSaveAs().actionPerformed(null);
			return;
		}
		// Existing. Overwrite the file.
		else {	
			// Set password for keystore. This is needed because keeping password in memory is bad and saving needs a password.
	    	SetPasswordDialog ksd = new SetPasswordDialog();
			ksd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			ksd.setVisible(true);
			
			// After returning from the modal dialog.					
			KeyStoreUtils.saveKeyStore(MainWindow.getInstance().getCurrentKeystore(), MainWindow.getInstance().getCurrentPath(), ksd.getPassword());
			Arrays.fill(ksd.getPassword(), '0');
			ksd.dispose();
			MainWindow.getInstance().getTxtCurrentKeystore().setText(MainWindow.getInstance().getCurrentPath());
			MainWindow.getInstance().getLblCurrentKeystore().setText("Current keystore:");
		}
	}
}
