import java.io.*;
import java.util.*;

public class FsaReaderWriter implements FsaIo {  
    //This class handles reading and writing FSA representations as 
    //described in the practical specification
    FsaImpl fsa;
    //Constructor for class FsaReadeWriter
    public FsaReaderWriter(){
    }

    //Read the description of a finite-state automaton from the 
    //Reader , r, and transfer it to Fsa, f.
    //If an error is detected, throw an exception that indicates the line
    //where the error was detected, and has a suitable text message
    public void read (Reader r, Fsa f) throws FsaFormatException, IOException{
        if(r == null || f == null){
            return;
        }

        //Read input in from the writer and store each line in a list of strings
        fsa = (FsaImpl)f;
        BufferedReader br = new BufferedReader(r);
        List<String> lines = new ArrayList<String>();
        String temp;
        while((temp = br.readLine()) != null){
            lines.add(temp);
        }

        //interpret lines
        for(int i = 0; i < lines.size(); i++){
            //skip over any lines of zero length
            if(lines.get(i).length() > 0){
                //Basic format checking, for either a comment line or a line containig space seperated aguments
                if(lines.get(i).indexOf(" ") >= 0 || lines.get(i).charAt(0) == '#'){
                    //Skip processing if it's a comment line
                    if(lines.get(i).charAt(0) != '#'){
                        //Process input line
                        StringTokenizer st = new StringTokenizer(lines.get(i));
                        List<String> args = new ArrayList<String>();
                        while(st.hasMoreTokens()){
                            args.add(st.nextToken());
                        }
                        switch(args.get(0)){
                            case "state":
                                readState(args, i+1);
                                break;
                            case "transition":
                                readTransition(args, i+1);
                                break;
                            case "initial":
                                readInitial(args, i+1);
                                break;
                            case "final":
                                readFinal(args, i+1);
                                break;
                            default:
                                //Throw format exception if the record identifier isn't valid
                                throw new FsaFormatException(i+1, "Invalid record identifier. identifier must be one of: 'state','transition','initial','final'");
                        }
                    }
                }else{
                    if(lines.get(i).charAt(0) != '\n' && lines.get(i).charAt(0) != '\r'){
                        //Throw format exception if the lines aren't formatted as a list of space seperated args
                        throw new FsaFormatException(i+1, "Invalid input line format. Format must either be record identifier followed by arguemnts or a comment lin denoted by a '#'");
                    }
                }
            }
        }
    }
    
    
    //Write a representation of the Fsa, f, to the Writer, w.
    public void write(Writer w, Fsa f) throws IOException{
        if(w == null || f == null){
            return;
        }

        //take the toString output from the Fsa and load it into a bufferedReader
        FsaImpl fsa2 = (FsaImpl)f;
        String res = fsa2.toString();
        InputStream ss = new ByteArrayInputStream(res.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(ss));

        //Read through each line of the toString output and convert it into the output file format.
        String temp = br.readLine();
        boolean newline = false;
        while(temp != null){
            if(newline){
                w.write("\n");
            }
            newline = true;

            //tokenize line
            StringTokenizer st = new StringTokenizer(temp);
            List<String> args = new ArrayList<String>();
            while(st.hasMoreTokens()){
                args.add(st.nextToken());
            }

            //convert it into correct format
            if(args.get(0).equals("STATE")){
                String line = "state ";
                line += args.get(1).substring(0, args.get(1).indexOf('(')) + " ";
                line += args.get(1).substring(args.get(1).indexOf('(')+1, args.get(1).indexOf(',')) + " ";
                line += args.get(1).substring(args.get(1).indexOf(',')+1, args.get(1).indexOf(')'));    
                w.write(line);
            }else if(args.get(0).equals("TRANSITION")){
                String line = "transition ";
                line += args.get(1).substring(0, args.get(1).indexOf('(')) + " " + args.get(1).substring(args.get(1).indexOf('(')+1, args.get(1).indexOf(')')) + " " + args.get(1).substring(args.get(1).indexOf(')')+1);
                w.write(line);
            }else if(args.get(0).equals("INITIAL")){
                String line = "initial " + args.get(1);
                w.write(line);
            }else if(args.get(0).equals("FINAL")){
                String line = "final " + args.get(1);
                w.write(line);
            } 
            temp = br.readLine();
        }

        br.close();
    }

