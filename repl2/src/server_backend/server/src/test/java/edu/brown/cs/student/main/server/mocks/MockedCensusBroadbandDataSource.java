package edu.brown.cs.student.main.server.mocks;

import edu.brown.cs.student.main.server.broadband.BroadbandAccessPercent;
import edu.brown.cs.student.main.server.broadband.BroadbandDatasource;
import edu.brown.cs.student.main.server.broadband.DatasourceException;
import edu.brown.cs.student.main.server.broadband.Location;

public class MockedCensusBroadbandDataSource implements BroadbandDatasource {

  private final BroadbandAccessPercent constantData;

  public MockedCensusBroadbandDataSource(BroadbandAccessPercent constantData) {
    this.constantData = constantData;
  }

  @Override
  public BroadbandAccessPercent getBroadbandPercent(Location location) throws DatasourceException {
    return this.constantData;
  }
}
