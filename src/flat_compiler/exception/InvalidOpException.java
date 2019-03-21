package flat_compiler.exception;

public class InvalidOpException extends Throwable {
	private static final long serialVersionUID = 1L;
	private String message;
	public InvalidOpException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
