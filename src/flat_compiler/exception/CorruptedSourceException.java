package flat_compiler.exception;

public class CorruptedSourceException extends Throwable {
	private static final long serialVersionUID = 1L;
	public String getMessage()
	{
		return "Fatal Error: Source file is corrupted or empty. Cannot begin parse.";
	}
}
