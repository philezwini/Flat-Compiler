package flat_compiler.csa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import flat_compiler.exception.InvalidOpException;
import flat_compiler.exception.InvalidParamException;
import flat_compiler.exception.InvalidProgException;
import flat_compiler.exception.InvalidTokenException;
import flat_compiler.exception.InvalidTypeException;
import flat_compiler.exception.SystemOutRequestException;
import flat_compiler.interpreter.Interpreter;
import flat_compiler.model.ArrayToken;
import flat_compiler.model.Scope;
import flat_compiler.model.TOKEN_DEF;
import flat_compiler.model.TOKEN_TYPE;
import flat_compiler.model.TYPE;
import flat_compiler.model.Token;
import flat_compiler.parser.PNode;
import flat_compiler.parser.ParseTree;

public class CSAHelper {
	private ParseTree asTree;
	private HashMap<Integer, Scope> table;
	private ArrayList<ArrayToken> declArrays;
	private Stack<Scope> activeScopes;
	private Scope progScope;
	private int scopeId;

	public CSAHelper(ParseTree asTree) throws InvalidProgException {
		scopeId = 0;
		this.asTree = asTree;
		table = new HashMap<>();
		activeScopes = new Stack<>();
		declArrays = new ArrayList<>();
		if (asTree.root().value().getType() == TOKEN_TYPE.PROGDECL) {
			Token t = asTree.root().value();
			for (PNode node : asTree.root().children()) {
				if (node.value().getType() == TOKEN_TYPE.ID) {
					t.setName(node.value().getName());
					t.setDatatype(null);
					node.setValue(t);
					this.progScope = addScope(t, null);
				}
			}
			asTree.root().setValue(t);
		} else
			throw new InvalidProgException("The program is missing a program declaration.");
	}

	public void checkUse() throws InvalidParamException, InvalidOpException, InvalidTokenException {
		PNode progNode = asTree.root();
		Token t = progNode.value();
		Scope s = table.get(t.getScopeId());
		if (s != null) {
			if (s.getToken() == t) {
				activeScopes.push(s);
				checkUse(asTree.root());
				System.out.println("Use check complete.");
			}
		} else {
			System.out.println("Scope " + t.getName() + " (id = " + t.getScopeId() + ") not found in symbol table");
		}
	}

	private void checkUse(PNode node) throws InvalidParamException, InvalidOpException, InvalidTokenException {
		for (PNode child : node.children()) {
			if (child.value().getName().equals("funcList")) {
				for (PNode c : child.children()) {
					if (isScope(c)) {
						validateParams(c);
						validateBranches(c);
					}
				}
			}
			checkUse(child);
		}
	}

	private void validateParams(PNode node) {
		for (PNode child : node.children()) {
			if (child.value().getName().equals("paramList")) {
				for (PNode c : child.children()) {
					if (c.value().getType() == TOKEN_TYPE.ID) {
						Scope s = table.get(node.value().getScopeId());
						if (s != null) {
							for (Token t : s.getTokens()) {
								if (c.value().getScopeId() == t.getScopeId()) {
									c.setValue(node.value());
								}
							}
						}
					}
					validateParams(c);
				}
			}
		}
	}

	private void validateBranches(PNode node) throws InvalidOpException, InvalidTokenException {
		for (PNode child : node.children()) {
			if (child.value().getName().equals("branchList")) {
				for (PNode c : child.children()) {
					if (isUnaryOp(c) || isBinaryOp(c)) {
						Scope s = table.get(node.value().getScopeId());
						if (s != null)
							validateOp(c, s);
					}
					validateBranches(c);
				}
			} else {
				if (isUnaryOp(child) || isBinaryOp(child)) {
					Scope s = table.get(node.value().getScopeId());
					validateOp(child, s);
				}
			}
		}
	}

	private void validateOp(PNode node, Scope s) throws InvalidTokenException, InvalidOpException {
		if (isUnaryOp(node)) {
			validateUnOp(node, s);

		} else if (isBinaryOp(node)) {
			validateBinOp(node, s);
		}
		for (PNode child : node.children())
			validateOp(child, s);
	}

