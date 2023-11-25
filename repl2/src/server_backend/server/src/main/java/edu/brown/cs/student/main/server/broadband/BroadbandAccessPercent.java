package edu.brown.cs.student.main.server.broadband;

/**
 * Record that represents the broadband percent value that is obtained from the Census API call. This
 * is useful for deserializing JSON data from the get request. Takes in a double representing the percent of
 * broadband access.
 * @param percent
 */

public record BroadbandAccessPercent(double percent) {}
