package flat_compiler.interpreter;

import java.util.Collection;
import java.util.HashMap;

import flat_compiler.exception.InvalidProgException;
import flat_compiler.exception.SystemOutRequestException;
import flat_compiler.model.Scope;
import flat_compiler.model.TOKEN_DEF;
import flat_compiler.model.TOKEN_TYPE;
import flat_compiler.model.TYPE;
import flat_compiler.model.Token;
import flat_compiler.parser.PNode;
import flat_compiler.parser.ParseTree;

public class Interpreter {
	private ParseTree asTree;
	private HashMap<Integer, Scope> table;
	
	public Interpreter(ParseTree asTree, HashMap<Integer, Scope> table) {
		super();
		this.asTree = asTree;
		this.setTable(table);
	}
	
	public void interpret() throws InvalidProgException, SystemOutRequestException {
		Collection<Scope> scopes = table.values();
		boolean mainFound = false;
		for(Scope s : scopes) {
			Token t = s.getToken();
			if((t.getDef() == TOKEN_DEF.FUNCTION) && (t.getDatatype() == TYPE.VOID) && t.getName().equals("main")) {
				mainFound = true;
			}
		}
		if(!mainFound)
			throw new InvalidProgException("Program is missing a main method.");
		
		PNode main = findMain(asTree.root());
		if(main != null) {
			System.out.println("Main found.");
			Scope mainScope = table.get(main.value().getScopeId());
			interpret(main, mainScope);
		}else {
			System.out.println("Main is null;");
		}
	}

	private PNode findMain(PNode node) {
		Token t = node.value();
		if((t.getDef() == TOKEN_DEF.FUNCTION) && (t.getDatatype() == TYPE.VOID) && t.getName().equals("main")) {
			return node;
		}
		for(PNode child : node.children())
			return findMain(child);
		
		return null;
	}
	
	private void interpret(PNode node, Scope s) throws InvalidProgException, SystemOutRequestException {
		for(PNode child : node.children()) {
			if(isBinaryOp(child)) {
				performBinOp(child);
				System.out.println("Interpreting: " + child.value());
			}else if(isUnaryOp(child)) {
				System.out.println("Interpreting: " + child.value());
				performUnOp(child, s);
			}
			interpret(child, s);
		}
	}

	private void performUnOp(PNode child, Scope s) throws SystemOutRequestException {
		TOKEN_TYPE t = child.value().getType();
		if (t == TOKEN_TYPE.SYSTEM) {
			makeSystemOp(child, s);
		}
		else if (t == TOKEN_TYPE.DEC) {
			
		}
		else if (t == TOKEN_TYPE.INC) {
			
		}
		else if (t == TOKEN_TYPE.RETURN) {
			
		}
		else if (t == TOKEN_TYPE.CALL) {
			
		}
		else if (t == TOKEN_TYPE.AT) {
			
		}
		else if (t == TOKEN_TYPE.ARRAY) {
			
		}
	}

	private void makeSystemOp(PNode node, Scope s) throws SystemOutRequestException {
		PNode child = node.children().get(0);
		Token t = child.value();
		if((t.getType() == TOKEN_TYPE.OUT) || (t.getType() == TOKEN_TYPE.LINEOUT)) {
			PNode out = child.children().get(0);
			Token tOut = out.value();
			for(Token temp : s.getTokens()) {
				System.out.println(temp);
				if(temp.getName().equals(tOut.getName()))
					tOut = temp;
			}
			System.out.println(tOut);
			throw new SystemOutRequestException(tOut.getValue());
		}
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

	private void performBinOp(PNode node) {
		Token t = node.value();
		if (t.getType() == TOKEN_TYPE.PLUS) {
		} else if (t.getType() == TOKEN_TYPE.MINUS) {
		} else if (t.getType() == TOKEN_TYPE.FWDSLASH) {
		} else if (t.getType() == TOKEN_TYPE.STAR) {
		} else if (t.getType() == TOKEN_TYPE.MOD) {
		} else if (t.getType() == TOKEN_TYPE.AND) {
		} else if (t.getType() == TOKEN_TYPE.OR) {
		} else if (t.getType() == TOKEN_TYPE.NOT) {
		} else if (t.getType() == TOKEN_TYPE.XOR) {
		} else if (t.getType() == TOKEN_TYPE.OR) {
		} else if (t.getType() == TOKEN_TYPE.GT) {
		} else if (t.getType() == TOKEN_TYPE.GTEQ) {
		} else if (t.getType() == TOKEN_TYPE.LT) {
		} else if (t.getType() == TOKEN_TYPE.LTEQ) {
		} else if (t.getType() == TOKEN_TYPE.EQ) {
		} else if (t.getType() == TOKEN_TYPE.NEQ) {
		} else if (t.getType() == TOKEN_TYPE.ASSIGN) {
			makeAssignment(node);
		}
	}
	private void makeAssignment(PNode node) {
		TYPE exprType = node.value().getDatatype();
		if(exprType == TYPE.STRING) {
			PNode lhs = node.children().get(0);
			PNode rhs = node.children().get(1);
			Token a = lhs.value();
			System.out.println("A = " + a);
			Token b = rhs.value();
			System.out.println("B = " + b);
			a.setValue(b.getValue());
			lhs.setValue(a);
			System.out.println(node.children().get(0).value());
			System.out.println(lhs.value());
			System.out.println(lhs.value() + "'s new value : " + lhs.value().getValue());
		}else {
			System.out.println("Expression type: " + exprType);
		}
	}

	public HashMap<Integer, Scope> getTable() {
		return table;
	}

	public void setTable(HashMap<Integer, Scope> table) {
		this.table = table;
	}
}
