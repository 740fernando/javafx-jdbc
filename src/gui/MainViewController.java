package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
	
	// ação de inicialização
	//DepartmentListController controller = loader.getController(); // loader.getController() acessa o COntroller e instancia para DepartmentListController
	// controller.setDepartmentService(new DepartmentService()); // processo manual de injeção de dependencia no controller
	// controller.updateTableView(); //atualiza os dados na tela
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml",(DepartmentListController controller)->{
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();// incluido segundo parametro, uma funcao lambda para inicializar o controlador, função de inicialização
		});
	}
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml",x->{});// incluido segundo parametro, uma funcao lambda para inicializar o controlador, função vazia
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
	}
	/*
	 * synchronized - Garente que o processamento não será interrompido por algum comportamento multithread
	 * loadView - método responsável por manipular a scena principal
	 * 
	 * Consumer - Interface funcional
	 */
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		
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
			
			// executa a função passada no argumento - inicio
			T controller = loader.getController(); // vai retornar o controlador do tipo que eu chamar 
			initializingAction.accept(controller);
			// executa a função passada no argumento - fim
			
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception","Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

}
