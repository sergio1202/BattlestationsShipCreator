package com.ericski.Battlestations.ui;

import com.ericski.Battlestations.Planet;
import com.ericski.Battlestations.Tile;

import static com.ericski.Battlestations.ui.SelectionPanel.DEFAULTSIZE;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

public class TileCreatorPanel extends JPanel
{
	private final int sizer = DEFAULTSIZE * 5;
	private static final long serialVersionUID = 1L;
	private final JPanel planetgrid;
	private final int gridSize = 3;

	List<PlanetSelectionPanel> tilePanels = new ArrayList<>();

	public TileCreatorPanel()
	{
		super(new FlowLayout());
		planetgrid = new JPanel(new GridLayout(gridSize, gridSize, 0, 0));
		planetgrid.setPreferredSize(new Dimension(sizer * gridSize, sizer * gridSize));

		for (int y = 0; y < gridSize; y++)
		{
			for (int x = 0; x < gridSize; x++)
			{
				PlanetSelectionPanel panel = new PlanetSelectionPanel(x, y);
				planetgrid.add(panel);
				tilePanels.add(panel);
			}
		}
		add(planetgrid);
	}

	public List<PlanetSelectionPanel> getTilePanels()
	{
		return new ArrayList<>(tilePanels);
	}

	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension((sizer * gridSize) + 5, (sizer * gridSize) + 5);
	}

	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension((sizer * gridSize) + 5, (sizer * gridSize) + 5);
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension((sizer * gridSize) + 5, (sizer * gridSize) + 5);
	}

	public Map<Integer, Tile> getModules()
	{
		Map<Integer, Tile> tiles = new HashMap<>();
		for (PlanetSelectionPanel panel : tilePanels)
		{
			Tile tile = (Tile) panel.getMoveableItem();
			int key = panel.getGridY() * gridSize + panel.getGridX();
			if (!tile.isBlankMoveableItem())
			{
				tiles.put(key, tile.copy());
			}
		}
		return tiles;
	}

	public void setModules(Planet city)
	{
		for (PlanetSelectionPanel panel : tilePanels)
		{
			int key = panel.getGridY() * gridSize + panel.getGridX();
			Tile tile = city.getTile(key);
			panel.setMoveableItem(tile.copy());
		}
	}

}
