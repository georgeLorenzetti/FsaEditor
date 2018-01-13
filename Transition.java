public interface Transition
{
    //Add a listener to this Transition
    public void addListener(TransitionListener tl);


    //Remove a listener tfrom this Transition
    public void removeListener(TransitionListener tl);


    //Return the from-state of this transition
    public State fromState();
    

    //Return the to-state of this transition
    public State toState();
    

    //Return the name of the event that causes this transition
    public String eventName();
    

    //Return a string containing information about this transition 
    //in the form (without quotes, of course!):
    //"fromStateName(eventName)toStateName"
    public String toString();
}
