package com.ericski.Battlestations.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ericski.Battlestations.Planet;
import com.ericski.Battlestations.ModuleFactory;
import com.ericski.Battlestations.PDFPlanetWriter;
import com.ericski.Battlestations.PDFPlanetWriterOptions;
import com.ericski.Battlestations.RuleSetVersion;
import com.ericski.ui.FileChooserExtensionFileFilter;
import com.ericski.ui.ImageIconScalable;
import com.ericski.ui.InfiniteProgressPanel;

public class DirtsideCreatorFrame extends ShipCreatorFrame
{

	private static final long serialVersionUID = 1L;
	
	private PlanetCreatorPanel cityPanel;

	public DirtsideCreatorFrame()
	{
		super("Dirtside City Sheet Creator");

		loadPrefs();
		if (Desktop.isDesktopSupported())
		{
			desktop = Desktop.getDesktop();
		}
		JPanel contentPane = new JPanel(new BorderLayout());

		cityPanel = new PlanetCreatorPanel();

		contentPane.add(cityPanel, BorderLayout.CENTER);

		// ImageIcon icon = new
		// ImageIcon(getClass().getResource("/img/icon.gif"));
		ImageIcon icon = new ImageIcon(getClass().getResource("/img/tentac_ship_icon.png"));
		setIconImage(icon.getImage());

		glassPane = new InfiniteProgressPanel();
		glassPane.setForeground(Color.BLUE);
		setGlassPane(glassPane);

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);

		ActionListener actionListener = new Actioner();

		JMenuItem newMenu = new JMenuItem("New");
		newMenu.addActionListener(actionListener);
		newMenu.setMnemonic('N');
		newMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
		newMenu.setActionCommand(NEWACTION);
		fileMenu.add(newMenu);

		JMenuItem loadMenu = new JMenuItem("Open");
		loadMenu.addActionListener(actionListener);
		loadMenu.setMnemonic('o');
		loadMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
		loadMenu.setActionCommand(LOADACTION);
		fileMenu.add(loadMenu);

		JMenuItem saveMenu = new JMenuItem("Save");
		saveMenu.addActionListener(actionListener);
		saveMenu.setMnemonic('S');
		saveMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		saveMenu.setActionCommand(SAVEACTION);
		fileMenu.add(saveMenu);

