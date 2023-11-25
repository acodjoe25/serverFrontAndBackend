package edu.brown.cs.student.main.server.TestCSVHandlers;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.CSVHandlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.CSVHandlers.searchCSVhandler;
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

/** Testing for the search functionality of server */

public class TestSearchCSV {

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  /** Helper for setting up a server for each test run */

  @BeforeEach
  public void setup() {
    State state = new State();

    //Restart the entire Spark server for every test!
    Spark.get("/search", new searchCSVhandler(state));
    Spark.get("/load", new LoadCSVHandler(state));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }
  /** Helper for turning off the server after each test runs */

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/search");
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
   * Tests to see that a failure message appears if you don't load in a file yet
   *
   * @throws IOException if the request fails
   */

  @Test
  public void TestNoFileLoadedYet() throws IOException {
    HttpURLConnection searchClientConnection = tryRequest("search?target=RI&headerGiven=true&headerName=1");
    assertEquals(200, searchClientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    //If this runs then we got the right response
    searchCSVhandler.searchBadRequest response =
        moshi.adapter(searchCSVhandler.searchBadRequest.class).fromJson(new Buffer().readFrom(searchClientConnection.getInputStream()));
    assertTrue(response.toString().contains("failure"));
    assertTrue(response.toString().contains("error_datasource"));

    searchClientConnection.disconnect();
  }

  /**
   * Tests that the system still works even when a column identifier isn't given.
   *
   * @throws IOException if the request fails
   */
  @Test
  public void TestNoColumnSpecified() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filename=census/dol_ri_earnings_disparity.csv");
    assertEquals(200, clientConnection.getResponseCode());
    //Our searcher works to prevent errors in querying, so it will automatically search a
    // column if a headerName is given. We expect to get a successful query with no errors.
    HttpURLConnection searchClientConnection = tryRequest("search?target=Multiracial&headerName=1");

    Moshi moshi = new Moshi.Builder().build();

    //If this runs then we got the right response
    searchCSVhandler.searchResponse response =
        moshi.adapter(searchCSVhandler.searchResponse.class).fromJson(new Buffer().readFrom(searchClientConnection.getInputStream()));
    assertTrue(response.toString().contains("success"));
    assertTrue(response.toString().contains("Multiracial"));
    assertFalse(response.toString().contains("White"));

    clientConnection.disconnect();
    searchClientConnection.disconnect();
  }


  /**
   * Tests that if a column isn't specified, the query will be searched for in all columns
   *
   * @throws IOException if request fails
   */
  @Test
  public void TestSearchAllColumns() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filename=census/dol_ri_earnings_disparity.csv");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection searchClientConnection = tryRequest("search?target=RI");

    Moshi moshi = new Moshi.Builder().build();

    //If this runs then we got the right response
    searchCSVhandler.searchResponse response =
        moshi.adapter(searchCSVhandler.searchResponse.class).fromJson(new Buffer().readFrom(searchClientConnection.getInputStream()));
    assertTrue(response.toString().contains("success"));
    assertTrue(response.toString().contains("Multiracial"));
    assertTrue(response.toString().contains("$0.92"));
    assertTrue(response.toString().contains("4%"));

    clientConnection.disconnect();
    searchClientConnection.disconnect();
  }

  /**
   * Tests to verify that even if a header is specified to be there, and no column is given, it
   * still resorts to searching through all the files
   *
   * @throws IOException if request fails
   */
  @Test
  public void TestNoHeaderNameSpecified() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filename=census/income_by_race_edited.csv");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection searchClientConnection = tryRequest("search?target=providence-county-ri&headerGiven=true");

    Moshi moshi = new Moshi.Builder().build();

    //If this runs then we got the right response
    searchCSVhandler.searchResponse response =
        moshi.adapter(searchCSVhandler.searchResponse.class).fromJson(new Buffer().readFrom(searchClientConnection.getInputStream()));
    assertFalse(response.toString().contains("newport-county-ri"));
    assertFalse(response.toString().contains("kent-county-ri"));
    assertTrue(response.toString().contains("Providence County, RI"));


    clientConnection.disconnect();
    searchClientConnection.disconnect();
  }


}

