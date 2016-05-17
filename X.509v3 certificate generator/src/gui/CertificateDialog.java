package gui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

import data.IssuerData;
import data.SubjectData;
import net.miginfocom.swing.MigLayout;
import security.CertificateUtils;
import javax.swing.JPasswordField;

public class CertificateDialog extends JDialog {
	
	private static final long serialVersionUID = -1203100536862771236L;
	
	private JTextField txtAlias;
	private JPasswordField passwordField;
	
	private JTextField txtName;
	private JTextField txtSurname;
	private JTextField txtCN;
	private JTextField txtOU;
	private JTextField txtO;
	private JTextField txtC;
	private JTextField txtE;
	private JTextField txtValidity;
	private JComboBox<IssuerData> comboBox;
	private JCheckBox chckbxSelfSigned;
	
	private IssuerData issuerData;
	private SubjectData subjectData;
	private KeyPair keypair;
	
	private X509Certificate createdCertificate = null;
	private String alias = null;
	private char[] password;
	
	
	/**
	 * Returns the generated certificate from this modal dialog.
	 * @return {@link X509Certificate}
	 */
	public X509Certificate getCertificate() {
		return createdCertificate;
	}
	
	/**
	 * Returns the defined password.
	 * @return char[]
	 */
	public char[] getPassword() {
		return password;
	}
	
	/**
	 * Returns created certificate's alias from this modal dialog. 
	 * @return {@link String}
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Create the dialog.
	 */
	public CertificateDialog(IssuerData issuer, SubjectData subject, KeyPair kp) {
		setTitle("Generate Certificate");
		setModal(true);
		setBounds(100, 100, 355, 498);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow][][][grow][grow]"));
		
		this.issuerData = issuer;
		this.subjectData = subject;
		this.keypair = kp;
		
		JPanel panelCert = new JPanel();
		getContentPane().add(panelCert, "cell 0 0,grow");
		panelCert.setLayout(new MigLayout("", "[27px][116px,grow]", "[][][22px]"));
		
		JLabel lblAlias = new JLabel("Alias");
		panelCert.add(lblAlias, "cell 0 0,alignx left,aligny center");
		
		txtAlias = new JTextField();
		panelCert.add(txtAlias, "cell 1 0,alignx left,aligny top");
		txtAlias.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		panelCert.add(lblPassword, "cell 0 2,alignx trailing");
		
		passwordField = new JPasswordField();
		passwordField.setColumns(10);
		panelCert.add(passwordField, "cell 1 2,alignx left");
		
		
	// Issuer data section ------------------------------------------------------------------------
		
		JPanel panelIssuer = new JPanel();
		getContentPane().add(panelIssuer, "cell 0 2,grow");
		panelIssuer.setLayout(new MigLayout("", "[][grow]", "[][][]"));
		
		// Labels
		
		JLabel lblIssuerData = new JLabel("Issuer Data:");
		lblIssuerData.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelIssuer.add(lblIssuerData, "cell 0 0");
		
		JLabel lblIssuer = new JLabel("Issuer");
		panelIssuer.add(lblIssuer, "flowy,cell 0 2");
		
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
		
		comboBox = new JComboBox<IssuerData>();
		//TODO: Replace comboBox with file opener.
		panelIssuer.add(comboBox, "cell 1 2,growx");
		
		
		
	// Subject data section -----------------------------------------------------------------------
		
		JPanel panelSubject = new JPanel();
		getContentPane().add(panelSubject, "cell 0 3,grow");
		panelSubject.setLayout(new MigLayout("", "[][][grow]", "[][][][][][][][][][][][]"));
		
		// Labels
		
		JLabel lblSubjectData = new JLabel("Subject Data:");
		lblSubjectData.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelSubject.add(lblSubjectData, "cell 0 0");
		
		JLabel lblName = new JLabel("Name");
		panelSubject.add(lblName, "cell 0 1");
		
		JLabel lblSurname = new JLabel("Surname");
		panelSubject.add(lblSurname, "cell 0 2");
		
