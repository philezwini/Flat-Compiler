package flat_compiler.parser;

import java.util.ArrayList;

import flat_compiler.model.Token;

public class PNode {
	private PNode parent;
	private ArrayList<PNode> children;
	private Token value;

	public PNode(PNode parent, Token value) {
		this.parent = parent;
		this.children = new ArrayList<>();
		this.value = value;
	}

	public Token value() {
		return value;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public String toString() {
		String result = "Token = <" + value.getName() + ", " + value.getType() + ">\n";
		if (parent != null) {
			result += "Parent Token = <" + parent.value().getName() + ", " + parent.value().getType() + ">\n";
		}

		result += "-----------Children----------: \n\n";
		for (PNode child : children) {
			result += "Child Token = <" + child.value().getName() + ", " + child.value().getType() + ">\n";
		}

		return result;
	}

	public void addChild(PNode child) {
		this.children.add(child);
	}
	
	
	public PNode parent() {
		return this.parent;
	}

	public ArrayList<PNode> children() {
		return this.children;
	}

	public void setParent(PNode parent) {
		this.parent = parent;
	}

	public void remove(PNode child) {
		this.children.remove(child);
	}

	public void setChildren(ArrayList<PNode> children) {
		this.children = children;
	}

	public void setValue(Token t) {
		this.value = t;
	}

}
