package actions.certificate;

import java.awt.event.ActionEvent;
import java.security.KeyStore;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import gui.CertificateDialog;
import gui.MainWindow;

/**
 * Generates a keypair.
 * Prompts the user to fill in certificate details and generates the certificate.
 */
public class ActionKeypair extends AbstractAction {
	private static final long serialVersionUID = -1411136323257319945L;
	public ActionKeypair() {
		putValue(NAME, "Keypair");
		putValue(SHORT_DESCRIPTION, "Generate new keypair");
	}
	public void actionPerformed(ActionEvent e) {
		KeyStore currentKeystore = MainWindow.getInstance().getCurrentKeystore();
		JTable keypairTable = MainWindow.getInstance().getKeypairTable();
		
		// Is there a KeyStore to which this will be stored?
		if(currentKeystore == null) {
			JOptionPane.showMessageDialog(MainWindow.getInstance(),
					"A keystore is needed to create a keypair. Please create a keystore first. "
					+ "You can do this by going to the File menu, selecting New and then Keystore");
			return;
		}
		
		// Open dialog for all certificate data.
		CertificateDialog cd = new CertificateDialog(currentKeystore);
		cd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		cd.setVisible(true);
		
		// After returning from the modal dialog.
		if (cd.getCertificate() != null) {					
			// Update view.
			int rows = ((DefaultTableModel)keypairTable.getModel()).getRowCount();
			((DefaultTableModel)keypairTable.getModel()).addRow(new Object[]{rows+1, cd.getAlias()});
			
	    	MainWindow.getInstance().getLblCurrentKeystore().setText("*Current keystore:");
		}
		cd.dispose();
	}
}
