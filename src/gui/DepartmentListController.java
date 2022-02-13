package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {

	private DepartmentService service;
	
	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private Button btNew;

	private ObservableList<Department> obsList; // Os departamentos s�o carregados aqui
	
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes(); // inicia alguma componente na tela

	}

	private void initializeNodes() {
		
		
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		// macete para o tableViewDepartment ser exibido at�  o final da janela - inicio
		Stage stage = (Stage) Main.getMainScene().getWindow();//referencia do stage atual, getWindow() � super classe do stage, � ness�rio fazer o downcast para atribuir para o stage
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		// macete para o tableViewDepartment ser exibido at�  o final da janela - fim
	}
	//Esse m�todo vai ser respons�vel por acessar os servi�os, carregar os departamentos  e jogar para obsList, a� eu vou associar meu obsList ao meu tableView, que exibir� as informa��es
	public void updateTableView() {
		// teste - Verifica se o service foi instanciado
		if(service==null) {
			throw new IllegalStateException("Service est� nulo");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);// Instancia o obsList recebendo os dados do list
		tableViewDepartment.setItems(obsList); // carrega as informa�oes contidas no obsList para o tableView e exibe na tela
	}
}
