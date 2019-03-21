package flat_compiler.lex_analyzer.scanner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import flat_compiler.exception.IllegalOperationException;
import flat_compiler.lex_analyzer.dfa.StateGraph;
import flat_compiler.lex_analyzer.fileIO.FileInputHandler;
import flat_compiler.model.TOKEN_TYPE;
import flat_compiler.model.Token;

public class CodeScanner 
{
	private String rawSource;
	private static final String DFA_FILE_PATH = "data/DFA/DFA.dfa";
	private ArrayList<String> prepSource; //This is for storing source code that has went through the preprocessor.
	
	private Queue<Token> finalTokens; //This is for storing final source code tokens to be passed on to the parser.
	private ArrayList<StateGraph> DFAs; //This is for storing the DFAs retrieved from file.
	
	public CodeScanner(String rawSource)
	{
		this.setRawSource(rawSource);	
		Preprocessor prep = new Preprocessor(rawSource);
		prepSource = prep.getTokenizedSource();
		DFAs = new ArrayList<>();
		finalTokens = new LinkedList<>();
	}

	public Queue<Token> getSourceTokens()
	{
		return finalTokens;
	}
	
	public void scanSource()
	{
		if(prepSource.size() > 0)
		{
			System.out.println("Scan starting....\n");
			DFAs = FileInputHandler.readDFAsFromFile(DFA_FILE_PATH);
			ArrayList<String> errorLog = new ArrayList<>();
			ArrayList<StateGraph> acceptingDFAs = new ArrayList<>();
			ArrayList<StateGraph> rejectingDFAs = new ArrayList<>();
			int cursor = 0;
			boolean moveToNext = true;
			String currentWord = "";
			try 
			{
				while(cursor < prepSource.size())
				{
					if(moveToNext)
					{
						cursor++;
						currentWord = prepSource.get(cursor - 1);
					}
					char[] chars = new char[currentWord.length()];
					chars = currentWord.toCharArray();
					String temp = "";
					boolean stillValid = true;
					int index = 0;

					while(stillValid && (index < chars.length))
					{
						String value = Character.toString(chars[index]);
						stillValid = false;
						
						for(StateGraph dfa : DFAs)
						{				
							if(dfa.hasRejected())
							{
								if(!rejectingDFAs.contains(dfa))
									rejectingDFAs.add(dfa);
									
								if(acceptingDFAs.contains(dfa))
									acceptingDFAs.remove(dfa);
							}
							else
							{
								if(dfa.makeTransition(value))
								{
									stillValid = true;
									dfa.increaseScore();
									if(dfa.hasAccepted())
									{
										acceptingDFAs.add(dfa);
									}
								}
							}
						}
						
						if(stillValid)
							temp += value;

						index++;
					}
					if(acceptingDFAs.size() == 0)
					{
						errorLog.add(currentWord);
						moveToNext = true;
					}
					else
					{
						int winnerIndex = 0;
						int highestScore = 0;
						ArrayList<StateGraph> ties = new ArrayList<StateGraph>();
						for(StateGraph dfa : acceptingDFAs)
						{
							if(dfa.getScore() > highestScore)
							{
								highestScore = dfa.getScore();		
							}
						}
						for(StateGraph dfa : acceptingDFAs)
						{
							if(dfa.getScore() == highestScore)
							{
								ties.add(dfa);
							}	
						} 
						//keywords take precedence over identifiers.
						if(ties.size() > 1)
						{
							for(StateGraph dfa : ties)
							{
								if(!(dfa.getTokenType() == TOKEN_TYPE.ID))
								{
									winnerIndex = ties.indexOf(dfa);
								}
							}
						}
						
						Token t = new Token(temp, ties.get(winnerIndex).getTokenType());
						finalTokens.add(t);
						
						if(!(temp.length() == currentWord.length()))
						{
							currentWord = currentWord.substring(t.getName().length());
							moveToNext = false;
						}
						else
							moveToNext = true;
					}
					
					//reinitialize accepting and rejecting DFAs
					acceptingDFAs = new ArrayList<>();
					rejectingDFAs = new ArrayList<>();

					
					//reinitialize the temporary DFAs
					for(StateGraph dfa : DFAs)
						dfa.resetGraph();
				}
				
				System.out.println("Scan completed.\n");
				
				/*if(finalTokens.size() > 0)
				{
					System.out.println("--------------- Valid Tokens ---------------\n");
					for(Token t : finalTokens)
						System.out.println("<" + t.getName() + ", " + t.getType() + ">\n");
					
					System.out.println();
				}
				
				if(errorLog.size() > 0)
				{
					System.err.println("--------------- Syntax Errors ---------------\n");
					
					for(String s : errorLog)
						System.out.println(s);
				}*/
			}
			catch(IllegalOperationException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}

	public String getRawSource() {
		return rawSource;
	}

	public void setRawSource(String rawSource) {
		this.rawSource = rawSource;
	}
}
