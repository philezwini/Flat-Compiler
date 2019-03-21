package flat_compiler.csa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;

import flat_compiler.model.TOKEN_TYPE;
import flat_compiler.model.TYPE;
import flat_compiler.model.Token;
import flat_compiler.parser.PNode;
import flat_compiler.parser.ParseTree;

public class ASTBuilder {
	private ParseTree pTree;
	private int counter;

	public ASTBuilder(ParseTree tree) {
		this.pTree = tree;
		counter = 0;
	}

	public void optTree() {
		Stack<PNode> stack = new Stack<>();
		getNTLeafNodes(pTree.root(), stack);
		remNTLeafNodes(stack);
		findRedTNodes(pTree.root(), stack);
		remRedTNodes(stack);
		Queue<PNode> queue = new LinkedList<>();
		findOpNodes(pTree.root(), queue);
		fixOpNodes(queue);
		getNTOneChildNodes(pTree.root(), stack);
		remNTOneChildNodes(stack);
		HashMap<PNode, PNode> map = new HashMap<>();
		findArrayDecl(pTree.root(), map);
		remArrayDecl(map);
	}

	private void remArrayDecl(HashMap<PNode, PNode> map) {
		for (Entry<PNode, PNode> entry : map.entrySet()) {
			PNode id = entry.getKey();
			PNode size = entry.getValue();
			id.addChild(size);
			size.parent().remove(size);
			size.setParent(id);
		}
	}

	private void findArrayDecl(PNode node, HashMap<PNode, PNode> map) {
		PNode id = null;
		PNode size = null;

		for (PNode child : node.children()) {
			if (child.value().getType() == TOKEN_TYPE.INTLIT)
				size = child;
			if (child.value().getType() == TOKEN_TYPE.ID)
				id = child;
		}

		if ((id != null) && (size != null))
			map.put(id, size);

		for (PNode child : node.children())
			findArrayDecl(child, map);
	}

	private void remRedTNodes(Stack<PNode> stack) {
		while (!stack.isEmpty())
			pTree.fastRemove(stack.pop());
	}

	private void findRedTNodes(PNode node, Stack<PNode> stack) {
		if (!pTree.isNonTerminal(node)) {
			if (isRedundant(node)) {
				stack.add(node);
			}
		}
		for (PNode child : node.children())
			findRedTNodes(child, stack);
	}

	private boolean isRedundant(PNode node) {
		Token t = node.value();
		if (isScopeToken(t))
			return true;
		else if (t.getType() == TOKEN_TYPE.SEMICOL)
			return true;
		else if ((t.getType() == TOKEN_TYPE.LPARENT) || (t.getType() == TOKEN_TYPE.RPARENT))
			return true;
		else if ((t.getType() == TOKEN_TYPE.LSQRB) || (t.getType() == TOKEN_TYPE.RSQRB))
			return true;
		else if (t.getType() == TOKEN_TYPE.TYPESPEC)
			return true;
		else if (t.getType() == TOKEN_TYPE.DOT)
			return true;
		else if (t.getType() == TOKEN_TYPE.COMMA)
			return true;

		return false;
	}

	private boolean isScopeToken(Token t) {
		if (t.getType() == TOKEN_TYPE.START)
			return true;
		else if (t.getType() == TOKEN_TYPE.ENDIF)
			return true;
		else if (t.getType() == TOKEN_TYPE.ENDELSE)
			return true;
		else if (t.getType() == TOKEN_TYPE.ENDFUNC)
			return true;
		else if (t.getType() == TOKEN_TYPE.ENDWHILE)
			return true;
		else if (t.getType() == TOKEN_TYPE.ENDFOR)
			return true;
		else if (t.getType() == TOKEN_TYPE.ENDPROG)
			return true;
		else if (t.getType() == TOKEN_TYPE.FUNC)
			return true;
		return false;
	}

	private boolean isDatatype(TOKEN_TYPE t) {
		if (t == TOKEN_TYPE.INTEGER)
			return true;
		else if (t == TOKEN_TYPE.FLOAT)
			return true;
		else if (t == TOKEN_TYPE.BOOLEAN)
			return true;
		else if (t == TOKEN_TYPE.CHARACTER)
			return true;
		else if (t == TOKEN_TYPE.STRING)
			return true;
		else if (t == TOKEN_TYPE.VOID)
			return true;

		return false;
	}

