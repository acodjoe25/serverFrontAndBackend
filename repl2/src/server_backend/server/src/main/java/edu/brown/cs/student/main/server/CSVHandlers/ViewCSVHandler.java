package edu.brown.cs.student.main.server.CSVHandlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import edu.brown.cs.student.main.server.State;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * ViewCSVHandler serves as an endpoint on the server for users who want to view all the values
 * within a csv file.
 */
public class ViewCSVHandler implements Route {
  private State state;

  // implementation of converting the parser (loadcsv) output to a json and displaying it
  // Return a list of list of strings with all entries

  /**
   * Constructor passes the state from loadcsv.
   *
   * @param state information from the loadcsv class
   */

  public ViewCSVHandler(State state) {
    this.state = state;
  }
  /**
   * Handle is always called whenever the endpoint is activated.
   *
   * @param request from users for parameters
   * @param response not used here
   * @return record indicating success or failure of response
   */
  @Override
  public Object handle(Request request, Response response) {
    if (!(this.state.getFile() == null)) {

      return new viewResponse("success", this.state.getData()).serialize();
    }
    else{
      return new viewBadRequest("error_bad_request").serialize();
    }
  }

  /**
   * viewResponse represents the record of information from a successful request to the server.
   *
   * @param result indicating the success of the request
   * @param data indicating the parsed information from the csv
   */
  public record viewResponse(String response, List<List<String>> data) {

    public viewResponse(List<List<String>> data) {
      this("success", data);
    }

     String serialize() {
      Moshi moshi = new Builder().build();
      JsonAdapter<viewResponse> adapter = moshi.adapter(viewResponse.class);
      return adapter.toJson(this);
    }
  }

  /**
   * viewBadRequest is a record that gets returned when something goes wrong with viewing the csv
   *
   * @param error_type
   */
  public record viewBadRequest(String response, String error_type) {
    public viewBadRequest(String error_type) { this("failure", error_type); }

    /**
     * Serialization into a JSON string for displaying on the server
     *
     * @return JSON string of response
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(viewBadRequest.class).toJson(this);
    }
  }
}
