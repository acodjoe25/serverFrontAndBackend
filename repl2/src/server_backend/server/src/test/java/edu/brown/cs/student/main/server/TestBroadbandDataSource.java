package edu.brown.cs.student.main.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.server.broadband.BroadbandAccessPercent;
import edu.brown.cs.student.main.server.broadband.CensusBroadbandDataSource;
import edu.brown.cs.student.main.server.broadband.DatasourceException;
import edu.brown.cs.student.main.server.broadband.Location;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/**
 * Unit tests that ensure the datasource calls to the Census API call are functional.
 */
public class TestBroadbandDataSource {

  /**
   * Tests the resolveStateCode method, which handles the creation of a hashmap mapping states to their ids.
   * @throws DatasourceException
   */
  @Test
  public void testResolveStateCode() throws DatasourceException {
    CensusBroadbandDataSource dataSource = new CensusBroadbandDataSource();
    String id = "06";
    Assert.assertEquals(dataSource.resolveStateCode().get("California"), id);
  }

  /**
   * This test ensures that the resolveCountyCode method is functional, which returns a county's id when a state, county, and state
   * id are passed into it.
   * @throws DatasourceException
   */
  @Test
  public void testResolveCountyCode() throws DatasourceException {
    CensusBroadbandDataSource dataSource = new CensusBroadbandDataSource();
    Assert.assertEquals(dataSource.resolveCountyCode("06","California","Kings County"), "031");
  }

  /**
   * This test ensures the getBroadbandPercent method is working, which should return the broadband
   * access percent when for any location that is passed in.
   * @throws DatasourceException
   */
  @Test
  public void testGetBroadbandPercent() throws DatasourceException {
    CensusBroadbandDataSource dataSource = new CensusBroadbandDataSource();
    Location location = new Location("California", "Kings County");
    BroadbandAccessPercent percent = new BroadbandAccessPercent(83.5);
    Assert.assertEquals(dataSource.getBroadbandPercent(location),percent);
  }


}