	private boolean isOperator(PNode node) {
		if (isBinaryOp(node))
			return true;

		TOKEN_TYPE t = node.value().getType();
		if (t == TOKEN_TYPE.SYSTEM)
			return true;
		else if (t == TOKEN_TYPE.IF)
			return true;
		else if (t == TOKEN_TYPE.ELSE)
			return true;
		else if (t == TOKEN_TYPE.WHILE)
			return true;
		else if (t == TOKEN_TYPE.FOR)
			return true;
		else if (t == TOKEN_TYPE.DEC)
			return true;
		else if (t == TOKEN_TYPE.INC)
			return true;
		else if (t == TOKEN_TYPE.LINEOUT)
			return true;
		else if (t == TOKEN_TYPE.OUT)
			return true;
		else if (t == TOKEN_TYPE.RETURN)
			return true;
		else if (t == TOKEN_TYPE.CALL)
			return true;
		else if (t == TOKEN_TYPE.AT)
			return true;
		else if (t == TOKEN_TYPE.FUNC)
			return true;

		else if (isDatatype(t))
			return true;

		return false;
	}

	private void fixOpNodes(Queue<PNode> queue) {
		while (!queue.isEmpty()) {
			PNode node = queue.remove();
			if (isBinaryOp(node)) {
				pTree.replace(node.parent(), node);
				if (pTree.isNonTerminal(node.parent()))
					pTree.replace(node.parent(), node);
			} else {
				while (pTree.isNonTerminal(node.parent()) && !isScope(node.parent())) {
					pTree.replace(node.parent(), node);
				}
			}
		}
	}

	private boolean isScope(PNode node) {
		Token t = node.value();
		if (t.getName().equals("branchList"))
			return true;
		else if (t.getName().equals("paramList"))
			return true;
		else if (t.getName().equals("funcList"))
			return true;
		return false;
	}