		if (desktop != null && desktop.isSupported(Desktop.Action.PRINT))
		{
			JMenuItem printMenu = new JMenuItem("Print");
			printMenu.addActionListener(actionListener);
			printMenu.setMnemonic('P');
			printMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK));
			printMenu.setActionCommand(PRINTACTION);
			fileMenu.add(printMenu);
		}

		if (desktop != null && desktop.isSupported(Desktop.Action.OPEN))
		{
			JMenuItem viewMenu = new JMenuItem("View in Reader");
			viewMenu.addActionListener(actionListener);
			viewMenu.setMnemonic('V');
			viewMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));
			viewMenu.setActionCommand(VIEWACTION);
			fileMenu.add(viewMenu);
		}

		JMenuItem xmlViewMenu = new JMenuItem("View XML Code");
		xmlViewMenu.addActionListener(actionListener);
		xmlViewMenu.setActionCommand(XMLVIEWACTION);
		xmlViewMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.CTRL_MASK));
		fileMenu.add(xmlViewMenu);

		fileMenu.addSeparator();

		JMenuItem exportJPGMenu = new JMenuItem("Export as JPG");
		exportJPGMenu.addActionListener(actionListener);
		exportJPGMenu.setMnemonic('J');
		exportJPGMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.CTRL_MASK));
		exportJPGMenu.setActionCommand(EXPORTJPGACTION);
		fileMenu.add(exportJPGMenu);

		JMenuItem exportPDFMenu = new JMenuItem("Export as PDF");
		exportPDFMenu.addActionListener(actionListener);
		exportPDFMenu.setMnemonic('D');
		exportPDFMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK));
		exportPDFMenu.setActionCommand(EXPORTPDFACTION);
		fileMenu.add(exportPDFMenu);

		fileMenu.addSeparator();

		JMenuItem exitMenu = new JMenuItem("Exit");
		exitMenu.addActionListener(actionListener);
		exitMenu.setMnemonic('x');
		exitMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK));
		exitMenu.setActionCommand(EXITACTION);
		fileMenu.add(exitMenu);

		JMenu optionMenu = new JMenu("Options");
		optionMenu.setMnemonic('O');
		menuBar.add(optionMenu);

		JMenuItem pathOptionsMenu = new JMenuItem("Open Module Directory");
		pathOptionsMenu.addActionListener(actionListener);
		pathOptionsMenu.setMnemonic('p');
		pathOptionsMenu.setActionCommand(PATHOPTIONS);
		optionMenu.add(pathOptionsMenu);

		JMenuItem pdfOptionsMenu = new JMenuItem("PDF Output");
		pdfOptionsMenu.addActionListener(actionListener);
		pdfOptionsMenu.setMnemonic('p');
		pdfOptionsMenu.setActionCommand(PDFOPTIONS);
		optionMenu.add(pdfOptionsMenu);

		showToolBarMenu = new JCheckBoxMenuItem("Show Toolbar");
		showToolBarMenu.setSelected(true);
		showToolBarMenu.setMnemonic('t');
		showToolBarMenu.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (e.getSource() instanceof JCheckBoxMenuItem)
				{
					JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
					boolean value = source.isSelected();
					Preferences prefs = Preferences.userNodeForPackage(DirtsideCreatorFrame.class);
					prefs.put(SHOW_TOOL_BAR_PREFS_KEY, Boolean.toString(value));
					toolBar.setVisible(value);
				}
			}
		});
		optionMenu.add(showToolBarMenu);

		RuleSetVersion.getInstance().loadPreferences();

		menuBar.add(Box.createHorizontalGlue());

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('h');
		menuBar.add(helpMenu);

		JMenuItem copyrightMenu = new JMenuItem("Copyright");
		copyrightMenu.setActionCommand(COPYRIGHT);
		copyrightMenu.addActionListener(actionListener);
		helpMenu.add(copyrightMenu);

		JMenuItem aboutMenu = new JMenuItem("About");
		aboutMenu.setActionCommand(ABOUT);
		aboutMenu.addActionListener(actionListener);
		helpMenu.add(aboutMenu);

		JMenuItem changeLogMenu = new JMenuItem("Changelog");
		changeLogMenu.setActionCommand(CHANGELOG);
		changeLogMenu.addActionListener(actionListener);
		helpMenu.add(changeLogMenu);

		setJMenuBar(menuBar);

		toolBar = new JToolBar("File Menu");
		JButton newButton = createToolBarButton("/toolbarButtonGraphics/general/New24.gif", NEWACTION, actionListener);
		newButton.setText("New");
		toolBar.add(newButton);

		JButton openButton = createToolBarButton("/toolbarButtonGraphics/general/Open24.gif", LOADACTION, actionListener);
		openButton.setText("Open");
		toolBar.add(openButton);

		JButton saveButton = createToolBarButton("/toolbarButtonGraphics/general/Save24.gif", SAVEACTION, actionListener);
		saveButton.setText("Save");
		toolBar.add(saveButton);

		if (desktop != null && desktop.isSupported(Desktop.Action.PRINT))
		{
			JButton printButton = createToolBarButton("/toolbarButtonGraphics/general/Print24.gif", PRINTACTION, actionListener);
			printButton.setText("Print");
			toolBar.add(printButton);
		}

		if (desktop != null && desktop.isSupported(Desktop.Action.OPEN))
		{
			JButton viewButton = createToolBarButton("/toolbarButtonGraphics/general/PrintPreview24.gif", VIEWACTION, actionListener);
			viewButton.setText("View");
			toolBar.add(viewButton);
		}
		toolBar.addSeparator();

		JButton exportPDFButton = createToolBarButton("/toolbarButtonGraphics/general/Export24.gif", EXPORTPDFACTION, actionListener);
		exportPDFButton.setText("Export as PDF");
		toolBar.add(exportPDFButton);

		JButton exportJPGButton = createToolBarButton("/toolbarButtonGraphics/general/Export24.gif", EXPORTJPGACTION, actionListener);
		exportJPGButton.setText("Export as JPG");
		toolBar.add(exportJPGButton);

		JButton exportLargeButton = createToolBarButton("/toolbarButtonGraphics/general/Export24.gif", EXPORTLARGEACTION, actionListener);
		exportLargeButton.setText("Export as Large JPG");
		toolBar.add(exportLargeButton);

		toolBar.addSeparator();

		JButton exitButton = createToolBarButton("/toolbarButtonGraphics/general/Stop24.gif", EXITACTION, actionListener);
		exitButton.setText("Exit");
		toolBar.add(exitButton);

		contentPane.add(toolBar, BorderLayout.PAGE_START);

		setContentPane(contentPane);
		pack();
		Dimension dim = new Dimension((int) toolBar.getPreferredSize().getWidth(), (int) cityPanel.getMinimumSize().getHeight());
		setMinimumSize(dim);

		setLocationRelativeTo(null);
	}

	private JButton createToolBarButton(String path, String action, ActionListener actionListener)
	{
		JButton button = new JButton();
		if (path != null && path.length() > 0)
		{
			try
			{
				URL url = this.getClass().getResource(path);
				ImageIconScalable icon = new ImageIconScalable(url);
				icon.setScaledSize(24, 24);
				button.setIcon(icon);
			}
			catch (Exception iggy)
			{
			}
		}
		button.addActionListener(actionListener);
		button.setActionCommand(action);
		return button;
	}

	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				JFrame.setDefaultLookAndFeelDecorated(true);
				try
				{
					// Set System L&F
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
				catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e)
				{
					// handle exception
				}
				DirtsideCreatorFrame frame = new DirtsideCreatorFrame();
				frame.toFront();
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});

	}

	private class Actioner implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent ae)
		{
			String action = ae.getActionCommand();
			switch (action)
			{
				case NEWACTION:
					cityPanel.setCity(new Planet());
					break;
				case SAVEACTION:
					saveCity();
					break;
				case LOADACTION:
					loadCity();
					break;
				case EXPORTJPGACTION:
					exportJPG();
					break;
				case EXPORTLARGEACTION:
					exportLarge();
					break;
				case EXPORTPDFACTION:
					exportPDF();
					break;
				case PRINTACTION:
					print();
					break;
				case VIEWACTION:
					view();
					break;
				case XMLVIEWACTION:
					Planet city = cityPanel.generatePlanet();
					String text = city.toXML();
					RawXmlDialog forumDialog = new RawXmlDialog(text, DirtsideCreatorFrame.this, "XML view", "Raw file view");
					forumDialog.setVisible(true);
					break;
				case EXITACTION:
					glassPane.setVisible(false);
					glassPane.interrupt();
					if (getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE)
					{
						System.exit(0);
					}
					else
					{
						setVisible(false);
					}
					break;
				case ABOUT:
				{
					String info = "Dirtside Sheet Creator\n"
									  + "Version: " + Version.INSTANCE.getVersion() + "\n"
									  + "Built at: " + Version.INSTANCE.getBuildDate() + "\n"
									  + "Contact: eric.fialkowski@gmail.com";
					if (Version.INSTANCE.isUptoDate() == Version.VersionUpToDate.No)
					{
						info += "\n\nA newer version is available for download";
					}
					else
					{
						info += "\n\nYou have the most current version.";
					}
					JOptionPane.showMessageDialog(DirtsideCreatorFrame.this, info, "About", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				case CHANGELOG:
				{
					String info = "2008(ish) Initial Public Release"
									  + "\n4/4/2012  Updates to current rules & expansions"
									  + "\n7/14/2014  Added Citys from How Much For Your Planet"
									  + "\n7/15/2014  ALT+Dragging a module will duplicate it. Ability to zoom modules";
					JOptionPane.showMessageDialog(DirtsideCreatorFrame.this, info, "Change log", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				case COPYRIGHT:
					String copyright = "Battlestations Copyright 2004, 2008 Jeff Siadek and Jason Siadek\n"
										   + "Battlestations and the Battlestations logo are trademarks owned by Jeff Siadek and Jason Siadek. All rights reserved.\n\n"
									   + "For more information see: " + "http://www.battlestations.info\n";
					JOptionPane.showMessageDialog(DirtsideCreatorFrame.this, copyright, "Copyright Information", JOptionPane.INFORMATION_MESSAGE);
					break;
				case PDFOPTIONS:
					PDFPreferencesDialog prefs = new PDFPreferencesDialog(DirtsideCreatorFrame.this);
					prefs.setVisible(true);
					break;
				case PATHOPTIONS:
					try
					{
						desktop.open(ModuleFactory.INSTANCE.getModuleDirectory());
					}
					catch (IOException ex)
					{
						logger.warn("Couldn't open directory", ex);
					}
					break;
			}
		}
	}

	protected void print()
	{
		glassPane.setText("Printing");
		glassPane.start();
		PrintCityTask task = new PrintCityTask(this, cityPanel.generatePlanet(), glassPane, desktop);
		task.execute();
	}

	protected void view()
	{
		glassPane.setText("Opening default viewer");
		glassPane.start();
		ViewCityTask task = new ViewCityTask(this, cityPanel.generatePlanet(), glassPane, desktop);
		task.execute();
	}

	protected void saveCity()
	{
		glassPane.setText("Preparing to Save City");
		glassPane.start();
		JFileChooser saver = getFileChooser();
		FileChooserExtensionFileFilter shipFilter = new FileChooserExtensionFileFilter(".ship", "City files");
		saver.addChoosableFileFilter(shipFilter);
		saver.setAcceptAllFileFilterUsed(false);
		int result = saver.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File f = saver.getSelectedFile();
			setDirectory(f);
			if (!f.getAbsolutePath().toLowerCase().endsWith(shipFilter.getExtension()))
			{
				f = new File(f.getAbsolutePath() + shipFilter.getExtension());
			}
			glassPane.setText("Saving");
			glassPane.start();
			SaveCityTask task = new SaveCityTask(this, cityPanel.generatePlanet(), f, glassPane);
			task.execute();
		}
		else
		{
			glassPane.setVisible(false);
		}

	}

	protected void exportJPG()
	{
		glassPane.setText("Preparing to Export JPEG");
		glassPane.start();
		JFileChooser saver = getFileChooser();
		FileChooserExtensionFileFilter jpgFilter = new FileChooserExtensionFileFilter(".jpg", "JPEG files");
		saver.addChoosableFileFilter(jpgFilter);
		saver.setAcceptAllFileFilterUsed(false);

		int result = saver.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			glassPane.setText("Exporting JPEG");
			glassPane.start();
			File jpgFile = saver.getSelectedFile();
			setDirectory(jpgFile);
			if (!jpgFile.getAbsolutePath().toLowerCase().endsWith(jpgFilter.getExtension()))
			{
				jpgFile = new File(jpgFile.getAbsolutePath() + jpgFilter.getExtension());
			}
			ExportJPGTask task = new ExportJPGTask(this, cityPanel.generatePlanet(), jpgFile, glassPane);
			task.execute();
		}
		else
		{
			glassPane.setVisible(false);
		}
	}

	protected void exportLarge()
	{
		glassPane.setText("Preparing to Export Large JPEG");
		glassPane.start();
		JFileChooser saver = getFileChooser();
		FileChooserExtensionFileFilter filter = new FileChooserExtensionFileFilter(".jpg", "JPEG files");
		saver.addChoosableFileFilter(filter);
		saver.setAcceptAllFileFilterUsed(false);

		int result = saver.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			glassPane.setText("Exporting Large JPEG");
			glassPane.start();
			File file = saver.getSelectedFile();
			setDirectory(file);
			if (!file.getAbsolutePath().toLowerCase().endsWith(filter.getExtension()))
			{
				file = new File(file.getAbsolutePath() + filter.getExtension());
			}
			ExportLargeTask task = new ExportLargeTask(this, cityPanel.generatePlanet(), file, glassPane);
			task.execute();
		}
		else
		{
			glassPane.setVisible(false);
		}
	}

	protected void exportPDF()
	{
		glassPane.setText("Preparing to Export PDF");
		glassPane.start();
		JFileChooser saver = getFileChooser();
		FileChooserExtensionFileFilter pdfFilter = new FileChooserExtensionFileFilter(".pdf", "PDF files");
		saver.addChoosableFileFilter(pdfFilter);
		saver.setAcceptAllFileFilterUsed(false);
		int result = saver.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			glassPane.setText("Exporting PDF");
			glassPane.start();
			File pdfFile = saver.getSelectedFile();
			setDirectory(pdfFile);
			if (!pdfFile.getAbsolutePath().toLowerCase().endsWith(pdfFilter.getExtension()))
			{
				pdfFile = new File(pdfFile.getAbsolutePath() + pdfFilter.getExtension());
			}
			ExportPDFTask task = new ExportPDFTask(this, cityPanel.generatePlanet(), pdfFile, glassPane);
			task.execute();
		}
		else
		{
			glassPane.setVisible(false);
		}

	}

	protected void loadCity()
	{
		glassPane.setText("Preparing to Load City");
		glassPane.start();
		JFileChooser loader = getFileChooser();
		FileChooserExtensionFileFilter shipFilter = new FileChooserExtensionFileFilter(".ship", "City files");
		loader.addChoosableFileFilter(shipFilter);
		loader.setAcceptAllFileFilterUsed(false);
		int result = loader.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION)
		{
			File f = loader.getSelectedFile();
			setDirectory(f);
			if (!f.getAbsolutePath().toLowerCase().endsWith(shipFilter.getExtension()))
			{
				f = new File(f.getAbsolutePath() + shipFilter.getExtension());
			}
			glassPane.setText("Loading");
			glassPane.start();
			LoadCityTask task = new LoadCityTask(this, cityPanel, f, glassPane);
			task.execute();
		}
		else
		{
			glassPane.setVisible(false);
		}

	}

	protected void setDirectory(File f)
	{
		String dir = f.getParent();
		if (dir != null)
		{
			exportPDFDirectory = dir;
			savePrefs();
		}
	}

	protected JFileChooser getFileChooser()
	{
		JFileChooser chooser;
		if (exportPDFDirectory.length() > 0)
		{
			chooser = new JFileChooser(exportPDFDirectory);
		}
		else
		{
			chooser = new JFileChooser();
		}
		return chooser;
	}


}

