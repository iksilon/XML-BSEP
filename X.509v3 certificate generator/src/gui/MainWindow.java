package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import net.miginfocom.swing.MigLayout;
import security.CRLUtils;
import security.CertificateUtils;
import security.KeyStoreUtils;

/**
 * Main view of the application, extension of the {@link JFrame} class.
 * This window shows a keystore file with all it's certificates.
 * User can create new keystores and add keypairs and certificates to it either by creating them or by importing them from file.
 * Certificates and keystores can then be exported/saved to file.
 * Only one keystore is showed at a time.
 * 
 */
public class MainWindow extends JFrame {
	// Registracija providera
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	private static final long serialVersionUID = 1198734643308937757L;
	
	private static MainWindow instance = null;
	private KeyStore currentKeystore = null;
	private String currentPath = "";
	
	private KeypairTable keypairTable;
	private JTextField txtCurrentKeystore;
	private JLabel lblCurrentKeystore = new JLabel("Current keystore:");
	
	private JPanel contentPane;
	private final Action actKeystore = new ActionKeystore();
	private final Action actKeypair = new ActionKeypair();
	private final Action actOpen = new ActionOpen();
	private final Action actSave = new ActionSave();
	private final Action actSaveAs = new ActionSaveAs();
	private final Action actExit = new ActionExit();
	private final Action actExportCertificate = new ActionExportCertificate();
	private final Action actExportAll = new ActionExportAll();
	private final Action actImportCertificate = new ActionImportCertificate();
	private final Action actCRL = new ActionCRL();
	
	// Main ---------------------------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	// Singleton ----------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Implementation of Singleton pattern.
	 * Returns the instance of the {@code MainWindow}.
	 * 
	 * @return {@link MainWindow}
	 */
	public static MainWindow getInstance() {
		if(instance == null) {
	         instance = new MainWindow();
	      }
	      return instance;
	}

	/**
	 * Create the frame.
	 */
	protected MainWindow() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setTitle("CerGen");
		
		// Exit prompt ------------------------------------------------------------------------
		
		this.addWindowListener(new WindowAdapter() {
			
			// TODO: Z:Minor: Check if the keystore has unsaved changes. Add a boolean for that.

			@Override
			public void windowClosing(WindowEvent e) {
				String ObjButtons[] = {"Yes","No"};
		        int PromptResult = 
		        		JOptionPane.showOptionDialog(null,
		        									"Are you sure you want to exit?",
		        									"Leaving CerGen",
		        									JOptionPane.DEFAULT_OPTION,
		        									JOptionPane.WARNING_MESSAGE,
		        									null,
		        									ObjButtons,
		        									ObjButtons[1]);
		        if(PromptResult == JOptionPane.YES_OPTION) {
		            System.exit(0);
		        }
			}
			
		});
		
		// MenuBar ------------------------------------------------------------------------
		
		JMenuBar menuBar = new JMenuBar();		setJMenuBar(menuBar);
			JMenu mnFile = new JMenu("File");										menuBar.add(mnFile);
			JMenu mntmNew = new JMenu("New");										mnFile.add(mntmNew);
			JMenuItem mntmKeystore = mntmNew.add(actKeystore);						mntmKeystore.setText("Keystore");
			JMenuItem mntmKeypair = mntmNew.add(actKeypair);						mntmKeypair.setText("Keypair");
						
						JMenuItem mntmCRL = mntmNew.add(actCRL);
						mntmCRL.setText("CRL");
			JMenuItem mntmOpen = mnFile.add(actOpen);								mntmOpen.setText("Open");
			
			JSeparator sepFile1 = new JSeparator();									mnFile.add(sepFile1);
		
			JMenuItem mntmSave = mnFile.add(actSave);								mntmSave.setText("Save");
			JMenuItem mntmSaveAs = mnFile.add(actSaveAs);							mntmSaveAs.setText("Save as...");
			
			JSeparator sepFile2 = new JSeparator();									mnFile.add(sepFile2);
		
			JMenuItem mntmExit = mnFile.add(actExit);								mntmExit.setText("Exit");
		
