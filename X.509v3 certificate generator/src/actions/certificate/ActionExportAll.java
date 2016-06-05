package actions.certificate;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import gui.MainWindow;

public class ActionExportAll extends AbstractAction {
	private static final long serialVersionUID = 5683267289392412616L;
	public ActionExportAll() {
		putValue(NAME, "Export All");
		putValue(SHORT_DESCRIPTION, "Export all certificates to a specified folder");
	}
	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(MainWindow.getInstance(), "Coming soon.");
		// TODO: Z:Minor: Export all certificates.
	}
}