	private void validateBinOp(PNode node, Scope s) throws InvalidTokenException, InvalidOpException {
		for (PNode child : node.children()) {
			if (child.value().getType() == TOKEN_TYPE.ID) {
				validateDecl(child, s);
			}
		}
		validateExprType(node);
	}

	private void validateExprType(PNode node) throws InvalidOpException {
		TYPE a = node.children().get(0).value().getDatatype();
		TYPE b = node.children().get(1).value().getDatatype();
		TYPE exprType = null;
		if (node.value().getType() != TOKEN_TYPE.ASSIGN)
			exprType = validateType(a, b, node.value().getType());
		else {
			if (a != b)
				throw new InvalidOpException("Invalid operand types '" + a + "' and '" + b + "' for binary operator '"
						+ node.value().getName() + "'");
			exprType = a;
		}
		if (exprType == null)
			throw new InvalidOpException("Invalid operand types '" + a + "' and '" + b + "' for binary operator '"
					+ node.value().getName() + "'");

		node.value().setDatatype(exprType);
	}

	private void validateUnOp(PNode node, Scope s) throws InvalidTokenException {
		Token t = node.value();
		if (t.getType() == TOKEN_TYPE.SYSTEM) {
			for (PNode child : node.children()) {
				if ((child.value().getType() == TOKEN_TYPE.OUT) || (child.value().getType() == TOKEN_TYPE.LINEOUT)) {
					for (PNode c : child.children()) {
						if (c.value().getType() == TOKEN_TYPE.ID) {
							validateDecl(c, s);
						}
					}
				}
			}
		} else if (t.getType() == TOKEN_TYPE.SYSTEM) {

		}
	}

	private void validateDecl(PNode node, Scope s) throws InvalidTokenException {
		Token t = node.value();
		boolean found = false;
		for(Token temp : s.getTokens()) {
			if(t.getName().equals(temp.getName()))
					found = true;
		}
		if(!found)
			throw new InvalidTokenException("Invalid token '" + t.getName() + "'");			
	}

