package edu.brown.cs.student.main.server.broadband;

/**
 * Record that represents the location that the user is looking to find Broadband access for
 * a specific state and county. Takes in those two parameters, a state and county.
 * @param stateName
 * @param countyName
 */
public record Location(String stateName, String countyName) {

  /**
   * Method used for mock testing to create a string representing query parameters based on
   * the locations the user inputted.
   * @return
   */
  public String sendParams() {
    return "state=" +this.stateName + "&county=" + this.countyName;
  }
}
