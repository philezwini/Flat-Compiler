package flat_compiler.lex_analyzer.fileIO;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import flat_compiler.lex_analyzer.dfa.StateGraph;

//Helper class for writing data to the file system.
public class FileOutputHandler {
	
	//Function for writing a collection of DFAs to the file system.
	public static void writeDFAsToFile(ArrayList<StateGraph> DFAs, String filePath)
	{
		try 
		{
			FileOutputStream fos = new FileOutputStream(filePath);
			//Use the ObjectOutputStream class for writing binary data.
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
			oos.writeObject(DFAs);
			oos.flush();
			oos.close();
			fos.close();
			System.out.println("DFAs saved to file");
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void writeDebugCodeToFile(String debugCode, String filePath)
	{
		File file = new File(filePath);
		PrintWriter out;
		try 
		{
			out = new PrintWriter(file);
			out.print(debugCode);
			out.flush();
			out.close();
			System.out.println("Debug code written to file.");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