		JMenu mnTools = new JMenu("Tools");		menuBar.add(mnTools);
			JMenuItem mntmExportCertificate = mnTools.add(actExportCertificate);	mntmExportCertificate.setText("Export Certificate");
			JMenuItem mntmExportAll = mnTools.add(actExportAll);					mntmExportAll.setText("Export All");
		
			JSeparator separator = new JSeparator();								mnTools.add(separator);
		
			JMenuItem mntmImportCertificate = mnTools.add(actImportCertificate);	mntmImportCertificate.setText("Import Certificate");
		
		// Components ------------------------------------------------------------------------
			
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		keypairTable = new KeypairTable();
		keypairTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(keypairTable);
		
		// Double click on table row to see certificate details.
		keypairTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
					String v = (String) keypairTable.getValueAt(keypairTable.getSelectedRow(), 1);
					
					try {
						Certificate cert = currentKeystore.getCertificate(v);
						if(cert != null) {
							ShowCertDialog scd = new ShowCertDialog();
							scd.setTitle("Certificate: " + v);
							scd.setCertificateText(cert.toString());
							scd.setVisible(true);
						}
						else {
							JOptionPane.showMessageDialog(MainWindow.getInstance(), 
									"There is no such certificate.");
						}
					} catch (KeyStoreException e1) {
						e1.printStackTrace();
					}
				}
			}
			
		});
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new MigLayout("", "[100px][grow,fill]", "[22px]"));
		
			lblCurrentKeystore = new JLabel("Current keystore:");
			panel.add(lblCurrentKeystore, "cell 0 0,alignx trailing,aligny center");
			
			txtCurrentKeystore = new JTextField();
			txtCurrentKeystore.setEditable(false);
			txtCurrentKeystore.setColumns(10);
			txtCurrentKeystore.setText("");
			panel.add(txtCurrentKeystore, "cell 1 0,growx");
	}
	
// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
// Actions section because I couldn't be bothered to move them to separate files.
// Also, nothing works via getInstance() so there you have it.
// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
// Keystore stuff ------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Creates a new keystore.
	 * Opens a dialog for defining keystore password.
	 */
	private class ActionKeystore extends AbstractAction {
		private static final long serialVersionUID = 425412543121784713L;
		public ActionKeystore() {
			putValue(NAME, "Keystore");
			putValue(SHORT_DESCRIPTION, "Create new keystore");
		}
		public void actionPerformed(ActionEvent e) {
			// Placeholder password. Will be set when keystore is saved to file.			
			currentKeystore = KeyStoreUtils.loadKeyStore(null, "placeholder".toCharArray());
			txtCurrentKeystore.setText("New keystore");
			lblCurrentKeystore.setText("*Current keystore:");
		}
	}
	
	/**
	 * Saves the current keystore into file.
	 * If it's a new keystore, {@link ActionSaveAs} is called.
	 *
	 */
	private class ActionSave extends AbstractAction {
		private static final long serialVersionUID = -4641089031850059072L;
		public ActionSave() {
			putValue(NAME, "Save");
			putValue(SHORT_DESCRIPTION, "Save keystore");
		}
		public void actionPerformed(ActionEvent e) {
			// Is there a keystore at all?
			if(currentKeystore == null) {
				JOptionPane.showMessageDialog(MainWindow.getInstance(),
						"There is no active keystore to be saved. Please create or open a keystore first.");
				return;
			}
			
			// This is a new keystore - call SaveAs.
			if(currentPath.equals("")) {
				actSaveAs.actionPerformed(null);
				return;
			}
			// Existing. Overwrite the file.
			else {	
				// Set password for keystore. This is needed because keeping password in memory is bad and saving needs a password.
		    	SetPasswordDialog ksd = new SetPasswordDialog();
				ksd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
				ksd.setVisible(true);
				
				// After returning from the modal dialog.					
				KeyStoreUtils.saveKeyStore(currentKeystore, currentPath, ksd.getPassword());
				Arrays.fill(ksd.getPassword(), '0');
				ksd.dispose();
				txtCurrentKeystore.setText(currentPath);
				lblCurrentKeystore.setText("Current keystore:");
			}
		}
	}
	
	/**
	 * Opens a file choosing dialog to select the path where the keystore will be saved.
	 *
	 */
	private class ActionSaveAs extends AbstractAction {
		private static final long serialVersionUID = 3925404848236570471L;
		public ActionSaveAs() {
			putValue(NAME, "Save As");
			putValue(SHORT_DESCRIPTION, "Save keystore to a file");
		}
		public void actionPerformed(ActionEvent e) {			
			// Is there a keystore at all?
			if(currentKeystore == null) {
				JOptionPane.showMessageDialog(MainWindow.getInstance(),
						"There is no active keystore to be saved. Please create or open a keystore first.");
				return;
			}
			
			// Set default file chooser directory. Create the dialog.
			String workingDir = System.getProperty("user.dir");
			workingDir = Paths.get(workingDir, "certificates").toString();
			JFileChooser chooser = new JFileChooser(workingDir);
		    FileNameExtensionFilter filter = new FileNameExtensionFilter("Java keystore files", "jks");
		    chooser.setFileFilter(filter);
		    
		    String path = "";
		    		    
		    // User gave up.
		    int returnVal = chooser.showSaveDialog(MainWindow.getInstance());
		    if (returnVal == JFileChooser.CANCEL_OPTION) {
		    	return;
		    }
		    
		    // User approved.
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		    	// TODO: Z:Minor: Prevent unsupported chars in filename by making a key press listener.
		    	
		    	// Check if user forgot file extension or got it wrong.
		        path = chooser.getSelectedFile().getAbsolutePath();
		        if(!path.endsWith(".jks")) {
		        	path = path.concat(".jks");
		        }
		        
		        // Does this file already exist? Overwrite it?
			    File f = new File(path);
			    if(f.isFile()) {
			    	String ObjButtons[] = {"Yes","No"};
			        int PromptResult = 
			        		JOptionPane.showOptionDialog(null,
			        									"Selected file already exists, would you like to overwrite existing file?",
			        									"Overwrite existing file",
			        									JOptionPane.DEFAULT_OPTION,
			        									JOptionPane.WARNING_MESSAGE,
			        									null,
			        									ObjButtons,
			        									ObjButtons[1]);
			        // Yep, overwrite.
			        if(PromptResult == JOptionPane.YES_OPTION)
			        {
			        	// Set password for keystore.
				    	SetPasswordDialog ksd = new SetPasswordDialog();
						ksd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
						ksd.setVisible(true);
						
						// After returning from the modal dialog.					
						KeyStoreUtils.saveKeyStore(currentKeystore, path, ksd.getPassword());
				    	currentPath = path;
				    	txtCurrentKeystore.setText(currentPath);
				    	lblCurrentKeystore.setText("Current keystore:");
				    	// Clean up.
				    	Arrays.fill(ksd.getPassword(), '0');
						ksd.dispose();
			        }
			        
			    }
			    // This is a new file.
			    else {
			    	// Set password for keystore.
			    	SetPasswordDialog ksd = new SetPasswordDialog();
					ksd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
					ksd.setVisible(true);
					
					// After returning from the modal dialog.					
					KeyStoreUtils.saveKeyStore(currentKeystore, path, ksd.getPassword());
			    	currentPath = path;
			    	txtCurrentKeystore.setText(currentPath);
			    	lblCurrentKeystore.setText("Current keystore:");
			    	// Clean up.
			    	Arrays.fill(ksd.getPassword(), '0');
					ksd.dispose();
			    }
		    }
		    
		    
		}
	}
	
	/**
	 * Opens a file choosing dialog to select the path which keystore will be opened.
	 *
	 */
	private class ActionOpen extends AbstractAction {
		private static final long serialVersionUID = 340823143919984037L;
		public ActionOpen() {
			putValue(NAME, "Open");
			putValue(SHORT_DESCRIPTION, "Open a keystore file");
		}
		public void actionPerformed(ActionEvent e) {			
			// Set default file chooser directory. Create the dialog.
			String workingDir = System.getProperty("user.dir");
			workingDir = Paths.get(workingDir, "certificates").toString();
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
		    	currentKeystore = loaded;
		    	
		    	currentPath = path;
		    	txtCurrentKeystore.setText(currentPath);
		    	// Clean up.
		    	Arrays.fill(ksd.getPassword(), '0');
		    	ksd.dispose();
		    	
		    	// Populate the table.
		    	Enumeration<String> aliases;
				try {
					aliases = currentKeystore.aliases();					
					System.out.println("Aliases:");
					while(aliases.hasMoreElements()) {
						String a = aliases.nextElement();
						System.out.println(a);
						int rows = ((DefaultTableModel)keypairTable.getModel()).getRowCount();
						((DefaultTableModel)keypairTable.getModel()).addRow(new Object[]{rows+1, a});
					}
					
				} catch (KeyStoreException e1) {
					e1.printStackTrace();
				}
		    }
		    
		}
	}
	
