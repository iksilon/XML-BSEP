package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

/**
 * Main view of the application, extension of the {@link JFrame} class.
 * This window shows a keystore file with all it's certificates.
 *
 */
public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1198734643308937757L;
	
	private JPanel contentPane;
	private final Action actKeystore = new ActionKeystore();
	private final Action actKeypair = new ActionKeypair();
	private final Action actOpen = new ActionOpen();
	private final Action actSave = new ActionSave();
	private final Action actSaveAs = new ActionSaveAs();
	private final Action actExit = new ActionExit();
	private final Action actExportCertificate = new ActionExportCertificate();
	private final Action actExportAll = new ActionExportAll();

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

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mntmNew = new JMenu("New");
		mnFile.add(mntmNew);
		
		JMenuItem mntmKeystore = mntmNew.add(actKeystore);
		mntmKeystore.setText("Keystore");
		
		JMenuItem mntmKeypair = mntmNew.add(actKeypair);
		mntmKeypair.setText("Keypair");
		
		JMenuItem mntmOpen = mnFile.add(actOpen);
		mntmOpen.setText("Open");
		
		JSeparator sepFile1 = new JSeparator();
		mnFile.add(sepFile1);
		
		JMenuItem mntmSave = mnFile.add(actSave);
		mntmSave.setText("Save");
		
		JMenuItem mntmSaveAs = mnFile.add(actSaveAs);
		mntmSaveAs.setText("Save as...");
		
		JSeparator sepFile2 = new JSeparator();
		mnFile.add(sepFile2);
		
		JMenuItem mntmExit = mnFile.add(actExit);
		mntmExit.setText("Exit");
		
		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);
		
		JMenuItem mntmExportCertificate = mnTools.add(actExportCertificate);
		mntmExportCertificate.setText("Export Certificate");
		
		JMenuItem mntmExportAll = mnTools.add(actExportAll);
		mntmExportAll.setText("Export All");
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		KeypairTable keypairTable = new KeypairTable();
		keypairTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(keypairTable);
	}
	
// ---------------------------------------------------------------------------------------------------
// Akcije, jer me mrzi da prebacujem u posebne faljove a GUI builder ih je ovde stavio.
// ---------------------------------------------------------------------------------------------------

	/**
	 * 
	 *
	 */
	private class ActionKeystore extends AbstractAction {
		private static final long serialVersionUID = 425412543121784713L;
		public ActionKeystore() {
			putValue(NAME, "Keystore");
			putValue(SHORT_DESCRIPTION, "Create new keystore");
		}
		public void actionPerformed(ActionEvent e) {
			KeystoreDialog ksd = new KeystoreDialog();
			ksd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			ksd.setVisible(true);
		}
	}
	private class ActionKeypair extends AbstractAction {
		private static final long serialVersionUID = -1411136323257319945L;
		public ActionKeypair() {
			putValue(NAME, "Keypair");
			putValue(SHORT_DESCRIPTION, "Generate new keypair");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class ActionOpen extends AbstractAction {
		private static final long serialVersionUID = 340823143919984037L;
		public ActionOpen() {
			putValue(NAME, "Open");
			putValue(SHORT_DESCRIPTION, "Open a keystore file");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class ActionSave extends AbstractAction {
		private static final long serialVersionUID = -4641089031850059072L;
		public ActionSave() {
			putValue(NAME, "Save");
			putValue(SHORT_DESCRIPTION, "Save keystore");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class ActionSaveAs extends AbstractAction {
		private static final long serialVersionUID = 3925404848236570471L;
		public ActionSaveAs() {
			putValue(NAME, "Save As");
			putValue(SHORT_DESCRIPTION, "Save keystore to a file");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class ActionExit extends AbstractAction {
		private static final long serialVersionUID = 2732771330480399657L;
		public ActionExit() {
			putValue(NAME, "Exit");
			putValue(SHORT_DESCRIPTION, "Close the application");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class ActionExportCertificate extends AbstractAction {
		private static final long serialVersionUID = -1698079888963949279L;
		public ActionExportCertificate() {
			putValue(NAME, "Export Certificate");
			putValue(SHORT_DESCRIPTION, "Export keypair to a certificate file");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class ActionExportAll extends AbstractAction {
		private static final long serialVersionUID = 5683267289392412616L;
		public ActionExportAll() {
			putValue(NAME, "Export All");
			putValue(SHORT_DESCRIPTION, "Export all certificates to a specified folder");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
}
