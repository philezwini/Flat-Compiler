package flat_compiler.exception;

public class InvalidProgException extends Throwable {
	private static final long serialVersionUID = 1L;
	private String message;
	public InvalidProgException(String message) {
		this.message = message;
	}
	public String getMessage()
	{
		return message;
	}
}
