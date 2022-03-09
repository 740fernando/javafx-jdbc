package gui.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Alerts {

	/**
	 * Exibe um alerta
	 * 
	 * @param title
	 * @param header
	 * @param content
	 * @param type
	 */
	public static void showAlert(String title, String header, String content, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.show();
	}
	/**
	 * Exibe uma confirmação antes de realizar o delete
	 * 
	 * @param title
	 * @param content
	 * @return
	 */
	public static Optional<ButtonType> showConfirmation(String title, String content){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		return alert.showAndWait();
	}
}
