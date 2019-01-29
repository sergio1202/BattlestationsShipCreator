package com.ericski.Battlestations;

import static com.ericski.Battlestations.Tile.BLANK;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum TileFactory {
	INSTANCE;

	private final Logger logger;
	private final Map<String, Tile> nameMap;
	private final Set<String> tileType;
	private File userDir;
	private Map<String, Set<String>> flipMap;

	private TileFactory() {
		logger = LogManager.getLogger(TileFactory.class);
		nameMap = new HashMap<>();
		tileType = new HashSet<>();
		userDir = getTileDirectory();
		flipMap = new HashMap<>();

		addTile("A", "A1", "tiles");
		addTile("lowercase_a_", "A2", "tiles");
		addTile("B", "B1", "tiles");
		addTile("lowercase_b_", "B2", "tiles");
		addTile("C", "C1", "tiles");
		addTile("lowercase_c_", "C2", "tiles");
		addTile("D", "D1", "tiles");
		addTile("lowercase_d_", "D2", "tiles");
		addTile("E", "E1", "tiles");
		addTile("lowercase_e_", "E2", "tiles");
		addTile("F", "F1", "tiles");
		addTile("lowercase_f_", "F2", "tiles");
		addTile("G", "G1", "tiles");
		addTile("lowercase_g_", "G2", "tiles");
		addTile("H", "H1", "tiles");
		addTile("lowercase_h_", "H2", "tiles");
		addTile("I", "I1", "tiles");
		addTile("lowercase_i_", "I2", "tiles");
		nameMap.put(Tile.BLANK, getBlankTile());

		addFlipTile("A", "lowercase_a_");
		addFlipTile("B", "lowercase_b_");
		addFlipTile("C", "lowercase_c_");
		addFlipTile("D", "lowercase_d_");
		addFlipTile("E", "lowercase_e_");
		addFlipTile("F", "lowercase_f_");
		addFlipTile("G", "lowercase_g_");
		addFlipTile("H", "lowercase_h_");
		addFlipTile("I", "lowercase_i_");
		
		//System.out.println(flipMap);
	}

	private void addFlipTile(String a, String b) {
		if (!flipMap.containsKey(a)) {
			flipMap.put(a, new HashSet<String>());
		}
		flipMap.get(a).add(b);

		if (!flipMap.containsKey(b)) {
			flipMap.put(b, new HashSet<String>());
		}
		flipMap.get(b).add(a);

	}

	public static Tile getBlankTile() {
		return new InternalTile(BLANK, 0, "Empty Space", "general",
				"/com/ericski/Battlestations/Images/Modules/blank.jpg");
	}

	private void addTile(String name, String description, String type) {
		tileType.add(type);

		Tile tile;
		tile = new InternalTile(name, 0, description, type,
				"/com/ericski/Battlestations/Images/Dirtside/" + name + ".jpg");
		// System.out.println("pushing " + name + " into name Map");
		nameMap.put(name, tile);
	}

	public List<String> getTileType() {
		List<String> rtn = new ArrayList<>(tileType);
		Collections.sort(rtn);
		return Collections.unmodifiableList(rtn);
	}

	public Collection<Tile> getAllTiles() {
		List<Tile> rtn = new ArrayList<>();
		for (Tile tile : nameMap.values()) {
			rtn.add(tile.copy());
		}
		Collections.sort(rtn);
		return Collections.unmodifiableCollection(rtn);
	}

	public List<Tile> getAllTilesForType(String tileType) {
		List<Tile> typeTiles = new ArrayList<>();
		for (Tile tile : nameMap.values()) {
			if (tileType.equalsIgnoreCase(tile.getTileType())) {
				typeTiles.add(tile.copy());
			}
		}
		Collections.sort(typeTiles);
		return Collections.unmodifiableList(typeTiles);
	}

	public Tile getTileByName(String nameString) {
		Tile fromMap = nameMap.get(nameString);
		if (fromMap == null) {
			//System.out.println("Name String:\t" + nameString);
			return nameMap.get("blank").copy();
		}
		return fromMap.copy();
	}

	public File getTileDirectory() {
		if (userDir == null) {
			userDir = new File(System.getProperty("user.home") + "/.citycreator/tiles");
			if (!userDir.exists()) {
				userDir.mkdirs();
			}
		}
		return userDir;
	}

	public Set<String> getFlipTiles(String name) {
		return this.flipMap.get(name);
	}

}
