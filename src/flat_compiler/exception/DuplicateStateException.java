package flat_compiler.exception;

public class DuplicateStateException extends Throwable {
	private static final long serialVersionUID = 1L;
	
	public String getMessage()
	{
		return "Cannot add duplicate start state.";
	}
}
