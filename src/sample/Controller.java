package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.canvas.*;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import static sample.Main.numeroCadeiras;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import static sample.Main.*;

public class Controller{

    @FXML
    private MediaView mv;

    @FXML
    private TextField tempoCasa;

    @FXML
    public Canvas canvas;

    @FXML
    public ImageView Cli1;

    @FXML
    public ImageView Cli2;

    @FXML
    public ImageView Cli3;

    @FXML
    public ImageView Cli4;

    @FXML
    public ImageView Cli5;

    @FXML
    public ImageView Cli6;

    @FXML
    public ImageView Cli7;

    @FXML
    public ImageView Cli8;

    @FXML
    public ImageView Cli9;

    @FXML
    public ImageView Cli10;

    @FXML
    private TextField tempoBar;

    @FXML
    private TextArea logSaida;

    @FXML
    private URL location;

    @FXML
    private ResourceBundle resources;

    @FXML
    private ImageView CadeirasBar;

    public Semaphore mutex;

    private int nClientes;

    public void desenhadorDeCadeiras(int nCadeiras){
        Image cadeiras = new Image("cadeiras/"+ nCadeiras +".png");
        CadeirasBar.setImage(cadeiras);
    }

    public Controller() {

    }

    @FXML
    private void initialize() {

        String path = "src/sfx/ost.mp3";
        Media media = new Media(new File(path).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.1);
        mediaPlayer.play();
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mv.setMediaPlayer(mediaPlayer);

        //FUNÇÃO PARA NÃO ACEITAR ELEMENTOS DIFERENTES DE NÚMEROS
        tempoCasa.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    tempoCasa.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        //FUNÇÃO PARA NÃO ACEITAR ELEMENTOS DIFERENTES DE NÚMEROS
        tempoBar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    tempoBar.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });


        desenhadorDeCadeiras(numeroCadeiras);
        mutex = new Semaphore(numeroCadeiras, true);

        ThreadControle Ct = new ThreadControle(mutex);
        Ct.start();

        //DEFINE AS POSIÇÕES LÓGICAS DAS CADEIRAS DA ESPERA
        for(int n=0;n<10;n++){
            CadeirasE.add(705 - n*70);
        }

        //DEFINE AS POSIÇÕES LÓGICAS DAS CADEIRAS DO BAR
        for(int n=0;n<10;n++){
            CadeirasBX.add(855-78*n);
        }

        //DEFINE AS OS ESTADOS DAS CADEIRAS DO BAR
        for(int n=0;n<10;n++){
            CadeirasBarV.add(false);
        }

        //DEFINE AS OS ESTADOS DAS CADEIRAS DA ESPERA
        for(int n=0;n<10;n++){
            CadeirasEsperaV.add(false);
        }
    }

    private void instanciadorDeClientes(GraphicsContext gc, int nC, int tb, int tc){
        switch(nC) {

            case 1:
                ThreadCliente C1 = new ThreadCliente(mutex, Cli1, logSaida, gc, nC, tb, tc);
                C1.start();
                break;

            case 2:
                ThreadCliente C2 = new ThreadCliente(mutex, Cli2, logSaida, gc, nC, tb, tc);
                C2.start();
                break;

            case 3:
                ThreadCliente C3 = new ThreadCliente(mutex, Cli3, logSaida, gc, nC, tb, tc);
                C3.start();
                break;

            case 4:
                ThreadCliente C4 = new ThreadCliente(mutex, Cli4, logSaida, gc, nC, tb, tc);
                C4.start();
                break;

            case 5:
                ThreadCliente C5 = new ThreadCliente(mutex, Cli5, logSaida, gc, nC, tb, tc);
                C5.start();
                break;

            case 6:
                ThreadCliente C6 = new ThreadCliente(mutex, Cli6, logSaida, gc, nC, tb, tc);
                C6.start();
                break;

            case 7:
                ThreadCliente C7 = new ThreadCliente(mutex, Cli7, logSaida, gc, nC, tb, tc);
                C7.start();
                break;

            case 8:
                ThreadCliente C8 = new ThreadCliente(mutex, Cli8, logSaida, gc, nC, tb, tc);
                C8.start();
                break;

            case 9:
                ThreadCliente C9 = new ThreadCliente(mutex, Cli9, logSaida, gc, nC, tb, tc);
                C9.start();
                break;

            case 10:
                ThreadCliente C10 = new ThreadCliente(mutex, Cli10, logSaida, gc, nC, tb, tc);
                C10.start();
                break;
        }
    }

    @FXML
    private void criarCliente(){
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if(Integer.parseInt(tempoBar.getText())==0 || Integer.parseInt(tempoCasa.getText())==0){
            updateLog("O TEMPO MÍNIMO É 1 SEGUNDO!", logSaida);
        }else{
            if(nClientes<10){
                int nC = nClientes+1;
                instanciadorDeClientes(gc, nClientes+1, Integer.parseInt(tempoBar.getText()), Integer.parseInt(tempoCasa.getText()));
                nClientes++;
            }else{
                updateLog("NÚMERO MÁXIMO DE CLIENTES ATINGIDO!", logSaida);
            }
        }
    }

    public void updateLog(String mensagem, TextArea logSaida){
        String log = logSaida.getText();
        if(log==null){
            log=mensagem;
        }else{
            log = log + "\n" + mensagem;
        }
        logSaida.setText(log);
        logSaida.appendText("");
    }
}
