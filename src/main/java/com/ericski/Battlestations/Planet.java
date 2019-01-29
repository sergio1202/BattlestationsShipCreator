package com.ericski.Battlestations;

import static com.ericski.Battlestations.TileFactory.INSTANCE;
import static com.ericski.Battlestations.TileFactory.getBlankTile;
import static java.awt.AlphaComposite.Src;
import java.awt.Color;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.awt.Font.BOLD;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
import static java.awt.Transparency.BITMASK;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static org.apache.logging.log4j.LogManager.getLogger;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import static org.jdom2.output.Format.TextMode.TRIM;
import static org.jdom2.output.Format.getPrettyFormat;
import org.jdom2.output.XMLOutputter;

import com.ericski.Battlestations.ui.BoardExporter;

public final class Planet implements Comparable<Planet>, BoardExporter
{
	private final static Logger logger = getLogger(Planet.class);
	String name = "";
	String species = "Generic";
	private List<String> notes;

	Map<Integer, Tile> tiles = new HashMap<>();

	public Planet()
	{
		name = "";
	}
	
	public String getSpecies() {
		return species;
	}
	
	public List<String> getNotes()
	{
		if (notes == null)
		{
			return new ArrayList<>();
		}
		return notes;
	}

	public String getNotesAsString()
	{
		StringBuilder buf = new StringBuilder();
		if (notes != null && notes.size() > 0)
		{
			for (String note : notes)
			{
				buf.append(note);
				buf.append('\n');
			}
			buf.deleteCharAt(buf.length() - 1); // kill the last newline
		}
		return buf.toString();
	}

	public void setNotes(LinkedList<String> notes)
	{
		this.notes = notes;
	}

	public void setNotes(String notesString)
	{
		notes = new ArrayList<>();
		if (notesString != null)
		{
			notes.addAll(asList(notesString.split("\n")));
		}
	}

	public void addNote(String note)
	{
		if (notes == null)
		{
			notes = new ArrayList<>();
		}
		notes.add(note);
	}

	public Planet(Planet copy)
	{
		name = copy.getName();
		notes = copy.getNotes();
		for (int i = 0; i < 49; i++)
		{
			Tile tile = copy.getTile(i);
			if (!"blank".equals(tile.getName()))
			{
				addTile(tile.copy(), i);
			}
		}

	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}


	public void setSpecies(String species)
	{
		if (species == null || species.isEmpty())
		{
			this.species = "Generic";
		}
		else
		{
			this.species = species;
		}
	}

	public void addTile(Tile tile, int key)
	{
		Integer ndx = key;
		tiles.put(ndx, tile);
	}

	public void addModule(Tile tile, int row, int col)
	{
		int key = (7 * (row - 4)) + (col - 4);
		addTile(tile, key);
	}

	public Tile getTile(int key)
	{
		Integer ndx = key;
		if (tiles.containsKey(ndx))
		{
			return tiles.get(ndx);
		}
		else
		{
			return getBlankTile();
		}
	}

	public Document toDocument()
	{
		Element shipElement = new Element("Ship");
		Document shipDocument = new Document(shipElement);
		shipElement.setAttribute("name", name);
		//shipElement.setAttribute("species", species);
		//shipElement.setAttribute("size", Integer.toString(size));
		//shipElement.setAttribute("damageSize", Integer.toString(damageSize));

		if (notes != null && notes.size() > 0)
		{
			Element notesElement = new Element("Notes");
			int i = 0;
			if (notes != null)
			{
				for (String note : notes)
				{
					Element noteElement = new Element("Note");
					noteElement.setAttribute("ndx", Integer.toString(i++));
					noteElement.setText(note);
					notesElement.addContent(noteElement);
				}
			}
			shipElement.addContent(notesElement);
		}

		Element modulesElement = new Element("Modules");
		shipElement.addContent(modulesElement);
		for (Entry<Integer, Tile> entry : tiles.entrySet())
		{
			Element moduleElement = new Element("Module");
			moduleElement.setAttribute("name", entry.getValue().getName());
			moduleElement.setAttribute("rotation", Integer.toString(entry.getValue().getRotation()));
			moduleElement.setAttribute("location", entry.getKey().toString());
			if (entry.getValue().isUpgraded())
			{
				moduleElement.setAttribute("upgraded", entry.getValue().isUpgraded() ? "T" : "F");
			}
			modulesElement.addContent(moduleElement);
		}
		return shipDocument;
	}

