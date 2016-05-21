package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

/**
 * Dialog for setting the keystore password.
 *
 */
public class KeystoreDialog extends JDialog {

	private static final long serialVersionUID = 1361284372352576724L;
	private final JPanel contentPanel = new JPanel();
	private JPasswordField passwordField;
	private JPasswordField passwordFieldRetype;
	private JLabel lblErrorLabel;
	
	private char[] createdPassword;
	
	/**
	 * Returns the newly created {@code char[]} password from this dialog.
	 * Dialog is modal so this works, do not change modality of the dialog.
	 * 
	 * @return char[]
	 */
	public char[] getPassword() {
		return createdPassword;
	}

	/**
	 * Create the dialog.
	 * Dialog is modal so that returning the created {@code password} could work - do not change modality!
	 * 
	 */
	public KeystoreDialog() {
		// Dialog setup.
		setResizable(false);
		setTitle("Create Keystore");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 528, 155);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[78px][][grow]", "[16px][][]"));
		
		// Components.
		{
			JLabel lblSetPassword = new JLabel("Set password");
			contentPanel.add(lblSetPassword, "cell 0 0,alignx left,aligny top");
		}
		{
			passwordField = new JPasswordField();
			contentPanel.add(passwordField, "cell 2 0,growx");
		}
		{
			JLabel lblRetypePassword = new JLabel("Retype password");
			contentPanel.add(lblRetypePassword, "cell 0 1");
		}
		{
			passwordFieldRetype = new JPasswordField();
			contentPanel.add(passwordFieldRetype, "cell 2 1,growx");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnCreate = new JButton("Create");				
				btnCreate.addActionListener(new ActionListener() {
										
					@Override
					public void actionPerformed(ActionEvent e) {
						
						// Password field validation.
						if(passwordField.getPassword().length == 0 || passwordFieldRetype.getPassword().length == 0) {
							lblErrorLabel.setText("Both password fields are mandatory, please try again.");
							passwordField.setText("");
							passwordFieldRetype.setText("");
						}
						else if(passwordField.getPassword().length != passwordFieldRetype.getPassword().length) {
							lblErrorLabel.setText("Typed passwords do not match, please try again.");
							passwordField.setText("");
							passwordFieldRetype.setText("");
						}
						// Success.
						else if(Arrays.equals(passwordField.getPassword(), passwordFieldRetype.getPassword())) {
							createdPassword = passwordField.getPassword();
							
							// Clean up.
							Arrays.fill(passwordField.getPassword(), '0');
							Arrays.fill(passwordFieldRetype.getPassword(), '0');
							dispose();
						}
					}
				});
				{
					JPanel panel = new JPanel();
					buttonPane.add(panel);
					{
						lblErrorLabel = new JLabel("");
						panel.add(lblErrorLabel);
					}
				}
				
				buttonPane.add(btnCreate);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
