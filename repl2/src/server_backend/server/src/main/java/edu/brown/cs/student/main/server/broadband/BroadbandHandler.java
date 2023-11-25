package edu.brown.cs.student.main.server.broadband;


import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Broadbandhandler class is responsible with all user queries with the intention of obtaining the
 * broadband percentage data for any given state and county. Information is then displayed back
 * to the user.
 */

public class BroadbandHandler implements Route {
  private String us_state;
  private String county;
  private final BroadbandDatasource datasource;

  /**
   * Constructor takes in a datasource that is used to get realtime data from the Census API.
   * @param datasource
   */
  public BroadbandHandler(BroadbandDatasource datasource) {
    this.datasource = datasource;
  }

  /**
   * Handle method fundamentally deals with the transfer of data to and from the user. It
   * uses query parameters from the user to obtain the State and County that the user
   * is requesting Broadband data from, and then displays it back to the user.
   *
   * @param request
   * @param response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {
    this.us_state = request.queryParams("state").replace("_"," "); //get the state from the user
    this.county = request.queryParams("county").replace("_"," "); //get the county from the user
    Location location = new Location(this.us_state,this.county); //make a new data type (record) that represents the location (this is useful for deserialization)



    Moshi moshi = new Builder().build();
    JsonAdapter<Map<String,Object>> responseAdapter = //adapter that can serialize Hashmap into JSON data
        moshi.adapter(Types.newParameterizedType(Map.class,String.class,Object.class));

    Map<String,Object> responseMap = new HashMap<>(); //hashmap that the

    JsonAdapter<BroadbandAccessPercent> percentAdapter = //adapter that can serialize Broadband percent into JSON data
        moshi.adapter(BroadbandAccessPercent.class);
    try{
      BroadbandAccessPercent data = this.datasource.getBroadbandPercent(location); //get the broadband percent from the data source at inputted location
      if (data.percent() < 0 || data.percent() >100) { //check for valid percent
        throw new DatasourceException("Invalid data from data source: " + data);
      }
      responseMap.put("type", "success"); //report success if a valid value is obtained and add to hashmap that will be returned to user
      responseMap.put("time", this.getTime()); //add the time
      responseMap.put("location", location); //add the location
      responseMap.put("percent", percentAdapter.toJsonValue(data)); //add the percent
      return responseAdapter.toJson(responseMap); //return the hashmap that has all of the aforementioned data to the user
    } catch (DatasourceException e) {
      responseMap.put("type", "error");
      responseMap.put("error_type", "datasource");
      responseMap.put("details", e.getMessage());
      return responseAdapter.toJson(responseMap);
    } catch (Throwable e) {
      responseMap.put("type", "error");
      responseMap.put("error_type", e.getCause());
      responseMap.put("details", e.getMessage());
      return responseAdapter.toJson(responseMap);
    }
  }

  /**
   * Method for getting the time so that it can be included as a data field in the JSON that is
   * displayed back to the user.
   * @return
   */
  private String getTime() {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    Date date = new Date(System.currentTimeMillis());
    return formatter.format(date);
  }

  }

