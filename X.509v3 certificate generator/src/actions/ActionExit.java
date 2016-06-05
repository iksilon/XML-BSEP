package actions;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;

import gui.MainWindow;

/**
 * Sends the window closing event, and triggers appropriate listeners.
 */
public class ActionExit extends AbstractAction {
	private static final long serialVersionUID = 2732771330480399657L;
	public ActionExit() {
		putValue(NAME, "Exit");
		putValue(SHORT_DESCRIPTION, "Close the application");
	}
	public void actionPerformed(ActionEvent e) {			
		MainWindow.getInstance().dispatchEvent(new WindowEvent(MainWindow.getInstance(), WindowEvent.WINDOW_CLOSING));
	}
}
