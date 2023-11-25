import { Dispatch, SetStateAction, useEffect, useCallback, useState, useContext } from "react";
import { Mode } from "./enums";
import { ControlledInput } from "./ControlledInput";
import "../styles/main.css";
import { REPLFunction, getCommandOptions } from "./FunctionList";
import {KeyboardProvider, KeyboardEvent} from "./keyboard";
import {KeyboardType} from "./keyboard";

// import KeyboardEvent from "./keyboard";
// import KeyboardType from "./keyboard";


/**
 * REPL Input component is in charge of dealing with everything
 * related to the inputs, accepts input commands and handles them 
 * accordingly, updating the history state for REPLHistory
 * to display
 */
interface REPLInputProps {
  history: (string | string[][])[];
  setHistory: Dispatch<SetStateAction<(string | string[][])[]>>;
  commandHistory: string[];
  setCommandStringHistory: Dispatch<SetStateAction<string[]>>;
  positionInHisoty : number;
  setPositionInHistory : Dispatch<SetStateAction<number>>;

}

/**
 * Main high level logic function for REPLInput
 * parses the commandString the user types through
 * the controlled input and submission from button
 * based on the command, directs concern to that 
 * command's handler, lastly refreshes the command 
 * str input state
 * @param props 
 * @returns 
 */
export function REPLInput(props: REPLInputProps) {
  const [commandString, setCommandString] = useState<string>("");
  const [mode, setMode] = useState<Mode>(Mode.Brief);
  //Get list of potential commands from functionList
  let commandOptions = getCommandOptions;

 /**
 * commandRegister enables the user to register commands to the command line. 
 * It requires the user to first define a function and a keyword in commandMap.
 * @param args string representing a new command
 * @returns string of success or failure
 */
 const commandRegister: REPLFunction = (args: string[]) => {
  return fetch('http://localhost:3232/')
  .then(response => response.json())
  .catch(e => {
    if(commandOptions.has(args[0])) {
      setCommandMap(map => new Map(map.set(args[0], commandOptions.get(args[0])!)));
      return("Successfully registered command " + args);
    }
    else {
      return("Command not found in list of possible commands!")
    }})
  }

  /**
   * handles mode by parsing the string
   * setting enum mode state variable
   * to correspond to the user's desired
   * mode, handles error case
   * @param commandString
   */
    const commandMode: REPLFunction = (args: string[]) => {
      return fetch('http://localhost:3232/')
      .then(response => response.json())
      .catch(e => {
        switch (args[0]) {
          case "verbose":
            setMode(Mode.Verbose);
            return("mode set to verbose");
          case "brief":
            setMode(Mode.Brief);
            return("mode set to brief")
          default:
            return("Mode does not exist, try either mode brief or mode verbose");
        }
      })
    }

  /**
   * returnRegistry returns the current commands registered in the command 
   * @param args 
   * @returns all keys in the commandMap map
   */
  const returnRegistry: REPLFunction = async (args: string[]) => {
    return fetch('http://localhost:3232/')
  .then(response => response.json())
  .catch(e => {
    let commands = commandMap.keys();

    const newArray: string[] = Array.from(commands);
    const doubleArray: string[][] = [newArray];
    return(doubleArray);
  });
  }


  let startMap: Map<string, REPLFunction> = new Map;
  startMap.set('register', commandRegister);

  //Add commands that need access to the frontend to the list of available commands
  commandOptions.set('mode', commandMode);
  commandOptions.set('registry', returnRegistry);

  const [commandMap, setCommandMap] = useState<Map<string, REPLFunction>>(startMap);

  /**
   * handleSubmit controls the functionality of recognizing commands and directing 
   * the input from the user to the correct backend or frontend pipeline. 
   * @param commandString input string from user
   */
  
  async function handleSubmit(commandString: string) {
    const commandArgs = commandString.split(" ");
    const k = [...props.commandHistory, commandString];
    props.setCommandStringHistory(k);
    if(commandMap.has(commandArgs[0])) {
      const result = await commandMap.get(commandArgs[0])!(commandArgs.splice(1));

      //set history in proper formatting
      formatResponse(commandString, result);
      const resetPosition = 0;
      props.setPositionInHistory(0)
    }
    else {
      formatResponse(commandString, "Command not recognized");
    }
    setCommandString("");
  }

/**
 * FormatResponse handles how the output from each function is displayed.
 * If the mode is in verbose, then the command is displayed with the output.
 * If in brief mode, only the output is returned.
 * @param command string
 * @param out output string or string[][]
 */
  function formatResponse(command: string, out: string | string[][]) {
    if(mode == Mode.Verbose) {
      props.setHistory((prevHistory) => [
        ...prevHistory,
        "Command: " + command + "\n Output: ",
      ]);

      props.setHistory((prevHistory) => [...prevHistory, out]);
    }
    else {
      props.setHistory([...props.history, out])
    }
  }
  
  /**
   * handle enter upon press
   */
  const [pressedKeys, setPressedKeys] = useState<Set<string>>(new Set());
  //const keyboard : React.Context<typeof KeyboardType> = useContext(KeyboardEvent);
  const keyboard = useContext(KeyboardEvent);
  useEffect(() => {
    setPressedKeys(keyboard.keys);

  // add commands here
  // press enter to submt
  if (pressedKeys.has("Enter")) {
      handleSubmit(commandString);
      setPressedKeys(new Set()); // Clear the pressed keys
    }
  // press arrow up to get the last returned key
  else if (pressedKeys.has("ArrowUp")){
    const leng = props.commandHistory.length - 1;
    const grabIndex = leng - props.positionInHisoty;
    // console.log("caught up")
    // console.log("up: this is grab index", grabIndex)
    // console.log("up:this is commandHistory", props.commandHistory)
    if (grabIndex <= leng && grabIndex >=0){
      setCommandString(props.commandHistory[grabIndex])
      const newVal = props.positionInHisoty + 1;
      props.setPositionInHistory(newVal);
      }
    // handles arrow pressed up 
    } else if (pressedKeys.has("ArrowDown")) {
      const leng = props.commandHistory.length - 1;
      const grabIndex = leng - props.positionInHisoty + 1;
      // console.log("caught down")
      // console.log("down: this is grab index", grabIndex)
      // console.log("down:this is commandHistory", props.commandHistory)
      if (grabIndex <= leng && grabIndex >=0){
        setCommandString(props.commandHistory[grabIndex])
        const newVal = props.positionInHisoty - 1;
        props.setPositionInHistory(newVal);
      }
    } 
    // else {
    //   const resetPosition = 0;
    //   props.setPositionInHistory(resetPosition);
  
    // }
  
  }, [pressedKeys, commandString]);
  
  return (
    <div className="container">
       <KeyboardProvider onKeyboardEvent={(key) => setPressedKeys((prevKeys) => new Set([...Array.from(prevKeys), key]))}>
        <fieldset>
          <ControlledInput
            value={commandString}
            setValue={setCommandString}
            ariaLabel={"Command input"}
          />
        <button
          onClick={() => handleSubmit(commandString)}
          style={{
            backgroundColor: "#FF7F50",
            color: "#ffffff",
            marginTop: "10px",
          }}
        >
          Submit
        </button>
        </fieldset>
      </KeyboardProvider>
    </div>
  );

}
