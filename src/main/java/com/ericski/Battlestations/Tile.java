package com.ericski.Battlestations;

import com.ericski.graphics.BetterImageScaler;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.toRadians;
import java.util.concurrent.ConcurrentHashMap;
import static org.apache.logging.log4j.LogManager.getLogger;
import org.apache.logging.log4j.Logger;

public abstract class Tile implements Comparable<Tile>, Cloneable, MoveableItem
{
	static final Logger logger = getLogger(Tile.class);
	public final static String BLANK = "blank";
	public final static String POD_SUFFIX = "_pod";

	protected static ConcurrentHashMap<String, Image> imageMap = new ConcurrentHashMap<>();
	protected Image image;
	protected String name = "";
	protected String description = "";
	protected int rotation = 0;
	protected boolean upgraded = false;
	protected String fileName = "";
	protected String tyleType;

	public Tile(String name, String description, String tileType, String fileName)
	{
		this(name, 0, description, tileType, fileName);
	}

	Tile(String name, int rotation, String description, String tileType, String fileName)
	{
		this.name = name;
		this.rotation = rotation;
		this.description = description;
		this.tyleType = tileType;
		this.fileName = fileName;
	}

	abstract public Tile copy();

	abstract protected Image loadImage();

	public boolean isBlankModule()
	{
		return name.equals(BLANK);
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String desc)
	{
		this.description = desc;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getRotation()
	{
		return rotation;
	}

	public void setRotation(int rotation)
	{
		this.rotation = rotation;
	}

	public String getTileType()
	{
		return tyleType;
	}

	public synchronized Image getNonRotatedImage()
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("Getting non rotated image for: " + toString());
		}

		if (image == null)
		{
			image = loadImage();
		}

		return image;
	}

	public synchronized Image getImage(int imageSize)
	{
		if (image == null)
		{
			image = loadImage();
		}

		if (rotation == 0 || image == null || name.equals(BLANK))
		{
			int side = image.getHeight(null);
			if (side == imageSize)
				return image;
			else
			{
				BufferedImage newImage = new BufferedImage(side,side, TYPE_INT_RGB);
				Graphics2D g2d = (Graphics2D) newImage.getGraphics();
				g2d.drawImage(image, 0,0,null);
				return BetterImageScaler.getScaledImage(newImage, imageSize,imageSize);
			}
		}

		int img_height = image.getHeight(null);
		int img_width = image.getWidth(null);


		BufferedImage newImage = new BufferedImage(img_width,img_height, TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) newImage.getGraphics();
		//creating the AffineTransform instance
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(toRadians(rotation), img_width / 2, img_width / 2);
		//draw the image using the AffineTransform
		g2d.drawImage(image, affineTransform, null);
		if ( img_width == imageSize)
			return newImage;
		else
			return BetterImageScaler.getScaledImage(newImage, imageSize,imageSize);
	}

	public synchronized Image getLargeImage()
	{
		if (image == null)
		{
			image = loadImage();
		}

		if (rotation == 0 || image == null || name.equals(BLANK))
		{
			int side = image.getHeight(null);
			if (side == 3000)
				return image;
			else
			{
				BufferedImage newImage = new BufferedImage(side,side, TYPE_INT_RGB);
				Graphics2D g2d = (Graphics2D) newImage.getGraphics();
				g2d.drawImage(image, 0,0,null);
				return BetterImageScaler.getScaledImage(newImage, 3000,3000);
			}
		}

		int img_height = image.getHeight(null);
		int img_width = image.getWidth(null);

		BufferedImage newImage = new BufferedImage(img_width,img_height, TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) newImage.getGraphics();
		//creating the AffineTransform instance
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(toRadians(rotation), img_width / 2, img_width / 2);
		//draw the image using the AffineTransform
		g2d.drawImage(image, affineTransform, null);
		if ( img_width == 3000)
			return newImage;
		else
			return BetterImageScaler.getScaledImage(newImage, 3000,3000);
	}


	@Override
	public int compareTo(Tile other)
	{
		if (isBlankModule() && other.isBlankModule())
		{
			return 0;
		}
		else if (isBlankModule() && !other.isBlankModule())
		{
			return -1;
		}
		else if (!isBlankModule() && other.isBlankModule())
		{
			return 1;
		}

		int rtn = tyleType.compareToIgnoreCase(other.getTileType());
		if (rtn == 0)
		{
			rtn = description.compareToIgnoreCase(other.getDescription());
			if (rtn == 0)
			{
				rtn = name.compareToIgnoreCase(other.getName());
			}
		}
		return rtn;
	}

	@Override
	public String toString()
	{
		return "Tile{" + "name=" + name + ", description=" + description + ", rotation=" + rotation + ", upgraded="
				   + upgraded + ", fileName=" + fileName + ", tileType=" + tyleType + '}';
	}

}
