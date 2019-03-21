package flat_compiler.exception;

public class IllegalTransitionException extends Throwable {

	private static final long serialVersionUID = 1L;
	
	public String getMessage()
	{
		return "Transition references a non-existing or null state.";
	}
}
