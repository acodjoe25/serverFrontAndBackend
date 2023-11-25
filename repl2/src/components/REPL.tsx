import { useState } from "react";
import { REPLInput } from "./REPLInput";
import { REPLHistory } from "./REPLHistory";
import { REPLFunction } from "./FunctionList";

/**
 * top level REPL component that ocntains the REPL Input and History
 * contains the history state passed into the child componenets
 * @returns the high level REPL Component
 */
export default function REPL() {
  const [history, setHistory] = useState<(string | string[][])[]>([]);
  const [commandHistory, setCommandStringHistory] = useState<string[]>([]);
  const [positionInHisoty, setPositionInHistory] = useState<number>(0)

  return (
    <div className="repl">

      <REPLHistory history={history}></REPLHistory>
      <REPLInput 
      history={history} 
      setHistory={setHistory} 
      commandHistory={commandHistory} 
      setCommandStringHistory={setCommandStringHistory} 
      positionInHisoty={positionInHisoty}
      setPositionInHistory={setPositionInHistory}
      />
      

    </div>
  );
}
