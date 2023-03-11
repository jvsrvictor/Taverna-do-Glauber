package sample;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.concurrent.Semaphore;
import static java.lang.Math.abs;
import static sample.Main.*;

public class ThreadCliente extends Thread{

    Semaphore mutex;
    private int CadeiraB=-1;
    private int CadeiraE=-1;
    private GraphicsContext gC;
    private int nCliente;
    private int tempoBar;
    private int tempoCasa;
    private double tempoCasaR;
    private double tempoBarR;
    private double tempoAnimacaoRecarga = 1;
    private Image barraStatus_b = new Image( "bstatus/Barra Bar.png" );
    private Image barraStatus_c = new Image( "bstatus/Barra Casa.png" );
    private TextArea logS;
    private ImageView ImageVC;
    private final int OFFSET_X =  0;
    private final int OFFSET_Y =  0;
    private final int WIDTH    = 80;
    private final int HEIGHT   = 120;


    //CONSTRUTOR DA THREAD CLIENTE
    public ThreadCliente(Semaphore mutex, ImageView Im, TextArea logSaida, GraphicsContext gc, int nC, int tb, int tc) {
        gC = gc;
        nCliente = nC;
        tempoBar = tb;
        tempoCasa = tc;
        logS=logSaida;
        ImageVC = Im;
        this.mutex=mutex;
    }

    //CÓDIGO PRINCIPAL DA THREAD CLIENTE
    @Override
    public void run() {
        updateLog("Cliente "+ nCliente + " Criado com sucesso!" , logS);

        //LOOP PRINCIPAL
        while(true){
            //FICAR EM CASA
            AnimacaoBarraCasa();

            //SE NÃO TIVER ESPAÇOS LIVRE NA MESA - IR PARA FILA DE ESPERA
            if(mutex.availablePermits()==0||mutex.hasQueuedThreads()){
                //SENTAR NA ESPERA
                sentarEspera();
                try {
                    mutex.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    //QUANDO ACORDAR IR PARA MESA
                    sentarBarVindoEspera();
                    AnimacaoBarraBar();
                    drinkSFX();

                    try {
                        acessoVariaveaveisGlobais.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        CadeirasBarV.set(CadeiraB, false);

                        if (mutex.availablePermits() == 0)
                            releaseAcumulados++;
                        else
                            mutex.release();

                        acessoVariaveaveisGlobais.release();
                    }

                    AnimacaoSair();
                }
            }else{
                try {
                    mutex.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    //IR PARA MESA
                    sentarBar();
                    AnimacaoBarraBar();
                    drinkSFX();

                    try {
                        acessoVariaveaveisGlobais.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        CadeirasBarV.set(CadeiraB, false);

                        if (mutex.availablePermits() == 0)
                            releaseAcumulados++;
                        else
                            mutex.release();

                        acessoVariaveaveisGlobais.release();
                    }

                    AnimacaoSair();
                }
            }
        }
    }

    //DESENHA OS BALÕES DE STATUS CASA
    private void AnimacaoBarraCasa(){
        tempoCasaR=tempoCasa;

        new AnimationTimer(){

            int flag=2;
            long tempoI = System.nanoTime();
            double tempoAtual;

            public void handle(long timeNow){
                tempoAtual = (timeNow - tempoI)/1000000000.0;

                if(flag==2) {
                    tempoCasaR = tempoAtual;
                    desenhadorDeBaloesStatus(gC, nCliente,1037 ,54*(nCliente-1) , 0, (tempoCasaR/tempoAnimacaoRecarga));
                    if(tempoAtual>tempoAnimacaoRecarga){
                        tempoI = System.nanoTime();
                        flag = 1;
                    }

                }else{
                    tempoCasaR = abs(tempoCasa - tempoAtual);
                    desenhadorDeBaloesStatus(gC, nCliente,1037 ,54*(nCliente-1) , 0, (tempoCasaR/tempoCasa));
                    if(tempoAtual>tempoCasa){
                        this.stop();
                    }
                }
            }
        }.start();
        EsperarTempo(tempoCasa+tempoAnimacaoRecarga);
        updateLog("Cliente "+ nCliente + " Saiu de Casa!" , logS);
    }

    private void drinkSFX(){
        String path = "src/sfx/drink.wav";
        Media media = new Media(new File(path).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.05);
        mediaPlayer.play();
    }

    //RESPONSÁVEL PELO GERENCIAMENTO DAS CADEIRAS DO BAR, QUANDO O CLIENTE SE SENTA
    private void sentarBar(){
        try {
            acessoVariaveaveisGlobais.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            for (int n = 0; n < numeroCadeiras; n++) {
                if (CadeirasBarV.get(n) == false) {
                    CadeirasBarV.set(n, true);
                    CadeiraB = n;
                    break;
                }
            }
            acessoVariaveaveisGlobais.release();
        }
        AnimacaoSentarNoBar();
    }

