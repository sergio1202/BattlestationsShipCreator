package com.ericski.Battlestations.ui;

import static com.ericski.Battlestations.BattlestationColors.Engineering;
import static com.ericski.Battlestations.BattlestationColors.Science;
import static com.ericski.Battlestations.TileFactory.INSTANCE;

import com.ericski.Battlestations.Tile;
import com.l2fprod.common.swing.JOutlookBar;
import java.awt.Color;
import java.awt.Dimension;
import static javax.swing.Box.createVerticalStrut;
import javax.swing.BoxLayout;
import static javax.swing.BoxLayout.Y_AXIS;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TileSelectorBar extends JOutlookBar {

	private static final long serialVersionUID = 1L;
	private static final int MODULESIZE = 64;

	public TileSelectorBar() {
		super(RIGHT);
		JPanel allPanel = new JPanel();
		allPanel.setLayout(new BoxLayout(allPanel, Y_AXIS));

		for (Tile tile : INSTANCE.getAllTiles()) {
			if (!"obsolete".equals(tile.getTileType())) {
				allPanel.add(createVerticalStrut(3));
				PlanetSelectionPanel msp = new PlanetSelectionPanel(-1, -1, false);
				msp.setMoveableItem(tile);
				msp.setWidth(MODULESIZE);
				msp.setHeight(MODULESIZE);
				msp.setShowToolTip(true);
				allPanel.add(msp);
			}
		}
		allPanel.add(createVerticalStrut(3));
		JScrollPane allScroller = new JScrollPane(allPanel);
		allScroller.setPreferredSize(new Dimension(84, 256));
		allScroller.setMinimumSize(new Dimension(84, 128));
		addTab("All", allScroller);

		JScrollPane tileScroller = getSelectPanel("tiles", null);
		addTab("Tiles", tileScroller);


		JScrollPane moduleScroller = getSelectPanel("modules", Engineering.getColor());
		addTab("Modules", moduleScroller);

		JScrollPane otherScroller = getSelectPanel("other", Science.getColor());
		addTab("Other", otherScroller);

		setSelectedIndex(1);
	}

	private JScrollPane getSelectPanel(String type, Color color) {
		PlanetSelectionPanel msp;
		JPanel panel = new JGradPanel();
		panel.setLayout(new BoxLayout(panel, Y_AXIS));
		if(color != null) {
			panel.setBackground(color);
		}
		for (Tile m : INSTANCE.getAllTilesForType(type)) {
			panel.add(createVerticalStrut(3));
			msp = new PlanetSelectionPanel(-1, -1, false);
			msp.setMoveableItem(m);
			msp.setWidth(MODULESIZE);
			msp.setHeight(MODULESIZE);
			msp.setShowToolTip(true);
			panel.add(msp);
		}
		panel.add(createVerticalStrut(3));
		JScrollPane pilotScroller = new JScrollPane(panel);
		pilotScroller.setPreferredSize(new Dimension(84, 256));
		pilotScroller.setMinimumSize(new Dimension(84, 128));
		return pilotScroller;
	}
}
