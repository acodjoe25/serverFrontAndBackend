package edu.brown.cs.student.main.server.CSVHandlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import edu.brown.cs.student.main.searching.Search;
import edu.brown.cs.student.main.server.State;
import java.io.IOException;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;


/**
 * searchCSVhandler is an endpoint handler for looking through a loaded csv file and displaying *
 * all the rows that match the query of interest.
 */
public class searchCSVhandler implements Route {

  private State state;

  /**
   * Constructor passes in the state from loadcsv
   *
   * @param state containing the loaded data from csv
   */
  public searchCSVhandler(State state) {
    this.state = state;
  }

  /**
   * Handle method is always called when the endpoint is accessed.
   *
   * @param request parameters from user
   * @param response not used for this
   * @return record of success or failure
   * @throws IOException if the file is not loaded or empty
   */
  @Override
  public Object handle(Request request, Response response) {
    if(this.state.getFile() == null) {
      return new searchBadRequest("error_datasource").serialize();
    }
    String target = request.queryParams("target");
    if (target.isEmpty()) { //if the user doesn't pass in a target parameter return an error
      return new searchBadRequest("error_bad_request").serialize();
    }
    try {
    Search searcher = new Search(this.state.getData(),target);
    String headerGiven = request.queryParams("headerGiven");
    String headerName = request.queryParams("headerName");
      if (!(headerGiven == null)) {
        if (headerGiven.equalsIgnoreCase("true")) {//check to see if there is a header
          //get the headerName from the query parameter
            return new searchResponse(searcher.findTargetHeader(headerName)).serialize();
        } else if (headerGiven.equalsIgnoreCase("false")) {//if there is no header
          if (!(headerName == null)) {
            return new searchBadRequest(
                "error_bad_request").serialize();//if they give a header name after signaling that headerGiven is false
          }
          String columnIndex = request.queryParams("column");
          if (!columnIndex.isEmpty()) {//check to see if they put in a column as a parameter
              return new searchResponse(
                  searcher.findTargetColumn(Integer.parseInt(columnIndex))).serialize();//do the column index search

          } else {
              return new searchResponse(searcher.findTargetAll()).serialize(); //if just a target was passed still do the general brute force search
          }
        }
        return new searchBadRequest(
            "error_bad_request").serialize();//if the headerGiven field has something either than true or false
      } else {
        return new searchResponse(
            searcher.findTargetAll()).serialize();//if there is a target but nothing else, can still search

      }
    }
    catch (IOException e){
      return new searchBadRequest(e.getMessage()).serialize();
    }
  }

  /**
   * searchResponse is the record containing information from a successful server response to a
   * request to search through the CSV loaded
   *
   * @param result indicating success of the operation
   * @param targetList Returns all rows that match the request query
   */
  public record searchResponse(String response, List<List<String>> data) {

    public searchResponse(List<List<String>> data) {
      this("success", data);
    }

    String serialize() {
      Moshi moshi = new Builder().build();
      JsonAdapter<searchResponse> adapter = moshi.adapter(searchResponse.class);
      return adapter.toJson(this);
    }
  }

  /**
   * Records the information given from a failed search
   *
   * @param error_type specifying what kind of error occurred
   */
  public record searchBadRequest(String response, String error_type) {
    public searchBadRequest(String error_type) { this("failure", error_type); }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(searchBadRequest.class).toJson(this);
    }
  }
  // inputs: String filepath, String targetValue, boolean header, String column
  // Uses the Searcher to find and return values that match the query

}