    //RESPONSÁVEL PELA CHAMADA DA ANIMAÇÃO E GERENCIAMENTO DAS CADEIRAS DA ESPERA, QUANDO O CLIENTE SE LEVANTA
    private void sentarBarVindoEspera(){

        try {
            acessoVariaveaveisGlobais.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            CadeirasEsperaV.set(CadeiraE, false);

            for (int n = 0; n < numeroCadeiras; n++) {
                if (CadeirasBarV.get(n) == false) {
                    CadeirasBarV.set(n, true);
                    CadeiraB = n;
                    break;
                }
            }

            acessoVariaveaveisGlobais.release();
        }
        AnimacaoSentarNoBarVindoEspera();
    }

    //RESPONSÁVEL PELO GERENCIAMENTO DAS CADEIRAS DA ESPERA, QUANDO O CLIENTE SE SENTA
    private void sentarEspera(){
        try {
            acessoVariaveaveisGlobais.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            for (int n = 0; n < 10; n++) {
                if (CadeirasEsperaV.get(n) == false) {
                    CadeirasEsperaV.set(n, true);
                    CadeiraE = n;
                    break;
                }
            }

            acessoVariaveaveisGlobais.release();
        }
        AnimacaoSentarNaEspera();
    }

    private void AnimacaoSair(){
        int xCadeira, xPorta;
        try {
            acessoVariaveaveisGlobais.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            xCadeira = CadeirasBX.get(CadeiraB);
            xPorta = portaSaida;
            acessoVariaveaveisGlobais.release();
        }
        AnimacaoDeslocamento(xCadeira, 192, xCadeira, 300);
        AnimacaoDeslocamento(xCadeira, 300, xPorta, 300);
        AnimacaoDeslocamento(xPorta, 300, xPorta, 600);
        ImageVC.setVisible(false);
    }

    private void AnimacaoSentarNoBarVindoEspera(){
        int xEspera, xBar;
        try {
            acessoVariaveaveisGlobais.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            xEspera = CadeirasE.get(CadeiraE);
            xBar = CadeirasBX.get(CadeiraB);
            acessoVariaveaveisGlobais.release();
        }
        AnimacaoDeslocamento(xEspera, 380, xEspera, 300);
        AnimacaoDeslocamento(xEspera, 300, xBar, 300);
        AnimacaoDeslocamento(xBar, 300, xBar, 192);
        sentarCadeira();
    }

    private void AnimacaoSentarNaEspera(){
        int xEntrada, xEspera;
        try {
            acessoVariaveaveisGlobais.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            xEntrada = portaEntrada;
            xEspera = CadeirasE.get(CadeiraE);
            acessoVariaveaveisGlobais.release();
        }
        ImageVC.setVisible(true);
        AnimacaoDeslocamento(xEntrada, 600, xEntrada, 300);
        AnimacaoDeslocamento(xEntrada, 300, xEspera, 300);
        AnimacaoDeslocamento(xEspera, 300, xEspera, 380);
        sentarCadeira();
    }

    private void AnimacaoSentarNoBar(){
        int xEntrada, xBar;
        try {
            acessoVariaveaveisGlobais.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            xEntrada = portaEntrada;
            xBar = CadeirasBX.get(CadeiraB);
            acessoVariaveaveisGlobais.release();
        }
        ImageVC.setVisible(true);
        AnimacaoDeslocamento(xEntrada, 600, xEntrada, 300);
        AnimacaoDeslocamento(xEntrada, 300, xBar, 300);
        AnimacaoDeslocamento(xBar, 300, xBar, 192);
        sentarCadeira();
    }

    //DECIDE QUAL SPRITE USAR DEPENDENDO DA CADEIRA
    public void sentarCadeira(){
        String URL = "sprites/var"+nCliente+"/S/";
        Image SPRITE = new Image(URL +"SF.png", 80,120,false,false);
        ImageVC.setImage(SPRITE);
    }

    //DESENHA BALÃO DE STATUS GENÉRICO(USADO PELOS DOIS MÉTODOS ABAIXO)
    private void desenhadorDeBaloesStatus(GraphicsContext gc, int idCliente, int Xc, int Yc, double tb, double tc){
        //CARREGA A IMAGEM DO RESCPECTIVO BALÃO DO CLIENTE
        String urlI;
        urlI = "bstatus/" + idCliente + ".png";
        Image balao = new Image( urlI );

        //PREAPARA A BARRA DE STATUS BAR
        if(tb<=0)tb=0.01;
        if(tb>1)tb=1;
        PixelReader readerb = barraStatus_b.getPixelReader();
        WritableImage barraStatus_bc = new WritableImage(readerb, (int) (barraStatus_b.getWidth()*tb), (int) barraStatus_b.getHeight());

        //PREAPARA A BARRA DE STATUS CASA
        if(tc<=0)tc=0.01;
        if(tc>1)tc=1;
        PixelReader readerc = barraStatus_c.getPixelReader();
        WritableImage barraStatus_cc = new WritableImage(readerc, (int) (barraStatus_c.getWidth()*tc), (int) barraStatus_c.getHeight());

        //DESENHA OS 3 NA TELA
        gc.drawImage( balao, Xc, Yc);
        gc.drawImage( barraStatus_bc, Xc, Yc);
        gc.drawImage( barraStatus_cc, Xc, Yc);
    }

