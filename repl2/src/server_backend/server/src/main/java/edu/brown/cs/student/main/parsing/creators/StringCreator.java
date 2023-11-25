package edu.brown.cs.student.main.parsing.creators;

import edu.brown.cs.student.main.parsing.CreatorFromRow;
import edu.brown.cs.student.main.parsing.FactoryFailureException;
import java.util.List;

/**
 * The String Creator class handles the specific "creating" of inputted CSV rows and returns them
 * into a List of a List of Strings.
 */
public class StringCreator implements CreatorFromRow<List<String>> {

  public StringCreator() {}

  /**
   * The one method is the create method that is implemented from the interface CreatorFromRow, and
   * it takes in the List of a List of strings, but since that is the type this particular creator
   * intends to output, it just spits the rows back out unchanged.
   *
   * @param row
   * @return
   * @throws FactoryFailureException
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}
