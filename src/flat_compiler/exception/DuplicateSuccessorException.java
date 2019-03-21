package flat_compiler.exception;

public class DuplicateSuccessorException extends Throwable {

	private static final long serialVersionUID = 1L;

	public String getMessage()
	{
		return "A successor with the same index already exists.";
	}
}
