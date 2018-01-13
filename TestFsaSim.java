import java.io.*;
import java.util.*;
import java.util.Iterator;

 
public class TestFsaSim {
 
    private FsaImpl Fsa;
 
    public TestFsaSim() {
        Fsa = new FsaImpl();
    }
 
    public static void main(String[] args) {
        new TestFsaSim().runTestSuite();
    }
 
    public void runTestSuite() {
        try {
            System.out.println("Running Simple DFA test");
            simpleDFATest();
        } catch (RuntimeException e) {
            System.err.println("\tIssue in SimpleDFA test: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            System.out.println("Running Simple ep-DFA test");
            simpleEpTest();
        } catch (RuntimeException e) {
            System.err.println("\tIssue in ep-DFA test: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            System.out.println("Running Simple NFA test");
            simpleNFATest();
        } catch (RuntimeException e) {
            System.err.println("\tIssue in Simple NFA test: " + e.getMessage());
            e.printStackTrace();
        }
    }
 
    // Clears all entries in the Fsa
    private void clearFsa() {
        for (State s: Fsa.getStates()) {
            Fsa.removeState(s);
        }
        Fsa.reset();
    }
 
    // Adds X number of states
    private void addXStates(int x) {
        for (int i=0;i < x; ++i) {
            try {
                Fsa.newState("q"+i,i,i);
            } catch (IllegalArgumentException e) {
                System.err.println("Already a state with name q" + i);
            }
        }
    }
 
    // evaluates a string of events seperated by ',', epsilon transitions are "?"
    // validTransition is if the events string should result in a valid state(s).
    private boolean evaluateEvents(String events, boolean validTransition) {
        for (String s: events.split(",")) {
            Fsa.step((s.equals("?") ? null: s));
        }
        return Fsa.isRecognised() == validTransition;
    }
 
    // Testing the regex "A*BC*"
    private void simpleDFATest() {
        clearFsa();
        if (!Fsa.getCurrentStates().isEmpty()) {
            throw new RuntimeException("Current states should be empty");
        }
        addXStates(2);
        Fsa.newTransition(Fsa.findState("q0"), Fsa.findState("q0"), "A");
        Fsa.newTransition(Fsa.findState("q0"), Fsa.findState("q1"), "B");
        Fsa.newTransition(Fsa.findState("q1"), Fsa.findState("q1"), "C");
        Fsa.findState("q0").setInitial(true);
        Fsa.findState("q1").setFinal(true);
        if (Fsa.isRecognised()) {
            throw new RuntimeException("isRecongised method does not work");
        }
        Fsa.reset();
        if (!evaluateEvents("A,A,A,A", false)) {
            throw new RuntimeException("Failed A* evaluation");
        }
        if (!Fsa.getCurrentStates().equals(Fsa.getInitialStates())) {
            throw new RuntimeException("Current states is incorrect");
        }
        if (!evaluateEvents("A,A,A,B,C,C,C,C", true)) {
            throw new RuntimeException("Does not evaluate A*BC* Correctly");
        }
        if (!Fsa.getCurrentStates().equals(Fsa.getFinalStates())) {
            throw new RuntimeException("Current states is incorrect");
        }
        Fsa.reset();
        if (!Fsa.getCurrentStates().equals(Fsa.getInitialStates())) {
            throw new RuntimeException("Does not reset the Fsa states");
        }
        if (!evaluateEvents("C,C,C,B,A,A,A,A", false)) {
            throw new RuntimeException("Process invalid states");
        }
        clearFsa();
    }
    // Evaluate A*B*
    private void simpleEpTest() {
        clearFsa();
        addXStates(2);
        Fsa.newTransition(Fsa.findState("q0"), Fsa.findState("q0"), "A");
        Fsa.newTransition(Fsa.findState("q0"), Fsa.findState("q1"), null);
        Fsa.newTransition(Fsa.findState("q1"), Fsa.findState("q1"), "B");
        Fsa.findState("q0").setInitial(true);
        Fsa.findState("q1").setFinal(true);
        Fsa.reset();
        if (!evaluateEvents("A,A,A,A", true)) {
            throw new RuntimeException("Failed A* evaluation");
        }
        if (!Fsa.getCurrentStates().equals(Fsa.getStates())) {
            throw new RuntimeException("Current states is incorrect");
        }
        if (!evaluateEvents("A,A,A,B,B,B,B,B", true)) {
            throw new RuntimeException("Does not evaluate A*B* Correctly");
        }
        clearFsa();
    }
 
    // Using the (0|1)*01 recogniser from the notes.
    private void simpleNFATest() {
        clearFsa();
        addXStates(3);
        Fsa.newTransition(Fsa.findState("q0"), Fsa.findState("q0"), "zero");
        Fsa.newTransition(Fsa.findState("q0"), Fsa.findState("q0"), "one");
        Fsa.newTransition(Fsa.findState("q0"), Fsa.findState("q1"), "zero");
        Fsa.newTransition(Fsa.findState("q1"), Fsa.findState("q2"), "one");
        Fsa.findState("q0").setInitial(true);
        Fsa.findState("q2").setFinal(true);
        // Ensure that we are inital state
        Fsa.reset();
        if (!evaluateEvents("zero,zero", false)) {
            throw new RuntimeException("Should not be in a final state");
        }
        if (Fsa.getCurrentStates().contains(Fsa.getFinalStates())) {
            throw new RuntimeException("Should not be in a final state");
        }
        if (Fsa.getCurrentStates().size() != 2) {
            throw new RuntimeException("Fsa should be in q0 and q1");
        }
        Fsa.step("one");
        if (!Fsa.isRecognised()) {
            throw new RuntimeException("Should be in a final state");
        }
        if (Fsa.getCurrentStates().size() != 2) {
            throw new RuntimeException("Should only be in two states");
        }
        if (Fsa.getCurrentStates().contains(Fsa.findState("q1"))) {
            throw new RuntimeException("q1 should not be in the current state set");
        }
        clearFsa();
    }
}