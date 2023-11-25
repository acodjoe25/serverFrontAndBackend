import { Dispatch, SetStateAction } from "react";
import { mockedBroadband, mockedLoad, mockedSearch, mockedView } from "./MockedData";

/**
 * The REPLFunction interface is used to define functions that can be accessed through
 * REPL. It helps create a unified system for making functions and registering them.
 */
export interface REPLFunction {
    (args: Array<string>): Promise<string | string[][]>;
  }

/*commandOptions contains all the commands that REPL has access to.
/As a developer, you can add more REPLFunction functions to commandMap
/simply by importing functions and adding them to the commandMap.
*/ 
let commandOptions: Map<string, REPLFunction> = new Map();

 /**
  * commandLoad handles the loading functionality of the backend. Users can 
  * input a csv file they would like to load in the data directory of the server
  * and set it for view and searching. 
  * @param args csv_filepath
  * @returns a string
  */
export const commandLoad: REPLFunction = (args: string[]) => {
    return fetch('http://localhost:3232/load?filename=' + args)
        .then(response => response.json())
        .then(json => {
            const load_result: string = json.filename;
            const load_response: string = json.response;
            if(load_response == "success") {
                return("Successfully loaded " + load_result);
            }
            else {
                return("Could not retrieve backend data for filepath "+ args);
            }
        })
        .catch(e => {
        return("API error: could not fetch backend");
        })
    }


/**
 * commandView handles fetching full csv table formats from the backend.
 * It lets users look at a dataset from a loaded csv file.
 * @param args none
 * @returns a list of list of strings, or a string
 */
export const commandView: REPLFunction = (args: string[]) => {
    return fetch('http://localhost:3232/view')
        .then(response => response.json())
        .then(json => {
            const load_result: string[][] = json.data;
            const load_response: string = json.response;
            if(load_response == "success") {
                return(load_result);
            }
            else {
                return("View command failed; load a valid csv file!");
            }
        })
        .catch(e => {
        return("API error: could not fetch backend");
        })
}

/**
 * commandSearch enables a user to search through a loaded in csv file
 * using a search query, a header, and a column name. It returns all 
 * rows in the table that contain the query in the column, if specified.
 * @param args query string, headerGiven string, column string
 * @returns a list of list of strings
 */
export const commandSearch: REPLFunction = (args: string[]) => {
    let searchUrl:string = 'http://localhost:3232/search?'
    if(args.length == 1) {
        searchUrl = searchUrl + "target=" + args[0]
    }
    if(args.length == 2) {
        searchUrl = searchUrl + "target=" + args[0] + "&column=" + args[1] 
    }
    if(args.length == 3) {
        searchUrl = searchUrl + "target=" + args[0] + "&headerGiven=" + args[1] + "&column=" + args[2]
    }
    
    return fetch(searchUrl)
        .then(response => response.json())
        .then(json => {
            
            const load_response: string = json.response;
            if(load_response == "success") {
                const load_result: string[][] = json.data;
                return(load_result);
            }
            else {
                const error_type = json.error_type;
                return("Search failed, error: " + error_type);
            }
        })
        .catch(e => {
        return("API error: Could not fetch backend");
        })
}


/**
 * commandBroadband allows the user to find the percent of 
 * people in a county in the US that have access to broadband internet. 
 * @param args a state and a county
 * @returns a string of the location and percent of the county's broadband access
 */
export const commandBroadband: REPLFunction = (args: string[]) => {
    return fetch('http://localhost:3232/broadband?state=' + args[0] + '&county=' + args[1])
        .then(response => response.json())
        .then(json => {
            
            const load_response: string = json.type;
            
            if(load_response == "success") {
                const load_percent: string = json.percent.percent;
                const load_state: string = json.location.stateName;
                const load_county: string = json.location.countyName;
                const load_time: string = json.time;
                return("Location: " + load_county + ", " + load_state + " \n Percent: " +load_percent);
            }
            else {
                const details = json.details;
                const error_type = json.type;
                // return("Broadband command failed; invalid state and county combination");
                return("Broadband command failed, error: " + error_type)
            }
        })
        .catch(e => {
        return("API error: could not fetch backend");
        })
}



//Design choice: We are excluding needing to register mode here because it would be 
//too disruptive to our currently established functionality.
commandOptions.set('load', commandLoad);
commandOptions.set('view', commandView);
commandOptions.set('search', commandSearch);
commandOptions.set('broadband', commandBroadband);
commandOptions.set("mockLoad", mockedLoad);
commandOptions.set("mockView", mockedView);
commandOptions.set("mockSearch", mockedSearch);
commandOptions.set("mockBroadband", mockedBroadband);


//The list of registerable commands is returned by getCommandOptions and can
//be accessed by REPLInput via importing this constant.
export const getCommandOptions: Map<string, REPLFunction> = commandOptions;


