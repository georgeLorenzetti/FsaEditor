import java.io.Reader;
import java.io.IOException;
import java.io.Writer;

public interface FsaIo 
{
    //This class handles reading and writing FSA representations as 
    //described in the practical specification

    //Read the description of a finite-state automaton from the 
    //Reader , r, and transfer it to Fsa, f.
    //If an error is detected, throw an exception that indicates the line
    //where the error was detected, and has a suitable text message
    public void read (Reader r, Fsa f) 
      throws IOException, FsaFormatException;
    
    
    //Write a representation of the Fsa, f, to the Writer, w.
    public void write(Writer w, Fsa f)
      throws IOException;
}
