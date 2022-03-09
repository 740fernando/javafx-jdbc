package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService service;

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;
	
	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Department> obsList; // Os departamentos são carregados aqui

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
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

		// macete para o tableViewDepartment ser exibido até o final da janela - inicio
		Stage stage = (Stage) Main.getMainScene().getWindow();// referencia do stage atual, getWindow() é super classe
																// do stage, é nessário fazer o downcast para atribuir
																// para o stage
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		// macete para o tableViewDepartment ser exibido até o final da janela - fim
	}

	/**
	 * Esse método vai ser responsável por acessar os serviços, carregar os
	 * departamentos e jogar para obsList, associado obsList ao meu
	 * tableView, que exibirá as informações
	 * 
	 * no updateTableView() passamos a lista para o javafx observar, e quando
	 * chamamos o método initEditButtons essa lista já está em memória e é ai que
	 * acontece o laço, de forma totalmente implícita.
	 * 
	 * Aplicações desktop tem essa característica de fazer muito uso de observables
	 * e listeners, muito bom por um lado, mas deixa muito implícito o que está
	 * acontecendo à primeira vista.
	 */
	public void updateTableView() {
		// teste - Verifica se o service foi instanciado
		if (service == null) {
			throw new IllegalStateException("Service está nulo");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);// Instancia o obsList recebendo os dados do list
		tableViewDepartment.setItems(obsList); // carrega as informaçoes contidas no obsList para o tableView e exibe na tela
		initEditButtons();
		initRemoveButtons();
	}
												

	/**
	 * Função para carregar a janela do formúlario para preenchimento de um novo
	 * departamento
	 * 
	 * @param Department
	 * @param String
	 * @param Stage
	 */
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this); // inscrevendo para receber o evento.
			controller.updateFormData();

			// QUANDO EU VOU CARREGAR UMA JANELA MODAL NA FRENTE DA JANELA EXISTENTE, É
			// NECESSÁRIO CRIAR UM NOVO STAGE
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);// janela ajustavel
			dialogStage.initOwner(parentStage);// stage pai da janela
			dialogStage.initModality(Modality.WINDOW_MODAL); // Esse método é responsável por definir o comportamento,
																// no caso, foi definido que a janela irá ficar travada,
																// enquanto não fechar, não poderá acessar as outras
																// janelas
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}

	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	/**
	 * Método responsável por criar um botão de edicao para cada linha da tabela
	 * para cada departamento, eu possa apertar esse botao e editar.
	 */
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);

				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(para -> new TableCell<Department,Department>(){
			private final Button button = new Button("remove");
			
			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj==null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Department obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		if(result.get()==ButtonType.OK){
			if(service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			}catch(DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
