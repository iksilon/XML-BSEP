/**
 * 
 */
package application;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import gui.MainFrameLegacy;

/**
 * <p>Main class of the application.</p>
 * <p>Contains the {@code main()} method and the application starts from here.</p>
 * 
 * @author ILA
 *
 */
public class Main {

	/**
	 * Launch the application
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		MainFrameLegacy main = MainFrameLegacy.getInstance();
		main.setVisible(true);

	}

}
