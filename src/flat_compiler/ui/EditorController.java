package flat_compiler.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Queue;
import java.util.ResourceBundle;

import flat_compiler.csa.ASTBuilder;
import flat_compiler.csa.CSAHelper;
import flat_compiler.exception.CorruptedSourceException;
import flat_compiler.exception.EOPException;
import flat_compiler.exception.GrammarException;
import flat_compiler.exception.InvalidOpException;
import flat_compiler.exception.InvalidParamException;
import flat_compiler.exception.InvalidProgException;
import flat_compiler.exception.InvalidTokenException;
import flat_compiler.exception.InvalidTypeException;
import flat_compiler.exception.SystemOutRequestException;
import flat_compiler.interpreter.Interpreter;
import flat_compiler.lex_analyzer.fileIO.FileInputHandler;
import flat_compiler.lex_analyzer.fileIO.FileOutputHandler;
import flat_compiler.lex_analyzer.scanner.CodeScanner;
import flat_compiler.model.Token;
import flat_compiler.parser.ParseTree;
import flat_compiler.parser.Parser;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EditorController extends Controller {
	@FXML
	private BorderPane rootPane;
	@FXML
	private TextArea txtSource, txtOut;
	@FXML
	private CheckBox chOption;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

	@FXML
	private void closeMenuItemClick() {
		closeWindow(rootPane, false);
	}

	@FXML
	private void importMenuItemClick() {
		Stage s = (Stage) rootPane.getScene().getWindow();
		FileChooser f = new FileChooser();
		f.setTitle("Import Source File");
		f.setInitialDirectory(new File("data/Source"));
		File file = f.showOpenDialog(s);
		String filePath = file.getPath();
		String source = FileInputHandler.loadSource(filePath);
		if (source != null) {
			txtSource.setText(source);
			return;
		}
		txtOut.setText("Error: Could not load source file.");
	}

	@FXML
	private void btnCompileClick() {
		if (txtSource.getText().isEmpty()) {
			output("Error: No source code to compile.");
			return;
		}
		String source = txtSource.getText();

		long startTime = System.currentTimeMillis();
		CodeScanner scanner = new CodeScanner(source);
		scanner.scanSource(); // start scanning source code.
		Queue<Token> tokens = scanner.getSourceTokens();
		try {
			Parser p = new Parser(tokens);
			p.beginParse();
			if (chOption.isSelected()) {
				genOutputTree(p.getDebugCode(), "ParseTree");
			}
			ParseTree tree = p.getParseTree();
			ASTBuilder builder = new ASTBuilder(tree);
			builder.optTree();
			genOutputTree(p.getDebugCode(), "ASTree");

			CSAHelper csa = new CSAHelper(builder.getASTree());
			csa.findDeclarations();
			
			builder.optTreeForUse();
			genOutputTree(builder.getDebugCode(), "ASTree_Use");
			csa.setASTree(builder.getASTree());
			csa.checkUse();
			
			csa.invokeInterpreter();
			
		} catch (InvalidProgException | CorruptedSourceException e) {
			output("****Compile Error****:  " + e.getMessage());
		} catch (GrammarException e) {
			output("****Compile Error****:  " + e.getMessage());
		} catch (EOPException e) {
			output("****Compile Error****:  " + e.getMessage());
		} catch (InvalidTypeException e) {
			output("****Compile Error****:  " + e.getMessage());
		} catch (InvalidParamException e) {
			output("****Compile Error****:  " + e.getMessage());
		} catch (InvalidOpException e) {
			output("****Compile Error****:  " + e.getMessage());
		} catch (InvalidTokenException e) {
			output("****Compile Error****:  " + e.getMessage());
		} catch (SystemOutRequestException e) {
			output("-------------------- Output --------------------\n" + e.getMessage());
		}
		long endTime = System.currentTimeMillis();
		output("Total estimated compile time: " + (float) ((endTime - startTime)) / 1000 + "s.");
	}

	private void genOutputTree(String debugCode, String fileName) {
		FileOutputHandler.writeDebugCodeToFile(debugCode, "data/Debug/" + fileName + ".dot");
		ProcessBuilder cmd = new ProcessBuilder("cmd.exe", "/c",
				" cd \"C:\\eclipse_oxygen\\workspace\\SP_SITHUNGU_201205819_IT08X87_2018_PROJECT\\data\\Debug\" && dot -Tpdf "
						+ fileName + ".dot -o A_" + fileName + ".pdf");
		cmd.redirectErrorStream(true);
		try {
			cmd.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void output(String out) {
		String text = txtOut.getText() + "\n";
		text += out;
		txtOut.setText(text);
	}
}