	private boolean isUnaryOp(PNode node) {
		TOKEN_TYPE t = node.value().getType();
		if (t == TOKEN_TYPE.SYSTEM)
			return true;
		else if (t == TOKEN_TYPE.DEC)
			return true;
		else if (t == TOKEN_TYPE.INC)
			return true;
		else if (t == TOKEN_TYPE.RETURN)
			return true;
		else if (t == TOKEN_TYPE.CALL)
			return true;
		else if (t == TOKEN_TYPE.AT)
			return true;
		else if (t == TOKEN_TYPE.ARRAY)
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

	private boolean isScope(PNode child) {
		TOKEN_DEF def = child.value().getDef();
		if (def == TOKEN_DEF.FUNCTION)
			return true;
		else if (def == TOKEN_DEF.FOR_LOOP)
			return true;
		else if (def == TOKEN_DEF.WHILE_LOOP)
			return true;
		else if (def == TOKEN_DEF.IF_STMNT)
			return true;
		return false;
	}

	public void findDeclarations() throws InvalidTypeException {
		for (PNode child : asTree.root().children())
			findDeclarations(child, progScope);
	}

	private void findDeclarations(PNode node, Scope parent) throws InvalidTypeException {
		if (!node.hasChildren())
			return;

		String name = node.value().getName();
		if (name.equals("funcList")) {
			for (PNode child : node.children()) {
				if (isDatatype(child.value().getType())) {
					Scope scope = makeFuncScope(child, parent);
					collectTokens(child, scope);
				}
			}
		}

		for (PNode child : node.children())
			findDeclarations(child, parent);
	}

	private void collectTokens(PNode node, Scope scope) throws InvalidTypeException {
		for (PNode child : node.children()) {
			if (child.value().getName().equals("paramList")) {
				for (PNode c : child.children()) {
					addParamDecl(c, scope);
				}
				collectTokens(child, scope);
			} else if (child.value().getName().equals("branchList")) {
				for (PNode c : child.children()) {
					if (isDatatype(c.value().getType())) {
						for (PNode n : c.children()) {
							if (n.value().getType() == TOKEN_TYPE.ID) {
								if (n.hasChildren())
									addArrayDecl(n, scope);
								else
									addDecl(c, scope);
							}
						}
					}
				}
				collectTokens(child, scope);
			} else if (child.value().getType() == TOKEN_TYPE.WHILE) {
				Scope s = makeWhileScope(child, scope);
				for (PNode c : child.children()) {
					if (isDatatype(c.value().getType())) {
						for (PNode n : c.children()) {
							if (n.value().getType() == TOKEN_TYPE.ID) {
								if (n.hasChildren())
									addArrayDecl(n, s);
								else
									addDecl(c, s);
							}
						}
					}
				}
				collectTokens(child, s);
			} else if (child.value().getType() == TOKEN_TYPE.FOR) {
				Scope s = makeForScope(child, scope);
				for (PNode c : child.children()) {
					if (isDatatype(c.value().getType())) {
						for (PNode n : c.children()) {
							if (n.value().getType() == TOKEN_TYPE.ID) {
								if (n.hasChildren())
									addArrayDecl(n, s);
								else
									addDecl(c, s);
							}
						}
					}
				}
				collectTokens(child, s);
			} else if (child.value().getType() == TOKEN_TYPE.IF) {
				Scope s = makeIfScope(child, scope);
				for (PNode c : child.children()) {
					if (isDatatype(c.value().getType())) {
						for (PNode n : c.children()) {
							if (n.value().getType() == TOKEN_TYPE.ID) {
								if (n.hasChildren())
									addArrayDecl(n, s);
								else
									addDecl(c, s);
							}
						}
					}
				}
				collectTokens(child, s);
			}
		}
	}

	private void addParamDecl(PNode node, Scope scope) {
		for (PNode child : node.children()) {
			if (child.value().getType() == TOKEN_TYPE.ID) {
				Token t = child.value();
				t.setDatatype(toType(node.value().getType()));
				t.setScopeId(scope.getId());
				t.setDef(TOKEN_DEF.PARAM);
				child.setValue(t);
				scope.addToken(t);
				System.out.println("Added: " + t + " to: " + scope.getToken().getName());
			}
		}
	}

	private Scope makeIfScope(PNode c, Scope parent) {
		Token t = c.value();
		t.setDef(TOKEN_DEF.IF_STMNT);
		int scId = scopeId++;
		t.setScopeId(scId);
		Scope s = new Scope(t, parent, scId);
		parent.addChild(s);
		System.out.println(parent.getToken().getName() + " now contains: " + t.getName());
		table.put(s.getId(), s);
		return s;
	}

	private Scope makeForScope(PNode c, Scope parent) {
		Token t = c.value();
		t.setDef(TOKEN_DEF.FOR_LOOP);
		int scId = scopeId++;
		t.setScopeId(scId);
		Scope s = new Scope(t, parent, scId);
		parent.addChild(s);
		System.out.println(parent.getToken().getName() + " now contains: " + t.getName());
		table.put(s.getId(), s);
		return s;
	}

	private Scope makeWhileScope(PNode c, Scope parent) {
		Token t = c.value();
		t.setDef(TOKEN_DEF.WHILE_LOOP);
		int scId = scopeId++;
		t.setScopeId(scId);
		Scope s = new Scope(t, parent, scId);
		parent.addChild(s);
		System.out.println(parent.getToken().getName() + " now contains: " + t.getName());
		table.put(s.getId(), s);
		return s;
	}

	private void addArrayDecl(PNode node, Scope scope) throws InvalidTypeException {
		if (node.children().get(0).value().getType() != TOKEN_TYPE.INTLIT) {
			throw new InvalidTypeException(
					"Invalid argument type for array variable: " + node.value().getName() + "'s size.");
		} else {
			Token t = node.value();
			t.setDatatype(toType(node.parent().value().getType()));
			t.setDef(TOKEN_DEF.ARRAY);
			t.setScopeId(scope.getId());
			node.setValue(t);
			scope.addToken(t);

			int size = Integer.parseInt(node.children().get(0).value().getName());
			ArrayToken aToken = new ArrayToken(t.getDatatype(), size, t.getName());
			declArrays.add(aToken);

			System.out.println("Added: " + aToken + " and " + t + " to: " + scope.getToken().getName());
			System.out.println("Symbol table size: " + table.size());
		}
	}

	private void addDecl(PNode node, Scope scope) {
		for (PNode child : node.children()) {
			if (child.value().getType() == TOKEN_TYPE.ID) {
				Token t = child.value();
				t.setDatatype(toType(node.value().getType()));
				t.setScopeId(scope.getId());
				child.setValue(t);
				scope.addToken(t);
				System.out.println("Added: " + t + " to: " + scope.getToken().getName());
			}
		}
	}

	private Scope makeFuncScope(PNode node, Scope parent) {
		for (PNode child : node.children()) {
			if (child.value().getType() == TOKEN_TYPE.ID) {
				Token id = child.value();
				id.setDatatype(toType(node.value().getType()));
				id.setDef(TOKEN_DEF.FUNCTION);
				Scope s = addScope(id, parent);
				parent.addChild(s);
				System.out.println(parent.getToken().getName() + " now contains: " + id.getName());
				child.setValue(id);
				table.put(s.getId(), s);
				System.out.println(s + " added to symbol table. Parent: " + parent);
				return s;
			}
		}
		return null;
	}

	private Scope addScope(Token token, Scope parent) {
		int scId = scopeId++;
		token.setScopeId(scId);
		Scope s = new Scope(token, parent, scId);
		if (token.getType() != TOKEN_TYPE.PROGDECL)
			parent.addChild(s);
		table.put(s.getId(), s);

		if (s.getToken().getType() != TOKEN_TYPE.PROGDECL)
			System.out.println("Scope: " + s.getToken().getName() + "[" + s.getToken().getDatatype() + ", "
					+ s.getToken().getType() + "] added to scope: " + parent.getToken().getName());
		else
			System.out.println(
					"Scope: " + s.getToken().getName() + "[" + s.getToken().getType() + "] added to scope: null");
		System.out.println("Symbol table size: " + table.size());
		return s;
	}

	private TYPE toType(TOKEN_TYPE type) {
		if (type == TOKEN_TYPE.INTEGER)
			return TYPE.INTEGER;
		else if (type == TOKEN_TYPE.FLOAT)
			return TYPE.FLOAT;
		else if (type == TOKEN_TYPE.BOOLEAN)
			return TYPE.BOOLEAN;
		else if (type == TOKEN_TYPE.CHARACTER)
			return TYPE.CHARACTER;
		else if (type == TOKEN_TYPE.STRING)
			return TYPE.STRING;
		else if (type == TOKEN_TYPE.VOID)
			return TYPE.VOID;

		return null;
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

	private boolean isBranch(PNode node) {

		if (node.value().getType() == TOKEN_TYPE.IF)
			return true;
		else if (node.value().getType() == TOKEN_TYPE.ELSE)
			return true;
		else if (node.value().getType() == TOKEN_TYPE.WHILE)
			return true;
		else if (node.value().getType() == TOKEN_TYPE.FOR)
			return true;

		return false;
	}

	public ArrayList<ArrayToken> getDeclArrays() {
		return declArrays;
	}

	public void setDeclArrays(ArrayList<ArrayToken> declArrays) {
		this.declArrays = declArrays;
	}

	private TYPE validateType(TYPE lhs, TYPE rhs, TOKEN_TYPE operator) {
		if (operator == TOKEN_TYPE.PLUS) {
			if (lhs == TYPE.INTEGER && rhs == TYPE.INTEGER) {
				return TYPE.INTEGER;
			} else if (lhs == TYPE.INTEGER && rhs == TYPE.FLOAT) {
				return TYPE.FLOAT;
			} else if (lhs == TYPE.FLOAT && rhs == TYPE.INTEGER) {
				return TYPE.FLOAT;
			} else if (lhs == TYPE.STRING && rhs == TYPE.STRING) {
				return TYPE.STRING;
			} else if (lhs == TYPE.STRING && rhs == TYPE.CHARACTER) {
				return TYPE.STRING;
			} else if (lhs == TYPE.CHARACTER && rhs == TYPE.STRING) {
				return TYPE.STRING;
			}
		} else if (operator == TOKEN_TYPE.MINUS) {
			if (lhs == TYPE.INTEGER && rhs == TYPE.INTEGER) {
				return TYPE.INTEGER;
			} else if (lhs == TYPE.INTEGER && rhs == TYPE.FLOAT) {
				return TYPE.FLOAT;
			} else if (lhs == TYPE.FLOAT && rhs == TYPE.INTEGER) {
				return TYPE.FLOAT;
			}
		} else if (operator == TOKEN_TYPE.FWDSLASH) {
			if (lhs == TYPE.INTEGER && rhs == TYPE.INTEGER) {
				return TYPE.INTEGER;
			} else if (lhs == TYPE.INTEGER && rhs == TYPE.FLOAT) {
				return TYPE.FLOAT;
			} else if (lhs == TYPE.FLOAT && rhs == TYPE.INTEGER) {
				return TYPE.FLOAT;
			}
		} else if (operator == TOKEN_TYPE.STAR) {
			if (lhs == TYPE.INTEGER && rhs == TYPE.INTEGER) {
				return TYPE.INTEGER;
			} else if (lhs == TYPE.INTEGER && rhs == TYPE.FLOAT) {
				return TYPE.FLOAT;
			} else if (lhs == TYPE.FLOAT && rhs == TYPE.INTEGER) {
				return TYPE.FLOAT;
			}
		} else if (operator == TOKEN_TYPE.MOD) {
			if (lhs == TYPE.INTEGER && rhs == TYPE.INTEGER) {
				return TYPE.INTEGER;
			} else if (lhs == TYPE.INTEGER && rhs == TYPE.FLOAT) {
				return TYPE.INTEGER;
			} else if (lhs == TYPE.FLOAT && rhs == TYPE.INTEGER) {
				return TYPE.INTEGER;
			}
		} else if (operator == TOKEN_TYPE.AND) {
			if (lhs == TYPE.BOOLEAN && rhs == TYPE.BOOLEAN) {
				return TYPE.BOOLEAN;
			}
		} else if (operator == TOKEN_TYPE.OR) {
			if (lhs == TYPE.BOOLEAN && rhs == TYPE.BOOLEAN) {
				return TYPE.BOOLEAN;
			}
		} else if (operator == TOKEN_TYPE.NOT) {
			if (lhs == TYPE.BOOLEAN && rhs == TYPE.BOOLEAN) {
				return TYPE.BOOLEAN;
			}
		} else if (operator == TOKEN_TYPE.XOR) {
			if (lhs == TYPE.BOOLEAN && rhs == TYPE.BOOLEAN) {
				return TYPE.BOOLEAN;
			}
		} else if ((operator == TOKEN_TYPE.EQ) || (operator == TOKEN_TYPE.NEQ) || (operator == TOKEN_TYPE.GTEQ)
				|| (operator == TOKEN_TYPE.LTEQ) || (operator == TOKEN_TYPE.GT) || (operator == TOKEN_TYPE.LT)) {

			if (lhs == TYPE.INTEGER && rhs == TYPE.INTEGER) {
				return TYPE.BOOLEAN;
			}
			if (lhs == TYPE.FLOAT && rhs == TYPE.FLOAT) {
				return TYPE.BOOLEAN;
			}
			if (lhs == TYPE.CHARACTER && rhs == TYPE.CHARACTER) {
				return TYPE.BOOLEAN;
			}
			if (lhs == TYPE.STRING && rhs == TYPE.STRING) {
				return TYPE.BOOLEAN;
			}
		}
		return null;
	}

	public void setASTree(ParseTree asTree) {
		this.asTree = asTree;
	}

	public void invokeInterpreter() throws InvalidProgException, SystemOutRequestException {
		Interpreter i = new Interpreter(asTree, table);
		i.interpret();
	}
}
