package flat_compiler.model;

public class Token {
	private TOKEN_TYPE type;
	private TOKEN_DEF def;
	private TYPE datatype;
	private String name;
	private String value;
	private int scopeId;

	public Token(String name, TOKEN_TYPE type) {
		setScopeId(-1);
		this.type = type;
		this.name = name;
		datatype = null;
		value = null;
		def = null;
	}

	public TYPE getDatatype() {
		return datatype;
	}

	public void setDatatype(TYPE datatype) {
		this.datatype = datatype;
	}

	public void setType(TOKEN_TYPE type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TOKEN_TYPE getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Token [type=" + type + ", datatype=" + datatype + ", name=" + name + ", value=" + value + "]";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public TOKEN_DEF getDef() {
		return def;
	}

	public void setDef(TOKEN_DEF def) {
		this.def = def;
	}

	public int getScopeId() {
		return scopeId;
	}

	public void setScopeId(int scopeId) {
		this.scopeId = scopeId;
	}

}
