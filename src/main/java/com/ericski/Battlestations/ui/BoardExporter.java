package com.ericski.Battlestations.ui;

import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ericski.Battlestations.PDFShipWriterOptions;
import com.ericski.Battlestations.PDFWriterOptions;
import com.ericski.ui.InfiniteProgressPanel;

public interface BoardExporter {

	BufferedImage generateImage();

	void drawPDF(FileOutputStream fout, PDFWriterOptions options) throws IOException;

	BufferedImage generatePrintImage();


}

class ExportLargeTask extends SwingWorker<Void, Void>
{
	private static final Logger logger = LogManager.getLogger(ShipCreatorFrame.class);
	BoardExporter shipToExport;
	JFrame parent;
	File file;
	InfiniteProgressPanel progressPanel;

	public ExportLargeTask(JFrame parent, BoardExporter shipToExport, File file, InfiniteProgressPanel progressPanel)
	{
		this.parent = parent;
		this.shipToExport = shipToExport;
		this.file = file;
		this.progressPanel = progressPanel;
	}

	@Override
	protected Void doInBackground() throws Exception
	{

		try
		{
			BufferedImage shipImage = shipToExport.generatePrintImage();

			boolean written = ImageIO.write(shipImage, "jpeg", file);
			progressPanel.stop();
			if (written)
				JOptionPane.showMessageDialog(parent, "JPEG Exported", "Success", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(parent, "JPEG NOT Exported", "Failed", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException | HeadlessException ex)
		{
			progressPanel.stop();
			logger.warn("Error Exporting Ship to JPEG", ex);
			JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error Exporting Ship to JPEG", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			progressPanel.setVisible(false);
		}

		return null;
	}
}

class ExportPDFTask extends SwingWorker<Void, Void>
{
	private static final Logger logger = LogManager.getLogger(ShipCreatorFrame.class);
	BoardExporter shipToExport;
	JFrame parent;
	File pdfFile;
	InfiniteProgressPanel progressPanel;

	public ExportPDFTask(JFrame parent, BoardExporter shipToExport, File pdfFile, InfiniteProgressPanel progressPanel)
	{
		this.parent = parent;
		this.shipToExport = shipToExport;
		this.pdfFile = pdfFile;
		this.progressPanel = progressPanel;
	}

	@Override
	protected Void doInBackground() throws Exception
	{

		try
		{
			// Prepare to write to the file
			FileOutputStream fout = new FileOutputStream(pdfFile);
			PDFShipWriterOptions options = new PDFShipWriterOptions();
			options.loadPreferences();
			shipToExport.drawPDF(fout, options);

			progressPanel.stop();
			JOptionPane.showMessageDialog(parent, "PDF Exported", "Success", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException | HeadlessException ex)
		{
			progressPanel.stop();
			logger.warn("Error Exporting Ship to PDF", ex);
			JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error Exporting Ship to PDF", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			progressPanel.setVisible(false);
		}
		return null;
	}

}