    //ESPERA UM TEMPO EM SEGUNDOS
    private void EsperarTempo(double tempo){
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        while (elapsedTime < tempo*1000) {
            elapsedTime = System.currentTimeMillis() - startTime;
        }
    }

    //DESENHA OS BALÕES DE STATUS BARRA
    private void AnimacaoBarraBar(){
        tempoBarR=tempoBar;

        new AnimationTimer(){

            int flag=2;
            long tempoI = System.nanoTime();
            double tempoAtual;

            public void handle(long timeNow){
                tempoAtual = (timeNow - tempoI)/1000000000.0;

                if(flag==2) {
                    tempoBarR = tempoAtual;
                    desenhadorDeBaloesStatus(gC, nCliente,1037 ,54*(nCliente-1) , (tempoBarR/tempoAnimacaoRecarga), 0);
                    if(tempoAtual>tempoAnimacaoRecarga){
                        tempoI = System.nanoTime();
                        flag = 1;
                    }
                }else{
                    tempoBarR = abs(tempoBar - tempoAtual);
                    desenhadorDeBaloesStatus(gC, nCliente,1037 ,54*(nCliente-1) , (tempoBarR/tempoBar), 0);
                    if(tempoAtual>tempoBar){
                        this.stop();
                    }
                }
            }
        }.start();
        EsperarTempo(tempoBar+tempoAnimacaoRecarga);
        updateLog("Cliente "+ nCliente + " Terminou de ser atendido!" , logS);
    }

    //RESPONSÁVEL PELA ANIMAÇÃO DO DESLOCAMENTO LINEAR
    private void AnimacaoDeslocamento(int Xi, int Yi, int Xf, int Yf){
        //DEFINE O TAMANHO DA VIEWPORT
        ImageVC.setViewport(new Rectangle2D(OFFSET_X, OFFSET_Y, WIDTH, HEIGHT));
        ImageVC.setSmooth(false);
        int dX;
        int dY;
        String URL;

        //DEFINE O SPRITE A SER USADO (ESQUERDA, DIREITA, CIMA, BAIXO)
        if(Xi==Xf){
            if(Yi>Yf){
                URL = "sprites/var"+nCliente+"/C/";
                dX=0;
                dY=-1;
            }else{
                URL = "sprites/var"+nCliente+"/B/";
                dX=0;
                dY=1;
            }
        }else{
            if(Xi>Xf){
                URL = "sprites/var"+nCliente+"/E/";
                dX=-1;
                dY=0;
            }else{
                URL = "sprites/var"+nCliente+"/D/";
                dX=1;
                dY=0;
            }
        }

        new AnimationTimer(){
            long tempoI = System.nanoTime();
            int tempoAtual;
            int tempoAtual2;

            //LOOP SPRITE
            public void handle(long timeNow){
                int n = tempoAtual % 2;
                tempoAtual = (int)((timeNow - tempoI)/250000000.0);
                tempoAtual2 = (int) ((timeNow - tempoI)/25000000.0);
                //DEFINE A POSIÇÃO
                ImageVC.setLayoutX(Xi+tempoAtual2*2*dX);
                ImageVC.setLayoutY(Yi+tempoAtual2*2*dY);
                //DEFINE O SPRITE A SER USADO (NÚMERO DO FRAME)
                Image SPRITE = new Image(URL + n + ".png", 80,120,false,false);
                ImageVC.setImage(SPRITE);

                if(dX==0){
                    if(dY==-1){
                        if((Yi+tempoAtual2*2*dY)<=Yf)this.stop();
                    }else{
                        if((Yi+tempoAtual2*2*dY)>=Yf)this.stop();
                    }

                }else{
                    if(dX==-1){
                        if((Xi+tempoAtual2*2*dX)<=Xf)this.stop();
                    }else{
                        if((Xi+tempoAtual2*2*dX)>=Xf)this.stop();
                    }
                }

            }
        }.start();

        try {
            sleep((long) ((abs(Xf-Xi)+abs(Yf-Yi))/0.08)+100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //FAZ O UPDATE DO LOG
    private void updateLog(String mensagem, TextArea logSaida){
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
