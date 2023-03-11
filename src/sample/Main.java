package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.util.Vector;
import java.util.concurrent.Semaphore;


public class Main extends Application{

    public static Vector<Integer> CadeirasBX = new Vector<Integer>(10);
    public static Vector<Integer> CadeirasE = new Vector<Integer>(10);
    public static Vector<Boolean> CadeirasBarV = new Vector<Boolean>(10);
    public static Vector<Boolean> CadeirasEsperaV = new Vector<Boolean>(10);
    public static int numeroCadeiras;
    public static int portaSaida = 920;
    public static int portaEntrada = 850;
    public static int releaseAcumulados;
    public static Semaphore acessoVariaveaveisGlobais;


    @Override
    public void start(Stage primaryStage) throws Exception{
        acessoVariaveaveisGlobais = new Semaphore(1, true);

        System.setProperty("prism.lcdtext", "false");
        //TÍTULO DA JANELA
        primaryStage.setTitle("Trabalho Parente");
        Image icone = new Image("Icone.png");
        primaryStage.getIcons().add(icone);
        System.setProperty("quantum.multithreaded", "false");

        //CARREGA A FONTE
        Font.loadFont(Main.class.getResource("PressStart2P-vaV7.ttf").toExternalForm(), 10);

        //DESABILITA  O REDIMENSIONAMENTO DA JANELA
        primaryStage.setResizable(false);

        //CARREGA O FXML
        Parent root1 = FXMLLoader.load(getClass().getResource("layout1.fxml"));

        //DEFINE A CENA
        primaryStage.setScene(new Scene(root1, 1280, 720, true, SceneAntialiasing.DISABLED));

        //MOSTRA A JANELA
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> handleExit());

    }

    private void handleExit(){
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
