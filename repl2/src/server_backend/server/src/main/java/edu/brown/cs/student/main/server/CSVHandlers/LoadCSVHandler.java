package edu.brown.cs.student.main.server.CSVHandlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.parsing.Parser;
import edu.brown.cs.student.main.parsing.creators.StringCreator;
import edu.brown.cs.student.main.server.State;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * LoadCSVHandler handles an endpoint on the server which stores the data of the specified CSV file
 * in a state. The state is then passed to other handler classes to access the loaded information.
 */
public class LoadCSVHandler implements Route {

  private State state;

  /**
   * Constructor of LoadCSVHandler. Assigns its state to the passed in state.
   *
   * @param state to store CSV info in
   */
  public LoadCSVHandler(State state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) {
    //Defensive - prevents access of other folders
    //repl2/src/backend/server/data/stardata.csv
    this.state.setFileName("data/" + request.queryParams("filename"));
    if (!(request.queryParams("filename") == null)) {
      this.state.setFile(new File(this.state.getFilename()));
      if (this.state.getFile().exists()) {
        StringCreator creator = new StringCreator();
        FileReader reader = null;
        try {
          reader = new FileReader(this.state.getFilename());
          Parser parser = new Parser<>(creator, reader);
          try {
            this.state.setData(parser.parse());//not sure how to deal with this, some issue about forcing the generic to be a list of strings
          } catch(Exception e) {
            System.out.println("JavaIO Exception");
          }
          return new loadCSVSuccessResponse(this.state.getFilename()).serialize();
        } catch (FileNotFoundException e) {
          System.out.println("Cannot find file " + this.state.getFilename());
          return new loadCSVFailureResponse(this.state.getFilename(), "error_datasource").serialize();
        }
      }
    }
    else {
      return new loadCSVFailureResponse(this.state.getFilename(), "error_bad_request").serialize();
    }
    return new loadCSVFailureResponse(this.state.getFilename(),"error_datasource").serialize();
  }

  /**
   * Record that stores the info gotten from a successful parsing of the file
   *
   * @param response message referring to the success of the program
   * @param filename name of CSV file
   */
  public record loadCSVSuccessResponse(String response, String filename) {

    public loadCSVSuccessResponse(String filename) {
      this("success", filename);
    }

    String serialize() {
      try {
        Moshi moshi =
            new Moshi.Builder().build();
        JsonAdapter<loadCSVSuccessResponse> adapter = moshi.adapter(loadCSVSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  /**
   * Record that stores the info gotten from a failed parsing of the file
   *
   * @param response message referring to the failure of the program
   * @param filename name of CSV file
   */
  public record loadCSVFailureResponse(String response, String errorType, String filename) {

    public loadCSVFailureResponse(String filename, String errorType) {
      this("failure", errorType, filename);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(loadCSVFailureResponse.class).toJson(this);
    }
  }

}
