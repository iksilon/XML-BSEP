package gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Security;
import java.security.cert.Certificate;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import actions.ActionExit;
import actions.certificate.ActionCreateCRL;
import actions.certificate.ActionExportAll;
import actions.certificate.ActionExportCertificate;
import actions.certificate.ActionImportCertificate;
import actions.certificate.ActionKeypair;
import actions.certificate.ActionRevokeCertificate;
import actions.keystore.ActionKeystore;
import actions.keystore.ActionKeystoreFromUser;
import actions.keystore.ActionOpen;
import actions.keystore.ActionSave;
import actions.keystore.ActionSaveAs;
import net.miginfocom.swing.MigLayout;

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
	private final Action actCRL = new ActionCreateCRL();
	private final Action actRevoke = new ActionRevokeCertificate();
	private final Action actKeystoreFromUser = new ActionKeystoreFromUser();
	
	
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
			
			JMenuItem mntmFromUser = mntmNew.add(actKeystoreFromUser);
			mntmFromUser.setText("Keystore from User");
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
	
	JMenuItem mntmRevokeCertificate = mnTools.add(actRevoke);
	mntmRevokeCertificate.setText("Revoke Certificate");
		
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
					System.out.println(v);
					System.out.println(currentKeystore);
					
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
	
	// Getters/Setters ----------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public KeyStore getCurrentKeystore() { return currentKeystore; }
	public void setCurrentKeystore(KeyStore ks) { currentKeystore = ks; }
	
	public String getCurrentPath() { return currentPath; }
	public void setCurrentPath(String p) { currentPath = p; }
	
	public JTable getKeypairTable() { return keypairTable; }
	  
	public JTextField getTxtCurrentKeystore() { return txtCurrentKeystore; }
	public JLabel getLblCurrentKeystore() {	return lblCurrentKeystore; }
	
	public Action getActSaveAs() { return actSaveAs; }
	public Action getActKeypair() { return actKeypair; }
}
