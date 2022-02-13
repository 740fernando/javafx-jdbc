package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DepartmentListController implements Initializable {

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private Button btNew;

	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes(); // inicia alguma componente na tela

	}

	private void initializeNodes() {
		
		
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		// macete para o tableViewDepartment ser exibido até  o final da janela - inio
		Stage stage = (Stage) Main.getMainScene().getWindow();//referencia do stage atual, getWindow() é super classe do stage, é nessário fazer o downcast para atribuir para o stage
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		// macete para o tableViewDepartment ser exibido até  o final da janela - inio
	}
}
