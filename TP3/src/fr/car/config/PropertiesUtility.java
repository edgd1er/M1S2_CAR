package fr.car.config;
/**
* is used to manage ini properties
*
*/
public interface PropertiesUtility {
/**
* Get property
*
* @param key
* The property token
* @return The property
*/
String getProperty(String key);
/**
* Get property
*
* @param key
* The property token
* @return The int property
*/
int getIntProperty(String key);
/**
* Get property
*
* @param key
* The property token
* @return The boolean property
*/
boolean getBooleanProperty(String key);
}