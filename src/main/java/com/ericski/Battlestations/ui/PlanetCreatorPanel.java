package com.ericski.Battlestations.ui;

//import com.ericski.Battlestations.Module;
import com.ericski.Battlestations.Planet;
import com.ericski.Battlestations.MoveableItem;
import com.ericski.Battlestations.SpeciesFactory;
import com.ericski.Battlestations.Tile;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class PlanetCreatorPanel extends ShipCreatorPanel
{
	private static final long serialVersionUID = 1L;

	JComboBox<String> planetTemplates;
	JTextField planetName;
	JComboBox<String> planetRegistry;
	TileCreatorPanel tilePanel;
	JTextArea planetNotes;

	public PlanetCreatorPanel()
	{
		super(new BorderLayout());

		JPanel topHolder = new JPanel();
		topHolder.setLayout(new GridLayout(2, 4, 1, 4));
		topHolder.setBorder(BorderFactory.createTitledBorder("Planet Details"));

		topHolder.add(new JLabel("Name", JLabel.CENTER));
		topHolder.add(planetName = new JTextField());
		
		topHolder.add(new JLabel("Planet", JLabel.CENTER));
		planetRegistry = new JComboBox<>();
		planetRegistry.setEditable(true);
		planetRegistry.addItem("");
		for (String name : SpeciesFactory.getInstance().getSpeciesNames())
		{
			planetRegistry.addItem(name);
		}
		planetRegistry.setSelectedIndex(0);
		topHolder.add(planetRegistry);
		
		JPanel mid = new JPanel(new BorderLayout());
		mid.add(topHolder, BorderLayout.NORTH);

		tilePanel = new TileCreatorPanel();
		JScrollPane planetscrollee = new JScrollPane(tilePanel);
		planetscrollee.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		planetscrollee.setAutoscrolls(true);
		planetscrollee.getVerticalScrollBar().setBlockIncrement(PlanetSelectionPanel.DEFAULTSIZE);
		planetscrollee.getHorizontalScrollBar().setBlockIncrement(PlanetSelectionPanel.DEFAULTSIZE);
		mid.add(planetscrollee, BorderLayout.CENTER);

		JPanel bottomHolder = new JPanel();
		bottomHolder.setLayout(new BorderLayout());
		bottomHolder.setBorder(BorderFactory.createTitledBorder("Notes"));

		planetNotes = new JTextArea(4, 40);
		planetNotes.setMaximumSize(new Dimension(2000, 100));
		planetNotes.setLineWrap(true);
		planetNotes.setWrapStyleWord(true);
		JScrollPane noteScrollee = new JScrollPane(planetNotes);
		noteScrollee.setAutoscrolls(true);
		bottomHolder.add(noteScrollee, BorderLayout.CENTER);

		mid.add(bottomHolder, BorderLayout.SOUTH);

		add(mid, BorderLayout.CENTER);
		add(new TileSelectorBar(), BorderLayout.WEST);
		//add(new StandardCityBar(this), BorderLayout.EAST);

	}

	@Override
	public Dimension getMinimumSize()
	{
		return tilePanel.getMinimumSize();
	}

	public void setPlanet(final Planet planetToSet)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				planetNotes.setText(planetToSet.getNotesAsString());
				planetName.setText(planetToSet.getName());

				boolean changed = false;
				for (int i = 0; i < planetRegistry.getItemCount(); i++)
				{
					if (planetRegistry.getItemAt(i).equals(planetToSet.getSpecies()))
					{
						planetRegistry.setSelectedIndex(i);
						changed = true;
						break;
					}
				}
				if (!changed)
				{
					planetRegistry.addItem(planetToSet.getSpecies());
					planetRegistry.setSelectedIndex(planetRegistry.getItemCount() - 1);
				}
				tilePanel.setTiles(planetToSet);
			}
		});
	}

	public Planet generatePlanet()
	{
		//System.out.println("\n\n\tGenerate it\n");
		Planet planet = new Planet();
		planet.setName(planetName.getText());
		if (!planetNotes.getText().isEmpty())
		{
			planet.setNotes(planetNotes.getText());
		}
		planet.setSpecies(planetRegistry.getSelectedItem().toString());
		for (PlanetSelectionPanel panel : tilePanel.getTilePanels())
		{
			MoveableItem tile = panel.getMoveableItem();
			int key = panel.getGridY() * 3 + panel.getGridX();
			if (!tile.isBlankMoveableItem())
			{
				planet.addTile((Tile)tile, key);
				
			}
		}
		return planet;
	}

}
