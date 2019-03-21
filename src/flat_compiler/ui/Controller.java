package flat_compiler.ui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class Controller implements Initializable {
	//Method for handling the switching between scenes.
	protected void switchScene(String fxmlURL, Node node) {
		try {
			Parent parent = FXMLLoader.load(getClass().getResource(fxmlURL));
			Stage stage = (Stage) node.getScene().getWindow();
			stage.setScene(new Scene(parent));
			stage.show();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	protected Controller showSecondary(String fxmlURL) {
		FXMLLoader loader = new FXMLLoader();
		try {
			loader.setLocation(getClass().getResource(fxmlURL));
			Parent parent = loader.load();
			Stage secStage = new Stage();
			secStage.setScene(new Scene(parent));
			secStage.initModality(Modality.APPLICATION_MODAL);
			secStage.showAndWait();
			return loader.getController();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}
	
	//method for handling closing how the program will close.
	protected void closeWindow(Node node, boolean confirmClose) {
		Stage stage = (Stage)node.getScene().getWindow();
		if(!confirmClose) {
			stage.close();
			return;
		}
		UIHelper.makeExitAlert(stage);
	}
}
