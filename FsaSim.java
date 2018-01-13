import java.util.List;

public interface FsaSim 
{
    //Reset the simulation to its initial state(s)
    public void reset();
    
    //Take one step in the simulation
    public void step(String event);


    //Returns true if the simulation has recognised
    //the sequence of events it has been given
    public boolean isRecognised();
}
