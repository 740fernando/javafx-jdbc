package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;

	@FXML
	private MenuItem menuItemDepartment;

	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView2("/gui/DepartmentList.fxml");
	}
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml");
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
	}
	/*
	 * synchronized - Garente que o processamento não será interrompido por algum comportamento multithread
	 * loadView - método responsável por manipular a scena principal
	 */
	private synchronized void loadView(String absoluteName) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			// Pega a referencia da scene do main
			Scene mainScene = Main.getMainScene();
			
			
			//getRoot() - obtem o primeiro elemento da view(ScrolLPane);
			//getContent() - Dentro do MainView.fxml, tem um elemento chamado content, é utilizado esse método para pode acessar
			VBox mainVBox =(VBox) ((ScrollPane) mainScene.getRoot()).getContent(); // Obtém a referencia do scroll pane e joga para o Vbox
			
			//Vou guardar uma referencia para o menu
			//getChildren() - obtém os Filhos do Vbox
			//get(0) - Primeiro filho do Vbox da janela principal(mainMenu)
			Node mainMenu = mainVBox.getChildren().get(0); 
			
			//limpa todos os filhos do VBox
			mainVBox.getChildren().clear();
			
			//Adiciona o menu
			mainVBox.getChildren().add(mainMenu);
			
			//Adiciona a coleção - Os filhos do new Vbox
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception","Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
private synchronized void loadView2(String absoluteName) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			// Pega a referencia da scene do main
			Scene mainScene = Main.getMainScene();
			
			
			//getRoot() - obtem o primeiro elemento da view(ScrolLPane);
			//getContent() - Dentro do MainView.fxml, tem um elemento chamado content, é utilizado esse método para pode acessar
			VBox mainVBox =(VBox) ((ScrollPane) mainScene.getRoot()).getContent(); // Obtém a referencia do scroll pane e joga para o Vbox
			
			//Vou guardar uma referencia para o menu
			//getChildren() - obtém os Filhos do Vbox
			//get(0) - Primeiro filho do Vbox da janela principal(mainMenu)
			Node mainMenu = mainVBox.getChildren().get(0); 
			
			//limpa todos os filhos do VBox
			mainVBox.getChildren().clear();
			
			//Adiciona o menu
			mainVBox.getChildren().add(mainMenu);
			
			//Adiciona a coleção - Os filhos do new Vbox
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			DepartmentListController controller = loader.getController(); // loader.getController() acessa o COntroller e instancia para DepartmentListController
			controller.setDepartmentService(new DepartmentService()); // processo manual de injeção de dependencia no controller
			controller.updateTableView(); //atualiza os dados na tela
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception","Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}
