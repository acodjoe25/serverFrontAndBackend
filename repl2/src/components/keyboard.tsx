import { createContext, ReactNode, useCallback, useEffect, useState } from "react"
import { REPLInput} from "./REPLInput";
/**
 * citation: watched his youtube video in order to write this class
 * https://www.youtube.com/watch?v=0IYsk2obkng
 */
// A collection of the seen keys
type KeyboardType = {
    keys : Set<string> // key mapped to its function
}
// the intial state of keyboard type
const initial: KeyboardType = {
    keys: new Set()
}
// creates the context object for the keyboard
const KeyboardEvent = createContext<KeyboardType>(initial);

// provides the interface relevant to handeling keyboard events
interface KeyboardProviderProps {
    children: ReactNode;
    onKeyboardEvent: (key: string) => void; // key event is detected
}

// instatiates an instance of th einerface above in o
const KeyboardProvider = ({ children, onKeyboardEvent }: KeyboardProviderProps) => {
    const [keys, setKeys] = useState<Set<string>>(new Set());

    // callback for the pressing down of a key
    const handleKeyDown = useCallback(
        (e: KeyboardEvent) => {
        console.log("down", e.key); // for debugging purposes
        const newKey = new Set([...Array.from(keys), e.key.toLocaleLowerCase()]);
        setKeys(newKey)

        onKeyboardEvent(e.key); 
        }, [onKeyboardEvent]);

    // callback for pressing up of a key
    const handleKeyUp = useCallback(
        (e: KeyboardEvent) => {

            const newKey = new Set([...Array.from(keys), e.key.toLocaleLowerCase()]);
            setKeys(newKey);}, []); 
    // listeners waiting for either a key pressed down or a key pressed up
    useEffect(() => {
        window.addEventListener("keydown", handleKeyDown);
        window.addEventListener("keyup", handleKeyUp);

        return () => {
            window.removeEventListener("keydown", handleKeyDown);
            window.removeEventListener("keyup", handleKeyUp);
            
        };}, [handleKeyUp, handleKeyDown]);
        
    
return (
    <KeyboardEvent.Provider value ={{keys}} >
        {children}
    </KeyboardEvent.Provider>);};
// exporting classes to acess in replInputs (see for more spefic keyboard functiionality)
export { KeyboardProvider, KeyboardEvent };    
export type { KeyboardType };

