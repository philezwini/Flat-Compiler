package flat_compiler.model;

import java.util.ArrayList;

public class Scope {
	private Token token;
	private int numChildren;
	private Scope parent;
	private ArrayList<Scope> children;
	private ArrayList<Token> tokens;
	private int id;

	public Scope(Token token, Scope parentScope, int id) {
		this.setToken(token);
		this.setParent(parentScope);
		this.tokens = new ArrayList<>();
		this.children = new ArrayList<>();
		numChildren = 0;
		this.token = token;
		this.setId(id);
	}

	public int getNumChildren() {
		return numChildren;
	}

	public void setNumChildren(int numChildren) {
		this.numChildren = numChildren;
	}

	public Scope getParent() {
		return parent;
	}

	public void setParent(Scope parent) {
		this.parent = parent;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public ArrayList<Scope> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Scope> children) {
		this.children = children;
	}

	public ArrayList<Token> getTokens() {
		return tokens;
	}

	public void setTokens(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}

	public void addChild(Scope child) {
		this.children.add(child);
	}

	public void addToken(Token token) {
		this.tokens.add(token);
	}

	@Override
	public String toString() {
		return this.token.getName() + " [" + this.token.getType() + ", " + this.token.getDatatype() + ", " + this.token.getDef() + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
