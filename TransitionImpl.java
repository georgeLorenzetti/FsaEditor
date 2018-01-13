import java.util.Set;
import java.util.*;

public class TransitionImpl implements Transition {

    //member variables
	private State toState;
	private State fromState;
	private String eventName;

    private Set<TransitionIcon> listeners;
    //Constructor for the TransImpl class.
    //Input:
    // t - to state of the new transition
    // f - from state of the new transition
    // e - event name of the new transition
	public TransitionImpl(State t, State f, String e){
        listeners = new HashSet<TransitionIcon>();
		toState = t;
		fromState = f;
        if(e == null){
            eventName = null;
        }else{
            if(e.equals("?")){
                eventName = null;
            }else{
                eventName = e;
            }
        }
	}

    public void addListener(TransitionListener sl){
        if(!listeners.contains((TransitionIcon)sl)){
            TransitionIcon si = (TransitionIcon)sl;
            listeners.add(si);
            si.TransitionHasChanged();
        }
    }


    //Add a listener to this state
    //**For Prac 3 - Currently Unimplemented**//
    public void removeListener(TransitionListener sl){
        listeners.remove((TransitionIcon)sl);
    }

    public Set<TransitionIcon> getListeners(){
        return listeners;
    }

    //Return the from-state of this transition
    public State fromState(){
    	return fromState;
    }

    //Return the to-state of this transition
    public State toState(){
    	return toState;
    }

    //Return the name of the event that causes this transition
    public String eventName(){
    	return eventName;
    }

    //Return a string containing information about this transition 
    //in the form (without quotes, of course!):
    //"fromStateName(eventName)toStateName"
    public String toString(){
        if(eventName == null){
    	   return fromState.getName() + "(" + "?" + ")" + toState.getName();
        }
        return fromState.getName() + "(" + eventName + ")" + toState.getName();
    }
}