class ViewCityTask extends SwingWorker<Void, Void>
{
	private static final Logger logger = LogManager.getLogger(DirtsideCreatorFrame.class);
	Planet shipToPrint;
	JFrame parent;
	InfiniteProgressPanel progressPanel;
	Desktop desktop;

	public ViewCityTask(JFrame parent, Planet shipToPrint, InfiniteProgressPanel progressPanel, Desktop desktop)
	{
		this.parent = parent;
		this.shipToPrint = shipToPrint;
		this.progressPanel = progressPanel;
		this.desktop = desktop;
	}

	@Override
	protected Void doInBackground() throws Exception
	{
		try
		{
			File f = File.createTempFile("shiptoprint", ".pdf");
			FileOutputStream fout = new FileOutputStream(f);
			PDFPlanetWriterOptions options = new PDFPlanetWriterOptions();
			options.loadPreferences();
			PDFPlanetWriter.drawPDF(shipToPrint, fout, options);
			desktop.open(f);
			progressPanel.stop();
		}
		catch (IOException ex)
		{
			progressPanel.stop();
			logger.warn("Error Viewing City", ex);
			JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error Viewing City", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			progressPanel.setVisible(false);
		}
		return null;
	}
}

class SaveCityTask extends SwingWorker<Void, Void>
{
	private static final Logger logger = LogManager.getLogger(DirtsideCreatorFrame.class);
	Planet shipToSave;
	JFrame parent;
	File shipFile;
	InfiniteProgressPanel progressPanel;

