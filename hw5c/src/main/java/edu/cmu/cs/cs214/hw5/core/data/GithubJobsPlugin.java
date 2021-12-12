package edu.cmu.cs.cs214.hw5.core.data;

import edu.cmu.cs.cs214.hw5.core.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * GithubJobsPlugin
 * Uses github jobs platform as a data source for finding number of open software development jobs given a state, and
 * indicates how long ago those jobs were posted in number of days. Only supports states in the US
 */
public class GithubJobsPlugin implements DataPlugin {

    // Name of plugin
    private static final String NAME = "Github Jobs Plugin";
    // Label names
    private static final String STATE = "State";
    private static final String JOBS_LABEL = "Jobs Posted";
    private static final String JOBS_AGE = "Age in Days";
    // States
    private static final String AL = "Alabama";
    private static final String AK = "Alaska";
    private static final String AZ = "Arizona";
    private static final String AR = "Arkansas";
    private static final String CA = "California";
    private static final String CO = "Colorado";
    private static final String CT = "Connecticut";
    private static final String DE = "Delaware";
    private static final String FL = "Florida";
    private static final String GA = "Georgia";
    private static final String HI = "Hawaii";
    private static final String ID = "Idaho";
    private static final String IL = "Illinois";
    private static final String IN = "Indiana";
    private static final String IA = "Iowa";
    private static final String KS = "Kansas";
    private static final String KY = "Kentucky";
    private static final String LA = "Louisiana";
    private static final String ME = "Maine";
    private static final String MD = "Maryland";
    private static final String MA = "Massachusetts";
    private static final String MI = "Michigan";
    private static final String MN = "Minnesota";
    private static final String MS = "Mississippi";
    private static final String MO = "Missouri";
    private static final String MT = "Montana";
    private static final String NE = "Nebraska";
    private static final String NV = "Nevada";
    private static final String NH = "NewHampshire";
    private static final String NJ = "NewJersey";
    private static final String NM = "NewMexico";
    private static final String NY = "NewYork";
    private static final String NC = "NorthCarolina";
    private static final String ND = "NorthDakota";
    private static final String OH = "Ohio";
    private static final String OK = "Oklahoma";
    private static final String OR = "Oregon";
    private static final String PA = "Pennsylvania";
    private static final String RI = "RhodeIsland";
    private static final String SC = "SouthCarolina";
    private static final String SD = "SouthDakota";
    private static final String TN = "Tennessee";
    private static final String TX = "Texas";
    private static final String UT = "Utah";
    private static final String VT = "Vermont";
    private static final String VA = "Virginia";
    private static final String WA = "Washington";
    private static final String WV = "WestVirginia";
    private static final String WI = "Wisconsin";
    private static final String WY = "Wyoming";
    // Error description strings
    private static final String SELECT_STATE_MSG = "Please select a state";
    // Strings needed for GET request
    private static final String PATH = "https://jobs.github.com/positions.json?location=";
    private static final String GET = "GET";
    private static final String PROPERTY_KEY = "User-Agent";
    private static final String PROPERTY_VAL = "Mozilla/5.0";
    // Strings needed for formatting
    private static final String REGEX = "[\\{]+";
    private static final String DATE_FORMAT = "E, MMM dd yyyy";
    // Extrema and indices specific to this plugin
    private static final int YEAR = 2019;
    private static final int MAX_DAY = 29;
    private static final int DATE_IDX = 3;

    private List<String> stateList;

    /**
     * Getter for the name of the data plugin
     * @return the name of the plugin
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Getter for the configurations used to getting input from user. These settings will display a drop down menu
     * so that the user can select a state to query for number of software development jobs (according to github jobs)
     * @return user input configurations
     */
    @Override
    public List<UserInputConfig> getUserInputConfigs() {
        List<UserInputConfig> configuration = new ArrayList<>();
        initStateList();
        configuration.add(new UserInputConfig(STATE, UserInputType.MULTI_SELECTION, stateList));
        return configuration;
    }

