package edu.brown.cs.student.main.server.broadband;

/**
 * Custom exception that displays a message. Used when there are errors with obtaining and displaying
 * data obtained from API calls.
 */
public class DatasourceException extends Exception{

  public DatasourceException(String message) {
    super(message);
  }
}
