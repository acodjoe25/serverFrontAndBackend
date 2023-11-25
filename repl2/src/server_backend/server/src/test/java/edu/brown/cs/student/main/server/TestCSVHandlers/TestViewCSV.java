package edu.brown.cs.student.main.server.TestCSVHandlers;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.CSVHandlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.CSVHandlers.ViewCSVHandler;
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
/** This class tests the functionality of the viewcsv endpoint. */
public class TestViewCSV {

  /** Sets up the server port */
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  /** Always creates a new server for each test */
  @BeforeEach
  public void setup() {
    State state = new State();

    //Restart the entire Spark server for every test!
    Spark.get("/view", new ViewCSVHandler(state));
    Spark.get("/load", new LoadCSVHandler(state));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  /** Always stops the server after each test */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/view");
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
   * Tests to see if the endpoint works as intended
   *
   * @throws IOException if request fails
   */
  @Test
  public void TestGoodResponse() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filename=census/dol_ri_earnings_disparity.csv");
    HttpURLConnection viewClientConnection = tryRequest("view");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    //If this runs then we got the right response
    ViewCSVHandler.viewResponse response =
        moshi.adapter(ViewCSVHandler.viewResponse.class).fromJson(new Buffer().readFrom(viewClientConnection.getInputStream()));
    assertTrue(response.toString().contains("success"));
    assertTrue(response.toString().contains("Number of Workers"));

    clientConnection.disconnect();
    viewClientConnection.disconnect();
  }

  /**
   * Tests what happens when load wasn't used before view
   *
   * @throws IOException if request fails
   */
  @Test
  public void TestNoFileLoaded() throws IOException {
    HttpURLConnection clientConnection = tryRequest("view");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    //If this runs then we got the right response
    ViewCSVHandler.viewBadRequest response =
        moshi.adapter(ViewCSVHandler.viewBadRequest.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertTrue(response.toString().contains("failure"));
    assertFalse(response.toString().contains("RI"));

    clientConnection.disconnect();
  }

  /**
   * Tests if view adapts to the latest loaded file
   *
   * @throws IOException if request fails
   */
  @Test
  public void TestChangeFileLoaded() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filename=census/dol_ri_earnings_disparity.csv");
    HttpURLConnection viewClientConnection = tryRequest("view");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    //If this runs then we got the right response
    ViewCSVHandler.viewResponse response =
        moshi.adapter(ViewCSVHandler.viewResponse.class).fromJson(new Buffer().readFrom(viewClientConnection.getInputStream()));
    assertTrue(response.toString().contains("success"));
    assertTrue(response.toString().contains("Number of Workers"));

    //New request
    clientConnection = tryRequest("load?filename=census/income_by_race_edited.csv");
    viewClientConnection = tryRequest("view");
    assertEquals(200, clientConnection.getResponseCode());


    moshi = new Moshi.Builder().build();
    ViewCSVHandler.viewResponse newResponse =
        moshi.adapter(ViewCSVHandler.viewResponse.class).fromJson(new Buffer().readFrom(viewClientConnection.getInputStream()));
    assertFalse(newResponse.toString().contains("Number of Workers"));
    assertTrue(newResponse.toString().contains("ID Geography"));
  }
}

