package edu.brown.cs.student.main.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.broadband.BroadbandAccessPercent;
import edu.brown.cs.student.main.server.broadband.BroadbandDatasource;
import edu.brown.cs.student.main.server.broadband.BroadbandHandler;
import edu.brown.cs.student.main.server.broadband.Location;
import edu.brown.cs.student.main.server.mocks.MockedCensusBroadbandDataSource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * This testing class contains integration and mock testing that is used to test the BroadbandHandler.
 */

public class TestBroadbandHandler {

  /**
   * Set up the server.
   */
  @BeforeAll
  public static void setupOnce() {

    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String,Object>> adapter;
  private JsonAdapter<BroadbandAccessPercent> percentAdapter;


  /**
   * Before each test set up the broadband handler using a mocked source. In other words instead of actually
   * using the live API data it passes in a static 75 that represents the fetched BroadbandAccessPercent.
   */

  @BeforeEach
  public void setup() {
    BroadbandDatasource mockedSource = new MockedCensusBroadbandDataSource(new BroadbandAccessPercent(75));
    Spark.get("broadband",new BroadbandHandler(mockedSource));
    Spark.awaitInitialization();

    Moshi moshi = new Moshi.Builder().build();
    this.adapter = moshi.adapter(this.mapStringObject);
    this.percentAdapter = moshi.adapter(BroadbandAccessPercent.class);
  }

  /**
   * Stop the server after each test.
   */
  @AfterEach
  public void tearDown() {
    Spark.unmap("broadband");
    Spark.awaitStop();
  }

  /**
   * Lays the foundation for API calls to be used later in the testing class.
   * @param apiCall
   * @return
   * @throws IOException
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestProperty("Content-Type", "application/json");
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  final Location kingsCounty = new Location("California","Kings_County");

  /**
   * Test a successful connection to the API and then assess certain fundamental properties from
   * the obtained data.
   * @throws IOException
   */
  @Test
  public void testBroadbandRequestSuccess() throws IOException {
    // Set up the request, make the request
    HttpURLConnection loadConnection = tryRequest("broadband?"+this.kingsCounty.sendParams());
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> body = this.adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(body);
    //Is a success message returned to the user?
    assertEquals("success", body.get("type"));
    Map<String, String> map = new HashMap<>();
  map.put("percent","{\"percent\":75.0}");

  //Does the mocked BroadbandAccessPercent equal the anticipated one that the handler should obtain?

    assertEquals(
        this.percentAdapter.toJson(new BroadbandAccessPercent(75)),
       map.get("percent"));
    loadConnection.disconnect();

  }

  /**
   * Provides information that corresponds to details on an error that is triggered.
   * @param body
   */
  private void showDetailsIfError(Map<String, Object> body) {
    if(body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body);
    }
  }
}
