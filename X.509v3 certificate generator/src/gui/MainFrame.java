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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author ILA
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private JPanel mainFormPanel;
	private JButton generateButton;

	private JLabel nameLabel = new JLabel("Name: ");
	private JLabel sernameLabel = new JLabel("Surname: ");
	private JLabel organizationIDLabel = new JLabel("Organization ID: ");
	private JLabel stateLabel = new JLabel("State: ");
	private JLabel localityLabel = new JLabel("Locality: ");
	private JLabel emailLabel = new JLabel("email: ");
	private JLabel yearsActiveLabel = new JLabel("Years active: ");

	private JTextField nameTextField = new JTextField(15);
	private JTextField sernameTextField = new JTextField(15);
	private JTextField organizationTextField = new JTextField(15);
	private JTextField stateTextField = new JTextField(15);
	private JTextField localityTextField = new JTextField(15);
	private JTextField emailTextField = new JTextField(15);
	private JTextField yearActiveTextField = new JTextField(1);

	private MainFrame() {
	}

	private static MainFrame instance = null;

	public static MainFrame getInstance() { // Singleton
		if (instance != null) {
			return instance;
		} else {
			instance = new MainFrame();
			instance.initialize();
			return instance;
		}
	}

	private void initialize() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();	//used to gather details of the display
		Dimension screenDimension = toolkit.getScreenSize();
		
		//this.setSize(screenDimension.width*2/3, screenDimension.height*2/3);
		setTitle("Certificate generator");
		//this.setLocationRelativeTo(null);
		
		addCloseAction();	//pops open a confirmation dialog for closing the application
		
		//GridBag Layout
		mainFormPanel = new JPanel(new GridBagLayout());
		this.getContentPane().add(mainFormPanel);
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		
		gbc.insets.set(5, 5, 5, 5);	//some padding
		
		JLabel header = new JLabel("Placeholder for Header");
		header.setFont(header.getFont().deriveFont(18.0f));
		header.setVisible(true);
		gbc.anchor = GridBagConstraints.CENTER;
		mainFormPanel.add(header, gbc);
		
		gbc.gridwidth = 1;
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.LINE_END;
		
		nameLabel.setVisible(true);
		mainFormPanel.add(nameLabel,gbc);
		
		gbc.gridy++;
		sernameLabel.setVisible(true);
		mainFormPanel.add(sernameLabel, gbc);
		
		gbc.gridy++;
		organizationIDLabel.setVisible(true);
		mainFormPanel.add(organizationIDLabel, gbc);
		
		gbc.gridy++;
		stateLabel.setVisible(true);
		mainFormPanel.add(stateLabel, gbc);
		
		gbc.gridy++;
		localityLabel.setVisible(true);
		mainFormPanel.add(localityLabel, gbc);
		
		gbc.gridy++;
		emailLabel.setVisible(true);
		mainFormPanel.add(emailLabel, gbc);
		
		gbc.gridy++;
		yearsActiveLabel.setVisible(true);
		mainFormPanel.add(yearsActiveLabel,gbc);
		
		
		//next column
		gbc.gridy = 1;	//at the 0th row is the header
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		
		nameTextField.setVisible(true);
		mainFormPanel.add(nameTextField, gbc);
		
		gbc.gridy++;
		sernameTextField.setVisible(true);
		mainFormPanel.add(sernameTextField, gbc);
		
		gbc.gridy++;
		organizationTextField.setVisible(true);
		mainFormPanel.add(organizationTextField, gbc);
		
		gbc.gridy++;
		stateTextField.setVisible(true);
		mainFormPanel.add(stateTextField, gbc);
		
		gbc.gridy++;
		localityTextField.setVisible(true);
		mainFormPanel.add(localityTextField, gbc);
		
		gbc.gridy++;
		emailTextField.setVisible(true);
		mainFormPanel.add(emailTextField, gbc);
		
		gbc.gridy++;
		yearActiveTextField.setVisible(true);
		mainFormPanel.add(yearActiveTextField, gbc);
		
		
		gbc.gridy++;	//so that we are in the last row;
		gbc.anchor = GridBagConstraints.CENTER;	//down and to the right
		//gbc.insets.set(5, 5, 5, 5);	//some padding
		
		generateButton = new JButton("Generate");
		mainFormPanel.add(generateButton, gbc);
		
		this.pack();	//set window size so that it just fits.
		this.setLocationRelativeTo(null);	//front and center
		
	}

	/**
	 * adds the close action to the "x" of the window of the {@code MainFrame}.
	 * That action is to display a dialog prompting the user to confirm that
	 * they really want to close the application.
	 */
	private void addCloseAction() { // done like this to enhance the code
									// readability

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				closeOperation();
			}
		});
	}

	/**
	 * Displays the <tt>Yes</tt>/<tt>No</tt> close dialog on top of the
	 * {@code MainFrame}.
	 */
	private void closeOperation() {
		Object[] options = { "       Yes       ", "       No       " };
		int n = JOptionPane.showOptionDialog(null, "Are you sure you want to close the X.509v3 certificate generator?",
				"Close certificate generator", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				// null,options,options[0]); //which option is selected by
				// default
				null, options, options[1]);
		if (n == 0) {
			System.exit(0);
		}
	}

}
