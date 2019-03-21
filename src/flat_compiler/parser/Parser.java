package flat_compiler.parser;

import java.util.LinkedList;
import java.util.Queue;

import flat_compiler.exception.CorruptedSourceException;
import flat_compiler.exception.EOPException;
import flat_compiler.exception.GrammarException;
import flat_compiler.exception.InvalidProgException;
import flat_compiler.model.TOKEN_TYPE;
import flat_compiler.model.Token;

public class Parser {
	private ParseTree tree;
	Queue<Token> source;
	LinkedList<Token> currentStream;
	
	public Parser(Queue<Token> source) throws CorruptedSourceException, InvalidProgException {
		if ((source == null) || (source.size() == 0))
			throw new CorruptedSourceException();

		this.source = source;
		Token progdecl = this.source.remove();
		tree = new ParseTree(progdecl);
		if (!tree.isValidRoot(progdecl))
			throw new InvalidProgException("Program is missing a program declaration.");
		
		currentStream = new LinkedList<>();
	}
	

	private boolean isDatatype(Token t) {
		if (t.getType() == TOKEN_TYPE.INTEGER)
			return true;
		else if (t.getType() == TOKEN_TYPE.FLOAT)
			return true;
		else if (t.getType() == TOKEN_TYPE.BOOLEAN)
			return true;
		else if (t.getType() == TOKEN_TYPE.CHARACTER)
			return true;
		else if (t.getType() == TOKEN_TYPE.STRING)
			return true;

		return false;
	}

	private void deriveFuncList(PNode parent) throws GrammarException {
		PNode funcListNode = tree.addNonTerminal(parent, new Token("funcList", null));
		deriveFunc(funcListNode);
		if (!isFuncListEmpty()) {
			deriveFuncList(funcListNode);
		}
	}

	private boolean isFuncListEmpty() {
		if (source.peek().getType() == TOKEN_TYPE.FUNC)
			return false;

		return true;
	}

	private void deriveFunc(PNode parent) throws GrammarException {
		PNode funcNode = tree.addNonTerminal(parent, new Token("func", null));

		tryEat(funcNode, TOKEN_TYPE.FUNC);
		tryEat(funcNode, TOKEN_TYPE.TYPESPEC);

		if (source.peek().getType() != TOKEN_TYPE.VOID)
			tryEatDatatype(funcNode);
		else
			tryEat(funcNode, TOKEN_TYPE.VOID);

		tryEat(funcNode, TOKEN_TYPE.ID);
		tryEat(funcNode, TOKEN_TYPE.LPARENT);
		deriveParamList(funcNode);

		tryEat(funcNode, TOKEN_TYPE.RPARENT);
		tryEat(funcNode, TOKEN_TYPE.START);

		deriveBranchList(funcNode);

		tryEat(funcNode, TOKEN_TYPE.ENDFUNC);
	}

	private void deriveBranchList(PNode parent) throws GrammarException {
		PNode bListNode = tree.addNonTerminal(parent, new Token("branchList", null));
		deriveBranch(bListNode);
		if (!isBranchListEmpty()) {
			deriveBranchList(bListNode);
		}
	}