	public SaveCityTask(JFrame parent, Planet shipToSave, File shipFile, InfiniteProgressPanel progressPanel)
	{
		this.parent = parent;
		this.shipToSave = shipToSave;
		this.shipFile = shipFile;
		this.progressPanel = progressPanel;
	}

	@Override
	protected Void doInBackground() throws Exception
	{
		try
		{
			try (FileWriter fw = new FileWriter(shipFile))
			{
				fw.write(shipToSave.toXML());
			}
			progressPanel.stop();
			JOptionPane.showMessageDialog(parent, "City saved", "Success", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException | HeadlessException ex)
		{
			progressPanel.stop();
			logger.warn("Error Saving City", ex);
			JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error Saving City", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			progressPanel.setVisible(false);
		}
		return null;
	}
}

class LoadCityTask extends SwingWorker<Void, Void>
{
	private static final Logger logger = LogManager.getLogger(DirtsideCreatorFrame.class);
	PlanetCreatorPanel shipPanel;
	JFrame parent;
	File shipFile;
	InfiniteProgressPanel progressPanel;

	public LoadCityTask(JFrame parent, PlanetCreatorPanel shipPanel, File shipFile, InfiniteProgressPanel progressPanel)
	{
		this.parent = parent;
		this.shipPanel = shipPanel;
		this.shipFile = shipFile;
		this.progressPanel = progressPanel;
	}

