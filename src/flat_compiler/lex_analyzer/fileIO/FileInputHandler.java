package flat_compiler.lex_analyzer.fileIO;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import flat_compiler.lex_analyzer.dfa.StateGraph;

//Helper class for reading persistent data from the file system.
public class FileInputHandler
{
	//Function for loading source code from a .aft file.
	public static String loadSource(String filePath)
	{
		File file = new File(filePath);
		Scanner in = null;
		String s = null;
		try {
			in = new Scanner(file);
			s = new String();
			while(in.hasNextLine()) {
				s += in.nextLine() + "\n";
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			if(in != null) {
				in.close();
			}
		}
		return s;
	}
	
	//Function for reading DFAs stored in the file system.
	@SuppressWarnings("unchecked")
	public static ArrayList<StateGraph> readDFAsFromFile(String filePath)
	{
		ArrayList<StateGraph> DFAs = null;
		//Use the ObjectInputStream class for reading binary data.
		ObjectInputStream ois = null;
		FileInputStream fis = null;
		try 
		{
			DFAs = new ArrayList<>();
			fis = new FileInputStream(filePath);
			ois = new ObjectInputStream(new BufferedInputStream(fis));
			DFAs = (ArrayList<StateGraph>)ois.readObject();
			System.out.println("Read successful.");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return DFAs;
	}
}
