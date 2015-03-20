package fr.car.config;

public interface RmiConfiguration extends PropertiesUtility {
	
		PropertiesUtility INSTANCE = new PropertiesUtilityImpl(
				RmiConfiguration.class.getResourceAsStream("rmi_config.ini"));
		}