	private void deriveBranch(PNode parent) throws GrammarException {
		Token t = source.peek();
		PNode branchNode = tree.addNonTerminal(parent, new Token("branch", null));
		if (t.getType() == TOKEN_TYPE.FOR) {
			tryEat(branchNode, TOKEN_TYPE.FOR);
			tryEat(branchNode, TOKEN_TYPE.LPARENT);
			tryEat(branchNode, TOKEN_TYPE.ID);
			tryEat(branchNode, TOKEN_TYPE.FROM);

			deriveExpr(branchNode);

			tryEat(branchNode, TOKEN_TYPE.TO);

			deriveExpr(branchNode);

			tryEat(branchNode, TOKEN_TYPE.STEP);
			tryEat(branchNode, TOKEN_TYPE.INTLIT);
			tryEat(branchNode, TOKEN_TYPE.RPARENT);
			tryEat(branchNode, TOKEN_TYPE.START);

			deriveBranchList(branchNode);

			tryEat(branchNode, TOKEN_TYPE.ENDFOR);

		} else if (t.getType() == TOKEN_TYPE.WHILE) {
			tryEat(branchNode, TOKEN_TYPE.WHILE);
			tryEat(branchNode, TOKEN_TYPE.LPARENT);

			deriveExpr(branchNode);

			tryEat(branchNode, TOKEN_TYPE.RPARENT);
			tryEat(branchNode, TOKEN_TYPE.START);

			deriveBranchList(branchNode);

			tryEat(branchNode, TOKEN_TYPE.ENDWHILE);
		} else if (t.getType() == TOKEN_TYPE.IF) {
			tryEat(branchNode, TOKEN_TYPE.IF);
			tryEat(branchNode, TOKEN_TYPE.LPARENT);

			deriveExpr(branchNode);

			tryEat(branchNode, TOKEN_TYPE.RPARENT);
			tryEat(branchNode, TOKEN_TYPE.START);

			deriveBranchList(branchNode);

			tryEat(branchNode, TOKEN_TYPE.ENDIF);

			if (!isElsePartEmpty())
				deriveElsePart(branchNode);
		} else if (!isSentEmpty()) {
			deriveSent(branchNode);
		}
	}

	private void deriveElsePart(PNode parent) throws GrammarException {
		PNode ePartNode = tree.addNonTerminal(parent, new Token("elsePart", null));
		tryEat(ePartNode, TOKEN_TYPE.ELSE);
		tryEat(ePartNode, TOKEN_TYPE.START);
		deriveBranchList(ePartNode);
		tryEat(ePartNode, TOKEN_TYPE.ENDELSE);
	}

	private boolean isElsePartEmpty() {
		return source.peek().getType() != TOKEN_TYPE.ELSE;
	}

	private void deriveExpr(PNode parent) throws GrammarException {
		PNode exprNode = tree.addNonTerminal(parent, new Token("Expr", null));
		if (!isFuncInvokeEmpty())
			deriveFuncInvoke(exprNode);
		else {
			deriveTerm(exprNode);

			if (!isExprSetEmpty())
				deriveExprSet(exprNode);
		}
	}

	private boolean isExprSetEmpty() {
		Token t = source.peek();
		if (t.getType() == TOKEN_TYPE.PLUS)
			return false;

		else if (t.getType() == TOKEN_TYPE.MINUS)
			return false;

		else if (t.getType() == TOKEN_TYPE.OR)
			return false;

		else if (t.getType() == TOKEN_TYPE.XOR)
			return false;

		return true;
	}

	private void deriveExprSet(PNode parent) throws GrammarException {
		PNode eSetNode = tree.addNonTerminal(parent, new Token("exprSet", null));
		Token t = source.peek();
		if (t.getType() == TOKEN_TYPE.PLUS) {
			tryEat(eSetNode, t.getType());
			deriveTerm(eSetNode);
			deriveExprSet(eSetNode);
		} else if (t.getType() == TOKEN_TYPE.MINUS) {
			tryEat(eSetNode, t.getType());
			deriveTerm(eSetNode);
			deriveExprSet(eSetNode);
		} else if (t.getType() == TOKEN_TYPE.OR) {
			tryEat(eSetNode, t.getType());
			deriveTerm(eSetNode);
			deriveExprSet(eSetNode);
		}

		else if (t.getType() == TOKEN_TYPE.XOR) {
			tryEat(eSetNode, t.getType());
			deriveTerm(eSetNode);
			deriveExprSet(eSetNode);
		}
	}

	private void deriveSysOutCmd(PNode parent) throws GrammarException {
		PNode sysCmdNode = tree.addNonTerminal(parent, new Token("sysCmd", null));
		Token t = source.peek();
		if (t.getType() == TOKEN_TYPE.IN) {
			tryEat(sysCmdNode, t.getType());
			tryEat(sysCmdNode, TOKEN_TYPE.TYPESPEC);
			tryEatDatatype(sysCmdNode);
			tryEat(sysCmdNode, TOKEN_TYPE.LPARENT);
			tryEat(sysCmdNode, TOKEN_TYPE.RPARENT);
		} else {
			deriveFormOut(sysCmdNode);
			tryEat(sysCmdNode, TOKEN_TYPE.LPARENT);
			deriveExpr(sysCmdNode);
			tryEat(sysCmdNode, TOKEN_TYPE.RPARENT);
		}
	}

