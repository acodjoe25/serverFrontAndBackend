package edu.brown.cs.student.main.server.broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import okio.Buffer;

/**
 * Class that handles all of the calls that the program makes to obtain information from the
 * Census API.
 */

public class CensusBroadbandDataSource implements BroadbandDatasource {

  private final HashMap<String,String> stateCodeMap;

  /**
   * Constructor populates a hashmap mapping state to their state codes by calling the resolveStaeCode
   * method.
   * @throws DatasourceException
   */

  public CensusBroadbandDataSource() throws DatasourceException {

    this.stateCodeMap = this.resolveStateCode();

  }

  /**
   * Method implemented from the BroadbandDatasource interface. Simply takes in a location, and uses
   * it to call the non-mock (actually connected to the API) get broadBandPercent method.
   * @param location
   * @return
   */
  @Override
  public BroadbandAccessPercent getBroadbandPercent(Location location){
    try {
      return this.getBroadbandPercent(location.stateName(), location.countyName());
    } catch(DatasourceException e){
      return null;
    }
  }

  /**
   * Returns the percentage of broadband access in an inputted state and county in the form of a
   * Record. That record represents a single number that is the percentage.
   * @param stateName
   * @param countyName
   * @return
   * @throws DatasourceException
   */
  private BroadbandAccessPercent getBroadbandPercent(String stateName, String countyName) throws DatasourceException {

    // Check for errors with the state and county names before determining them using helpers.
    String stateCode;
    if (stateName == null || !(this.stateCodeMap.containsKey(stateName))) //checks to see if the state inputted is a key in the state to code hashmap
      throw new DatasourceException("Invalid state name: " + stateName);
    try {
      stateCode = this.stateCodeMap.get(stateName); //store the code that corresponds to the state
    } catch (NumberFormatException e) {
      throw new DatasourceException("Invalid state code for state: " + stateName);
    }
    String countyCode = this.resolveCountyCode(stateCode, stateName, countyName); //store the code that corresponds to the county using the state code
    try {
      // Build and connect to URL, feeding in above codes
      URL requestURL = new URL("https", "api.census.gov",
          "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
              + countyCode + "&in=state:" + stateCode);

      HttpURLConnection clientConnection = connect(requestURL); //request using the state and county codes to get the broadband percent in that region

      // Build Moshi and adapter
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List<List<String>>> adapter = moshi.adapter( //adapter used to turn json body into a list of list of strings
          Types.newParameterizedType(List.class, List.class, String.class));
      List<List<String>> data = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

      // Disconnect from the URL
      clientConnection.disconnect();

      // Null- and negative-checking
      if (data == null || data.size() > 2 || data.get(1) == null || data.get(1).get(1) == null) {
        throw new DatasourceException("Malformed response from Census");
      }

      return new BroadbandAccessPercent(Double.parseDouble(data.get(1).get(1))); //return the part of the JSON output that contains the broadband percent

    } catch(IOException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  /**
   * Method that returns a string representing the code corresponding with the relevant county the
   * user is searching for. Takes in the state code, and name of the county and state.
   * @param stateCode
   * @param stateName
   * @param countyName
   * @return
   * @throws DatasourceException
   */
  public String resolveCountyCode(String stateCode, String stateName, String countyName) throws DatasourceException {
    if (countyName == null) { //null checking the inputted county name parameter
      throw new DatasourceException("Invalid county name.");
    }
    try {
      URL requestURL = new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode);
      HttpURLConnection clientConnection = connect(requestURL); //request url using the inputted state code to get the database of all counties in the inputted state
      Type stringMap = Types.newParameterizedType(List.class, List.class, String.class);
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(stringMap); //adapter used to deserialize JSON database into a list of list of strings
      List<List<String>> countyList = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();

      if (countyList == null) //checks to see if database was null
        throw new DatasourceException("Received a malformed response from Census when trying to "
            + "resolve county code for " + countyName + " in " + stateName);
      for (List<String> countyData : countyList) { //iterate through each list containing data on one of many counties within the inputted state
        String query = countyName + ", " + stateName;
        if (countyData.get(0).equals(query)) { //if list contains the name of the inputted state and county name
          return countyData.get(2); //return the county code
        }
      }
    } catch(IOException e) {
      throw new DatasourceException(e.getMessage()); }

    throw new DatasourceException(countyName + " was not found in the state you requested.");
  }

  /**
   * This method is used to return a hashmap that maps every state to their respective codes. This is
   * vital because the broadband API call only takes in ID codes, not the names.
   * @return
   * @throws DatasourceException
   */
  public HashMap<String, String> resolveStateCode() throws DatasourceException {
    try {
      URL requestURL = new URL("https", "api.census.gov",
          "/data/2010/dec/sf1?get=NAME&for=state:*");
      HttpURLConnection clientConnection = connect(requestURL); //reequest the database that contains all of the data of states and their id codes
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(
          Types.newParameterizedType(List.class, List.class, String.class)); //adapter used to deserialize JSON data to a list of list of strings
      List<List<String>> stateCodeList = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (stateCodeList == null) { //check to see if database is null
        throw new DatasourceException("Malformed response from Census");
      }

      // Turn List of Lists into HashSet, skipping header row
      HashMap<String, String> stateCodeMap = new HashMap<>(); //make an empty hashmap
      for (List<String> stateCode : stateCodeList.subList(1, stateCodeList.size())) { //get only the data (not headers)
        stateCodeMap.put(stateCode.get(0), stateCode.get(1)); //for each state, add the state name as a key and add its code as the corresponding value to the hashmap
      }
      return stateCodeMap; //return the hashmap

    } catch(IOException | DatasourceException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  /**
   * Static method that handles request URL connections.
   * @param requestURL
   * @return
   * @throws DatasourceException
   * @throws IOException
   */
  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection)) {
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    }
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect();
    if (clientConnection.getResponseCode() != 200) {
      throw new DatasourceException("Unexpected API connection status: "
          + clientConnection.getResponseMessage() + "for URL: " + requestURL);
    }
    return clientConnection;
  }


}
