package flat_compiler.exception;

public class InvalidStringException extends Throwable{
	private static final long serialVersionUID = 1L;
	
	private String errorToken;
	
	public InvalidStringException(String errorToken)
	{
		this.errorToken = errorToken;
	}
	
	public String getMessage()
	{
		return errorToken + "does not have a closing quoation mark.";
	}
}
