package actions.keystore;

import java.awt.event.ActionEvent;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import app.DBConnection;
import gui.CertificateDialog;
import gui.MainWindow;
import security.KeyStoreUtils;

public class ActionKeystoreFromUser extends AbstractAction {
	private static final long serialVersionUID = -7110049756005517143L;
	public ActionKeystoreFromUser() {
		putValue(NAME, "Keystore from User");
		putValue(SHORT_DESCRIPTION, "Create keystore from a registered user.");
	}
	public void actionPerformed(ActionEvent e) {
		
		try {
			Connection conn = DBConnection.getConnection();
			ArrayList<String> users = new ArrayList<>();
			
			// Get all usernames.
			String psString = "SELECT username FROM Users where role_id in (1,2);";
			PreparedStatement ps = conn.prepareStatement(psString);
			ResultSet res = ps.executeQuery();
			
			while(res.next()) {				
				users.add(res.getString(1));
			}
			
			res.close();
			
			String un = (String)JOptionPane.showInputDialog(
			                    MainWindow.getInstance(),
			                    "Choose user for which to create a keystore:",
			                    "Keystore User",
			                    JOptionPane.PLAIN_MESSAGE,
			                    null,
			                    users.toArray(),
			                    users.get(0));
			
			// Get password.
			psString = "SELECT password FROM Users where username=?";
			ps = conn.prepareStatement(psString);
			ps.setString(1, un);
			res = ps.executeQuery();
			res.next();
			String pw = res.getString(1);
			
			// Create keystore.
			KeyStore ks = KeyStoreUtils.loadKeyStore(null, pw.toCharArray());
			MainWindow.getInstance().setCurrentKeystore(ks);
			// Open dialog for all certificate data.
			CertificateDialog cd = new CertificateDialog(ks);
			cd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			// Alter the dialog so it can't be edited.
				cd.getTxtAlias().setText(un);						cd.getTxtAlias().setEnabled(false);
				cd.getPassField().setText(pw.toString());			cd.getPassField().setEnabled(false);
				cd.getPasswordRetype().setText(pw.toString());		cd.getPasswordRetype().setEnabled(false);
				cd.getChckbxSelfSigned().setSelected(false);		cd.getChckbxSelfSigned().setEnabled(false);
			cd.setVisible(true);
			
			// After returning from the modal dialog.
			if (cd.getCertificate() != null) {
				String path = System.getProperty("user.dir");
				path = Paths.get(path, "keystores", un+".jks").toString();
				KeyStoreUtils.saveKeyStore(ks, path, pw.toCharArray());
			}
			cd.dispose();
			res.close();
			conn.close();
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
	}

}
