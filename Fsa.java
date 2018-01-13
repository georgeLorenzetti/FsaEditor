import java.util.Set;

public interface Fsa 
{
    //Create a new State and add it to this FSA
    //Returns the new state
    //Throws IllegalArgumentException if:
    //the name is not valid or is the same as that
    //of an existing state
    public State newState(String name, int x, int y)
      throws IllegalArgumentException;


    //Remove a state from the FSA
    //If the state does not exist, returns without error
    public void removeState(State s);


    //Find and return the State with the given name
    //If no state exists with given name, return NULL
    public State findState(String stateName);


    //Return a set containing all the states in this Fsa
    public Set<State> getStates();


    //Create a new Transition and add it to this FSA
    //Returns the new transition.
    //eventName==null specifies an epsilon-transition
    //Throws IllegalArgumentException if:
    //  The fromState or toState does not exist or
    //  The eventName is invalid or
    //  An identical transition already exists
    public Transition newTransition(State fromState, State toState,
      String eventName) 
      throws IllegalArgumentException;


    //Remove a transition from the FSA
    //If the transition does not exist, returns without error
    public void removeTransition(Transition t);


    //Find all the transitions between two states
    //Throws IllegalArgumentException if:
    //  The fromState or toState does not exist
    public Set<Transition> findTransition(State fromState, State toState);


    //Return the set of initial states of this Fsa
    public Set<State> getInitialStates();


    //Return the set of final states of this Fsa
    public Set<State> getFinalStates();


    //Returns a set containing all the current states of this FSA
    public Set<State> getCurrentStates();
    

    //Return a string describing this Fsa
    //Returns a string that contains (in this order):
    //for each state in the FSA, a line (terminated by \n) containing
    //  STATE followed the toString result for that state
    //for each transition in the FSA, a line (terminated by \n) containing
    //  TRANSITION followed the toString result for that transition
    //for each initial state in the FSA, a line (terminated by \n) containing
    //  INITIAL followed the name of the state
    //for each final state in the FSA, a line (terminated by \n) containing
    //  FINAL followed the name of the state
    public String toString();


    //Add a listener to this FSA
    public void addListener(FsaListener fl);


    //Remove a listener from this FSA
    public void removeListener(FsaListener fl);
}