		JLabel lblCommonName = new JLabel("Common Name");
		panelSubject.add(lblCommonName, "cell 0 3");
		
		JLabel lblcn = new JLabel("(CN)");
		panelSubject.add(lblcn, "cell 1 3,alignx trailing");
		
		JLabel lblOrganisationUnit = new JLabel("Organisation Unit");
		panelSubject.add(lblOrganisationUnit, "cell 0 4");
		
		JLabel lblou = new JLabel("(OU)");
		panelSubject.add(lblou, "cell 1 4,alignx trailing");
		
		JLabel lblOrganisationName = new JLabel("Organisation Name");
		panelSubject.add(lblOrganisationName, "cell 0 5");
		
		JLabel lblo = new JLabel("(O)");
		panelSubject.add(lblo, "cell 1 5,alignx trailing");
		
		JLabel lblCountry = new JLabel("Country");
		panelSubject.add(lblCountry, "cell 0 8");
		
		JLabel lblc = new JLabel("(C)");
		panelSubject.add(lblc, "cell 1 8,alignx trailing");

		JLabel lblEmail = new JLabel("Email");
		panelSubject.add(lblEmail, "cell 0 9");
		
		JLabel lble = new JLabel("(E)");
		panelSubject.add(lble, "cell 1 9,alignx trailing");
		
		JLabel lblValidFormonths = new JLabel("Valid For (months)");
		panelSubject.add(lblValidFormonths, "cell 0 11");
		
		// Fields
		
		txtName = new JTextField();
		panelSubject.add(txtName, "cell 2 1,growx");
		txtName.setColumns(10);
		
		txtSurname = new JTextField();
		panelSubject.add(txtSurname, "cell 2 2,growx");
		txtSurname.setColumns(10);
		
		txtCN = new JTextField();
		panelSubject.add(txtCN, "cell 2 3,growx");
		txtCN.setColumns(10);
		
		txtOU = new JTextField();
		panelSubject.add(txtOU, "cell 2 4,growx");
		txtOU.setColumns(10);
				
		txtO = new JTextField();
		panelSubject.add(txtO, "cell 2 5,growx");
		txtO.setColumns(10);
				
		txtC = new JTextField();
		panelSubject.add(txtC, "cell 2 8,growx");
		txtC.setColumns(10);
		
		txtE = new JTextField();
		panelSubject.add(txtE, "cell 2 9,growx");
		txtE.setColumns(10);
		
		txtValidity = new JTextField();
		panelSubject.add(txtValidity, "cell 2 11,growx");
		txtValidity.setColumns(10);
		
		
		
		// Buttons section ---------------------------------------------------------------------------------
		
		JPanel panelButtons = new JPanel();
		getContentPane().add(panelButtons, "cell 0 4,grow");
		panelButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO: Field validation.
				
				X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
				builder.addRDN(BCStyle.GIVENNAME,	txtName.getText());
			    builder.addRDN(BCStyle.SURNAME, 	txtSurname.getText());
			    builder.addRDN(BCStyle.CN, 			txtCN.getText());
			    builder.addRDN(BCStyle.O, 			txtO.getText());
			    builder.addRDN(BCStyle.OU,			txtOU.getText());
			    builder.addRDN(BCStyle.C,			txtC.getText());
			    builder.addRDN(BCStyle.E, 			txtE.getText());
			    // TODO: Set when there are users. Is this receiver ID?
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
			    
			    // Subject
			    subject.setStartDate(startDate.getTime());
			    subject.setEndDate(expireDate.getTime());
			    subject.setSerialNumber(se);
			    subject.setX500name(builder.build());
			    subject.setPublicKey(keypair.getPublic());
			    
			    // Issuer
			    if(chckbxSelfSigned.isSelected()) {
			    	issuer.setPrivateKey(keypair.getPrivate());
			    	issuer.setX500name(builder.build());
			    }
			    else {
			    	// TODO: Open file for issuer.
			    }
			    
			    createdCertificate = CertificateUtils.generateCertificate(issuerData, subjectData);
			    alias = txtAlias.getText();
			    password = passwordField.getPassword();
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