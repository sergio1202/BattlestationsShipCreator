package com.ericski.Battlestations.ui;

import com.ericski.Battlestations.Module;
import com.ericski.Battlestations.ModuleFactory;
import com.ericski.Battlestations.MoveableItem;
import com.ericski.Battlestations.Tile;
import com.ericski.Battlestations.TileFactory;
import com.ericski.ui.ImageIconScalable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

public class PlanetSelectionPanel extends JPanel {

	public static final int DEFAULTSIZE = 50 * 5;
	private static final boolean DEBUG = false;
	private static final String IMAGEACTION = "IMAGEACTION";
	private static final String DELETEACTION = "DELETE";
	private static final String DELETETILEACTION = "DELETETILE";
	private static final long serialVersionUID = 1L;
	private static final String ROTATECWACTION = "ROTATECWACTION";
	private static final String ROTATECCWACTION = "ROTATECCWACTION";
	private static final String ZOOMACTION = "ZOOMDACTION";
	private static final String FLIPACTION = "FLIPACTION";
	private Image image;
	private int width = DEFAULTSIZE;
	private int height = DEFAULTSIZE;
	private int clickXOffset = 0;
	private int clickYOffset = 0;
	private long angle;
	private Dimension dim;
	private JPopupMenu menu;
	private boolean canRotate = false;
	private boolean draggable = false;
	private int gridX;
	private int gridY;
	private MoveableItem tile = null;
	private MoveableItem flippedTile = null;

	private JMenuItem flipMenu = null;
	private int scrollWheelie = 0;
	private boolean showTooltip;
	private JMenuItem nameItem;
	private boolean showZoom = true;
	// private List<PlanetSelectionPanel> flipPanels = null;

	public MoveableItem getMoveableItem() {
		return tile;
	}

	public int getGridX() {
		return gridX;
	}

	public void setGridX(int shipX) {
		this.gridX = shipX;
	}

	public int getGridY() {
		return gridY;
	}

	public void setGridY(int shipY) {
		this.gridY = shipY;
	}