	public String toXML()
	{
		XMLOutputter outputer = new XMLOutputter();
		Format format = getPrettyFormat().setIndent("\t");
		format.setTextMode(TRIM);
		outputer.setFormat(format);
		return outputer.outputString(toDocument());
	}

	public static Planet fromXML(String xml)
	{
		SAXBuilder builder = new SAXBuilder();
		StringReader sr = new StringReader(xml);
		try
		{
			//builder.setValidation(true);
			builder.setIgnoringElementContentWhitespace(true);
			//builder.setIgnoringBoundaryWhitespace(true);
			Document shipDocument = builder.build(sr);
			Element shipElement = shipDocument.getRootElement();
			return fromXML(shipElement);
		}
		catch (JDOMException | IOException e)
		{
			logger.error("Couldn't deserialize xml into a Ship object", e);
		}

		return new Planet();
	}

	public static Planet fromXML(Element shipElement)
	{
		Planet planet = new Planet();
		planet.setName(shipElement.getAttributeValue("name"));
		Element notesElement = shipElement.getChild("Notes");
		if (notesElement != null)
		{
			Object notesArray[] = notesElement.getChildren("Note").toArray();
			for (Object noteObj : notesArray)
			{
				Element noteElem = (Element) noteObj;
				String note = noteElem.getTextNormalize();
				planet.addNote(note);
			}
		}

		if (logger.isTraceEnabled())
		{
			logger.trace("Planet: " + planet.toString());
		}

		Element modulesElement = shipElement.getChild("Tiles");
		Object array[] = modulesElement.getChildren("Tile").toArray();
		for (Object obj : array)
		{
			Element moduleElem = (Element) obj;
			String nameString = moduleElem.getAttributeValue("name");
			String rotationString = moduleElem.getAttributeValue("rotation");
			String locationString = moduleElem.getAttributeValue("location");
			String rowString = moduleElem.getAttributeValue("row");
			String colString = moduleElem.getAttributeValue("col");
			String upgradedString = moduleElem.getAttributeValue("upgraded");

			if (logger.isTraceEnabled())
			{
				StringBuilder sb = new StringBuilder("Planet Data:");
				sb.append(" name = ").append(nameString);
				sb.append(" location = ").append(locationString);
				sb.append(" rotation = ").append(rotationString);
				sb.append(" upgrade = ").append(upgradedString);
				logger.trace(sb.toString());
			}

			try
			{
				Tile tile = INSTANCE.getTileByName(nameString);
				if (logger.isTraceEnabled())
				{
					logger.trace("Before " + tile.toString());
				}
				tile.setRotation(parseInt(rotationString));
				tile.setUpgraded("T".equals(upgradedString));

				if (logger.isTraceEnabled())
				{
					logger.trace("After " + tile.toString());
				}

				if (locationString != null && locationString.length() > 0)
				{
					planet.addTile(tile, parseInt(locationString));
				}
				else
				{
					planet.addModule(tile, parseInt(rowString), parseInt(colString));
				}
			}
			catch (NumberFormatException iggy)
			{
			}
		}
		return planet;
	}

	public static List<Planet> fromCitiesXML(String xml)
	{
		StringReader sr = new StringReader(xml);
		return fromCitiesXML(sr);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (species.equals(name))
		{
			sb.append(name);
		}
		else
		{
			sb.append(species);
			sb.append(" ");
			sb.append(name);
		}
		return sb.toString();
	}

	public static List<Planet> fromCitiesXML(Reader reader)
	{
		List<Planet> cities = new ArrayList<>();
		try
		{
			SAXBuilder builder = new SAXBuilder();
			Document shipDocument = builder.build(reader);
			Element citiesElement = shipDocument.getRootElement();
			Object array[] = citiesElement.getChildren("Planet").toArray();
			for (Object obj : array)
			{
				Element planetElem = (Element) obj;
				Planet ship = fromXML(planetElem);
				if (ship != null)
				{
					cities.add(ship);
				}
			}
			reader.close();
		}
		catch (IOException | JDOMException ioex)
		{
			logger.error("Couldn't read stream into Planet objects", ioex);
		}

		return cities;
	}
	
	public BufferedImage generateImage()
	{
		return generateImage(3);
	}

