package flat_compiler.exception;

public class GrammarException extends Throwable {
	private static final long serialVersionUID = 1L;
	private String previousToken, missingToken;
	
	public GrammarException(String missingToken, String previousToken)
	{
		this.previousToken = previousToken;
		this.missingToken = missingToken;
	}
	public String getMessage()
	{
		return "Error: Missing " + missingToken + " near '" + previousToken + "'.";
	}
}
