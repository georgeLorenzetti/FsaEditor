public class TestFsa {
 
    private FsaImpl Fsa;
 
    public TestFsa() {
        Fsa = new FsaImpl();
    }
 
    public void runTestSuite() {
        try {
            System.out.println("Testing State Functions");
            testStateMethods();
        } catch (RuntimeException e) {
            System.err.println("\tissue with state method: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            System.out.println("Testing Transition methods");
            testTransitionMethods();
        } catch (RuntimeException e) {
            System.err.println("\tissue with transition method: " + e.getMessage());
            e.printStackTrace();
        }
    }
 
    public void testStateMethods() {
        State s = Fsa.newState("q1",0,0);
        if (!s.equals(Fsa.findState("q1"))) {
            throw new RuntimeException("findState method is incorrect");
        }
        boolean exceptionThrown = false;
        // Try adding a new state of the same name
        try {
            Fsa.newState("q1",100,100);
        } catch (IllegalArgumentException e){
            exceptionThrown = true;
        }
        if (!exceptionThrown) {
            throw new RuntimeException("Failed to throw an exception when duplicate state added");
        }
        // Adding an invalid state
        exceptionThrown = false;
        try {
            Fsa.newState("___",0,0);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        if (!exceptionThrown) {
            throw new RuntimeException("Added invalid state");
        }
        // Trying to add NULL into the Fsa Object.
        exceptionThrown = false;
        try {
            Fsa.newState(null,0,0);
        } catch (IllegalArgumentException e){
            exceptionThrown = true;
        }
        if (!exceptionThrown) {
            throw new RuntimeException("Added a state with no name");
        }
        if (Fsa.getStates().size() != 1) {
            throw new RuntimeException("The Fsa Object has incorrect amount of states");
        }
        // Updating the State object outside of the FsaImpl
        s.setFinal(true);
        s.setInitial(true);
        if (Fsa.getInitialStates().size() != 1) {
            throw new RuntimeException("Fsa did not properly check Initial states");
        }
        if (Fsa.getFinalStates().size() != 1) {
            throw new RuntimeException("Fsa did not properly check Final states");
        }
        Fsa.getStates().clear();
        if (Fsa.getStates().isEmpty()) {
            throw new RuntimeException("Cleared internal states of the Fsa object");
        }
        // Asking for an invalid object
        if (Fsa.findState("") != null) {
            throw new RuntimeException("Returned an object when FsaImpl should of returned null");
        }
        // Removing the State object I created at the start
        Fsa.removeState(s);
        if (!Fsa.getStates().isEmpty()) {
            throw new RuntimeException("Failed to remove State from Fsa");
        }
 
    }
 
    public void testTransitionMethods() {
        // Need to ensure that Fsa has removed all States.
        for (State s: Fsa.getStates()) {
            Fsa.removeState(s);
        }
        if (!Fsa.getStates().isEmpty()) {
            throw new RuntimeException("Fsa Object is storing removed states");
        }
        // Creating two new states that I control.
        State q1 = Fsa.newState("q1",0,0);
        q1.setInitial(true);
        State q2 = Fsa.newState("q2",0,0);
        q1.setFinal(true);
        // Starting civil testing.
        Transition A = Fsa.newTransition(q1,q1,"A");
        // Ensure the construction of the Transition is correct
        if (!A.toState().equals(q1) && !A.fromState().equals(q1)) {
            throw new RuntimeException("Does not correctly store states");
        }
        if (!A.eventName().equals("A")) {
            throw new RuntimeException("Does not store the name correctly");
        }
        boolean exceptionThrown = false;
        // Lets try and make the Fsa Thrown an exception
        // Repeated Transition
        try {
            Fsa.newTransition(q1,q1,"A");
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        if (!exceptionThrown) {
            throw new RuntimeException("Fails to detect repeated transitions");
        }
        exceptionThrown = false;
        // Null Transitions
        try {
            Fsa.newTransition(null,null,null);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        if (!exceptionThrown) {
            throw new RuntimeException("Fsa object does not properly check for incorrect states");
        }
        exceptionThrown = false;
        // Testing for invalid transition name
        try {
            Fsa.newTransition(q1,q1,"1111");
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        if (!exceptionThrown) {
            throw new RuntimeException("Does not check valid event names");
        }
        // Continue to create the rest of the Transitions.
        Transition B = Fsa.newTransition(q1,q2,"B");
        Transition C = Fsa.newTransition(q2,q2,"C");
        // Epsilon transition
        Transition Ep = Fsa.newTransition(q2,q1,null);
        if (!Ep.toString().matches("^[a-zA-Z][a-zA-Z0-9_]*\\(\\)[a-zA-Z][a-zA-Z0-9_]*$")) {
            throw new RuntimeException("ToString method for Epsilon transition is incorrect");
        }
        if (q1.transitionsFrom().size() != 2 && q1.transitionsTo().size() != 2) {
            throw new RuntimeException("State failes to ensure that it updates its Transitions");
        }
        // Testing find Transitions
        if (Fsa.findTransition(q1,q1).size() != 1 && Fsa.findTransition(q2,q2).size() != 1) {
            throw new RuntimeException("Find Transitions does not work as intended");
        }
        Fsa.removeTransition(C);
        if (!Fsa.findTransition(q2,q2).isEmpty()) {
            throw new RuntimeException("Failed to remove transition from Fsa");
        }
        // Testing removing state and updating Transitions
        Fsa.removeState(q2);
        exceptionThrown = false;
        try {
            if (Fsa.findTransition(q2,q2).isEmpty()) {
                throw new RuntimeException("Returned an empty Set instead of throwing an exception");
            }
        } catch (IllegalArgumentException e){
            exceptionThrown = true;
        }
        if (!exceptionThrown) {
            throw new RuntimeException("Storing transitions that lead to no where");
        }
        if (q1.transitionsTo().size() != 1 && q1.transitionsFrom().size() != 1) {
            throw new RuntimeException("Extra transitions are being stored");
        }
 
    }
 
 
    public static void main(String[] args) {
        new TestFsa().runTestSuite();
    }
}