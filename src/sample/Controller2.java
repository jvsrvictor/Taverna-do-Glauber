package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static sample.Main.numeroCadeiras;

public class Controller2 {


    @FXML
    private TextField nCad;

    @FXML
    private void initialize() {
        //FUNÇÃO PARA NÃO ACEITAR ELEMENTOS DIFERENTES DE NÚMEROS
        nCad.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    nCad.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    @FXML
    public void onEnter(ActionEvent ae){
        SetarCadeirasBar();
    }

    @FXML
    private void SetarCadeirasBar(){
        if(Integer.parseInt(nCad.getText())>10){
            Alert a = new Alert(Alert.AlertType.NONE);
            a.setTitle("Número de Cadeiras Exedido");
            a.setHeaderText(null);
            a.setContentText("Número Máximo de Cadeiras  é 10");
            a.setAlertType(Alert.AlertType.ERROR);
            a.show();
        }else if(Integer.parseInt(nCad.getText())==0){
            Alert a = new Alert(Alert.AlertType.NONE);
            a.setTitle("Número mínimo de Cadeiras");
            a.setHeaderText(null);
            a.setContentText("Número Número mínimo de Cadeiras é 1");
            a.setAlertType(Alert.AlertType.ERROR);
            a.show();
        }else{
            numeroCadeiras = Integer.parseInt(nCad.getText());
            Stage stage = (Stage) nCad.getScene().getWindow();
            Parent root2 = null;
            try {
                root2 = FXMLLoader.load(getClass().getResource("layout2.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(root2, 1280, 720, true, SceneAntialiasing.DISABLED));
        }


    }


}
