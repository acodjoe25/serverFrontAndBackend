package edu.brown.cs.student.main.server;

import java.io.File;
import java.util.List;
/** State is a class used to store all the data we want about a CSV file. */

public class State {

  private File file;
  private String filename;
  private List<List<String>> data;

  /** Initializes file, filename, and data */

  public State(){
    this.file = null;
    this.filename = null;
    this.data = null;
  }

  /**
   * @return filename
   */
  public String getFilename(){
    return this.filename;
  }
  /**
   * Sets the file name to fileName
   *
   * @param fileName
   */
  public void setFileName(String fileName) {
    this.filename = fileName;
  }
  /**
   * @return file as file class
   */
  public File getFile() {
    return this.file;
  }
  /**
   * Sets the state's file
   *
   * @param file to pass to other classes.
   */
  public void setFile(File file) {
    this.file = file;
  }
  /**
   * Return the parsed data structure within the CSV
   *
   * @return parsed data structure
   */
  public List<List<String>> getData() {
    return this.data;
  }
  /**
   * Stores parsed data
   *
   * @param data add parsed data to state
   */
  public void setData(List<List<String>> data) {
    this.data = data;
  }


}
