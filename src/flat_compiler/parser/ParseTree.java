package flat_compiler.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import flat_compiler.model.TOKEN_TYPE;
import flat_compiler.model.Token;

public class ParseTree {
	private PNode root;
	private int size;
	private int counter;

	public ParseTree(Token rootToken) {
		root = new PNode(null, rootToken);
		size = 1;
		counter = 0;
	}
	
	public PNode addNonTerminal(PNode parent, Token t) {
		PNode newNode = new PNode(parent, t);
		addChild(newNode);
		return newNode;
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean isValidRoot(Token t) {
		return t.getType() == TOKEN_TYPE.PROGDECL;
	}

	public String genDebugCode() {
		String debug = "digraph DFA {\n";
		debug += "rankdir=LR;\n";
		debug += "size=\"10,5;\"\n";
		//debug += "splines=false;\n";
		debug += "node [shape = circle];\n";
		debug += "S0 -> S1 [label = \"[" + root.value().getName() + ", " + root.value().getType() + "]\"];\n";
		counter = 2;
		System.out.println();
		debug += genStates(root, 1);
		debug += "}";
		return debug;
	}

	private String genStates(PNode node, int parentCounter) {
		String debug = "";
		if (!node.hasChildren()) {
			return debug;
		}

		for (PNode child : node.children()) {
			Token t = child.value();
			if (t.getType() == TOKEN_TYPE.STRINGLIT) {
				String name = t.getName().replaceAll("\"", "");
				debug += "S" + parentCounter + " -> S" + counter + " [label = \"[" + name + ", " + t.getType()
				+ "]\"];\n";
			}
			else
				debug += "S" + parentCounter + " -> S" + counter + " [label = \"[" + t.getName() + ", " + t.getType()
						+ "]\"];\n";
			counter++;
			if (child.hasChildren()) {
				debug += genStates(child, counter - 1);
			}
		}
		return debug;
	}

	public void addChild(PNode child) {
		PNode parent = child.parent();
		parent.addChild(child);
		size++;
	}

	public PNode root() {
		return this.root;
	}

	public boolean isNonTerminal(PNode node) {
		return node.value().getType() == null;
	}
	
	public boolean isRoot(PNode node) {
		return node.equals(root);
	}
	
	public void removeNode(PNode node) {
			int index = node.parent().children().indexOf(node);
			node.parent().remove(node);
			node.parent().children().add(index, node.children().get(0));
			node.children().get(0).setParent(node.parent());
			node.setParent(null);
			node.setChildren(null);
	}

	public void remove(PNode node) {
		for(PNode child : node.children()) {
			int index = node.parent().children().indexOf(node);
			node.parent().children().add(index, node);
			node.parent().addChild(child);
			child.setParent(node.parent());
		}
		fastRemove(node);
	}

	public void fastRemove(PNode node) {
		if(!isRoot(node))
			node.parent().remove(node);
		
		node.setParent(null);
		node.setChildren(null);
	}

	public int numTermChildren(PNode node) {
		int c = 0;
		for(PNode child : node.children()) {
			if(!isNonTerminal(child))
				c++;
		}
		return c;
	}

	public void replace(PNode node, PNode child) {
		if(node.value().getType() == TOKEN_TYPE.PROGDECL) {
			makeRoot(child);
			return;
		}
		child.setParent(node.parent());
		node.parent().addChild(child);
		node.children().remove(child);
		for(PNode c : node.children()) {
			c.setParent(child);
			child.addChild(c);
		}
		fastRemove(node);
	}

	private void makeRoot(PNode node) {
		root.remove(node);
		node.setParent(null);
		for(PNode child : root.children()) {
			child.setParent(node);
			node.addChild(child);
		}
		root = node;
	}

	public Queue<PNode> queueChildren(PNode node) {
		Queue<PNode> list = new LinkedList<>();
		for(PNode child : node.children()) {
			list.add(child);
		}
		return list;
	}

	public boolean isLeafNode(PNode node) {
		return node.children().isEmpty();
	}

	public void swap(PNode parent, PNode child) {
		parent.setParent(child);
		child.addChild(parent);
		parent.remove(child);
		for(PNode c : parent.children()) {
			
			c.setParent(child);
			System.out.println(c + "'s parent is now " + child.value().getName() );
			child.addChild(c);
		}
		for(int i = 0; i < parent.children().size(); i++) {
			parent.children().remove(parent.children().get(i));
		}
	}
}