    /**
     * Requests the number of software development jobs in states selected by the user, according to github jobs board,
     * and orders the data by number of jobs posted how many days ago with a cap on 29 days (posts older than 30) days
     * are supposed to be removed, but sometimes they persist.
     * @param params a parameter mapping from configuration name to concrete parameters user specifies.
     * @return the data set to be visualized
     */
    @Override
    public DataSet loadData(Map<String, List<String>> params) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        List<String> states = params.get(STATE);
        if (states.isEmpty()) throw new IllegalArgumentException(SELECT_STATE_MSG);
        try {
            List<List<Object>> data = new ArrayList<>();
            for (String state : states) {
                String line;
                Map<Integer, Integer> daysAgoJobs = initDaysAgoMap();
                // get request
                String path = PATH + state;
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(GET);
                connection.setRequestProperty(PROPERTY_KEY, PROPERTY_VAL);

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                List<String> jobs = new ArrayList<>();
                while ((line = br.readLine()) != null) jobs.addAll(Arrays.asList(line.split(REGEX)));
                if (!jobs.isEmpty()) jobs.remove(0);
                for (String job : jobs) {
                    // some really ugly json parsing code to extract the date a job was posted
                    List<String> subStrings = Arrays.asList(job.split(","));
                    List<String> subStrings2 = Arrays.asList(subStrings.get(DATE_IDX).split("\":\""));
                    String unformattedDate = subStrings2.get(1).replace("\"", "");
                    unformattedDate = unformattedDate.replace("UTC ", "");
                    List<String> dateComponents = Arrays.asList(unformattedDate.split(" "));
                    StringBuilder sb = new StringBuilder();
                    sb.append(dateComponents.get(0)); sb.append(", ");
                    sb.append(dateComponents.get(1)); sb.append(" ");
                    sb.append(dateComponents.get(2)); sb.append(" ");
                    sb.append(YEAR);
                    SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
                    // convert date from String to Date
                    try {
                        Date date = formatter.parse(sb.toString());
                        calendar.setTime(date);
                        int daysAgo = Math.min(currentDayOfYear - calendar.get(Calendar.DAY_OF_YEAR), MAX_DAY);
                        daysAgoJobs.replace(daysAgo, daysAgoJobs.get(daysAgo) + 1);
                    } catch (ParseException e) {
                        throw new IllegalArgumentException("failed to parse date");
                    }
                }
                // create "row" element needed for data set
                for (Map.Entry jobsEntry : daysAgoJobs.entrySet()) {
                    List<Object> row = new ArrayList<>();
                    row.add(state);
                    row.add(jobsEntry.getKey());
                    row.add(jobsEntry.getValue());
                    data.add(row);
                }
            }
            List<String> labels = new ArrayList<>();
            List<DataType> types = new ArrayList<>();
            labels.add(STATE);
            types.add(DataType.STRING);
            labels.add(JOBS_AGE);
            types.add(DataType.INTEGER);
            labels.add(JOBS_LABEL);
            types.add(DataType.INTEGER);
            return new DataSet(labels, types, data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Initializes the array used for the state selection
     */
    private void initStateList() {
        stateList = new ArrayList<>();
        stateList.addAll(Arrays.asList(AL, AK, AZ, AR, CA, CO, CT, DE, FL, GA));
        stateList.addAll(Arrays.asList(HI, ID, IL, IN, IA, KS, KY, LA, ME, MD));
        stateList.addAll(Arrays.asList(MA, MI, MN, MS, MO, MT, NE, NV, NH, NJ));
        stateList.addAll(Arrays.asList(NM, NY, NC, ND, OH, OK, OR, PA, RI, SC));
        stateList.addAll(Arrays.asList(SD, TN, TX, UT, VT, VA, WA, WV, WI, WY));
    }

    private Map<Integer, Integer> initDaysAgoMap() {
        Map<Integer, Integer> daysAgoJobsMap = new HashMap<>();
        for (int i = 0; i <= MAX_DAY; i++)
            daysAgoJobsMap.put(i, 0);
        return daysAgoJobsMap;
    }

}