	private void deriveFormOut(PNode parent) throws GrammarException {
		PNode fOutNode = tree.addNonTerminal(parent, new Token("formOut", null));
		Token t = source.peek();
		if (t.getType() == TOKEN_TYPE.LINEOUT)
			tryEat(fOutNode, t.getType());

		else if (t.getType() == TOKEN_TYPE.OUT)
			tryEat(fOutNode, t.getType());
	}

	private void deriveTermSet(PNode parent) throws GrammarException {
		PNode tSetNode = tree.addNonTerminal(parent, new Token("termSet", null));
		Token t = source.peek();
		if (t.getType() == TOKEN_TYPE.STAR) {
			tryEat(tSetNode, t.getType());
			deriveFactor(tSetNode);
			deriveTermSet(tSetNode);
		}

		else if (t.getType() == TOKEN_TYPE.FWDSLASH) {
			tryEat(tSetNode, t.getType());
			deriveFactor(tSetNode);
			deriveTermSet(tSetNode);
		}

		else if (t.getType() == TOKEN_TYPE.AND) {
			tryEat(tSetNode, t.getType());
			deriveFactor(tSetNode);
			deriveTermSet(tSetNode);
		}

		else if (t.getType() == TOKEN_TYPE.NOT) {
			tryEat(tSetNode, t.getType());
			deriveFactor(tSetNode);
			deriveTermSet(tSetNode);
		}

		else if (t.getType() == TOKEN_TYPE.GT) {
			tryEat(tSetNode, t.getType());
			deriveFactor(tSetNode);
			deriveTermSet(tSetNode);
		}

		else if (t.getType() == TOKEN_TYPE.GTEQ) {
			tryEat(tSetNode, t.getType());
			deriveFactor(tSetNode);
			deriveTermSet(tSetNode);
		}

		else if (t.getType() == TOKEN_TYPE.LT) {
			tryEat(tSetNode, t.getType());
			deriveFactor(tSetNode);
			deriveTermSet(tSetNode);
		}

		else if (t.getType() == TOKEN_TYPE.LTEQ) {
			tryEat(tSetNode, t.getType());
			deriveFactor(tSetNode);
			deriveTermSet(tSetNode);
		}

		else if (t.getType() == TOKEN_TYPE.EQ) {
			tryEat(tSetNode, t.getType());
			deriveFactor(tSetNode);
			deriveTermSet(tSetNode);
		}

		else if (t.getType() == TOKEN_TYPE.NEQ) {
			tryEat(tSetNode, t.getType());
			deriveFactor(tSetNode);
			deriveTermSet(tSetNode);
		}

		else if (t.getType() == TOKEN_TYPE.MOD) {
			tryEat(tSetNode, t.getType());
			deriveFactor(tSetNode);
			deriveTermSet(tSetNode);
		}
	}

	private void deriveTerm(PNode parent) throws GrammarException {
		PNode termNode = tree.addNonTerminal(parent, new Token("term", null));
		deriveFactor(termNode);

		if (!isTermSetEmpty())
			deriveTermSet(termNode);
	}

	private boolean isTermSetEmpty() {
		Token t = source.peek();
		if (t.getType() == TOKEN_TYPE.STAR)
			return false;

		else if (t.getType() == TOKEN_TYPE.FWDSLASH)
			return false;

		else if (t.getType() == TOKEN_TYPE.AND)
			return false;

		else if (t.getType() == TOKEN_TYPE.NOT)
			return false;

		else if (t.getType() == TOKEN_TYPE.GT)
			return false;

		else if (t.getType() == TOKEN_TYPE.GTEQ)
			return false;

		else if (t.getType() == TOKEN_TYPE.LT)
			return false;

		else if (t.getType() == TOKEN_TYPE.LTEQ)
			return false;

		else if (t.getType() == TOKEN_TYPE.EQ)
			return false;

		else if (t.getType() == TOKEN_TYPE.NEQ)
			return false;

		else if (t.getType() == TOKEN_TYPE.MOD)
			return false;

		return true;
	}

