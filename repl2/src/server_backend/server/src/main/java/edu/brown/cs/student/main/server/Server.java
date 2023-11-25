package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.CSVHandlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.CSVHandlers.searchCSVhandler;
import edu.brown.cs.student.main.server.CSVHandlers.ViewCSVHandler;
import edu.brown.cs.student.main.server.broadband.BroadbandHandler;
import edu.brown.cs.student.main.server.broadband.CensusBroadbandDataSource;
import edu.brown.cs.student.main.server.broadband.DatasourceException;
import spark.Spark;


/**
 * Welcome to Server! The server is the main class of the project and sets up the local environment
 * that is needed to access csv files or request things from the ACS API.
 */

public class Server {

  /**
   * Initializes the endpoints with a fresh state
   *
   * @param args not used for this assignment
   * @throws DatasourceException
   */
  public static void main(String[] args) throws DatasourceException {

    int port = 3232;
    Spark.port(port);
    State state = new State();
    Spark.get("load", new LoadCSVHandler(state));
    Spark.get("view", new ViewCSVHandler(state));
    Spark.get("search", new searchCSVhandler(state));
    Spark.get("broadband", new BroadbandHandler(new CensusBroadbandDataSource()));

    Spark.init();
    Spark.awaitInitialization();

    /*
       Setting CORS headers to allow cross-origin requests from the client; this is necessary for the client to
       be able to make requests to the server.

       By setting the Access-Control-Allow-Origin header to "*", we allow requests from any origin.
       This is not a good idea in real-world applications, since it opens up your server to cross-origin requests
       from any website. Instead, you should set this header to the origin of your client, or a list of origins
       that you trust.

       By setting the Access-Control-Allow-Methods header to "*", we allow requests with any HTTP method.
       Again, it's generally better to be more specific here and only allow the methods you need, but for
       this demo we'll allow all methods.

       We recommend you learn more about CORS with these resources:
           - https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
           - https://portswigger.net/web-security/cors
    */
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET /order and /mock endpoints ENDPOINTS
    // Spark.get("order", new OrderHandler(menu));
    // Spark.get("mock", new MockHandler());
    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }
}
