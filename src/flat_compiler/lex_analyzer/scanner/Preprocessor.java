package flat_compiler.lex_analyzer.scanner;

import java.util.ArrayList;

import flat_compiler.exception.InvalidStringException;

public class Preprocessor {
	String rawSource;
	String[] sourceTokens;
	ArrayList<String> source;
	
	public Preprocessor(String rawSource)
	{
		this.rawSource = rawSource;
		source = new ArrayList<>();
		
		try
		{
			findStringLiterals();
		} 
		catch (InvalidStringException e)
		{
			System.err.println("Syntax Error: " + e.getMessage());
		}
	}
	
	private void findStringLiterals()throws InvalidStringException
	{
		while(rawSource.contains("\""))
		{
			int index = rawSource.indexOf("\"");
			String temp = rawSource.substring(index);
			String last = temp;
			String first = rawSource.substring(0, index);
			String s = Character.toString(last.charAt(0));
			last = last.substring(1);
			boolean isValidString = false;
			
			do
			{	
				if(last.charAt(0) == '"')
					isValidString = true;
				
				s += last.charAt(0);				
				last = last.substring(1);
				
				if(last.length() == 0)
					throw new InvalidStringException(temp);
			}
			while(!isValidString);
			
			rawSource = last;
			String[] firstTokens = first.split("\\s");
			
			for(String str : firstTokens)
				source.add(str);
			
			source.add(s);
		}
		
		//make sure there is nothing left out in the raw source code.
		if(rawSource.length() > 0)
		{
			String[] remSource = rawSource.split("\\s");
			for(String s : remSource)
				source.add(s);
		}
	}
		
	public ArrayList<String> getTokenizedSource()
	{
		return source;
	}
}
