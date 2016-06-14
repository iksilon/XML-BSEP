package gui;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;
import security.KeyStoreUtils;

public class CAChooseDialog extends JDialog {

	private static final long serialVersionUID = 808185396961107807L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JComboBox<String> comboBox;
	
	private KeyStore issuerKeystore = null;
	
	private Certificate issuerCertificate = null;
	private PrivateKey issuerPK = null;
	
	

	public Certificate getIssuerCertificate() { return issuerCertificate; }
	public PrivateKey getIssuerPK() { return issuerPK; }


	/**
	 * Create the dialog.
	 */
	public CAChooseDialog() {
		setModal(true);
		setBounds(100, 100, 452, 186);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow][]", "[][][][]"));
		
		JLabel lblSelectTheCa = new JLabel("Select the CA");			contentPanel.add(lblSelectTheCa, "cell 0 0");
		JLabel lblKeystore = new JLabel("keystore:");					contentPanel.add(lblKeystore, "cell 0 2,alignx trailing");
		JLabel lblCa = new JLabel("CA:");								contentPanel.add(lblCa, "cell 0 3,alignx trailing");
			
		textField = new JTextField();
		textField.setEditable(false);
		textField.setColumns(10);										contentPanel.add(textField, "cell 1 2,growx");
		
		JButton btnChoose = new JButton("Choose");						contentPanel.add(btnChoose, "cell 2 2");
		comboBox = new JComboBox<String>();								contentPanel.add(comboBox, "cell 1 3,growx");
		// Choose the .jks file from which we'll get issuer.
		btnChoose.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// Set default file chooser directory. Create the dialog.
				String workingDir = System.getProperty("user.dir");
				workingDir = Paths.get(workingDir, "keystores").toString();
				JFileChooser chooser = new JFileChooser(workingDir);
			    FileNameExtensionFilter filter = new FileNameExtensionFilter("Java keystore files", "jks");
			    chooser.setFileFilter(filter);
			    
			    String path = "";
			    		    
			    // User gave up.
			    int returnVal = chooser.showOpenDialog(MainWindow.getInstance());
			    if (returnVal == JFileChooser.CANCEL_OPTION) {
			    	return;
			    }
			    
			    // User approved.
			    if (returnVal == JFileChooser.APPROVE_OPTION) {
			    	// Enter password.
			    	EnterPasswordDialog ksd = new EnterPasswordDialog();
					ksd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
					ksd.setVisible(true);
					
					// After returning from the modal dialog.
					path = chooser.getSelectedFile().getAbsolutePath();
			    	KeyStore loaded = KeyStoreUtils.loadKeyStore(path, ksd.getPassword());
			    	issuerKeystore = loaded;
			    	populateCheckBox(issuerKeystore);
			    	textField.setText(chooser.getSelectedFile().getName());
			    }
				
			}
		});
		
		// Buttons
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String alias = (String)comboBox.getSelectedItem();
					try {
						issuerCertificate = issuerKeystore.getCertificate(alias);
						EnterPasswordDialog epd = new EnterPasswordDialog();
						epd.setDefaultCloseOperation(HIDE_ON_CLOSE);
						epd.setVisible(true);
						issuerPK = (PrivateKey) issuerKeystore.getKey(alias, epd.getPassword());
						// Clean up.
						Arrays.fill(epd.getPassword(), '0');
						dispose();
					} catch (KeyStoreException e1) {
						e1.printStackTrace();
					} catch (UnrecoverableKeyException e1) {
						e1.printStackTrace();
					} catch (NoSuchAlgorithmException e1) {
						e1.printStackTrace();
					}
				}
			});
			okButton.setActionCommand("OK");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);
		}
		{
			JButton cancelButton = new JButton("Cancel");
			cancelButton.setActionCommand("Cancel");
			buttonPane.add(cancelButton);
		}
	}
	
	
	/**
	 * Fills the issuer selection {@link Checkbox} with keys from given {@link KeyStore}.
	 * @param from - {@link KeyStore} from which issuer will be selected 
	 */
	private void populateCheckBox(KeyStore from) {
		// Populate comboBox with aliases from currentKeystore. Only those having both keys.
		Enumeration<String> aliases;
		try {
			aliases = from.aliases();
			while(aliases.hasMoreElements()) {
				String a = aliases.nextElement();
				if(from.isKeyEntry(a)) {
					comboBox.addItem(a);
				}
			}
		} catch (KeyStoreException e2) {
			e2.printStackTrace();
		}
	}

}
