package edu.cmu.cs.cs214.hw5.core.data;

import edu.cmu.cs.cs214.hw5.core.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CensusPlugin -- A data plugin that extracts state population data from the US Government's Census Data API.
 * All population data is from the year 2014.
 */
public class CensusPlugin implements DataPlugin {

    private static final String NAME = "Census Data API Plugin";

    private static final String KEY = "aacbd292681e5467547f82c9b23b662f7c2249fc";

    private static final String STATE = "State";

    /**
     * Map of state name to state number
     */
    private Map<String, String> stateMap;

    /**
     * Load state names and numbers from Census Data API
     */
    private void loadStatesFromCensus() throws IOException {
        String url = "https://api.census.gov/data/2014/pep/natstprc?get=STNAME,POP&DATE_=7&for=state:*&key=" + KEY;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;

        stateMap = new TreeMap<>(); // initialize state map

        while ((inputLine = in.readLine()) != null) {
            final String regex = "\\[\"(.*?)\",\".*?\",\".*?\",\"(\\d{2})\"";
            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(inputLine);

            while (matcher.find()) {
                if (matcher.groupCount() == 2) {
                    String stateName = matcher.group(1);
                    String stateNum = matcher.group(2);
                    stateMap.put(stateName, stateNum);
                }
            }
        }
        in.close();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<UserInputConfig> getUserInputConfigs() {
        List<UserInputConfig> options = new ArrayList<>();
        try {
            loadStatesFromCensus();
            assert stateMap.size() != 0; // check that the stateMap is successfully loaded
            options.add(new UserInputConfig(STATE, UserInputType.MULTI_SELECTION, new ArrayList<>(stateMap.keySet())));
            return options;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DataSet loadData(Map<String, List<String>> params) {
        // Check if states are selected.
        List<String> stateList = params.get(STATE);
        if (stateList.isEmpty())
            throw new IllegalArgumentException("Choose states first.");

        try {
            return loadDataFromCensus(stateList);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Error in retrieving US Census data");
        }
    }

    private DataSet loadDataFromCensus(List<String> stateList) throws IOException {
        String stateNums = "";
        for (String state : stateList) {
            String stateNum = stateMap.get(state);
            stateNums = stateNums + stateNum + ",";
        }
        stateNums = stateNums.substring(0, stateNums.length()-1); // get rid of the last comma

        String url = "https://api.census.gov/data/2014/pep/natstprc?get=STNAME,POP&DATE_=7&for=state:" + stateNums +
                "&key=" + KEY;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;
        List<List<Object>> data = new ArrayList<>();

        while ((inputLine = in.readLine()) != null) {
            List<Object> row = new ArrayList<>();
            final String regex = "\\[\\\"(.*?)\",\"(\\d*?)\"";
            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(inputLine);

            while (matcher.find()) {
                if (matcher.groupCount() == 2) {
                    String stateName = matcher.group(1);
                    String statePop = matcher.group(2);
                    row.add(stateName);
                    row.add(Integer.parseInt(statePop));
                    data.add(row);
                }
            }
        }
        in.close();

        List<String> labels = new ArrayList<>();
        List<DataType> types = new ArrayList<>();
        labels.add("State");
        types.add(DataType.STRING);
        labels.add("Population");
        types.add(DataType.INTEGER);

        return new DataSet(labels, types, data);
    }
}
