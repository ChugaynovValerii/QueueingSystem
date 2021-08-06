package controllers;

import functional.Simulation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;

import java.io.IOException;


public class MainPageController {
    @FXML
    public Scene mainPage;
    @FXML
    public Button stepMode;
    @FXML
    public Button autoMode;
    @FXML
    public Spinner<Integer> sourceCountSpinner;
    @FXML
    public Spinner<Integer> deviceCountSpinner;
    @FXML
    public Spinner<Integer> bufferSizeSpinner;
    @FXML
    public Spinner<Double> coefficientASpinner;
    @FXML
    public Spinner<Double> coefficientBSpinner;
    @FXML
    public Spinner<Double> coefficientLSpinner;
    
    
    @FXML
    public void autoModeAction() throws IOException {
        Stage stage = (Stage) mainPage.getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/auto_mode_page.fxml"));
        Scene autoModeScene = loader.load();
        stage.setScene(autoModeScene);
        stage.centerOnScreen();
        
        AutoModeController autoModeController = loader.getController();
        autoModeController.sendStatistic(new Simulation(
                sourceCountSpinner.getValue(),
                deviceCountSpinner.getValue(),
                bufferSizeSpinner.getValue(),
                coefficientASpinner.getValue(),
                coefficientBSpinner.getValue(),
                coefficientLSpinner.getValue(), true)
                .getStatistic());
        stage.show();
    }
    
    @FXML
    public void stepModeAction() throws IOException {
        Stage stage = (Stage) mainPage.getWindow();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/step_mode_page.fxml"));
        Scene stepModeScene = loader.load();
        stage.setScene(stepModeScene);
        stage.centerOnScreen();
        
        StepModeController stepModeController = loader.getController();
        stepModeController.sendStatistic(new Simulation(
                sourceCountSpinner.getValue(),
                deviceCountSpinner.getValue(),
                bufferSizeSpinner.getValue(),
                coefficientASpinner.getValue(),
                coefficientBSpinner.getValue(),
                coefficientLSpinner.getValue(), false)
                .getStatistic());
        stage.show();
    }
}
