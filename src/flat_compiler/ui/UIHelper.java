package flat_compiler.ui;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class UIHelper {
	//Helper function for first confirming with the user before closing the window.
		public static void makeExitAlert(Stage stage) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Close flatIDE");
			alert.setHeaderText("You are about to exit flatIDE.");
			alert.setContentText("Are you sure you want to exit?");
			findCenter(alert, stage);
			Optional<ButtonType> result = alert.showAndWait();
			if(result.get() == ButtonType.OK)
				stage.close();
		}
		
		public static void makeCustomAlert(Stage stage, String text) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("GroceryPal");
			alert.setHeaderText(text);
			findCenter(alert, stage);
			alert.showAndWait();
		}
		
		public static void findCenter(Alert alert, Stage window) {
			double x1 = window.getX();
			double y1 = window.getY();
			double x2 = x1 + window.getWidth();
			double y2 = y1 + window.getHeight();
			double xa = 0.5 * (x1 + x2);
			double ya = 0.5 * (y1 + y2);
			double xf = xa - 185;
			double yf = ya - 70;
			alert.setX(xf);
			alert.setY(yf);
		}
}
