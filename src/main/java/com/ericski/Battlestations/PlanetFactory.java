package com.ericski.Battlestations;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 *
 */
public enum PlanetFactory {
	INSTANCE;

	public List<Planet> getStandardTemplates() {
		return getStandardTemplatesV1();
	}

	private List<Planet> getStandardTemplatesV1() {
		InputStream xmlStream = Planet.class.getResourceAsStream("/com/ericski/Battlestations/CityTemplates.xml");
		InputStreamReader reader = new InputStreamReader(xmlStream);
		return Planet.fromCitiesXML(reader);
	}

}