	private void deriveFactor(PNode parent) throws GrammarException {
		PNode factNode = tree.addNonTerminal(parent, new Token("factor", null));
		Token t = source.peek();
		if (t.getType() == TOKEN_TYPE.ID) {
			tryEat(factNode, t.getType());
			deriveFactID(factNode);
		} else if (t.getType() == TOKEN_TYPE.LPARENT) {
			tryEat(factNode, t.getType());
			deriveExpr(factNode);
			tryEat(factNode, TOKEN_TYPE.RPARENT);
		} else if (t.getType() == TOKEN_TYPE.STRING) {
			tryEat(factNode, t.getType());
			tryEat(factNode, TOKEN_TYPE.DOT);
			tryEat(factNode, TOKEN_TYPE.AT);
			tryEat(factNode, TOKEN_TYPE.LPARENT);
			tryEat(factNode, TOKEN_TYPE.ID);
			tryEat(factNode, TOKEN_TYPE.COMMA);
			deriveExpr(factNode);
			tryEat(factNode, TOKEN_TYPE.RPARENT);
		} else if (t.getType() == TOKEN_TYPE.ARRAY) {
			tryEat(factNode, TOKEN_TYPE.ARRAY);
			tryEat(factNode, TOKEN_TYPE.DOT);
			tryEat(factNode, TOKEN_TYPE.LENGTH);
			tryEat(factNode, TOKEN_TYPE.LPARENT);
			tryEat(factNode, TOKEN_TYPE.ID);
			tryEat(factNode, TOKEN_TYPE.RPARENT);
		} else if (t.getType() == TOKEN_TYPE.SYSTEM) {
			tryEat(factNode, t.getType());
			tryEat(factNode, TOKEN_TYPE.DOT);
			tryEat(factNode, TOKEN_TYPE.IN);
			tryEat(factNode, TOKEN_TYPE.TYPESPEC);
			tryEatDatatype(factNode);
			tryEat(factNode, TOKEN_TYPE.LPARENT);
			tryEat(factNode, TOKEN_TYPE.RPARENT);
		} else if (!isFuncInvokeEmpty()) {
			deriveFuncInvoke(factNode);
		} else if (t.getType() == TOKEN_TYPE.INTLIT)
			tryEat(factNode, t.getType());

		else if (t.getType() == TOKEN_TYPE.STRINGLIT)
			tryEat(factNode, t.getType());

		else if (t.getType() == TOKEN_TYPE.FLOATLIT)
			tryEat(factNode, t.getType());

		else if (t.getType() == TOKEN_TYPE.BOOLLIT)
			tryEat(factNode, t.getType());

		else if (t.getType() == TOKEN_TYPE.CHARLIT)
			tryEat(factNode, t.getType());
		else
			throwGrammarException("FACTOR");
	}

	private void deriveFuncInvoke(PNode parent) throws GrammarException {
		PNode funcInvNode = tree.addNonTerminal(parent, new Token("factor", null));
		Token t = source.peek();
		tryEat(funcInvNode, t.getType());
		tryEat(funcInvNode, TOKEN_TYPE.LPARENT);
		tryEat(funcInvNode, TOKEN_TYPE.ID);

		while (!isCParamListEmpty())
			deriveCallParamList(funcInvNode);

		tryEat(funcInvNode, TOKEN_TYPE.RPARENT);
	}

	private boolean isFuncInvokeEmpty() {
		return !(source.peek().getType() == TOKEN_TYPE.CALL);
	}

	private void deriveFactID(PNode parent) throws GrammarException {
		PNode fIDNode = tree.addNonTerminal(parent, new Token("factID", null));
		Token t = source.peek();
		if (t.getType() == TOKEN_TYPE.LSQRB) {
			tryEat(fIDNode, t.getType());
			deriveExpr(fIDNode);
			tryEat(fIDNode, TOKEN_TYPE.RSQRB);
		}
	}

