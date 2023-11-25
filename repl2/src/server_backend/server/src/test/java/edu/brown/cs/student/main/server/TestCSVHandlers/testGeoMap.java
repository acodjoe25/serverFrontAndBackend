package edu.brown.cs.student.main.server.TestCSVHandlers;

import edu.brown.cs.student.main.server.broadband.Geometry;
import edu.brown.cs.student.main.server.broadband.Properties;
import edu.brown.cs.student.main.server.broadband.geoMap;
import edu.brown.cs.student.main.server.broadband.geoMapAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class testGeoMap {
  @Test
  public void testGeoMaapGeneral() throws IOException {
    geoMap expectedResult = new geoMap();

    expectedResult.type = "Feature";

    Geometry geo = new Geometry();
    geo.type = "Point";
    List<Double> dub = new ArrayList<>();
    dub.add(125.6);
    dub.add(10.1);
    geo.coordinates = dub;
    expectedResult.geometry = geo;

    Properties prop = new Properties();
    prop.name = "Dinagat Islands";
    expectedResult.propeties = prop;

    geoMapAdapter adapter = new geoMapAdapter();



  /* code from w3 schools https://www.w3schools.com/java/java_files_read.asp */
    File myObj = new File("/Users/arianacodjoe/repl-dhan25-acodjoe/repl2/src/server_backend/server/src/test/java/edu/brown/cs/student/main/server/TestCSVHandlers/map.json");
    Scanner myReader = new Scanner(myObj);
    String jsonString = "";
    while (myReader.hasNextLine()) {
      String data = myReader.nextLine();
      jsonString = jsonString + data;
    }
    geoMap actual = adapter.fromJson(jsonString);

    Assert.assertEquals(expectedResult.type, actual.type);

  }

@Test
  public void testGeoMap2General() throws IOException {
    geoMap expectedResult = new geoMap();

    expectedResult.type = "bannna";

    Geometry geo = new Geometry();
    geo.type = "joey";
    List<Double> dub = new ArrayList<>();
    dub.add(11.1);
    dub.add(22.2);
    dub.add(33.3);
    geo.coordinates = dub;
    expectedResult.geometry = geo;

    Properties prop = new Properties();
    prop.name = "ello";
    expectedResult.propeties = prop;

    geoMapAdapter adapter = new geoMapAdapter();



    /* code from w3 schools https://www.w3schools.com/java/java_files_read.asp */
    File myObj = new File("/Users/arianacodjoe/repl-dhan25-acodjoe/repl2/src/server_backend/server/src/test/java/edu/brown/cs/student/main/server/TestCSVHandlers/map2.json");
    Scanner myReader = new Scanner(myObj);
    String jsonString = "";
    while (myReader.hasNextLine()) {
      String data = myReader.nextLine();
      jsonString = jsonString + data;
    }
    geoMap actual = adapter.fromJson(jsonString);

    Assert.assertEquals(expectedResult.geometry.coordinates, actual.geometry.coordinates);

  }

}
