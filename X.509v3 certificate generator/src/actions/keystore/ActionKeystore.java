package actions.keystore;

import java.awt.event.ActionEvent;
import java.security.KeyStore;

import javax.swing.AbstractAction;

import gui.MainWindow;
import security.KeyStoreUtils;

/**
 * Creates a new keystore.
 * Opens a dialog for defining keystore password.
 */
public class ActionKeystore extends AbstractAction {
	private static final long serialVersionUID = 425412543121784713L;
	public ActionKeystore() {
		putValue(NAME, "Keystore");
		putValue(SHORT_DESCRIPTION, "Create new keystore");
	}
	public void actionPerformed(ActionEvent e) {
		// Placeholder password. Will be set when keystore is saved to file.		
		KeyStore ks = KeyStoreUtils.loadKeyStore(null, "placeholder".toCharArray());
		MainWindow.getInstance().setCurrentKeystore(ks);
		
		MainWindow.getInstance().getTxtCurrentKeystore().setText("New keystore");
		MainWindow.getInstance().getLblCurrentKeystore().setText("*Current keystore:");
	}
}
