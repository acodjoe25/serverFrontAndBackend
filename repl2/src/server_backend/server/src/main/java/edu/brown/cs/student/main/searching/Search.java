package edu.brown.cs.student.main.searching;

import edu.brown.cs.student.main.parsing.Parser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the fundamental functionality of the searching part of the application. Within
 * the class are three distinct search methods that go through the parsed data, depending on
 * different ways that the user can input data.
 */
public class Search {

  private Parser parser;
  private List<List<String>> data;
  private List<List<String>> targetList;
  private String target;

  /**
   * The search constructor takes in a parser, and also a String that represents the target value
   * that a user has inputted into the program.
   *
   * @param target
   * @throws IOException
   */
  public Search(List<List<String>> data, String target) throws IOException {
    this.data = data;
    this.target = target;
    this.targetList = new ArrayList<>();
  }

  /**
   * This is the most basic search method that can be called, and is the default search that is used
   * when the user has not provided any indicator as to where the target value might be. It goes row
   * by row looking for rows that contain the target value.
   *
   * @return
   * @throws IOException
   */
  public List<List<String>> findTargetAll() throws IOException {
    for (List<String> row : this.data) { // goes through every row looking for the target value
      if (row.contains(this.target)) {
        this.targetList.add(row); // if a row contains the target, add it to the return list
      }
    }
    if (!(this.targetList.isEmpty())) { // if the target was found
      return this.targetList;
    } else {
      throw new IOException(
          "Target value was not found"); // else throw this exception with this message
    }
  }

  /**
   * The findTargetHeader method takes in a String that is the passed in Header that the user inputs
   * to narrow the search. After verifying if the header actually exists in the file, the method
   * checks each row in the column under the inputted header to see if the target value is there. At
   * the end a list of the rows with the target is returned. If the header cannot be found or if an
   * incorrect header is passed in, the method automatically calls the findTargetAll method to see
   * if the target value is contained anywhere in the file.
   *
   * @param header
   * @return
   * @throws IOException
   */
  public List<List<String>> findTargetHeader(String header) throws IOException {
    List<String> headerRow = this.data.get(0);
    int column = -1;
    for (int i = 0; i < headerRow.size(); i++) {
      if (headerRow.get(i).equals(header)) {
        column = i; // find the column index of the header
      }
    }
    if (column < 0) { // checks to see if the for loop was able to find the header
      System.out.println("Header was not found");
      return this.findTargetAll();
    } else {
      for (List<String> row : this.data) { // for each row
        if (row.get(column)
            .equals(this.target)) { // check to see if the column index at that row has the target
          this.targetList.add(row);
        }
      }
      return this.targetList;
    }
  }

  /**
   * The findTargetColumn method takes in an Integer that is the passed in column index that the
   * user inputs to narrow the search. After verifying if the index is within the bounds of file,
   * the method checks each row in the column under the inputted column index to see if the target
   * value is there. At the end a list of the rows with the target is returned. If there are no
   * values under that inputted column index the method automatically calls findTargetAll in case
   * the user has inputted an incorrect column index by mistake.
   *
   * @param column
   * @return
   * @throws IOException
   */
  public List<List<String>> findTargetColumn(Integer column) throws IOException {
    List<String> headerRow = this.data.get(0);
    if (column < 0 || column >= headerRow.size()) {
      System.out.println("Column index is outside the bounds of the file");
      return this.findTargetAll();
    } else {
      for (List<String> row : this.data) {
        if (row.get(column)
            .equals(this.target)) { // check to see if the column index at that row has the target
          this.targetList.add(row);
        }
      }
      if (this.targetList
          .isEmpty()) { // this line is checking to see if maybe the user put in the wrong column
        // and nothing came up, the broad search is ran
        System.out.println("Value not found in column index");
        return this.findTargetAll();
      } else {
        return this.targetList;
      }
    }
  }
}
