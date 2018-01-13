import java.util.*;

public class StateImpl implements State{   

    //member variables
    private String name;
    private int y, x;
    private int size = 100;
    private boolean isInitial;
    private boolean isFinal;
    private boolean isCurrent;

    //Sets of transitions where this state is a to or from state.
    private Set<Transition> fromTransitions;
    private Set<Transition> toTransitions;

    private Set<StateIcon> listeners;

    //Constructor for the StateImpl class.
    //Input:
    // n - name of the new state
    // x - x coordinate of the new state
    // y - y coordinate of the new state
    public StateImpl(String n, int x, int y){
        name = n;
        this.y = y;
        this.x = x;

        fromTransitions = new HashSet<Transition>();
        toTransitions = new HashSet<Transition>();
        listeners = new HashSet<StateIcon>();
    }

    //Add a listener to this state
    //**For Prac 3 - Currently Unimplemented**//
    public void addListener(StateListener sl){
        if(!listeners.contains((StateIcon)sl)){
            StateIcon si = (StateIcon)sl;
            si.setPosition(x, y);
            si.setState(this);
            listeners.add(si);
            size = si.getIconSize();
        }
    }


    //Add a listener to this state
    //**For Prac 3 - Currently Unimplemented**//
    public void removeListener(StateListener sl){
        listeners.remove((StateIcon)sl);
    }


    //Return a set containing all transitions FROM this state
    public Set<Transition> transitionsFrom(){
        return fromTransitions;
    }


    //Return a set containing all transitions TO this state
    public Set<Transition> transitionsTo(){
        return toTransitions;
    }
    

    //Move the position of this state 
    //by (dx,dy) from its current position
    public void moveBy(int dx, int dy){
        x += dx;
        y += dy;


        if(x < 0){
            x = 0;
        }
        if(y < 0){
            y = 0;
        }

        for(Iterator<StateIcon> it = listeners.iterator(); it.hasNext(); ){
            StateIcon sl = it.next();
            if(x+sl.getIconSize() > sl.getMaxX()){
                x = sl.getMaxX()-sl.getIconSize();
            }

            if(y+sl.getIconSize() > sl.getMaxY()){
                y = sl.getMaxY()-sl.getIconSize()+5;
            }
        }

        for(Iterator<StateIcon> it = listeners.iterator(); it.hasNext(); ){
            StateIcon sl = it.next();
            sl.StateHasChanged();
        }
    }
    

    //Return a string containing information about this state 
    //in the form (without the quotes, of course!) :
    //"stateName(xPos,yPos)jk"
    //where j is 1/0 if this state is/is-not an initial state  
    //where k is 1/0 if this state is/is-not a final state  
    public String toString(){
        String res = "";
        res += name;
        res += "(" + x + "," + y + ")";

        if(isInitial){
            res += "1";
        }else{
            res += "0";
        }

        if(isFinal){
            res += "1";
        }else{
            res += "0";
        }

        return res;
    }
    

    //Return the name of this state 
    public String getName(){
        return name;
    }
    

    //Return the X position of this state
    public int getXpos(){
        return x;
    }
    

    //Return the Y position of this state
    public int getYpos(){
        return y;
    }

    //Set/clear this state as an initial state
    public void setInitial(boolean b){
        isInitial = b;

        for(Iterator<StateIcon> it = listeners.iterator(); it.hasNext(); ){
            StateIcon sl = it.next();
            sl.StateHasChanged();
        }
    }

    //Indicate if this is an initial state
    public boolean isInitial(){
        return isInitial;
    }

    //Set/clear this state as a final state
    public void setFinal(boolean b){
        isFinal = b;

        for(Iterator<StateIcon> it = listeners.iterator(); it.hasNext(); ){
            StateIcon sl = it.next();
            sl.StateHasChanged();
        }
    }

    //Indicate if this is a final state
    public boolean isFinal(){
        return isFinal;
    }

    //Indicate if this is a current state
    public boolean isCurrent(){
        return isCurrent;
    }

    /*<----------HELPERS---------->*/

    //Set/clear this state as a current state
    public void setCurrent(boolean b){
        isCurrent = b;
        for(Iterator<StateIcon> it = listeners.iterator(); it.hasNext(); ){
            StateIcon sl = it.next();
            sl.StateHasChanged();
        }
    }

    //add a transition to the list of trasition in which this state is a to state.
    public void addTransitionTo(Transition t){
        toTransitions.add(t);

        for(Iterator<StateIcon> it = listeners.iterator(); it.hasNext(); ){
            StateIcon sl = it.next();
            sl.StateHasChanged();
        }
    }

    //add a transition to the list of trasition in which this state is a from state.
    public void addTransitionFrom(Transition f){
        fromTransitions.add(f);

        for(Iterator<StateIcon> it = listeners.iterator(); it.hasNext(); ){
            StateIcon sl = it.next();
            sl.StateHasChanged();
        }
    }

    public Set<StateIcon> getListeners(){
        return listeners;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getSize(){
        return size;
    }
}