// Certificate stuff --------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Generates a keypair.
	 * Prompts the user to fill in certificate details and generates the certificate.
	 */
	private class ActionKeypair extends AbstractAction {
		private static final long serialVersionUID = -1411136323257319945L;
		public ActionKeypair() {
			putValue(NAME, "Keypair");
			putValue(SHORT_DESCRIPTION, "Generate new keypair");
		}
		public void actionPerformed(ActionEvent e) {
			// Is there a KeyStore to which this will be stored?
			if(currentKeystore == null) {
				JOptionPane.showMessageDialog(MainWindow.getInstance(),
						"A keystore is needed to create a keypair. Please create a keystore first. "
						+ "You can do this by going to the File menu, selecting New and then Keystore");
				return;
			}
			
			// Open dialog for all certificate data.
			CertificateDialog cd = new CertificateDialog(currentKeystore);
			cd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			cd.setVisible(true);
			
			// After returning from the modal dialog.
			if (cd.getCertificate() != null) {					
				// Update view.
				int rows = ((DefaultTableModel)keypairTable.getModel()).getRowCount();
				((DefaultTableModel)keypairTable.getModel()).addRow(new Object[]{rows+1, cd.getAlias()});
				
		    	lblCurrentKeystore.setText("*Current keystore:");
			}
			cd.dispose();
		}
	}
	
	/**
	 * 
	 * Exports the certificate into specified file and encoding.
	 *
	 */
	private class ActionExportCertificate extends AbstractAction {
		private static final long serialVersionUID = -1698079888963949279L;
		public ActionExportCertificate() {
			putValue(NAME, "Export Certificate");
			putValue(SHORT_DESCRIPTION, "Export keypair to a certificate file");
		}
		
		/**
		 * Saves the given {@link Certificate} to the specified {@code path} based on the type of the file (file extension {@code ex}).
		 * If the encoding is not specified (.cer or .crt), user will be prompted to choose encoding type.
		 * 
		 * @param path {@link String}
		 * @param cert {@link Certificate}
		 * @param ex {@link String}
		 */
		private void saveFile(String path, Certificate cert, String ex) {
			System.out.println(ex);
			
			switch (ex) {
			case "cer":
			case "crt":
				// Ask which encoding is to be used. A misuse of Y/N dialog, I know, I don't care.
				Object[] options = {"PEM", "DER"};
				int n = JOptionPane.showOptionDialog(MainWindow.getInstance(),
				    "Which encoding you want to use?",
				    "Certificate encoding",
				    JOptionPane.YES_NO_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    null,
				    options,
				    options[0]);
				
				if(n == 0) {
					CertificateUtils.savePEMfile(path, cert);
				}
				else {
					CertificateUtils.saveDERfile(path, cert);
				}
				
				break;
			case "der":
				CertificateUtils.saveDERfile(path, cert);
				break;
			case "pem":
				CertificateUtils.savePEMfile(path, cert);
				break;
			default:
				break;
			}
		}
		
		public void actionPerformed(ActionEvent e) {			
			// Is anything selected?
			if(keypairTable.getSelectedRow() == -1) {
				JOptionPane.showMessageDialog(MainWindow.getInstance(),
						"No row in table is selected, certificate cannot be exported."
						+ "Please select the certificate first and try again.");
				return;
			}
			// Yes, it is.
			else {
				String alias = keypairTable.getValueAt(keypairTable.getSelectedRow(), 1).toString();
				try {
					Certificate cert = currentKeystore.getCertificate(alias);
					
					// Set default file chooser directory. Create the dialog.
					String workingDir = System.getProperty("user.dir");
					workingDir = Paths.get(workingDir, "certificates").toString();
					JFileChooser chooser = new JFileChooser(workingDir);
				    FileNameExtensionFilter filterDef = new FileNameExtensionFilter("Certificate files", "cer", "crt");
				    chooser.setFileFilter(filterDef);
				    FileNameExtensionFilter filterPEM = new FileNameExtensionFilter("PEM encoded certificate files", "pem");
				    FileNameExtensionFilter filterDER = new FileNameExtensionFilter("DER encoded certificate files", "der");
				    chooser.addChoosableFileFilter(filterPEM);
				    chooser.addChoosableFileFilter(filterDER);
				    
				    // User gave up.
				    int returnVal = chooser.showSaveDialog(MainWindow.getInstance());
				    if (returnVal == JFileChooser.CANCEL_OPTION) {
				    	return;
				    }
				    
				    // User approved.
				    if (returnVal == JFileChooser.APPROVE_OPTION) {
				    	FileNameExtensionFilter ff = (FileNameExtensionFilter) chooser.getFileFilter();
				    	String path = chooser.getSelectedFile().getAbsolutePath();
				    	String[] exts = ff.getExtensions();
				    	
				    	// Find extension match
				    	for (String ex : exts) {
							if(path.endsWith("." + ex)) {
								// Match found, save file.
								saveFile(path, cert, ex);
								return;
							}
						}
				    	
				    	// No match, add the extension and save file.
				    	path = path.concat("." + exts[0]);
				    	saveFile(path, cert, exts[0]);
				    }
					
				} catch (KeyStoreException e1) {
					e1.printStackTrace();
				}				
			}
			
		}
	}
	
	private class ActionExportAll extends AbstractAction {
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
	
	/**
	 * Opens a certificate from a file and adds it to the current keystore.
	 * File chooser gives possible file extension filters but this is only for UX,
	 * extension and encoding are checked nevertheless. 
	 *
	 */
	private class ActionImportCertificate extends AbstractAction {
		private static final long serialVersionUID = 4384732596786044097L;
		public ActionImportCertificate() {
			putValue(NAME, "Export All");
			putValue(SHORT_DESCRIPTION, "Export all certificates to specified folder.");
		}
		
		public void actionPerformed(ActionEvent e) {			
			// Set default file chooser directory. Create the dialog.
			String workingDir = System.getProperty("user.dir");
			workingDir = Paths.get(workingDir, "certificates").toString();
			JFileChooser chooser = new JFileChooser(workingDir);
		    FileNameExtensionFilter filterDef = new FileNameExtensionFilter("Certificate files", "cer", "crt");
		    chooser.setFileFilter(filterDef);
		    FileNameExtensionFilter filterPEM = new FileNameExtensionFilter("PEM encoded certificate files", "pem");
		    FileNameExtensionFilter filterDER = new FileNameExtensionFilter("DER encoded certificate files", "der");
		    chooser.addChoosableFileFilter(filterPEM);
		    chooser.addChoosableFileFilter(filterDER);
		    
		    // User gave up.
		    int returnVal = chooser.showOpenDialog(MainWindow.getInstance());
		    if (returnVal == JFileChooser.CANCEL_OPTION) {
		    	return;
		    }
		    
		    // User approved.
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		    	String path = chooser.getSelectedFile().getAbsolutePath();
		    	String[] exts = {".cer", ".crt", ".der", ".pem"};
		    	
		    	// Find extension match
		    	for (String ex : exts) {
					if(path.endsWith(ex)) {
						// Match found, set alias.
						String alias = JOptionPane.showInputDialog(MainWindow.getInstance(), 
					    		"Please enter certificate alias:");
					    if(alias == null || alias.equals("")) {
					    	JOptionPane.showMessageDialog(MainWindow.getInstance(), 
					    			"That is not a valid alias.");
					    	return;
					    }
						
						// Open file.
						Certificate c = CertificateUtils.openFile(path, ex);
						if(c == null) {
							return;
						}
						
						// Yay, let's present this mofo.
						try {
							currentKeystore.setCertificateEntry(alias, c);
							int rows = ((DefaultTableModel)keypairTable.getModel()).getRowCount();
							((DefaultTableModel)keypairTable.getModel()).addRow(new Object[]{rows+1, alias});
							//TODO: Z:Minor: Have some kind of check in the table whether certificate has a private key available.
							lblCurrentKeystore.setText("*Current keystore:");
						} catch (KeyStoreException e1) {
							e1.printStackTrace();
						}
						
						return;
					}
				}
		    	
		    	// No match, alert the user.
		    	JOptionPane.showMessageDialog(MainWindow.getInstance(),
		    			"Selected file type is not supported.");
		    }
		}
	}
	
	/**
	 * Creates a dialog for new Certificate revocation list (CRL)
	 *
	 */
	private class ActionCRL extends AbstractAction {
		private static final long serialVersionUID = -4655131886494842553L;
		public ActionCRL() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			// Choose the CA to sign the CRL.
			ArrayList<String> options = new ArrayList<>();
			
			Enumeration<String> aliases;
			try {
				aliases = currentKeystore.aliases();
				while(aliases.hasMoreElements()) {
					String a = aliases.nextElement();
					if(currentKeystore.isKeyEntry(a)) {
						options.add(a);
					}
				}
				
				String[] poss = new String[options.size()];
				for(int i = 0; i < options.size(); i++) {
					poss[i] = options.get(i);
				}
				String alias = (String)JOptionPane.showInputDialog(
	                    MainWindow.getInstance(),
	                    "Select the CA:",
	                    "CA",
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    options.toArray(poss),
	                    poss[0]);
				
				// Extract the certificate and CA data from the keystore.
				EnterPasswordDialog epd = new EnterPasswordDialog();
				epd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
				epd.setVisible(true);
				PrivateKey pk = (PrivateKey) currentKeystore.getKey(alias, epd.getPassword());
				X509Certificate cert = (X509Certificate) currentKeystore.getCertificate(alias);
				
				// Issuer name
				X500Principal prnc = cert.getIssuerX500Principal();
				X500Name CA = X500Name.getInstance(prnc.getEncoded());
				
				// Valid from
				Date today = Calendar.getInstance().getTime();
				
				// Signed by our CA
				JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
				builder.setProvider("BC");
				ContentSigner contentSigner = builder.build(pk);
				
				// Create the CRL
				X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(CA, today);
				X509CRLHolder holder = crlBuilder.build(contentSigner);
				JcaX509CRLConverter cnv = new JcaX509CRLConverter();
				cnv.setProvider("BC");
				X509CRL crl = cnv.getCRL(holder);
				
				// Set the path
				String path = System.getProperty("user.dir");
				path = Paths.get(path, "crls").toString();
				path = Paths.get(path, alias+".crl").toString();
				
				CRLUtils.saveCRLfile(path, crl);
				
			} catch (UnrecoverableKeyException e1) {
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (KeyStoreException e2) {
				e2.printStackTrace();
			} catch (OperatorCreationException e1) {
				e1.printStackTrace();
			} catch (CRLException e1) {
				e1.printStackTrace();
			}
			
		}
	}
	
// Window stuff --------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Sends the window closing event, and triggers appropriate listeners.
	 */
	private class ActionExit extends AbstractAction {
		private static final long serialVersionUID = 2732771330480399657L;
		public ActionExit() {
			putValue(NAME, "Exit");
			putValue(SHORT_DESCRIPTION, "Close the application");
		}
		public void actionPerformed(ActionEvent e) {			
			MainWindow.getInstance().dispatchEvent(new WindowEvent(MainWindow.getInstance(), WindowEvent.WINDOW_CLOSING));
		}
	}
}
