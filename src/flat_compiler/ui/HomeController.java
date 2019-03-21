package flat_compiler.ui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;

public class HomeController extends Controller {
	@FXML private BorderPane rootPane;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//Load the welcome screen image from the file system.
		File imageFile = new File("img/flatIDE.png"); //Extract image file.
		Image image = new Image(imageFile.toURI().toString()); //Get the image using its URI.
		BackgroundSize size = new BackgroundSize(100, 100, true, true, true, false); //Create background size;
		//Create a background image.
		BackgroundImage bImg = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, size);
		rootPane.setBackground(new Background(bImg)); //Set the background for the VBox as the background image.
	}
	
	@FXML
	private void startMenuItemClick(){
		showSecondary("Editor.fxml");
	}
	
	@FXML
	private void closeMenuItemClick(){
		closeWindow(rootPane, true);
	}
	
	@FXML
	private void aboutMenuItemClick(){
		showSecondary("About.fxml");
	}
}
