package edu.cmu.cs.cs214.hw5.core;

import java.util.List;

/**
 * DataPlugin - interface for data plugin
 */
public interface DataPlugin {
    /**
     * Getter for the name of the plugin
     * @return the name of the data plugin
     */
    String getName();

    /**
     * Getter for instructions
     * @return a brief description on how to use the visual plugin
     */
    String getDescription();

    /**
     * Allows the plugin to do any required setup when it is registered
     */
    void onRegister();

    /**
     * Retrieves the data for the framework to process from a source specified in params
     * @param params the source of the data to retrieve
     * @return a list of text
     * @throws Exception if the data could not be retrieved
     */
    List<String> getData(List<String> params) throws Exception;



}
