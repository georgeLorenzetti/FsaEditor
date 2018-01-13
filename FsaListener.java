public interface FsaListener
{
    //Called whenever the number of states in the FSA has changed
    public void statesChanged();

    //Called whenever the number of transitions in the FSA has changed
    public void transitionsChanged();

    //Called whenever something about the FSA has changed
    //(other than states or transitions)
    public void otherChanged();
}