	private boolean isBinaryOp(PNode node) {
		Token t = node.value();
		if (t.getType() == TOKEN_TYPE.PLUS) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.MINUS) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.FWDSLASH) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.STAR) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.MOD) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.AND) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.OR) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.NOT) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.XOR) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.OR) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.GT) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.GTEQ) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.LT) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.LTEQ) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.EQ) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.NEQ) {
			return true;
		} else if (t.getType() == TOKEN_TYPE.ASSIGN) {
			return true;
		}

		return false;
	}

	private void findOpNodes(PNode node, Queue<PNode> queue) {
		if (isOperator(node))
			queue.add(node);

		for (PNode child : node.children())
			findOpNodes(child, queue);
	}

	private void getNTLeafNodes(PNode node, Stack<PNode> stack) {
		for (PNode child : node.children()) {
			getNTLeafNodes(child, stack);
		}
		if ((pTree.isLeafNode(node)) && pTree.isNonTerminal(node))
			stack.add(node);
	}

	private void remNTLeafNodes(Stack<PNode> stack) {
		while (!stack.isEmpty()) {
			PNode node = stack.pop();
			pTree.fastRemove(node);
		}
	}

	private void remNTOneChildNodes(Stack<PNode> stack) {
		while (!stack.isEmpty()) {
			PNode node = stack.pop();
			if (!node.value().getName().equals("funcList"))
				pTree.removeNode(node);
		}
	}

	private void getNTOneChildNodes(PNode node, Stack<PNode> stack) {
		if ((node.children().size() == 1) && (pTree.isNonTerminal(node)))
			stack.add(node);

		for (PNode child : node.children())
			getNTOneChildNodes(child, stack);
	}

	public String getDebugCode() {
		return genDebugCode();
	}

	public String genDebugCode() {
		String debug = "digraph DFA {\n";
		debug += "rankdir=UD;\n";
		debug += "size=\"10,5;\"\n";
		// debug += "splines=false;\n";
		debug += "node [shape = circle];\n";
		debug += "S0 -> S1 [label = \"[" + pTree.root().value().getName() + ", " + pTree.root().value().getType() + ", "
				+ pTree.root().value().getDatatype() + ", " + pTree.root().value().getDef() + ", "
				+ pTree.root().value().getScopeId() + "]\"];\n";
		counter = 2;
		System.out.println();
		debug += genStates(pTree.root(), 1);
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
				debug += "S" + parentCounter + " -> S" + counter + " [label = \"[" + name + ", " + t.getType() + ", "
						+ t.getDatatype() + ", " + t.getDef() + "]\"];\n";
			} else {
				debug += "S" + parentCounter + " -> S" + counter + " [label = \"[" + t.getName() + ", " + t.getType()
						+ ", " + t.getDatatype() + ", " + t.getDef() + ", " + t.getScopeId() + "]\"];\n";
			}
			counter++;
			if (child.hasChildren()) {
				debug += genStates(child, counter - 1);
			}
		}
		return debug;
	}

	public ParseTree getASTree() {
		return this.pTree;
	}

	public void optTreeForUse() {
		Queue<PNode> queue = new LinkedList<>();
		getDeclarations(pTree.root(), queue);
		remDeclarations(queue);
		getFuncDeclarations(pTree.root(), queue);
		remFuncDeclarations(queue);
		updateLitTypes(pTree.root());
		reverseOrder(pTree.root());
	}

	private void reverseOrder(PNode node) {
		Stack<PNode> stack = new Stack<>();
		for(PNode child : node.children())
			stack.push(child);
		
		ArrayList<PNode> rightOrder = new ArrayList<>();
		while(!stack.isEmpty()) 
			rightOrder.add(stack.pop());
		
		node.setChildren(rightOrder);
		
		for(PNode child : node.children())
			reverseOrder(child);
	}

	private void updateLitTypes(PNode node) {
		if(node.value().getType() == TOKEN_TYPE.INTLIT) {
			node.value().setDatatype(TYPE.INTEGER);
			node.value().setValue(node.value().getName());
		}
		else if(node.value().getType() == TOKEN_TYPE.CHARLIT) {
			node.value().setDatatype(TYPE.CHARACTER);
			node.value().setValue(node.value().getName());
		}
		else if(node.value().getType() == TOKEN_TYPE.STRINGLIT) {
			node.value().setDatatype(TYPE.STRING);
			node.value().setValue(node.value().getName());
		}
		else if(node.value().getType() == TOKEN_TYPE.BOOLLIT) {
			node.value().setDatatype(TYPE.BOOLEAN);
			node.value().setValue(node.value().getName());
		}
		else if(node.value().getType() == TOKEN_TYPE.FLOATLIT) {
			node.value().setDatatype(TYPE.FLOAT);
			node.value().setValue(node.value().getName());
		}
		
		for(PNode child : node.children())
			updateLitTypes(child);
	}

	private void remFuncDeclarations(Queue<PNode> queue) {
		while (!queue.isEmpty()) {
			PNode node = queue.remove();
			PNode rep = null;
			for (PNode child : node.children()) {
				if ((child.value().getType() == TOKEN_TYPE.ID))
					rep = child;
				else if(child.value().getType() == TOKEN_TYPE.PROGDECL)
					rep = child;
			}
			if (rep != null)
				pTree.replace(node, rep);
		}
	}

	private void getFuncDeclarations(PNode node, Queue<PNode> queue) {
		if (node.value().getType() == TOKEN_TYPE.PROGDECL)
			queue.add(node);
		else if (isDatatype(node.value().getType())) {
			if (node.hasChildren()) {
				queue.add(node);
			} else {
			}
		}
		for (PNode child : node.children())
			getFuncDeclarations(child, queue);
	}

	private void remDeclarations(Queue<PNode> queue) {
		while (!queue.isEmpty()) {
			PNode node = queue.remove();
			if (node.children().size() == 1) {
				pTree.removeNode(node);
			} else {
				PNode replacement = null;
				for (PNode child : node.children()) {
					if (child.value().getType() == TOKEN_TYPE.ASSIGN) {
						replacement = child;
					}
				}
				if (replacement != null) {
					pTree.replace(node, replacement);
				}
			}
		}
	}

	private void getDeclarations(PNode node, Queue<PNode> queue) {
		if (isDatatype(node.value().getType())) {
			queue.add(node);
		}
		for (PNode child : node.children())
			getDeclarations(child, queue);
	}
}
