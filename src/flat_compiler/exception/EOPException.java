package flat_compiler.exception;

public class EOPException extends Throwable{
	private static final long serialVersionUID = 1L;
	private String prevToken;
	public EOPException(String prevToken)
	{
		this.prevToken = prevToken;
	}
	public String getMessage()
	{
		return "Error: Unexpected End of Program near '" + prevToken + "'.";
	}
}
