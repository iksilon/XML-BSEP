package gui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.x500.X500Principal;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

import data.IssuerData;
import data.SubjectData;
import net.miginfocom.swing.MigLayout;
import security.CertificateUtils;
import security.KeyStoreUtils;

public class CertificateDialog extends JDialog {
	
	private static final long serialVersionUID = -1203100536862771236L;
	
	private JTextField txtAlias;
	private JPasswordField passField;
	private JPasswordField passwordRetype;
	
	private JTextField txtName;
	private JTextField txtSurname;
	private JTextField txtCN;
	private JTextField txtOU;
	private JTextField txtO;
	private JTextField txtC;
	private JTextField txtE;
	private JFormattedTextField txtValidity;
	private JComboBox<String> comboBox;
	private JCheckBox chckbxSelfSigned;
	
	private IssuerData issuerData;
	private SubjectData subjectData;
	private KeyPair keypair;
	
	private X509Certificate createdCertificate = null;
	private String alias = null;
	
	
	
	/**
	 * Returns the generated certificate from this modal dialog.
	 * @return {@link X509Certificate}
	 */
	public X509Certificate getCertificate() { return createdCertificate; }
	
	/**
	 * Returns created certificate's alias from this modal dialog. 
	 * @return {@link String}
	 */
	public String getAlias() { return alias; }
	
	public boolean isSelfSigned() { return chckbxSelfSigned.isSelected(); }

	/**
	 * Create the dialog.
	 */
	public CertificateDialog(KeyStore currentKeystore) {
		setTitle("Generate Certificate");
		setModal(true);
		setBounds(100, 100, 357, 561);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow][][][grow][grow]"));
		
		// Data
		
		this.issuerData = new IssuerData();
		this.subjectData = new SubjectData();
		this.keypair = null;
		
	// Certificate panel section ------------------------------------------------------------------------
		
		JPanel panelCert = new JPanel();
		getContentPane().add(panelCert, "cell 0 0,grow");
		panelCert.setLayout(new MigLayout("", "[27px][116px,grow]", "[][][][]"));
		
		JLabel lblAlias = new JLabel("Alias");
		panelCert.add(lblAlias, "cell 0 0,alignx left,aligny center");
		
		txtAlias = new JTextField();
		txtAlias.setColumns(10);
		panelCert.add(txtAlias, "cell 1 0,alignx left,aligny top");
		
		JLabel lblPassword = new JLabel("Password");
		panelCert.add(lblPassword, "cell 0 2");
		
		passField = new JPasswordField();
		passField.setColumns(10);
		panelCert.add(passField, "cell 1 2");
		
		JLabel lblRetypePassword = new JLabel("Retype password");
		panelCert.add(lblRetypePassword, "cell 0 3,alignx trailing");
		
		passwordRetype = new JPasswordField();
		passwordRetype.setColumns(10);
		panelCert.add(passwordRetype, "cell 1 3,alignx left");
		
		
	// Issuer panel section ------------------------------------------------------------------------
		
		JPanel panelIssuer = new JPanel();
		getContentPane().add(panelIssuer, "cell 0 2,grow");
		panelIssuer.setLayout(new MigLayout("", "[][grow][right]", "[][][]"));
		
		JLabel lblIssuerData = new JLabel("Issuer Data:");
		lblIssuerData.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelIssuer.add(lblIssuerData, "cell 0 0");
		
		// Label
		
		JLabel lblIssuer = new JLabel("Issuer");
		panelIssuer.add(lblIssuer, "flowy,cell 0 2,alignx leading");
		
		// Fields
		
		chckbxSelfSigned = new JCheckBox("Self Signed");
		chckbxSelfSigned.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				boolean state = ((JCheckBox)e.getSource()).isSelected();
				
