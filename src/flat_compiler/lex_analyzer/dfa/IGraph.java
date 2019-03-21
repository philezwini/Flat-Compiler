package flat_compiler.lex_analyzer.dfa;

import java.util.ArrayList;

import flat_compiler.exception.IllegalOperationException;
import flat_compiler.exception.IllegalTransitionException;
import flat_compiler.exception.StateNotFoundException;

public interface IGraph
{
	boolean addTransition(int from, int to, String[] value)throws IllegalTransitionException;
	boolean setAcceptingStates(int[] states)throws StateNotFoundException;
	boolean makeTransition(String value) throws IllegalOperationException;
	
	//Helper functions for making the graph iterable by converting its states or transitions to lists.
	ArrayList<Integer> toStateList();
	ArrayList<String[]> toTransitionList();
	
	int size();
	boolean isEmpty();
	boolean hasAccepted();
	boolean hasRejected();
}
