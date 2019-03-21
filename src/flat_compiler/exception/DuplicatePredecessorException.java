package flat_compiler.exception;

public class DuplicatePredecessorException extends Throwable {

	private static final long serialVersionUID = 1L;
	
	public String getMessage()
	{
		return "A predecessor with the same index already exists.";
	}
}
