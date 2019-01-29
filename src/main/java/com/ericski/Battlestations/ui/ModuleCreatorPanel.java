package com.ericski.Battlestations.ui;

import com.ericski.Battlestations.Module;
import com.ericski.Battlestations.Ship;
import static com.ericski.Battlestations.ui.SelectionPanel.DEFAULTSIZE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

public class ModuleCreatorPanel extends JPanel
{
	private final int sizer = DEFAULTSIZE;
	private static final long serialVersionUID = 1L;
	private final JPanel shipgrid;
	private final int gridSize;
	List<SelectionPanel> modulePanels = new ArrayList<>();

	public ModuleCreatorPanel(int gridSize, Color color)
	{
		super(new FlowLayout());
		this.gridSize = gridSize;
		shipgrid = new JPanel(new GridLayout(gridSize, gridSize, 0, 0));
		shipgrid.setPreferredSize(new Dimension(sizer * gridSize, sizer * gridSize));

		for (int y = 0; y < gridSize; y++)
		{
			for (int x = 0; x < gridSize; x++)
			{
				SelectionPanel panel = new SelectionPanel(x, y);
				if(color != null) {
					panel.setBackground(color);
				}
				else if(x == 3 || y == 3)
				{
					panel.setBackground(new Color(225, 225, 225));
				}
				else
				{
					panel.setBackground(Color.WHITE);
				}
				shipgrid.add(panel);
				modulePanels.add(panel);
			}
		}
		add(shipgrid);
	}
	
	public ModuleCreatorPanel() {
		this(7, null);
	}

	public List<SelectionPanel> getModulePanels()
	{
		return new ArrayList<>(modulePanels);
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

	public Map<Integer, Module> getModules()
	{
		Map<Integer, Module> modules = new HashMap<>();
		for (SelectionPanel panel : modulePanels)
		{
			Module module = (Module) panel.getMoveableItem();
			int key = panel.getShipY() * gridSize + panel.getShipX();
			if (!module.isBlankModule())
			{
				modules.put(key, module.copy());
			}
		}
		return modules;
	}

	public void setModules(Ship ship)
	{
		for (SelectionPanel panel : modulePanels)
		{
			int key = panel.getShipY() * gridSize + panel.getShipX();
			Module module = ship.getModule(key);
			panel.setMoveableItem(module.copy());
		}
	}

}