	private boolean isBranchListEmpty() {
		Token t = source.peek();
		if (t.getType() == TOKEN_TYPE.FOR)
			return false;

		else if (t.getType() == TOKEN_TYPE.WHILE)
			return false;

		else if (t.getType() == TOKEN_TYPE.IF)
			return false;

		return isSentEmpty();
	}

	private boolean isSentEmpty() {
		return isStmntEmpty();
	}

	private boolean isStmntEmpty() {
		Token t = source.peek();
		if (isDatatype(t))
			return false;

		else if (t.getType() == TOKEN_TYPE.ID)
			return false;

		else if (t.getType() == TOKEN_TYPE.RETURN)
			return false;

		else if (t.getType() == TOKEN_TYPE.DEC)
			return false;

		else if (t.getType() == TOKEN_TYPE.INC)
			return false;

		return isExprEmpty();
	}

	private boolean isExprEmpty() {
		Token t = source.peek();
		if (t.getType() == TOKEN_TYPE.STRING)
			return false;

		else if (t.getType() == TOKEN_TYPE.SYSTEM)
			return false;

		else if (t.getType() == TOKEN_TYPE.CALL)
			return false;

		return isTermEmpty();
	}

	private boolean isTermEmpty() {
		return isFactorEmpty();
	}

	private boolean isFactorEmpty() {
		Token t = source.peek();
		if (t.getType() == TOKEN_TYPE.ID)
			return false;

		else if (t.getType() == TOKEN_TYPE.LPARENT)
			return false;

		else if (t.getType() == TOKEN_TYPE.INTLIT)
			return false;

		else if (t.getType() == TOKEN_TYPE.FLOATLIT)
			return false;

		else if (t.getType() == TOKEN_TYPE.BOOLLIT)
			return false;

		else if (t.getType() == TOKEN_TYPE.STRINGLIT)
			return false;

		else if (t.getType() == TOKEN_TYPE.CHARLIT)
			return false;

		return true;
	}

	private void deriveSent(PNode parent) throws GrammarException {
		PNode sentNode = tree.addNonTerminal(parent, new Token("sent", null));
		deriveStmnt(sentNode);
		tryEat(sentNode, TOKEN_TYPE.SEMICOL);
	}

	private void deriveStmnt(PNode parent) throws GrammarException {
		PNode stmntNode = tree.addNonTerminal(parent, new Token("stmnt", null));
		Token t = source.peek();

		if (isDatatype(t)) {
			tryEatDatatype(stmntNode);
			tryEat(stmntNode, TOKEN_TYPE.ID);
			if(!isArgListEmpty())
				deriveArgList(stmntNode);
			if (!isArgListIDEmpty())
				deriveArgLstID(stmntNode);
		} else if (t.getType() == TOKEN_TYPE.RETURN) {
			tryEat(stmntNode, t.getType());
			deriveExpr(stmntNode);
		} else if (t.getType() == TOKEN_TYPE.SYSTEM) {
			tryEat(stmntNode, t.getType());
			tryEat(stmntNode, TOKEN_TYPE.DOT);
			deriveSysOutCmd(stmntNode);
		} else if (!isFuncInvokeEmpty()) {
			deriveFuncInvoke(stmntNode);
		} else if (t.getType() == TOKEN_TYPE.INC) {
			tryEat(stmntNode, t.getType());
			tryEat(stmntNode, TOKEN_TYPE.ID);
		} else if (t.getType() == TOKEN_TYPE.DEC) {
			tryEat(stmntNode, t.getType());
			tryEat(stmntNode, TOKEN_TYPE.ID);
		} else {
			deriveExpr(stmntNode);
			tryEat(stmntNode, TOKEN_TYPE.ASSIGN);
			deriveExpr(stmntNode);
		}
	}

	private boolean isArgListEmpty() {
		return !(source.peek().getType() == TOKEN_TYPE.LSQRB);
	}


	private boolean isArgListIDEmpty() {
		return !(source.peek().getType() == TOKEN_TYPE.ASSIGN);
	}

	private boolean isCParamListEmpty() {
		return !(source.peek().getType() == TOKEN_TYPE.COMMA);
	}

