package flat_compiler.exception;

public class StateNotFoundException extends Throwable {

	private static final long serialVersionUID = 1L;
	
	public String getMessage()
	{
		return "State has not been created";
	}
}
