package flat_compiler.lex_analyzer.dfa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import flat_compiler.exception.DuplicateStateException;
import flat_compiler.exception.IllegalOperationException;
import flat_compiler.exception.IllegalTransitionException;
import flat_compiler.exception.StateNotFoundException;
import flat_compiler.model.TOKEN_TYPE;

public class StateGraph implements IGraph, Serializable
{
	private static final long serialVersionUID = 1L;
	
	//Make StateNode an inner class so that its implementation is hidden completely.
	private class StateNode implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		private boolean isAccepting;
		private boolean isStart;
		private int index;
		
		public StateNode(boolean isStart, boolean isAccepting, int index)throws DuplicateStateException
		{
			this.isAccepting = isAccepting;
			this.isStart = isStart;
			this.index = index;
		}
		
		public int index()
		{
			return index;
		}
		
		public boolean isAccepting()
		{
			return isAccepting;
		}
		
		public void setAccepting(boolean accepting)
		{
			this.isAccepting = accepting;
		}
	}
	
	//Graph edges should also be hidden.
	private class TransitionEdge implements Serializable
	{
		private static final long serialVersionUID = 1L;
		private String[] value;
		private StateNode from;
		private StateNode to;
		private int numTimesAccessed;
		
		public TransitionEdge(StateNode from, StateNode to, String[] value)
		{
			this.value = value;
			this.from = from;
			this.to = to;
			
			numTimesAccessed = 0;
		}
		
		boolean isFirstTransition()
		{
			return this.from.isStart;
		}
		
		boolean isLastTransition()
		{
			return transitions.isEmpty();
		}
		
		public String[] value() 
		{
			return value;
		}

		public StateNode getTo() 
		{
			return to;
		}
	}
	
	//All ASCII numerical values.
	private static final String[] INT_ASCII = {"48", "49", "50", "51", "52", "53", "54", "55", "56", "57"};
	
	//All allowed ASCII characters.
	private static final String[] CHAR_ASCII = {"32", "33", "35", "36", "37", "38", "40", "41", "42", "43",
			"44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
			"60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75",
			"76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91",
			"92", "93", "94", "97", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105",
			"106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119",
			"120", "121", "122", "123", "124", "125", "126"};
	
	//All ASCII lower case letters.
	private static final String[] LC_ASCII = {"97", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105",
			"106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119",
			"120", "121", "122"};
	
	//All ASCII upper case letters.
	private static final String[] UC_ASCII = {"65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75",
			"76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90"};
	
	//Certain data types need to have a maximum length of characters.
	private static final int MAX_ID_LENGTH = 20;
	private static final int MAX_INT_LENGTH = 12;
	private static final int MAX_STRING_LENGTH = 128;
	
	private int size;
	private int currentScore;
	private int idCounter;
	
	private String name;
	
	private boolean accepting;
	private boolean rejecting;
	
	private StateNode currentState;
	private StateNode startState;
	
	private TOKEN_TYPE type;
	
	private ArrayList<StateNode> states;
	private ArrayList<StateNode> acceptingStates;
	private Queue<TransitionEdge> transitions;
	
	
	public StateGraph(String name, TOKEN_TYPE type)
	{
		this.type = type;
		this.name = name;
		
		initializeGraph();
	}
	
	private void initializeGraph()
	{
		size = 0;
		currentScore = 0;
		idCounter = 0;
		
		accepting = false;
		rejecting = false;
		
		states = new ArrayList<>();
		acceptingStates = new ArrayList<>();
		transitions = new LinkedList<>();
		
		buildDFA();
	}
	
	//This function builds a Deterministic Finite State Automaton give the required token type.
	private void buildDFA()
	{
		try 
		{
			addState(true, false, 0);
			startState = findState(0);
			currentState = startState;
			
			//------ tokens with values that can take more than one form ------
			
			if(this.type == TOKEN_TYPE.BOOLLIT)
			{
				String[] t = {"t"};
				addState(false, false, 1);
				addTransition(0, 1, t);
				
				addState(false, false, 2);
				String[] r = {"r"};
				addTransition(1, 2, r);
				
				addState(false, false, 3);
				String[] u = {"u"};
				addTransition(2, 3, u);
				
				addState(false, true, 4);
				String[] e = {"e"};
				addTransition(3, 4, e);
				
				addState(false, false, 5);
				String[] f = {"f"};
				addTransition(0, 5, f);
				
				addState(false, false, 6);
				String[] a = {"a"};
				addTransition(5, 6, a);
				
				addState(false, false, 7);
				String[] l = {"l"};
				addTransition(6, 7, l);

				addState(false, false, 8);
				String[] s = {"s"};
				addTransition(7, 8, s);
				
				addState(false, true, 9);
				addTransition(8, 9, e);
			}
			
			else if(this.type == TOKEN_TYPE.INTLIT)
			{
				addState(false, true, 1);
				addTransition(0, 1, INT_ASCII);
				
				addTransition(1, 1, INT_ASCII);
			}
			
			else if(this.type == TOKEN_TYPE.FLOATLIT)
			{
				addState(false, false, 1);
				addTransition(0, 1, INT_ASCII);

				addTransition(1, 1, INT_ASCII);
				
				String[] temp = {"."};
				addState(false, false, 2);
				addTransition(1, 2, temp);
				
				addState(false, true, 3);
				addTransition(2, 3, INT_ASCII);
				addTransition(3, 3, INT_ASCII);
			}
			
			else if(this.type == TOKEN_TYPE.CHARLIT)
			{
				String[] temp = {"'"};
				addState(false, false, 1);
				addTransition(0, 1, temp);
				
				addState(false, false, 2);
				addTransition(1, 2, CHAR_ASCII);
				
				addState(false, true, 3);
				addTransition(2, 3, temp);
			}
			
			else if(this.type == TOKEN_TYPE.STRINGLIT)
			{
				String[] temp = {"\""};
				addState(false, false, 1);
				addTransition(0, 1, temp);
				
				addTransition(1, 1, CHAR_ASCII);
				
				addState(false, true, 2);
				addTransition(1, 2, temp);
				
			}
			
			else if(this.type == TOKEN_TYPE.ID)
			{
				addState(false, true, 1);
				addTransition(0, 1, LC_ASCII);
				addTransition(1, 1, LC_ASCII);
				
				addState(false, true, 2);
				addTransition(0, 2, UC_ASCII);
				addTransition(1, 2, UC_ASCII);
				addTransition(2, 2, UC_ASCII);
				addTransition(2, 1, LC_ASCII);
				
				addState(false, true, 3);
				addTransition(1, 3, INT_ASCII);
				addTransition(2, 3, INT_ASCII);
				addTransition(3, 3, INT_ASCII);
				addTransition(3, 2, UC_ASCII);
				addTransition(3, 1, LC_ASCII);
			}
			
			//------ tokens that have exact values ------
			else
			{
				char[] chars = name.toCharArray();
				int i = 1;
				do
				{
					String[] tempChar = {Character.toString(chars[i - 1])};
					addState(false, false, i);
					addTransition(i - 1, i, tempChar);
					
					if(i == chars.length)
						states.get(i).setAccepting(true);
					
					i++;
				}
				while(i <= chars.length);
			}			
		} 
		catch (DuplicateStateException e)
		{
			e.printStackTrace();
		}
		catch(IllegalTransitionException e)
		{
			e.printStackTrace();
		}
	}
	
	public void resetGraph()
	{
		initializeGraph();
	}
	
	private boolean stateExists(int index)
	{
		for(StateNode state : states)
		{
			if(state.index() == index)
				return true;
		}
		return false;
	}
	
	private StateNode findState(int index)
	{
		if(!stateExists(index))
			return null;
		
		return states.get(index);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}
	
	public boolean addState(boolean isStart, boolean isAccepting, int index)throws DuplicateStateException
	{
		if(stateExists(index))
			throw new DuplicateStateException();
		
		StateNode newState = new StateNode(isStart, isAccepting, index);
		states.add(newState);
		size++;
		return true;
	}
	
	public int getScore()
	{
		return currentScore;
	}
	
	public void increaseScore()
	{
		currentScore++;
	}
	
	public TOKEN_TYPE getTokenType()
	{
		return type;
	}
	
	public String getName()
	{
		return name;
	}

	@Override
	public boolean addTransition(int from, int to, String[] value)throws IllegalTransitionException
	{
		//you should not be able to add a transition to an empty DFA.
		if(isEmpty())
			throw new IllegalTransitionException();
		
		StateNode fromState = findState(from);
		StateNode toState = findState(to);
		
		// also ensure that the related states are not null.
		if((fromState == null) || (toState == null))
			throw new IllegalTransitionException();
		
		TransitionEdge transition = new TransitionEdge(fromState, toState, value);
		transitions.add(transition);
		
		return true;
	}
	
	@Override
	public ArrayList<Integer> toStateList()
	{
		ArrayList<Integer> indices = new ArrayList<>();
		for(StateNode state : states)
			indices.add(state.index());
		
		return indices;
	}

	@Override
	public ArrayList<String[]> toTransitionList()
	{
		ArrayList<String[]> values = new ArrayList<>();
		for(TransitionEdge transition : transitions)
			values.add(transition.value());
		
		return values;
	}

	@Override
	public boolean setAcceptingStates(int[] states)throws StateNotFoundException
	{
		StateNode cursor;

		for(int i = 0; i < states.length; i++)
		{
			cursor = findState(states[i]);
			if(findState(states[i]) == null)
				throw new StateNotFoundException();
			
		
			this.states.get(states[i]).setAccepting(true);
			this.acceptingStates.add(cursor);
		}
		return true;
	}

	@Override
	public boolean makeTransition(String value) throws IllegalOperationException
	{	
		if(transitions.size() > 0)
		{
			//------first check if the token type is a special case------
			
			if(this.type == TOKEN_TYPE.STRINGLIT)
				return validateStringLit(value);
			
			else if(this.type == TOKEN_TYPE.CHARLIT)
				return validateCharLit(value);
			
			else if(this.type == TOKEN_TYPE.INTLIT)
				return validateIntLit(value);
			
			else if(this.type == TOKEN_TYPE.FLOATLIT)
				return validateFloatLit(value);
			
			else if(this.type == TOKEN_TYPE.BOOLLIT)
				return validateBoollit(value);
			
			else if(this.type == TOKEN_TYPE.ID)
				return validateID(value);
						
			else //it is a token type with exact values.
			{
				TransitionEdge temp = transitions.remove();
				if(temp.value[0].equals(value))
				{
					eatChar(temp);
					return true;
				}
				rejecting = true;
				accepting = false;
				return false;
			}
		}
		return false;
	}
	
	
	public boolean validateStringLit(String value)
	{
		TransitionEdge temp = transitions.peek();
		
		//check if the string has not reached its max character length.
		if(temp.numTimesAccessed == MAX_STRING_LENGTH)
			transitions.remove();
		
		temp.numTimesAccessed++;
		
		if(temp.isFirstTransition())
		{
			transitions.remove();
			if(temp.value[0].equals(value))
			{
				eatChar(temp);
				return true;
			}
			rejecting = true;
			return false;
		}
		
		//Always check if the string has not reached the end (second quotation mark).
		Iterator<TransitionEdge> iter = transitions.iterator();
		while(iter.hasNext())
		{
			if(iter.next().value[0].equals(value))
			{
				accepting = true;
				break;
			}
		}
		
		//this will be true if the second quotation mark has been reached.
		if(accepting)
		{
			transitions.clear();
			return true;
		}
		
		//The string has not reached its end.
		int ascii = (int)value.charAt(0);
		for(int i = 0; i < temp.value.length; i++)
		{
			//get ascii value of the current character.
			int myAscii = Integer.parseInt(temp.value[i]); 
			if(myAscii == ascii)
			{
				//This is an accepted ASCII character.
				eatChar(temp);
				return true;
			}
		}
		rejecting = true;
		accepting = false;
		return false;
	}
	
	public boolean validateIntLit(String value)
	{
		TransitionEdge temp = transitions.peek(); //Do not remove the transition yet, as it might be a looping transition.
				
		if(!(temp.from.index == temp.to.index))
			transitions.remove(); //it is not a looping transition
			
		if(temp.numTimesAccessed == MAX_INT_LENGTH)
			transitions.remove(); //It is a looping transition but has looped enough times.
		
		temp.numTimesAccessed++;
		
		for(int i = 0; i < temp.value.length; i++)
		{
			int ascii = (int)value.charAt(0);
			int myAscii = Integer.parseInt(temp.value[i]);
			
			if(ascii == myAscii)
			{
				//This is a valid integer.
				eatChar(temp);
				return true;
			}
		}
		rejecting = true;
		accepting = false;
		return false;
	}
	
	public boolean validateFloatLit(String value)
	{
		boolean hasReachedDot = false;
		TransitionEdge temp = transitions.peek(); //Do not remove transition yet. It might be a looping transition.
				
		if(!(temp.from.index == temp.to.index))
			transitions.remove(); //it is not a looping transition
		
		if(temp.numTimesAccessed == MAX_INT_LENGTH)
			transitions.remove(); //It is a looping transition but it has looped enough times.
		
		temp.numTimesAccessed++;
		
		//first check for whether the floating point entry has reached the dot character.
		Iterator<TransitionEdge> iter = transitions.iterator();
		TransitionEdge dotTransition = null;
		
		while(iter.hasNext())
		{
			dotTransition = iter.next();
			if(dotTransition.value[0].equals(value))
			{
				hasReachedDot = true; //Floating point literal has reached the dot.
				break;
			}
		}
		
		if(hasReachedDot)
		{
			transitions.remove(temp); //Make sure the integer transition before the dot is removed.
			transitions.remove(dotTransition); // Remove the dot transition. It can only be performed once.
			return true;
		}
		int ascii = (int)value.charAt(0);
		for(int i = 0; i < temp.value.length; i++)
		{	
			int myAscii = Integer.parseInt(temp.value[i]);
			if(ascii == myAscii)
			{
				//It is an accepted ASCII value.
				eatChar(temp);
				return true;
			}
		}
		rejecting = true;
		accepting = false;
		return false;
	}
	
	private boolean validateCharLit(String value)
	{
		TransitionEdge temp = transitions.remove();
		if(temp.isFirstTransition() || temp.isLastTransition())
		{
			if(temp.value[0].equals(value))
			{
				eatChar(temp);
				return true;
			}
			rejecting = true;
			return false;
		}
	
		int ascii = (int)value.charAt(0);
		for(int i = 0; i < temp.value.length; i++)
		{	
			int myAscii = Integer.parseInt(temp.value[i]);
			if(ascii == myAscii)
			{
				//It is an accepted ASCII value.
				eatChar(temp);
				return true;
			}
		}
		
		rejecting = true;
		accepting = false;
		return false;
	}
	
	private boolean validateBoollit(String value)
	{
		TransitionEdge temp = transitions.remove();
		if(temp.isFirstTransition())
		{
			if(!temp.value[0].equals(value))
			{
				for(int i = 0; i < 4; i++)
					temp = transitions.remove();
				
				if(!temp.value[0].equals(value))
				{
					rejecting = true;
					accepting = false;
					return false;
				}
				else
				{
					eatChar(temp);
					return true;
				}
			}
			eatChar(temp);
			return true;
		}
		
		if(temp.value[0].equals(value))
		{
			eatChar(temp);
			return true;
		}
		rejecting = true;
		accepting = false;
		return false;
	}
	
	private boolean validateID(String value)
	{
		//Restrict the maximum length of identifiers.
		if(idCounter > MAX_ID_LENGTH)
		{
			rejecting = true;
			accepting = false;
			return false;
		}
		
		while(!transitions.isEmpty())
		{
			TransitionEdge temp = transitions.peek(); //Do not remove the transition yet, as it might be a looping transition.
			int ascii = (int)value.charAt(0);
			for(int i = 0; i < temp.value.length; i++)
			{
				int myAscii = Integer.parseInt(temp.value[i]);
				if(ascii == myAscii)
				{
					eatChar(temp);
					idCounter++; //Keep track of the length of the identifier.
					return true;
				}
			}
			transitions.remove();
		}
		
		rejecting = true;
		accepting = false;
		return false;
	}
	
	private void eatChar(TransitionEdge temp)
	{
		currentState = findState(temp.getTo().index()); //Make the next state the current state of the DFA.
		
		if(currentState.isAccepting()) //The DFA has reached its accepting state.
			accepting = true;
	}

	@Override
	public boolean hasAccepted() {
		return accepting;
	}

	@Override
	public boolean hasRejected() {
		return rejecting;
	}
	
	public String toString()
	{
		String st = "<" + name + ", " + type + "> \n"; //Display the properties of the DFa.
		//Display the transitions
		for(TransitionEdge t : transitions)
		{
			st += "S" + t.from.index + " -> S" + t.to.index + " = " + Arrays.toString(t.value) + "\n";
		}
		//Display the accepting states.
		st += "Accepting States: ";
		for(StateNode s : states)
		{
			if(s.isAccepting)
				st += "S" + s.index + " ";
		}
		return st;
	}
}