	private void deriveCallParamList(PNode parent) throws GrammarException {
		PNode cParListNode = tree.addNonTerminal(parent, new Token("callParamList", null));
		Token t = source.peek();
		tryEat(cParListNode, t.getType());
		deriveExpr(cParListNode);
	}

	private void deriveArgList(PNode parent) throws GrammarException {
		PNode aListNode = tree.addNonTerminal(parent, new Token("argarr", null));
		tryEat(aListNode, TOKEN_TYPE.LSQRB);
		deriveExpr(aListNode);
		tryEat(aListNode, TOKEN_TYPE.RSQRB);
	}

	private void deriveArgLstID(PNode parent) throws GrammarException {
		PNode aLstIDNode = tree.addNonTerminal(parent, new Token("arginit", null));
		Token t = source.peek();
		if (t.getType() == TOKEN_TYPE.ASSIGN) {
			tryEat(aLstIDNode, t.getType());
			deriveExpr(aLstIDNode);
		}
	}

	private void deriveParamList(PNode parent) throws GrammarException {
		if (!isParamListEmpty()) {
			PNode pListNode = tree.addNonTerminal(parent, new Token("paramList", null));
			deriveParam(pListNode);

			while (!isParamSetEmpty())
				deriveParamSet(pListNode);
		}
	}

	private boolean isParamSetEmpty() {
		if (source.peek().getType() != TOKEN_TYPE.COMMA)
			return true;

		return false;
	}

	private boolean isParamListEmpty() {
		if (!isDatatype(source.peek()))
			return true;

		return false;
	}

	private void deriveParam(PNode parent) throws GrammarException {
		PNode paramNode = tree.addNonTerminal(parent, new Token("param", null));
		tryEatDatatype(paramNode);
		tryEat(paramNode, TOKEN_TYPE.ID);
	}

	private void deriveParamSet(PNode parent) throws GrammarException {
		PNode pSetNode = tree.addNonTerminal(parent, new Token("paramSet", null));
		tryEat(pSetNode, TOKEN_TYPE.COMMA);
		deriveParam(pSetNode);
	}

	private void tryEatDatatype(PNode parent) throws GrammarException {
		if (!isDatatype(source.peek())) {
			throwGrammarException("DATATYPE");
		}

		TOKEN_TYPE datatype = source.peek().getType();
		if (datatype == TOKEN_TYPE.INTEGER)
			tryEat(parent, datatype);

		else if (datatype == TOKEN_TYPE.FLOAT)
			tryEat(parent, datatype);

		else if (datatype == TOKEN_TYPE.BOOLEAN)
			tryEat(parent, datatype);

		else if (datatype == TOKEN_TYPE.CHARACTER)
			tryEat(parent, datatype);

		else if (datatype == TOKEN_TYPE.STRING)
			tryEat(parent, datatype);
	}

	private void throwGrammarException(String missing) throws GrammarException {
		String s = "";
		for (Token t : currentStream)
			s += t.getName() + " ";

		throw new GrammarException(missing, s);
	}
	
	private boolean isTokenValid(TOKEN_TYPE type) {
		if ((type != source.peek().getType()))
			return false;

		return true;
	}

	public void beginParse() throws GrammarException, EOPException {
		tryEat(tree.root(), TOKEN_TYPE.ID);
		tryEat(tree.root(), TOKEN_TYPE.START);
		try {
			deriveFuncList(tree.root());
			tryEat(tree.root(), TOKEN_TYPE.ENDPROG);
		} catch (NullPointerException e) {
			throw new EOPException(currentStream.removeLast().getName());
		}
	}

	private void tryEat(PNode parent, TOKEN_TYPE type) throws GrammarException {
		if ((source.size() == 0) || !isTokenValid(type)) {
			throwGrammarException(type.toString());
		}
		Token t = source.remove();

		if (currentStream.size() == 10)
			currentStream.removeFirst();

		currentStream.addLast(t);
		PNode newNode = new PNode(parent, t);
		tree.addChild(newNode);
	}

	public String getDebugCode() {
		return tree.genDebugCode();
	}

	public ParseTree getParseTree() {
		return tree;
	}
}
