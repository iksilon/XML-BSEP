package actions.keystore;

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.AbstractAction;

import app.DBConnection;

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
			
			//TODO: Set this.
			String psString = "SELECT ";
			
			PreparedStatement ps = conn.prepareStatement(psString);
			
			ResultSet res = ps.executeQuery();
			
			while(!res.isLast()) {
				
			}
			//TODO: Extract usernames into an array
			
			//TODO: Show JOptionPane
			
			//TODO: Get user and pass from selected
			
			//TODO: Create keystore
			
			//TODO: Create keypair
			
			//TODO: Create certificate (to include CA choice)
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
	}

}
