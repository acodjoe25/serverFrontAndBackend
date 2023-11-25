import { REPLFunction } from "./FunctionList";
import { loadcsv, mockBroadband, searchcsv, viewcsv } from "./mockedJson";



 /**
  * mockedLoad handles loading a file but does so using mocked data from mockedJson.tsx. Users can 
  * input a csv file they would like to load in the data directory of the server
  * and set it for view and searching. 
  * @param args csv_filepath
  * @returns a string
  */
 export const mockedLoad: REPLFunction = (args: string[]) => {
    return fetch("fake backend")
    .then(response => response.json()) 
    .catch( e => {
        return(loadcsv(args[0]));
    })
}

 /**
  * mockedView handles viewing a file but does so using mocked data from mockedJson. 
  * Used for front-end testing.
  * @param args 
  * @returns a table representing all csv entries
  */
 export const mockedView: REPLFunction = (args: string[]) => {
    return fetch("fake backend")
    .then(response => response.json()) 
    .catch( e => {
        return(viewcsv());
    })
}


/**
 * mockedSearch pretends to execute a search command but instead gets its data
 * from our mockedJson file. Used for testing front-end.
 * @param args 
 * @returns table of search results 
 */
 export const mockedSearch: REPLFunction = (args: string[]) => {
    return fetch("fake backend")
    .then(response => response.json()) 
    .catch( e => {
      let column = "";
      let value = "";
      let hasHeader = "";
      let outputMsg: string | string[][];
      if (args.length > 3) {
        outputMsg = "please give a max of 3 arguments for search.";
      } else if (args.length < 1) {
        outputMsg =
          "please provide a search term and optional column identifier for search.";
      } else {
        value = args[0];
        if (args.length == 3) {
          column = args[0];
          value = args[1];
          hasHeader = args[2];
        }
        if (args.length == 2) {
          column = args[0];
          value = args[1];
        }
        outputMsg = searchcsv(column, value, hasHeader);
      }
  
      return outputMsg;
      })
}


/**
 * mockedBroadband pretends to execute the broadband command but instead gets its 
 * data from our mockedJson file. Used for testing front-end. 
 * @param args 
 * @returns broadband entry
 */
 export const mockedBroadband: REPLFunction = (args: string[]) => {
    return fetch("fake backend")
    .then(response => response.json()) 
    .catch( e => {
        return(mockBroadband(args));
    })
}

  /**
   * handles load case after error handling,
   * sends the inputted request to the mocked
   * backend through loadcsv
   * @param commandString
   */
  function handleLoad(commandString: string) {
    const commandArgs = commandString.split(" ");
    let outputMsg;
    if (commandArgs.length != 2) {
      outputMsg =
        "Please provide 1 argument for load: load <csv-file-path>";
    } else {
      fetch('http://localhost:3232/load?filename=' + commandArgs[1])
      .then(response => response.json())
      .then(json => {
        const load_result: string = json.filename;
        const load_response: string = json.response;
        if(load_response != "success") {
          outputMsg = load_result;
        }
        else {
          outputMsg = "Could not retrieve backend data for filepath "+ commandArgs[1]
        }
      })
      .catch(e => {
        console.log("datasource error")
      })
    }
    return outputMsg;
    
  }

   /**
   * handles view case after error handling,
   * sends the inputted request to the mocked
   * backend through viewcsv
   * @param commandString
   */
   function handleView(commandString: string) {
    const commandArgs = commandString.split(" ");
    let outputMsg: string | string[][];
    if (commandArgs.length > 1) {
      outputMsg = "view does not take any arguments!";
    } else {
      outputMsg = viewcsv();
    }
    return outputMsg;

  }