	public BufferedImage generateImage(int gridSize)
	{
//		BufferedImage shipImage = new BufferedImage(1821,1821,BufferedImage.TYPE_INT_RGB);
//		int keyOffset = 50;

		BufferedImage planetImage = new BufferedImage(1771, 1771, TYPE_INT_RGB);
		int keyOffset = 0;

		Graphics2D g = planetImage.createGraphics();
		//
		// Set up some anti-aliasing to look pretty
		//
		// for antialising geometric shapes
		g.addRenderingHints(new RenderingHints(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON));
		// for antialiasing text
		g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
		java.awt.Font f = new java.awt.Font("Courier", BOLD, 35);
		g.setFont(f);

		int imageSize = 1771/gridSize; // 590
		for (int i = 0; i < gridSize; i++)
		{
			for (int j = 0; j < gridSize; j++)
			{
				int key = (i * gridSize) + j;
				Tile tile = getTile(key);
				if (logger.isDebugEnabled())
					logger.debug("Tile: " + tile.toString());
				Image img = tile.getImage(imageSize);
				if (img != null)
				{
					g.drawImage(img, keyOffset + j * imageSize, keyOffset + i * imageSize, null);
					if (tile.isUpgraded())
					{
						int x = (keyOffset + j * imageSize) + 120;
						int y = (keyOffset + i * imageSize) + 120;
						g.setColor(BLACK);
						g.drawString("UPG", x, y);
						g.setColor(WHITE);
						g.drawString("UPG", x - 1, y - 1);
					}
				}
			}
		}
		/*
         // draw the silhouette
         g.setColor(Color.BLACK);
         for ( int i = 4; i < 11; i++)
         {
         Integer s = new Integer(i);
         int offset = keyOffset + 120 + ((i-4) * 253);
         g.drawString(s.toString(),offset, 30);
         g.drawString(s.toString(),offset, 1761);
         if ( i > 9)
         {
         g.drawString(s.toString(), 5, offset);
         g.drawString(s.toString(), 1727, offset);
         }
         else
         {
         g.drawString(s.toString(), 10, offset);
         g.drawString(s.toString(), 1742, offset);
         }
         }
		 */
		return planetImage;
	}

	public BufferedImage generatePrintImage()
	{
		BufferedImage planetImage = new BufferedImage(21000, 21000, BufferedImage.TYPE_INT_RGB);
		int keyOffset = 0;

		Graphics2D g = planetImage.createGraphics();
		g.addRenderingHints(new RenderingHints(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON));


		for (int i = 0; i < 7; i++)
		{
			for (int j = 0; j < 7; j++)
			{
				int key = (i * 7) + j;
				Tile tile = getTile(key);
				Image img = tile.getLargeImage();
				if (img != null)
				{
					g.drawImage(img, keyOffset + j * 3000, keyOffset + i * 3000, null);
				}
			}
		}
		return planetImage;
	}

	public BufferedImage generateThumbnailImage()
	{
		return generateThumbnailImage(4);
	}

	public BufferedImage generateThumbnailImage(int pixelSize)
	{
		GraphicsEnvironment ge = getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gs.getDefaultConfiguration();

		// Create an image that supports transparent pixels
		BufferedImage cityImage = gc.createCompatibleImage(pixelSize * 7, pixelSize * 7, BITMASK);

		Graphics2D g = cityImage.createGraphics();
		//
		// Set up some anti-aliasing to look pretty
		//
		// for antialising geometric shapes
		g.addRenderingHints(new RenderingHints(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON));
		// for antialiasing text
		g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
		java.awt.Font f = new java.awt.Font("Courier", BOLD, pixelSize * 7);
		g.setFont(f);

		Color transparent = new Color(0, 0, 0, 0);
		g.setColor(transparent);
		g.setComposite(Src);

		for (int i = 0; i < 7; i++)
		{
			for (int j = 0; j < 7; j++)
			{
				int key = (i * 7) + j;
				Tile tile = getTile(key);
				if ("blank".equals(tile.getName()))
				{
					g.setColor(transparent);
				}
				else
				{
					//String profName = ModuleImageMapFactory.getInstance().getProfessionForModule(module.getName());
					//g.setColor(BattlestationColors.getColorFromName(profName));
					g.setColor(BLACK);
					g.drawRect(j * pixelSize, i * pixelSize, pixelSize, pixelSize);
					g.fillRect(j * pixelSize, i * pixelSize, pixelSize, pixelSize);
				}
			}
		}
		g.dispose();
		return cityImage;
	}

	@Override
	public int compareTo(Planet otherCity)
	{
			return 0;
	}

	@Override
	public void drawPDF(FileOutputStream fout, PDFWriterOptions options) throws IOException {
		PDFPlanetWriter.drawPDF(this, fout, options);
		
	}
}
