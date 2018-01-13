import java.util.Set;

public interface State
{
    //Add a listener to this state
    public void addListener(StateListener sl);


    //Add a listener to this state
    public void removeListener(StateListener sl);


    //Return a set containing all transitions FROM this state
    public Set<Transition> transitionsFrom();


    //Return a set containing all transitions TO this state
    public Set<Transition> transitionsTo();
    

    //Move the position of this state 
    //by (dx,dy) from its current position
    public void moveBy(int dx, int dy);
    

    //Return a string containing information about this state 
    //in the form (without the quotes, of course!) :
    //"stateName(xPos,yPos)jk"
    //where j is 1/0 if this state is/is-not an initial state  
    //where k is 1/0 if this state is/is-not a final state  
    public String toString();
    

    //Return the name of this state 
    public String getName();
    

    //Return the X position of this state
    public int getXpos();
    

    //Return the Y position of this state
    public int getYpos();

    //Set/clear this state as an initial state
    public void setInitial(boolean b);

    //Indicate if this is an initial state
    public boolean isInitial();

    //Set/clear this state as a final state
    public void setFinal(boolean b);

    //Indicate if this is a final state
    public boolean isFinal();

    //Indicate if this is a current state
    public boolean isCurrent();
}
