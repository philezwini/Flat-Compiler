package flat_compiler.exception;

public class InvalidParamException extends Throwable {
	private static final long serialVersionUID = 1L;
	private String message;
	public InvalidParamException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
