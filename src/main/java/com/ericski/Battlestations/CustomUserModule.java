package com.ericski.Battlestations;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import static javax.imageio.ImageIO.read;
import org.apache.logging.log4j.message.FormattedMessage;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class CustomUserModule extends Module
{
	public CustomUserModule(String name, String description, String profession, String fileName)
	{
		this(name, 0, description, profession, fileName);
	}

	public CustomUserModule(String name, int rotation, String description, String profession, String fileName)
	{
		super(name, rotation, description, profession, fileName);
	}

	@Override
	public Module copy()
	{
		return new CustomUserModule(name, rotation, description, profession, fileName);
	}

	@Override
	protected Image loadImage()
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("loading image for " + toString());
		}
		if (!imageMap.containsKey(name))
		{
			try
			{
				try (InputStream imageStream = new FileInputStream(fileName))
				{
					Image img = read(imageStream);
					if (img != null)
					{
						image = img;
						imageMap.putIfAbsent(name, image);
					}
				}
			}
			catch (IOException ignore)
			{
				if (logger.isWarnEnabled())
				{
					FormattedMessage fm = new FormattedMessage("Error loading module %s", toString());
					logger.warn(fm, ignore);
				}
			}
		}
		return imageMap.get(name);
	}

	public static CustomUserModule fromXml(File f)
	{
		SAXBuilder builder = new SAXBuilder();
		try (FileReader reader = new FileReader(f))
		{
			String dir = f.getParent();
			Document moduleDocument = builder.build(reader);
			Element moduleElement = moduleDocument.detachRootElement();
			Element nameElement = moduleElement.getChild("Name");
			Element descElement = moduleElement.getChild("Description");
			Element fileElement = moduleElement.getChild("File");
			Element professionElement = moduleElement.getChild("Profession");

			CustomUserModule m = new CustomUserModule(nameElement.getTextNormalize(),
													  descElement.getTextNormalize(),
													  professionElement.getTextNormalize(),
													  dir + "/" + fileElement.getTextNormalize());

			return m;
		}
		catch (JDOMException | IOException ex)
		{
			logger.warn("Couldn't load module", ex);
			return null;
		}
	}

	@Override
	public boolean isBlankMoveableItem() {
		return false;
	}
}