	public boolean isDraggable() {
		return draggable;
	}

	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
	}

	public void setShowToolTip(boolean showToolTip) {
		this.showTooltip = showToolTip;
		if (showToolTip) {
			setToolTipText(tile.getDescription());
		}
	}

	public boolean isShowToolTip() {
		return showTooltip;
	}

	public PlanetSelectionPanel() {
		this(0, 0);
	}

	public PlanetSelectionPanel(boolean showZoom) {
		this(0, 0, true, showZoom);
	}

	public PlanetSelectionPanel(int shipX, int shipY) {
		this(shipX, shipY, true, true);
	}

	public PlanetSelectionPanel(int shipX, int shipY, boolean addMenu) {
		this(shipX, shipY, addMenu, true);
	}

	public PlanetSelectionPanel(int gridX, int gridY, boolean addMenu, boolean showZoom) {
		super(new FlowLayout());
		this.gridX = gridX;
		this.gridY = gridY;
		tile = ModuleFactory.getBlankModule();
		this.showZoom = showZoom;

		if ((gridX) % 2 == 0 ^ (gridY) % 2 == 0) {
			setBackground(new Color(225, 225, 225));
		} else {
			setBackground(Color.WHITE);
		}
		dim = new Dimension(width, height);

		// this.add(new ModuleCreatorPanel(5, getBackground()));

		if (addMenu) {
			addMenu();

		}

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				canRotate = false;
				PlanetSelectionPanelActionController.getInstance().removeActee((PlanetSelectionPanel) e.getComponent());
				clickXOffset = 0;
				clickYOffset = 0;
				repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				PlanetSelectionPanelActionController.getInstance().setActee((PlanetSelectionPanel) e.getComponent());
				canRotate = true;
				requestFocusInWindow();
				repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (menu != null) {
					if (e.getClickCount() == 2) {
						// System.out.print(e.getX() + " X " + getWidth());
						if (e.getX() > (getWidth() / 3)) {
							// System.out.println(" Rotate CW");
							setAngle((getAngle() + 90) % 360);
						} else if (e.getX() < (getWidth() / 3)) {
							// System.out.println(" Rotate CCW");
							setAngle((getAngle() - 90) % 360);
						}
					}
				}

			}

			@Override
			public void mousePressed(MouseEvent evt) {
				if (menu != null && evt.isPopupTrigger()) {
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				} else {
					PlanetSelectionPanelActionController.getInstance()
							.setActor((PlanetSelectionPanel) evt.getComponent(), evt.isAltDown());
					clickXOffset = evt.getX();
					clickYOffset = evt.getY();
				}

			}

			@Override
			public void mouseReleased(MouseEvent evt) {
				if (menu != null && evt.isPopupTrigger()) {
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				} else {
					ModuleSelectionPanelAction action = PlanetSelectionPanelActionController.getInstance().getAction();
					if (action == ModuleSelectionPanelAction.ROTATE) {
						dragRotate(evt.getX(), evt.getY());
					} else if (action == ModuleSelectionPanelAction.SWAP) {
						swap();
					} else if (action == ModuleSelectionPanelAction.DELETE) {
						PlanetSelectionPanelActionController.getInstance().clearAction();
						setMoveableItem(ModuleFactory.getBlankModule());
					} else if (action == ModuleSelectionPanelAction.ADD) {
						PlanetSelectionPanelActionController.getInstance().clearAction();
						PlanetSelectionPanelActionController.getInstance().getActee().setMoveableItem(
								PlanetSelectionPanelActionController.getInstance().getActor().getMoveableItem().copy());

						repaint();
					} else {
						System.out.println("unknown action: " + action);
					}
					clickXOffset = 0;
					clickYOffset = 0;
				}
			}
		});
	}

	private void addMenu() {
		ActionListener actionListener = new Actioner();

		setFocusable(true);
		addKeyListener(new KeyHandler());
		addMouseWheelListener(new Mouser());

		menu = new JPopupMenu();

		nameItem = new JMenuItem(tile.getDescription());
		menu.add(nameItem);
		menu.add(new JSeparator());

		URL url;
		ImageIcon icon;

		if (showZoom) {
			JMenuItem zoomMi = new JMenuItem("Zoom");
			url = getClass().getResource("/toolbarButtonGraphics/general/Zoom24.gif");
			icon = new ImageIcon(url);
			zoomMi.setIcon(icon);
			zoomMi.setActionCommand(ZOOMACTION);
			zoomMi.addActionListener(actionListener);
			menu.add(zoomMi);

			menu.add(new JSeparator());
		}

		/*
		 * if (flipMenu == null) { flipMenu = new JMenu("Flip Tile"); }
		 * menu.add(flipMenu); menu.add(new JSeparator());
		 */
		JMenuItem rotateCW = new JMenuItem("Rotate Right");
		rotateCW.setActionCommand(ROTATECWACTION);
		rotateCW.addActionListener(actionListener);
		menu.add(rotateCW);

		JMenuItem rotateCCW = new JMenuItem("Rotate Left");
		rotateCCW.setActionCommand(ROTATECCWACTION);
		rotateCCW.addActionListener(actionListener);
		menu.add(rotateCCW);

		menu.add(new JSeparator());

		JMenuItem zeroDegree = new JMenuItem("Face Forward");
		url = getClass().getResource("/toolbarButtonGraphics/navigation/Up24.gif");
		icon = new ImageIcon(url);
		zeroDegree.setIcon(icon);
		zeroDegree.setActionCommand("0");
		zeroDegree.addActionListener(actionListener);
		menu.add(zeroDegree);

		JMenuItem ninetyDegree = new JMenuItem("Face Right");
		url = getClass().getResource("/toolbarButtonGraphics/navigation/Forward24.gif");
		icon = new ImageIcon(url);
		ninetyDegree.setIcon(icon);
		ninetyDegree.setActionCommand("90");
		ninetyDegree.addActionListener(actionListener);
		menu.add(ninetyDegree);

		JMenuItem oneeightyDegree = new JMenuItem("Face Backwards");
		url = getClass().getResource("/toolbarButtonGraphics/navigation/Down24.gif");
		icon = new ImageIcon(url);
		oneeightyDegree.setIcon(icon);
		oneeightyDegree.setActionCommand("180");
		oneeightyDegree.addActionListener(actionListener);
		menu.add(oneeightyDegree);

		JMenuItem twoseventyDegree = new JMenuItem("Face Left");
		url = getClass().getResource("/toolbarButtonGraphics/navigation/Back24.gif");
		icon = new ImageIcon(url);
		twoseventyDegree.setIcon(icon);
		twoseventyDegree.setActionCommand("270");
		twoseventyDegree.addActionListener(actionListener);
		menu.add(twoseventyDegree);

		menu.add(new JSeparator());

		JMenuItem deleteItem = new JMenuItem("Delete");
		deleteItem.setActionCommand(DELETEACTION);
		deleteItem.addActionListener(actionListener);
		menu.add(deleteItem);

		menu.add(new JSeparator());

		JMenu subMenu = new JMenu("Modules");
		menu.add(subMenu);

		for (Module m : ModuleFactory.INSTANCE.getAllModules()) {
			if (!m.isBlankModule()) {
				subMenu.add(createMenuItem(m, Color.WHITE, actionListener));
			}
		}
	}

	private void rotateCW() {
		setAngle((getAngle() + 90) % 360);
	}

	private void rotateCCW() {
		setAngle((getAngle() - 90) % 360);
	}

	private void swap() {
		PlanetSelectionPanel acteePanel = PlanetSelectionPanelActionController.getInstance().getActee();
		PlanetSelectionPanel actorPanel = PlanetSelectionPanelActionController.getInstance().getActor();
		
		MoveableItem tempFront = acteePanel.getMoveableItem();
		MoveableItem tempBack = acteePanel.flippedTile;
		

		//acteePanel.tile = actorPanel.tile;
		acteePanel.setMoveableItem(actorPanel.getMoveableItem());
		acteePanel.flippedTile = actorPanel.flippedTile;
		//actorPanel.tile = tempFront;
		actorPanel.setMoveableItem(tempFront);
		actorPanel.flippedTile = tempBack;


		PlanetSelectionPanelActionController.getInstance().clearAction();
		getParent().repaint();
	}
	
	

	private void dragRotate(int x, int y) {
		if (canRotate && (image != null)) {
			boolean xChangedCW = (clickYOffset < (getHeight() / 2)) ? (clickXOffset - x) < 0 : (clickXOffset - x) > 0;
			boolean yChangedCW = (clickXOffset < (getWidth() / 2)) ? (clickYOffset - y) > 0 : (clickYOffset - y) < 0;

			int xMagnitude = Math.abs(clickXOffset - x);
			int yMagnitude = Math.abs(clickYOffset - y);

			if ((xMagnitude > getWidth() / 8.0) || (yMagnitude > getHeight() / 8.0)) {
				if (xMagnitude > yMagnitude) {
					if (xChangedCW) {
						rotateCW();
					} else {
						rotateCCW();
					}
				} else if (yChangedCW) {
					rotateCW();
				} else {
					rotateCCW();
				}
			}
		}
		PlanetSelectionPanelActionController.getInstance().clearAction();
	}

	public Image getImage() {
		return image;
	}

	public void setImage(String path) {
		URL url = this.getClass().getResource(path);
		image = Toolkit.getDefaultToolkit().getImage(url);
		this.repaint();
	}

	public void setImage(Image image) {
		this.image = image;
		this.repaint();
	}

	private void updateFlipMenu() {
		if (flipMenu == null) {
			flipMenu = new JMenu("Flip Tile");
		}
		flipMenu.removeAll();
		if (tile.getName() != "blank") {
			Set<String> flipTileList = TileFactory.INSTANCE.getFlipTiles(tile.getName());
			if (flipTileList == null) {
				System.out.println("ERROR:  no flip tile for " + tile);
				return;
			}
			ActionListener actionListener = new Actioner();
			for (String name : flipTileList) {
				Tile m = TileFactory.INSTANCE.getTileByName(name);
				flipMenu.add(createMenuItem(m, Color.WHITE, actionListener));
			}
		}

	}

	public void setTwoSidedMoveableItem(MoveableItem front, MoveableItem back) {
		//this.tile = front;
		this.flippedTile = back;
		setMoveableItem(front);
		//System.out.println("setting front to " + front.getDescription() + " back back to " + back.getDescription());
	}

	public void setMoveableItem(MoveableItem module) {
		this.tile = module.copy();
		updateFlipMenu();
		//flippedTile = null;

		if (showTooltip) {
			setToolTipText(module.getDescription());
		}
		if (module.isBlankMoveableItem()) {
			deleteModule();
		} else {
			if (nameItem != null) {
				nameItem.setText(module.getDescription());
			}
			setAngle(module.getRotation());
			Image loadedImage = module.getNonRotatedImage();
			setImage(loadedImage);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image == null) {
			if (menu != null && canRotate) {
				g.setColor(Color.RED);
			}
			g.drawRect(0, 0, width - 1, height - 1);
			g.drawRect(1, 1, width - 2, height - 2);
			if (DEBUG) {
				g.drawString(Integer.toString(gridY * 7 + gridX), getWidth() / 2, getHeight() / 2);
			}
		} else {
			Graphics2D g2 = (Graphics2D) g;
			g2.rotate(Math.toRadians(tile.getRotation()), width / 2, height / 2);
			g2.drawImage(image, 0, 0, width, height, this);
			addDescription(g);

			if (menu != null && canRotate) {
				g2.setColor(Color.RED);
				g2.drawRect(0, 0, width - 1, height - 1);
				g2.drawRect(1, 1, width - 2, height - 2);
			}
			g2.dispose();
		}
	}

	private void addDescription(Graphics g) {
		if (menu != null) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Serif", Font.PLAIN, 30));
			g.drawString(tile.getDescription(), (getWidth() / 20) + 3, (getHeight() / 10) + 3);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Serif", Font.PLAIN, 30));
			g.drawString(tile.getDescription(), getWidth() / 20, getHeight() / 10);
		}

	}

	@Override
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		dim = new Dimension(width, height);
		this.height = height;
	}

	@Override
	public Dimension getMaximumSize() {
		return dim;
	}

	@Override
	public Dimension getMinimumSize() {
		return dim;
	}

	@Override
	public Dimension getPreferredSize() {
		return dim;
	}

	@Override
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		dim = new Dimension(width, height);
		this.width = width;
	}

	public void setAngle(long degrees) {
		if (degrees < 0) {
			this.angle = 360 + degrees;
		} else {
			this.angle = degrees;
		}

		if (angle != tile.getRotation()) {
			tile.setRotation((int) angle);
			repaint();
		}
	}

	public long getAngle() {
		return angle;
	}

	private void deleteModule() {
		setImage((Image) null);
		tile = ModuleFactory.getBlankModule();
		if (nameItem != null) {
			nameItem.setText(tile.getDescription());
		}
	}

	private void deleteTile() {
		setImage((Image) null);
		tile = ModuleFactory.getBlankModule();
		if (nameItem != null) {
			nameItem.setText(tile.getDescription());
		}
	}

	private void flipTile() {

		PlanetSelectionPanel actorPanel = PlanetSelectionPanelActionController.getInstance().getActee();
		if (actorPanel == null) {
			return;
		}
		MoveableItem back = actorPanel.getFlipTile();
		if (back != null) {
			//System.out.println("saving front " + back.getDescription());
			MoveableItem front = tile;
			//System.out.println("setting to " + back.getDescription());
			actorPanel.setMoveableItem(back);
			flippedTile = front;
			PlanetSelectionPanelActionController.getInstance().clearAction();
			getParent().repaint();
			updateFlipMenu();
		}
	}

	private MoveableItem getFlipTile() {
		// we're going to flip to the other side
		MoveableItem flipTile = flippedTile;
		// if we had another side, return it
		if (flipTile != null) {
			return flipTile;
		}
		// get the other side
		if (tile != null && !tile.isBlankMoveableItem()) {
			Set<String> tiles = TileFactory.INSTANCE.getFlipTiles(tile.getName());

			if (tiles.size() > 0) {
				flipTile = TileFactory.INSTANCE.getTileByName(tiles.toArray()[0].toString());
			}
		}
		return flipTile;
	}

	private void zoom() {
		if (!tile.isBlankMoveableItem()) {
			ModuleZoomDialog mzd = new ModuleZoomDialog(tile);
			mzd.setModal(true);
			int x = getLocationOnScreen().x - (mzd.getWidth() / 2);
			int y = getLocationOnScreen().y - (mzd.getHeight() / 2);
			mzd.setLocation(x, y);
			mzd.setVisible(true);
			setAngle(tile.getRotation());
		}
	}

	private JMenuItem createMenuItem(MoveableItem module, Color color, ActionListener actionListener) {
		JMenuItem menuItem = new JMenuItem();
		menuItem.setBackground(color);
		ImageIconScalable icon = new ImageIconScalable(module.getNonRotatedImage());
		icon.setScaledSize(32, 32);
		menuItem.setIcon(icon);
		menuItem.setActionCommand(IMAGEACTION + "_" + module.getName());
		menuItem.addActionListener(actionListener);
		menuItem.setText(module.getDescription());
		return menuItem;
	}

	private class Actioner implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {

			String action = ae.getActionCommand();
			System.out.println(action);
			if (action.equals(DELETEACTION)) {
				deleteModule();
			} else if (action.equals(DELETETILEACTION)) {
				deleteTile();
			} else if (action.startsWith(IMAGEACTION)) {
				if (ae.getSource() instanceof JMenuItem) {

					JMenuItem src = (JMenuItem) ae.getSource();
					if (src.getIcon() != null) {
						setImage(((ImageIcon) src.getIcon()).getImage());
					}
					String moduleName = action.substring(IMAGEACTION.length() + 1);
					tile.setName(moduleName);
					nameItem.setText(src.getText());
					updateFlipMenu();
				}
			} else if (action.equals(ROTATECWACTION)) {
				rotateCW();
				repaint();
			} else if (action.equals(ROTATECCWACTION)) {
				rotateCCW();
				repaint();
			} else if (action.equals(ZOOMACTION)) {
				zoom();
			} else if (action.equals(FLIPACTION)) {
				flipTile();
			} else {
				try {
					long rotate = Long.parseLong(ae.getActionCommand());
					setAngle(rotate);
				} catch (NumberFormatException iggy) {
				}
			}
		}
	}

	private class Mouser implements MouseWheelListener {

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getWheelRotation() < 0) {
				if (scrollWheelie > 0) {
					scrollWheelie = 0;
				}
				if (--scrollWheelie < -2) {
					rotateCCW();
					scrollWheelie = 0;
				}
			} else if (e.getWheelRotation() > 0) {
				if (scrollWheelie < 0) {
					scrollWheelie = 0;
				}
				if (++scrollWheelie > 2) {
					rotateCW();
					scrollWheelie = 0;
				}
			}

		}
	}

	private class KeyHandler implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			// no op
		}

		@Override
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_DELETE:
				deleteModule();
				break;
			case KeyEvent.VK_RIGHT:
				setAngle(90);
				break;
			case KeyEvent.VK_LEFT:
				setAngle(270);
				break;
			case KeyEvent.VK_UP:
				setAngle(0);
				break;
			case KeyEvent.VK_DOWN:
				setAngle(180);
				break;
			case KeyEvent.VK_EQUALS:
				rotateCW();
				break;
			case KeyEvent.VK_MINUS:
				rotateCCW();
				break;
			case KeyEvent.VK_F:
				flipTile();
				break;
			case KeyEvent.VK_Z:
				if (showZoom) {
					zoom();
				}
				break;
			default:
				break;
			}

		}

		@Override
		public void keyTyped(KeyEvent e) {
			// no op
		}
	}
}