	@Override
	protected Void doInBackground() throws Exception
	{
		try
		{
			//
			// load ship
			//
			BufferedReader br = new BufferedReader(new FileReader(shipFile));
			String line;
			StringBuilder sb = new StringBuilder(1024);
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}
			br.close();
			Planet ship = Planet.fromXML(sb.toString());
			shipPanel.setCity(ship);
			progressPanel.stop();
			JOptionPane.showMessageDialog(parent, "City Loaded", "Success", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException | HeadlessException ex)
		{
			progressPanel.stop();
			logger.warn("Error Loading City", ex);
			JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error Loading City", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			progressPanel.setVisible(false);
		}
		return null;
	}
}



class PrintCityTask extends SwingWorker<Void, Void>
{
	private static final Logger logger = LogManager.getLogger(DirtsideCreatorFrame.class);
	Planet shipToPrint;
	JFrame parent;
	InfiniteProgressPanel progressPanel;
	Desktop desktop;

	public PrintCityTask(JFrame parent, Planet shipToPrint, InfiniteProgressPanel progressPanel, Desktop desktop)
	{
		this.parent = parent;
		this.shipToPrint = shipToPrint;
		this.progressPanel = progressPanel;
		this.desktop = desktop;
	}

	@Override
	protected Void doInBackground() throws Exception
	{
		try
		{
			File f = File.createTempFile("shiptoprint", ".pdf");
			FileOutputStream fout = new FileOutputStream(f);
			PDFPlanetWriterOptions options = new PDFPlanetWriterOptions();
			options.loadPreferences();
			PDFPlanetWriter.drawPDF(shipToPrint, fout, options);
			desktop.print(f);
			progressPanel.stop();
		}
		catch (IOException ex)
		{
			progressPanel.stop();
			logger.warn("Error Printing City", ex);
			JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error Printing City", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			progressPanel.setVisible(false);
		}
		return null;
	}
}