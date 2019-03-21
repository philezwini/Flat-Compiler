package flat_compiler.model;

public class ArrayToken {
	private TYPE type;
	private int size;
	private String name;

	public ArrayToken(TYPE type, int size, String name) {
		super();
		this.type = type;
		this.size = size;
		this.name = name;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ArrayToken [type=" + type + ", size=" + size + ", name=" + name + "]";
	}
}
