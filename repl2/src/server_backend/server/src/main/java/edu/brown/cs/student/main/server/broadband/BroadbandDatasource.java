package edu.brown.cs.student.main.server.broadband;

/**
 * Interface used to represent the datasource, in other words the data that is obtained from the
 * Census API that is used to return information to the user. An interface is necessary for mock testing,
 * where the mock datasource class can contain a static output so as to not need an uneccesary
 * amount of API calls.
 */
public interface BroadbandDatasource {

  BroadbandAccessPercent getBroadbandPercent(Location location) throws DatasourceException;
}
