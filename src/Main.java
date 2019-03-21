
	
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import flat_compiler.lex_analyzer.dfa.StateGraph;
import flat_compiler.lex_analyzer.fileIO.FileInputHandler;
import flat_compiler.lex_analyzer.fileIO.FileOutputHandler;
import flat_compiler.model.TOKEN_TYPE;
import flat_compiler.ui.UIHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;


public class Main extends Application {
	private Stage window;
	
	public static void main(String[] args) {
		//Superclass method for launching the graphical user interface.
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.window = primaryStage;
		loadFXML();
	}
	
	private void loadFXML() {
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("flat_compiler/ui/Home.fxml"));
			window.setScene(new Scene(parent));
			window.setTitle("flatIDE");
			window.centerOnScreen();
			window.setOnCloseRequest(e -> {
				e.consume(); //Consume the event so we can handle it manually.
				closeWindow();
			});
			window.show();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void closeWindow() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Close flatIDE");
		alert.setHeaderText("You are about to exit flatIDE.");
		alert.setContentText("Are you sure you want to exit?");
		UIHelper.findCenter(alert, window);
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK)
			window.close();
	}
	
	//This method creates Deterministic Finite State Automata and saved them to a binary file for persistent storage.
	static void createDFAs()
	{
		ArrayList<StateGraph> list = new ArrayList<>();
		String filePath = "data/DFA/DFA.dfa";
		
		//-------create DFAs -------
		StateGraph DFA = new StateGraph("program", TOKEN_TYPE.PROGDECL);
		list.add(DFA);
		
		DFA = new StateGraph("start", TOKEN_TYPE.START);
		list.add(DFA);
		
		DFA = new StateGraph("endprogram", TOKEN_TYPE.ENDPROG);
		list.add(DFA);
		
		DFA = new StateGraph("method", TOKEN_TYPE.FUNC);
		list.add(DFA);
		
		DFA = new StateGraph("endmethod", TOKEN_TYPE.ENDFUNC);
		list.add(DFA);
		
		DFA = new StateGraph("return", TOKEN_TYPE.RETURN);
		list.add(DFA);
		
		DFA = new StateGraph("void", TOKEN_TYPE.VOID);
		list.add(DFA);
		
		DFA = new StateGraph("call", TOKEN_TYPE.CALL);
		list.add(DFA);
		
		DFA = new StateGraph("for", TOKEN_TYPE.FOR);
		list.add(DFA);
		
		DFA = new StateGraph("from", TOKEN_TYPE.FROM);
		list.add(DFA);
		
		DFA = new StateGraph("to", TOKEN_TYPE.TO);
		list.add(DFA);
		
		DFA = new StateGraph("step", TOKEN_TYPE.STEP);
		list.add(DFA);
		
		DFA = new StateGraph("endfor", TOKEN_TYPE.ENDFOR);
		list.add(DFA);
		
		DFA = new StateGraph("while", TOKEN_TYPE.WHILE);
		list.add(DFA);
		
		DFA = new StateGraph("endwhile", TOKEN_TYPE.ENDWHILE);
		list.add(DFA);
		
		DFA = new StateGraph("if", TOKEN_TYPE.IF);
		list.add(DFA);
		
		DFA = new StateGraph("endif", TOKEN_TYPE.ENDIF);
		list.add(DFA);
		
		DFA = new StateGraph("else", TOKEN_TYPE.ELSE);
		list.add(DFA);
		
		DFA = new StateGraph("endelse", TOKEN_TYPE.ENDELSE);
		list.add(DFA);
		
		DFA = new StateGraph("boolean", TOKEN_TYPE.BOOLEAN);
		list.add(DFA);
		
		DFA = new StateGraph("integer", TOKEN_TYPE.INTEGER);
		list.add(DFA);
		
		DFA = new StateGraph("character", TOKEN_TYPE.CHARACTER);
		list.add(DFA);
		
		DFA = new StateGraph("float", TOKEN_TYPE.FLOAT);
		list.add(DFA);
		
		DFA = new StateGraph("string", TOKEN_TYPE.STRING);
		list.add(DFA);
		
		DFA = new StateGraph("system", TOKEN_TYPE.SYSTEM);
		list.add(DFA);
		
		DFA = new StateGraph("out", TOKEN_TYPE.OUT);
		list.add(DFA);
		
		DFA = new StateGraph("in", TOKEN_TYPE.IN);
		list.add(DFA);
		
		DFA = new StateGraph("lineout", TOKEN_TYPE.LINEOUT);
		list.add(DFA);
		
		DFA = new StateGraph("array", TOKEN_TYPE.ARRAY);
		list.add(DFA);
		
		DFA = new StateGraph("length", TOKEN_TYPE.LENGTH);
		list.add(DFA);
		
		DFA = new StateGraph("+", TOKEN_TYPE.PLUS);
		list.add(DFA);
		
		DFA = new StateGraph("-", TOKEN_TYPE.MINUS);
		list.add(DFA);
		
		DFA = new StateGraph("*", TOKEN_TYPE.STAR);
		list.add(DFA);
		
		DFA = new StateGraph("/", TOKEN_TYPE.FWDSLASH);
		list.add(DFA);
		
		DFA = new StateGraph("%", TOKEN_TYPE.MOD);
		list.add(DFA);
		
		DFA = new StateGraph("||", TOKEN_TYPE.OR);
		list.add(DFA);
		
		DFA = new StateGraph("&&", TOKEN_TYPE.AND);
		list.add(DFA);
		
		DFA = new StateGraph("!", TOKEN_TYPE.NOT);
		list.add(DFA);
		
		DFA = new StateGraph("^", TOKEN_TYPE.XOR);
		list.add(DFA);
		
		DFA = new StateGraph("=", TOKEN_TYPE.ASSIGN);
		list.add(DFA);
		
		DFA = new StateGraph("==", TOKEN_TYPE.EQ);
		list.add(DFA);
		
		DFA = new StateGraph("!=", TOKEN_TYPE.NEQ);
		list.add(DFA);
		
		DFA = new StateGraph(">", TOKEN_TYPE.GT);
		list.add(DFA);
		
		DFA = new StateGraph("<", TOKEN_TYPE.LT);
		list.add(DFA);
		
		DFA = new StateGraph(">=", TOKEN_TYPE.GTEQ);
		list.add(DFA);
		
		DFA = new StateGraph("<=", TOKEN_TYPE.LTEQ);
		list.add(DFA);
		
		DFA = new StateGraph("(", TOKEN_TYPE.LPARENT);
		list.add(DFA);
		
		DFA = new StateGraph(")", TOKEN_TYPE.RPARENT);
		list.add(DFA);
		
		DFA = new StateGraph("[", TOKEN_TYPE.LSQRB);
		list.add(DFA);
		
		DFA = new StateGraph("]", TOKEN_TYPE.RSQRB);
		list.add(DFA);
		
		DFA = new StateGraph("::", TOKEN_TYPE.TYPESPEC);
		list.add(DFA);
		
		DFA = new StateGraph(".", TOKEN_TYPE.DOT);
		list.add(DFA);
		
		DFA = new StateGraph(",", TOKEN_TYPE.COMMA);
		list.add(DFA);
		
		DFA = new StateGraph(";", TOKEN_TYPE.SEMICOL);
		list.add(DFA);
		
		DFA = new StateGraph("boolean literal", TOKEN_TYPE.BOOLLIT);
		list.add(DFA);
		
		DFA = new StateGraph("integer literal", TOKEN_TYPE.INTLIT);
		list.add(DFA);
		
		DFA = new StateGraph("string literal", TOKEN_TYPE.STRINGLIT);
		list.add(DFA);
		
		DFA = new StateGraph("character literal", TOKEN_TYPE.CHARLIT);
		list.add(DFA);
		
		DFA = new StateGraph("floating point literal", TOKEN_TYPE.FLOATLIT);
		list.add(DFA);
		
		DFA = new StateGraph("identifier", TOKEN_TYPE.ID);
		list.add(DFA);
		
		DFA = new StateGraph("at", TOKEN_TYPE.AT);
		list.add(DFA);
		
		DFA = new StateGraph("start", TOKEN_TYPE.START);
		list.add(DFA);
		
		DFA = new StateGraph("inc", TOKEN_TYPE.INC);
		list.add(DFA);
		
		DFA = new StateGraph("dec", TOKEN_TYPE.DEC);
		list.add(DFA);
		
		System.out.println("\n Saving DFAs....");
		FileOutputHandler.writeDFAsToFile(list, filePath);
		System.out.println("Saved.");
		
		System.out.println("\nReading DFAs from file....");
		list = FileInputHandler.readDFAsFromFile(filePath);
		System.out.println("\n");
		
		for(StateGraph dfa : list)
			System.out.println(dfa.toString() + "\n");
	}
}
