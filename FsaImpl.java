import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class FsaImpl implements Fsa, FsaSim {

    public Set<FsaListener> fsaListeners;
    //Sets of states and transitions
	public Set<State> states;
    public Set<Transition> transitions;

    //State Subsets, for initial states, final states, current states
	public Set<State> initialStates;
	public Set<State> finalStates;
	public Set<State> currentStates;

    //Default constructor
	public FsaImpl(){
		states = new HashSet<State>();
        fsaListeners = new HashSet<FsaListener>();
		initialStates = getInitialStates();
		finalStates = getFinalStates();
		currentStates = initialStates;
		transitions = new HashSet<Transition>();
	}

    //Create a new State and add it to this FSA
    //Returns the new state
    //Throws IllegalArgumentException if:
    //the name is not valid or is the same as that
    //of an existing state
    //Inputs:
    // name - the name the new state will have
    // x - x coordinate for the new state
    // y - y coordinate for the new state
	public State newState(String name, int x, int y) throws IllegalArgumentException {

        //Checks for valid name. Throw exception if invalid.
        if(name == null){
            throw new IllegalArgumentException("State name is null");
        }

        if(name.length() <= 0){
            throw new IllegalArgumentException("State name is empty");
        }

		if(findState(name) != null){
			throw new IllegalArgumentException("State '" + name + "' already exists");
		}

        if(!Character.isLetter(name.charAt(0))){
            throw new IllegalArgumentException("State name '" + name + "' does not begin with a letter");
        }

        for(int i = 0; i < name.length(); i++){
            if(name.charAt(i) != '_' && !Character.isLetter(name.charAt(i)) && !Character.isDigit(name.charAt(i))){
                throw new IllegalArgumentException("State name'"+ name + "' is not of valid format ");
            }
        }

        //add the new state to the fsa
		State newState = new StateImpl(name, x, y);
        newState.addListener(new StateIcon(newState, FsaEditor.width, FsaEditor.height));
		states.add(newState);
        for(Iterator<FsaListener> it = fsaListeners.iterator(); it.hasNext();){
            FsaPanel fl = (FsaPanel)it.next();
            fl.statesChanged();
        }
		return newState;
	}


    //Remove a state from the FSA
    //If the state does not exist, returns without error
    //Input:
    // s - The state to remove
    public void removeState(State s){

        //find and remove transitions connected to s
        Set<Transition> toTrans = findToTransition(s.getName());
        Set<Transition> fromTrans = findFromTransition(s.getName());

        for(Iterator<Transition> it = toTrans.iterator(); it.hasNext();){
            Transition t = it.next();
            removeTransition(t);
        }

        for(Iterator<Transition> it = fromTrans.iterator(); it.hasNext();){
            Transition t = it.next();
            removeTransition(t);
        }

        //remove s from all state sets
    	states.remove(s);
        initialStates.remove(s);
        finalStates.remove(s);
        currentStates.remove(s);

        for(Iterator<FsaListener> it = fsaListeners.iterator(); it.hasNext();){
            FsaListener fl = it.next();
            fl.statesChanged();
        }
    }


    //Find and return the State with the given name
    //If no state exists with given name, return null
    //Input:
    // stateName - the name of the state to look for
    public State findState(String stateName){
    	for(Iterator<State> it = states.iterator(); it.hasNext();){
    		State s = it.next();
    		if(s.getName().equals(stateName)){
    			return s;
    		}
    	}
    	return null;
    }


    //Return a set containing all the states in this Fsa
    public Set<State> getStates(){
        Set<State> copy = new HashSet<State>(states);
    	return copy;
    }

    //Return a set containing all the transitions in this Fsa
    public Set<Transition> getTransitions(){
    	return transitions;
    }


    //Create a new Transition and add it to this FSA
    //Returns the new transition.
    //eventName==null specifies an epsilon-transition
    //Throws IllegalArgumentException if;
    //  The fromState or toState does not exist or
    //  The eventName is invalid or
    //  An identical transition already exists
    //
    //Input:
    // fromState - from state for the new transition
    // toState - to state for the new transition
    // eventName - name for the new transition
    public Transition newTransition(State fromState, State toState, String eventName) throws IllegalArgumentException{
        
        //check if to and from states are valid
        String msg = "";

        if(fromState == null || toState == null){
            if(fromState == null){
                msg += "From state does not exist. ";
            }
            if(toState == null){
                msg += "To state does not exist. ";
            }
            throw new IllegalArgumentException(msg);
        }
    	boolean valid = true;

  		StateImpl f = (StateImpl)findState(fromState.getName());
  		StateImpl t = (StateImpl)findState(toState.getName());
		if(f == null || t == null){
            if(f == null){
                msg += "From state does not exist. ";
            }
            if(t == null){
                msg += "To state does not exist. ";
            }
			valid = false;
		}

        //check for valid event name
        if(eventName != null){
            if(!(eventName.length() == 1 && eventName.charAt(0) == '?')){
                for(int i = 0; i < eventName.length(); i++){
                    if(!Character.isLetter(eventName.charAt(i))){
                        throw new IllegalArgumentException("Event name must be only letters or '?' ");
                    }
                }
            }
        }
        

        //check if new transition is valid
		if(checkIfTransitionExists(eventName, fromState, toState)){
            msg += "Transition with that name and states already exists";
			valid = false;
		}

        int count = 0;
        for(Iterator<Transition> it = transitions.iterator(); it.hasNext(); ){
            Transition tt = it.next();
            if(tt.fromState().getName().equals(fromState.getName()) && tt.toState().getName().equals(toState.getName())){
                count++;
            }
        }

		if(!valid){
			throw new IllegalArgumentException(msg);
		}

        //add the new transition to the Fsa
        Transition newTransition = new TransitionImpl(toState, fromState, eventName);
        newTransition.addListener(new TransitionIcon(newTransition, count));
		t.addTransitionTo(newTransition);
		f.addTransitionFrom(newTransition);

		transitions.add(newTransition);

        for(Iterator<FsaListener> it = fsaListeners.iterator(); it.hasNext();){
            FsaListener fl = it.next();
            fl.transitionsChanged();
        }
		return newTransition;

    }


    //Remove a transition from the FSA
    //If the transition does not exist, returns without error
    //Input:
    // t - transition to remove from the Fsa
    public void removeTransition(Transition t){

        //remove transition from connected states
        for(Iterator<State> it = states.iterator(); it.hasNext();){
            State s = it.next();

            if(s.transitionsTo().contains(t)){
                s.transitionsTo().remove(t);
            }
            if(s.transitionsFrom().contains(t)){
                s.transitionsFrom().remove(t);
            }
        }

        //remove the transition
    	transitions.remove(t);

        for(Iterator<FsaListener> it = fsaListeners.iterator(); it.hasNext();){
            FsaListener fl = it.next();
            fl.transitionsChanged();
        }
    }


    //Find all the transitions between two states
    //Throws IllegalArgumentException if;
    //  The fromState or toState does not exist
    //Input: 
    // fromState - from state for the returned set of transitions
    // toState - to state for the returned set of transitions
    public Set<Transition> findTransition(State fromState, State toState) throws IllegalArgumentException{

        //throw exception if states don't exist
    	if(findState(fromState.getName()) == null || findState(toState.getName()) == null){
    		throw new IllegalArgumentException();
    	}

        //search for matching transitions
    	Transition[] transitionArray = transitions.toArray(new Transition[0]);
    	Set<Transition> matches = new HashSet<Transition>();
    	for(int i = 0; i < transitionArray.length; i++){
    		if(fromState.getName().equals(transitionArray[i].fromState().getName()) && 
    		  toState.getName().equals(transitionArray[i].toState().getName())){
    			matches.add(transitionArray[i]);
    		}
    	}

    	return matches;
    }

    //Return the set of initial states of this Fsa
    public Set<State> getInitialStates(){
        //create a new set and add all states flagged as initial
    	Set<State> initial = new HashSet<State>();
        State[] stateArray = states.toArray(new State[0]);

        for(int i = 0; i < stateArray.length; i++){
            if(stateArray[i].isInitial()){
                initial.add(stateArray[i]);
            }
        }

        //save and also return set of initial states
        initialStates = initial;
        return initial;
    }


    //Return the set of final states of this Fsa
    public Set<State> getFinalStates(){
        //create a new set and add all states flagged as final
    	Set<State> fin = new HashSet<State>();
        State[] stateArray = states.toArray(new State[0]);

        for(int i = 0; i < stateArray.length; i++){
            if(stateArray[i].isFinal()){
                fin.add(stateArray[i]);
            }
        }

        //save and also return set of final states
        finalStates = fin;
        return fin;
    }


    //Returns a set containing all the current states of this FSA
    public Set<State> getCurrentStates(){
    	return currentStates;
    }
    

    //Return a string describing this Fsa
    //Returns a string that contains (in this order);
    //for each state in the FSA, a line (terminated by \n) containing
    //  STATE followed the toString result for that state
    //for each transition in the FSA, a line (terminated by \n) containing
    //  TRANSITION followed the toString result for that transition
    //for each initial state in the FSA, a line (terminated by \n) containing
    //  INITIAL followed the name of the state
    //for each final state in the FSA, a line (terminated by \n) containing
    //  FINAL followed the name of the state
    public String toString(){

        //refresh and update lists of initial and final staes
        getInitialStates();
        getFinalStates();

        //print output
   		Transition[] transitionArray = transitions.toArray(new Transition[0]);
    	State[] stateArray = states.toArray(new State[0]);
    	State[] initialArray = getInitialStates().toArray(new State[0]);
    	State[] finalArray = getFinalStates().toArray(new State[0]);

    	String res  = "";

    	for(int i = 0; i < stateArray.length; i++){
    		res += "STATE " + stateArray[i].toString() + " " + stateArray[i].isCurrent() + "\n";
    	}
    	for(int i = 0; i < transitionArray.length; i++){
    		res += "TRANSITION " + transitionArray[i].toString() + "\n";
    	}
    	for(int i = 0; i < initialArray.length; i++){
    		res += "INITIAL " + initialArray[i].getName() + "\n";
    	}
    	for(int i = 0; i < finalArray.length; i++){
    		res += "FINAL " + finalArray[i].getName();
            if(i < finalArray.length - 1){
                res += "\n";
            }
    	}

    	return res;
    }


    //Add a listener to this FSA
    //**For Prac 3 - Currently Unimplemented**//
    public void addListener(FsaListener fl){
        if(!fsaListeners.contains(fl)){
            fsaListeners.add(fl);
        }
    }


    //Remove a listener from this FSA
    //**For Prac 3 - Currently Unimplemented**//
    public void removeListener(FsaListener fl){
        fsaListeners.remove(fl);
    }

    //FSASIM FUNCTIONS//

    //Reset the simulation to its initial state(s)
    public void reset(){
    	currentStates = getInitialStates();
        for(Iterator<State> it = states.iterator(); it.hasNext();){
            StateImpl s = (StateImpl)it.next();
            if(currentStates.contains(s)){
                s.setCurrent(true);
            }else{
                s.setCurrent(false);
            }
        }

        for(Iterator<FsaListener> it = fsaListeners.iterator(); it.hasNext();){
            FsaListener fl = it.next();
            fl.otherChanged();
        }
    }
    
    //Take one step in the simulation
    //input:
    // event - name of the event that will be executed
    public void step(String event){
        //compute eclose
        currentStates = eclose();
        //System.out.println(event);
        //iterate through post-eclose current states and execute transition
        Set<State> newCurrent = new HashSet<State>();
    	for(Iterator<Transition> it = transitions.iterator(); it.hasNext();){
            Transition t = it.next();
            if(t.eventName() != null){
                if(t.eventName().equals(event)){
                    for(Iterator<State> itt = currentStates.iterator(); itt.hasNext();){
                        StateImpl s = (StateImpl)itt.next();
                        if(s.getName().equals(t.fromState().getName())){
                            //System.out.println("  STEPPING");
                            newCurrent.add(t.toState());
                            StateImpl to = (StateImpl)t.toState();
                            s.setCurrent(false);
                            to.setCurrent(true);
                        }
                    }
                }
            }else{
                if(event.equals("?")){
                    for(Iterator<State> itt = currentStates.iterator(); itt.hasNext();){
                        StateImpl s = (StateImpl)itt.next();
                        if(s.getName().equals(t.fromState().getName())){
                            //System.out.println("  STEPPING");
                            newCurrent.add(t.toState());
                            StateImpl to = (StateImpl)t.toState();
                            s.setCurrent(false);
                            to.setCurrent(true);
                        }
                    }
                }
            }
        }

        if(newCurrent.isEmpty()){
            return;
        }else{
            for(Iterator<State> it = currentStates.iterator(); it.hasNext();){
                State si = it.next();
                if(!newCurrent.contains(si)){
                    ((StateImpl)si).setCurrent(false);
                }
            }
        }

        currentStates = newCurrent;

        for(Iterator<FsaListener> it = fsaListeners.iterator(); it.hasNext();){
            FsaListener fl = it.next();
            fl.otherChanged();
        }
    }


    //Returns true if the simulation has recognised
    //the sequence of events it has been given
    //Function computes an eclose and then checks to
    // see if any current states are also final states.
    //Returns: 
    // True - if there is a current state that is also a final state
    // False - if no current states are final states
    public boolean isRecognised(){
        Set<State> finals = getFinalStates();
        currentStates = eclose();
        for(Iterator<State> it = currentStates.iterator(); it.hasNext();){
            State c = it.next();
            if(finals.contains(c)){
                return true;
            }
        }
    	return false;
    }

    /*<----------HELPERS---------->*/

    //Check if a specific transition inlcusing connected states and transition name. 
    //Input:
    // eventName - name of the transition
    // fromState - from state of the transition
    // toState - to state of the transition
    //Return:
    // True - if transition exists
    // False - if transition does not exist or if any inputs are null
    public boolean checkIfTransitionExists(String eventName, State fromState, State toState){
        if(eventName == null || fromState == null || toState == null){
            return false;
        }

   		for(Iterator<Transition> it = transitions.iterator(); it.hasNext(); ){
            Transition t = it.next();
            if(t.eventName() != null){
                if(t.eventName().equals(eventName) && t.fromState().getName().equals(fromState.getName()) && t.toState().getName().equals(toState.getName())){
       				return true;
       			}
            }else{
                if(eventName.equals("?") && t.fromState().getName().equals(fromState.getName()) && t.toState().getName().equals(toState.getName())){
                    return true;
                }
            }
   		}

   		return false;
    }

    //Find transitions with a specified to transition.
    //Input:
    // toSTate - Name of the to state for the set of transitions
    //Return:
    // matches - a set of trasitions with toState as their to state
    public Set<Transition> findToTransition(String toState) throws IllegalArgumentException{
    	if(findState(toState) == null){
    		throw new IllegalArgumentException();
    	}

    	Transition[] transitionArray = transitions.toArray(new Transition[0]);
    	Set<Transition> matches = new HashSet<Transition>();
    	for(int i = 0; i < transitionArray.length; i++){
    		if(transitionArray[i].toState().getName().equals(toState)){
    			matches.add(transitionArray[i]);
    		}
    	}

    	return matches;
    }

    //Find transitions with a specified form transition.
    //Input:
    // fromSTate - Name of the from state for the set of transitions
    //Return:
    // matches - a set of trasitions with fromState as their from state
    public Set<Transition> findFromTransition(String fromState) throws IllegalArgumentException{
    	if(findState(fromState) == null){
    		throw new IllegalArgumentException();
    	}

    	Transition[] transitionArray = transitions.toArray(new Transition[0]);
    	Set<Transition> matches = new HashSet<Transition>();
    	for(int i = 0; i < transitionArray.length; i++){
    		if(transitionArray[i].fromState().getName().equals(fromState)){
    			matches.add(transitionArray[i]);
    		}
    	}

    	return matches;
    }

    //Compute an eclose on the current states of the Fsa
    //return:
    // ecl - The set of including all current states plus any states
    //       reachable by epsilon transitions from current states.
    public Set<State> eclose(){
        Set<State> ecl = new HashSet<State>();

        for(Iterator<State> it = currentStates.iterator(); it.hasNext();){
            State s = it.next();
            ecl.add(s);
            Set<Transition> fromTrans = s.transitionsFrom();
            for(Iterator<Transition> itt = fromTrans.iterator(); itt.hasNext();){
                Transition t = itt.next();

                if(t.eventName() == null){
                    ecl.add(t.toState());
                }
            }
        }

        return ecl;
    }

}