				if(state) {
					comboBox.setEnabled(false);
				}
				else {
					comboBox.setEnabled(true);
				}
			}
		});
		panelIssuer.add(chckbxSelfSigned, "cell 0 1");		
		
		comboBox = new JComboBox<String>();
		panelIssuer.add(comboBox, "cell 1 2,growx");
		
			// Populate comboBox with aliases from currentKeystore. Only those having both keys.
			Enumeration<String> aliases;
			try {
				aliases = currentKeystore.aliases();
				while(aliases.hasMoreElements()) {
					String a = aliases.nextElement();
					if(currentKeystore.isKeyEntry(a)) {
						comboBox.addItem(a);
					}
				}
			} catch (KeyStoreException e2) {
				e2.printStackTrace();
			}
		
		
	// Subject panel section -----------------------------------------------------------------------
		
		JPanel panelSubject = new JPanel();
		getContentPane().add(panelSubject, "cell 0 3,grow");
		panelSubject.setLayout(new MigLayout("", "[][][grow]", "[][][][][][][][][][][][][][]"));
		
		JLabel lblSubjectData = new JLabel("Subject Data:");
		lblSubjectData.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelSubject.add(lblSubjectData, "cell 0 0");
		
		// Freakin' labels
		
		JLabel lblName = new JLabel("Name");							panelSubject.add(lblName, "cell 0 1");
		JLabel lblSurname = new JLabel("Surname");						panelSubject.add(lblSurname, "cell 0 2");
		JLabel lblCommonName = new JLabel("Common Name");				panelSubject.add(lblCommonName, "cell 0 3");
		JLabel lblcn = new JLabel("(CN)");								panelSubject.add(lblcn, "cell 1 3,alignx trailing");
		JLabel lblOrganisationUnit = new JLabel("Organisation Unit");	panelSubject.add(lblOrganisationUnit, "cell 0 4");
		JLabel lblou = new JLabel("(OU)");								panelSubject.add(lblou, "cell 1 4,alignx trailing");
		JLabel lblOrganisationName = new JLabel("Organisation Name");	panelSubject.add(lblOrganisationName, "cell 0 5");
		JLabel lblo = new JLabel("(O)");								panelSubject.add(lblo, "cell 1 5,alignx trailing");
		JLabel lblCountry = new JLabel("Country");						panelSubject.add(lblCountry, "cell 0 8");
		JLabel lblc = new JLabel("(C)");								panelSubject.add(lblc, "cell 1 8,alignx trailing");
		JLabel lblEmail = new JLabel("Email");							panelSubject.add(lblEmail, "cell 0 9");
		JLabel lble = new JLabel("(E)");								panelSubject.add(lble, "cell 1 9,alignx trailing");
		JLabel lblValidFormonths = new JLabel("Valid For (months)");	panelSubject.add(lblValidFormonths, "flowy,cell 0 11");
		JLabel lblError = new JLabel("");								panelSubject.add(lblError, "cell 0 12 3 1");
		
		// Fields
		
		txtName = new JTextField();			txtName.setColumns(10);				panelSubject.add(txtName, "cell 2 1,growx");
		txtSurname = new JTextField();		txtSurname.setColumns(10);			panelSubject.add(txtSurname, "cell 2 2,growx");
		txtCN = new JTextField();			txtCN.setColumns(10);				panelSubject.add(txtCN, "cell 2 3,growx");
		txtOU = new JTextField();			txtOU.setColumns(10);				panelSubject.add(txtOU, "cell 2 4,growx");
		txtO = new JTextField();			txtO.setColumns(10);				panelSubject.add(txtO, "cell 2 5,growx");
		txtC = new JTextField();			txtC.setColumns(10);				panelSubject.add(txtC, "cell 2 8,growx");
		txtE = new JTextField();			txtE.setColumns(10);				panelSubject.add(txtE, "cell 2 9,growx");
		
		txtValidity = new JFormattedTextField(NumberFormat.getIntegerInstance());	panelSubject.add(txtValidity, "cell 2 11,growx");
				
		// Buttons section ---------------------------------------------------------------------------------
		
		JPanel panelButtons = new JPanel();
		getContentPane().add(panelButtons, "cell 0 4,grow");
		panelButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnGenerate = new JButton("Generate");
		
		// THE MAIN SHINDIG
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				// Empty validation. I like it compact because definition of insanity is doing the same thing over and over again. Like empty field validation. It's neat.
				
				if(txtName.getText() == null 		|| txtName.getText().equals("")) 		{ lblError.setText("Name field is mandatory, please fill in and try again.");	return;	}
				if(txtSurname.getText() == null 	|| txtSurname.getText().equals("")) 	{ lblError.setText("Surname field is mandatory, please fill in and try again.");	return; }
				if(txtCN.getText() == null 			|| txtCN.getText().equals("")) 			{ lblError.setText("Common name field is mandatory, please fill in and try again.");	return;	}
				if(txtO.getText() == null 			|| txtO.getText().equals("")) 			{ lblError.setText("Organization field is mandatory, please fill in and try again.");	return;	}
				if(txtOU.getText() == null 			|| txtOU.getText().equals("")) 			{ lblError.setText("Organization unit field is mandatory, please fill in and try again.");	return;	}
				if(txtC.getText() == null 			|| txtC.getText().equals("")) 			{ lblError.setText("Country field is mandatory, please fill in and try again.");	return;	}
				if(txtE.getText() == null 			|| txtE.getText().equals("")) 			{ lblError.setText("Email field is mandatory, please fill in and try again.");	return; }
				if(txtValidity.getText() == null 	|| txtValidity.getText().equals("")) 	{ lblError.setText("Validity field is mandatory, please fill in and try again.");	return;	}				
				if(txtAlias.getText() == null 		|| txtAlias.getText().equals("")) 		{ lblError.setText("Alias field is mandatory, please fill in and try again.");	return; }
				if(passField.getPassword() == null 	|| passField.getPassword().equals("")) 	{ lblError.setText("Password field is mandatory, please fill in and try again.");	return;	}
				
				// Value validation.
				
				int val = Integer.parseInt(txtValidity.getText());
				if(val <= 0) {
					lblError.setText("Validity must be 1 month or more, please fill in and try again.");
					return;
				}
				
				String emailRE = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
				Pattern p = Pattern.compile(emailRE);
				Matcher m = p.matcher(txtE.getText());
				if(!m.matches()) {
					lblError.setText("Email does not match correct format, please try again.");
					return;
				}
				
				// Password validation.
				
				if(passField.getPassword() == null || passwordRetype.getPassword() == null) {
					lblError.setText("Both password fields are mandatory, please try again.");
					passField.setText("");
					passwordRetype.setText("");
					return;
				}
				else if(passField.getPassword().length == 0 || passwordRetype.getPassword().length == 0) {
					lblError.setText("Both password fields are mandatory, please try again.");
					passField.setText("");
					passwordRetype.setText("");
					return;
				}
				else if(!Arrays.equals(passField.getPassword(), passwordRetype.getPassword())) {
					lblError.setText("Typed passwords do not match, please try again.");
					passField.setText("");
					passwordRetype.setText("");
					return;
				}
				
		// Generate certificate --------------------------------------------------------------------------------------
				alias = txtAlias.getText();
				
			// Subject info.
				
				X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
				builder.addRDN(BCStyle.GIVENNAME,	txtName.getText());
			    builder.addRDN(BCStyle.SURNAME, 	txtSurname.getText());
			    builder.addRDN(BCStyle.CN, 			txtCN.getText());
			    builder.addRDN(BCStyle.O, 			txtO.getText());
			    builder.addRDN(BCStyle.OU,			txtOU.getText());
			    builder.addRDN(BCStyle.C,			txtC.getText());
			    builder.addRDN(BCStyle.E, 			txtE.getText());
			    // TODO: Question: What in the name of Y'ffre is this?
			    builder.addRDN(BCStyle.UID, "123445");
			    
			    Calendar startDate = Calendar.getInstance();
			    Calendar expireDate = Calendar.getInstance();
			    
			    // NOTE: Months are an enumeration and start from 0 (January)
			    int month = expireDate.get(Calendar.MONTH);
			    int year = expireDate.get(Calendar.YEAR);
			    
			    int totalMonth = month + Integer.parseInt(txtValidity.getText());
			    if(totalMonth > 11) {
			    	year += totalMonth / 11;
			    	totalMonth = totalMonth % 11;
			    }
			    
			    expireDate.set(Calendar.MONTH, totalMonth);
			    expireDate.set(Calendar.YEAR, year);
			    
			    String se = String.valueOf(this.hashCode());
			    se = se.concat(String.valueOf(Calendar.getInstance().getTimeInMillis()));
			    
			    // This will be subject's key pair.
			    keypair = CertificateUtils.generateKeyPair();
			    
			// Issuer info.
			    
			    // Issuer's private key. If it's self signed, then it's from the keypair we just generated.
			    if(chckbxSelfSigned.isSelected()) {
			    	issuerData.setPrivateKey(keypair.getPrivate());
			    	issuerData.setPublicKey(keypair.getPublic());
			    	issuerData.setX500name(builder.build());
			    }
			    // Get private key and certificate from the keystore using the chosen alias. They are chosen to sign us.
			    else {
			    	EnterPasswordDialog epd = new EnterPasswordDialog();
			    	epd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
					epd.setVisible(true);
					
					// After returning from the dialog.
					try {
						// Issuer PK
						PrivateKey pk = (PrivateKey) currentKeystore.getKey((String)comboBox.getSelectedItem(), epd.getPassword());
						X509Certificate issuerCert = (X509Certificate) currentKeystore.getCertificate((String)comboBox.getSelectedItem());
						Arrays.fill(epd.getPassword(), '0');
						issuerData.setPrivateKey(pk);
						
						issuerData.setPublicKey(issuerCert.getPublicKey());
						
						// Issuer name
						X500Principal prnc = issuerCert.getSubjectX500Principal();
						X500Name name = X500Name.getInstance(prnc.getEncoded());
						issuerData.setX500name(name);
						
						// Check if parent's certificate expires before entered end date.
						Calendar icStart = Calendar.getInstance();
						icStart.setTime(issuerCert.getNotBefore());
						Calendar icEnd = Calendar.getInstance();
						icEnd.setTime(issuerCert.getNotAfter());
						
						// TODO: Z:Minor: Show users when it's the end time.
						if(startDate.compareTo(icStart) < 0 ) {
							JOptionPane.showMessageDialog(MainWindow.getInstance(), 
									"Invalid start date: certificate can only be valid since parent certificate start date.");
							return;
						}
						else if(expireDate.compareTo(icEnd) >= 0) {
							JOptionPane.showMessageDialog(MainWindow.getInstance(), 
									"Invalid end date: certificate can only be valid until parent certificate end date.");
							return;
						}
						
					} catch (UnrecoverableKeyException e) {
						JOptionPane.showMessageDialog(MainWindow.getInstance(), "Wrong password, please try again.");
						e.printStackTrace();
						return;
					} catch (KeyStoreException e) {
						e.printStackTrace();
						return;
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
						return;
					}
			    }
			    
			// Subject info. Again.
			    
			    subjectData.setStartDate(startDate.getTime());
			    subjectData.setEndDate(expireDate.getTime());
			    subjectData.setSerialNumber(se);
			    subjectData.setX500name(builder.build());
			    subjectData.setPublicKey(keypair.getPublic());
			    
			// The generating part. (Get it? De-generating, the-generating)
			    
			    createdCertificate = CertificateUtils.generateCertificate(issuerData, subjectData);
			    try {
					createdCertificate.verify(issuerData.getPublicKey());
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				} catch (CertificateException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (NoSuchProviderException e) {
					e.printStackTrace();
				} catch (SignatureException e) {
					e.printStackTrace();
				}
			    
			    KeyStoreUtils.insertCertificate(currentKeystore, alias, createdCertificate);
				KeyStoreUtils.insertKey(currentKeystore, alias, keypair.getPrivate(), passField.getPassword(), createdCertificate);
			    
			    // Clean up.
			    Arrays.fill(passField.getPassword(), '0');
			    Arrays.fill(passwordRetype.getPassword(), '0');
			    
			    System.out.println("New certificate generated : " + alias);
			    System.out.println(createdCertificate);
			    dispose();
			}
		});
		panelButtons.add(btnGenerate);
		
		JButton btnCancel = new JButton("Cancel");
		panelButtons.add(btnCancel);

	}

}
