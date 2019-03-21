package flat_compiler.exception;

public class IllegalOperationException extends Throwable 
{

	private static final long serialVersionUID = 1L;
	
	public String getMessage()
	{
		return "DFA has already reached its accepting state.";
	}
}
