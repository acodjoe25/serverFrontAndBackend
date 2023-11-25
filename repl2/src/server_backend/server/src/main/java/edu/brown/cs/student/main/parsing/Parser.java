package edu.brown.cs.student.main.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class handles the fundamental functionality of the parsing part of the application. The
 * class has a single method, parse, that deals with the parsing and breaking down of the CSV data.
 */
public class Parser<T> {
  private CreatorFromRow<T> creator;
  private Reader reader;

  /**
   * The parser constructor takes in a creator that implements the interface CreatorFromRow, and it
   * takes in a reader of type Reader.
   *
   * @param creator
   * @param reader
   */
  public Parser(CreatorFromRow<T> creator, Reader reader) {
    this.creator = creator;
    this.reader = reader;
  }

  /**
   * The parse method splits each CSV row on a regex that removes commans and other functions. Then
   * each row is created upon by the inputted creator that transforms the rows into whatever is
   * needed. These new transformed rows are then added to a return list that is outputted.
   *
   * @return
   * @throws IOException
   */
  public List<T> parse() throws IOException {

    final Pattern regexSplitCSVRow =
        Pattern.compile(
            ",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))"); // store the regex in a string

    List<T> returnList = new ArrayList<>(); // this is the list that is essentially the parsed CSV

    BufferedReader bufferedReader = new BufferedReader(this.reader);
    String line = bufferedReader.readLine();
    if (line == null) { // checks to see if the file is empty
      throw new IOException("Empty file is being used");
    }
    while (line != null) { // this loop goes through each row of the CSV until there are no more
      List<String> rows = List.of(regexSplitCSVRow.split(line));
      try {
        returnList.add(
            this.creator.create(
                rows)); // the creator creates each row, and then it is added into the returnList
      } catch (FactoryFailureException e) {
        System.out.println("Error converting and creating file rows");
      }

      line = bufferedReader.readLine(); // next line is set
    }
    return returnList;
  }
}
