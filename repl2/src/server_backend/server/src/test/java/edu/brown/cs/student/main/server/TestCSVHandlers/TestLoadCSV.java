package edu.brown.cs.student.main.server.TestCSVHandlers;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.CSVHandlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.State;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import spark.Spark;

/** Tests the loading functionality of the server */

public class TestLoadCSV {

  /** Setup port */

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  /** Always resets the server with the desired endpoint */

  @BeforeEach
  public void setup() {

    // In fact, restart the entire Spark server for every test!
    Spark.get("/load", new LoadCSVHandler(new State()));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  /** Always closes the server after each test */

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/load");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }


  /**
   * Helper to start a connection to a specific API endpoint/params
   * @param apiCall the call string, including endpoint
   *                (NOTE: this would be better if it had more structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  static private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    //clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Tests that an error is raised if the file being loaded doesn't exist
   *
   * @throws IOException if request fails
   */
  @Test
  public void TestBadFileInput() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filename=badrequest");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();

    //If this runs then we got the right failure response
    LoadCSVHandler.loadCSVFailureResponse response =
        moshi.adapter(LoadCSVHandler.loadCSVFailureResponse.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertTrue(response.toString().contains("error_datasource"));

    clientConnection.disconnect();
  }

  /**
   * Tests a proper usage of the load file
   *
   * @throws IOException if request fails
   */

  @Test
  public void TestGoodFileInput() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filename=census/dol_ri_earnings_disparity.csv");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    //If this runs then we got the right response
    LoadCSVHandler.loadCSVSuccessResponse response =
        moshi.adapter(LoadCSVHandler.loadCSVSuccessResponse.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertTrue(response.toString().contains("success"));

    clientConnection.disconnect();
  }

  /**
   * Tests that an error is raised if the filename is empty
   *
   * @throws IOException is request fails
   */
  @Test
  public void TestErrorDatasource() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filename=");
    Moshi moshi = new Moshi.Builder().build();

    //If this runs then we got the right failure response
    LoadCSVHandler.loadCSVFailureResponse response =
        moshi.adapter(LoadCSVHandler.loadCSVFailureResponse.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertTrue(response.toString().contains("error_datasource"));
  }

  /**
   * Tests if the function still works after loading multiple times
   *
   * @throws IOException if request fails
   */
  @Test
  public void TestLoadTwice() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filename=census/dol_ri_earnings_disparity.csv");
    Moshi moshi = new Moshi.Builder().build();

    //Load in the first csv
    LoadCSVHandler.loadCSVSuccessResponse response =
        moshi.adapter(LoadCSVHandler.loadCSVSuccessResponse.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertTrue(response.toString().contains("success"));
    assertTrue(response.toString().contains("dol_ri_earnings_disparity"));

    //Now load in the second csv
    HttpURLConnection nextClientConnection = tryRequest("load?filename=census/income_by_race_edited.csv");
    moshi = new Moshi.Builder().build();

    response =
        moshi.adapter(LoadCSVHandler.loadCSVSuccessResponse.class).fromJson(new Buffer().readFrom(nextClientConnection.getInputStream()));
    assertTrue(response.toString().contains("success"));
    assertTrue(response.toString().contains("income_by_race_edited"));
    assertFalse(response.toString().contains("dol_ri_earnings_disparity"));

    clientConnection.disconnect();
    nextClientConnection.disconnect();
  }
}