    /*<----------HELPERS---------->*/

    //Handle the input of a state record. Checks for valid state name and then adds state to the fsa.
    //Input: 
    // l - list of arguments from the input state record
    // lineNum - line number of input file for exception throwing.
    public void readState(List<String> l, int lineNum) throws FsaFormatException{
        //check for correct number of arguments in the record
        if(l.size() != 4){
            throw new FsaFormatException(lineNum,"Wrong number of args. State records accept 3 args in the form: stateName, xPos, yPos");
        }

        //check for valid state name
        String name = l.get(1);
        if(!Character.isLetter(name.charAt(0))){
            throw new FsaFormatException(lineNum, "State name must start with a letter");
        }
        for(int i = 0; i < name.length(); i++){
            if(name.charAt(i) != '_' && !Character.isLetter(name.charAt(i)) && !Character.isDigit(name.charAt(i))){
                throw new FsaFormatException(lineNum, "Invalid character in name");
            }
        }

        //add state to the Fsa
        StateImpl st = (StateImpl)fsa.newState(l.get(1), Integer.parseInt(l.get(2)), Integer.parseInt(l.get(3)));
    }

    //Handle the input of a transition record. Checks for valid event name and then attempts to add transition to the fsa.
    //Input: 
    // l - list of arguments from the input state record
    // lineNum - line number of input file for exception throwing.
    public void readTransition(List<String> l, int lineNum) throws FsaFormatException{
        //check for correct number of arguments in the record
        if(l.size() != 4){
            throw new FsaFormatException(lineNum, "Wrong number of args. Transition records accept 3 args int the for: fromState, eventName ,toState");
        }

        State s1 = fsa.findState(l.get(1));
        State s2 = fsa.findState(l.get(3));

        //check event name validity
        String eventName = l.get(2);
        if(!(eventName.length() == 1 && eventName.charAt(0) == '?')){
            for(int i = 0; i < eventName.length(); i++){
                if(!Character.isLetter(eventName.charAt(i))){
                    throw new FsaFormatException(lineNum, "Event name must be only letters or '?' ");
                }
            }
        }

        fsa.newTransition(s1, s2, l.get(2));
    }

    //Handle the input of an initial state record. Check that the state has been declared previously and then set the state to be an initial state.
    //Input: 
    // l - list of arguments from the input state record
    // lineNum - line number of input file for exception throwing.
    public void readInitial(List<String> l, int lineNum) throws FsaFormatException{
        //check for correct number of arguments in the record
        if(l.size() != 2){
            throw new FsaFormatException(lineNum, "Wrong number of args. Initial records accept 1 argument: stateName");
        }

        //check if state has been declared
        StateImpl sta = (StateImpl)fsa.findState(l.get(1)); 
        if(sta == null){
            throw new FsaFormatException(lineNum, "Invalid state name: " + l.get(1));
        }

        //Set state to initial
        sta.setInitial(true);
        sta.setCurrent(true);
        fsa.getInitialStates();
        fsa.reset();
    }

    //Handle the input of an final state record. Check that the state has been declared previously and then set the state to be a final state.
    //Input: 
    // l - list of arguments from the input state record
    // lineNum - line number of input file for exception throwing.
    public void readFinal(List<String> l, int lineNum) throws FsaFormatException{
        //check for correct number of arguments in the record
        if(l.size() != 2){
            throw new FsaFormatException(lineNum, "Wrong number of args. Final records accept 1 argument: stateName");
        }
        //check if state has been declared
        StateImpl sta = (StateImpl)fsa.findState(l.get(1));
        if(sta == null){
            throw new FsaFormatException(lineNum, "Invalid state name: " + l.get(1));        
        }

        //set state to final
        sta.setFinal(true);
        fsa.getFinalStates();
    }
  }