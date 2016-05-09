/**
 * 
 */
package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author ILA
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	
	private JPanel mainFormPanel;
	private JButton generateButton;
	
	private MainFrame(){
	}
	
	private static MainFrame instance = null;
	
	public static MainFrame getInstance() {	//Singleton
		if (instance != null){
			return instance;
		} else{
			instance = new MainFrame();
			instance.initialize();
			return instance;
		}
	}
	
	private void initialize() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();	//used to gather details of the display
		Dimension screenDimension = toolkit.getScreenSize();
		
		this.setSize(screenDimension.width*2/3, screenDimension.height*2/3);
		setTitle("UPs");
		this.setLocationRelativeTo(null);
		
		addCloseAction();	//pops open a confirmation dialog for closing the application
		
		//GridBag Layout
		mainFormPanel = new JPanel(new GridBagLayout());
		this.getContentPane().add(mainFormPanel);
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		
		gbc.gridy++;	//so that we are in the last row;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;	//down and to the right
		gbc.insets.set(5, 5, 5, 5);	//some padding
		
		generateButton = new JButton("Generate");
		mainFormPanel.add(generateButton, gbc);
		
		//this.pack();	//set window size so that it just fits.
		
	}
	
	/**
	 * adds the close action to the "x" of the window of the {@code MainFrame}. That action is to 
	 * display a dialog prompting the user to confirm that they really want to close the application.
	 */
	private void addCloseAction(){	//done like this to enhance the code readability
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				closeOperation();
			}
		});
	}
	
	/**
	 * Displays the <tt>Yes</tt>/<tt>No</tt> close dialog on top of the {@code MainFrame}.
	 */
	private void closeOperation(){
		Object[] options = {"       Yes       ","       No       "};
		int n = JOptionPane.showOptionDialog(null,
				"Are you sure you want to close the X.509v3 certificate generator?",
				"Close certificate generator",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,
				//null,options,options[0]);	//which option is selected by default
				null,options,options[1]);
		if (n==0){
			System.exit(0);
		}
	}

}
