package sample;

import static sample.Main.*;
import java.util.concurrent.Semaphore;

public class ThreadControle extends Thread{

    private Semaphore mutex;

    public void run(){

        while(true){
            if(mutex.availablePermits()==0){
                if(releaseAcumulados == numeroCadeiras){
                    mutex.release(numeroCadeiras);
                    releaseAcumulados =0;
                }
            }
        }
    }

    public ThreadControle(Semaphore mutex){
        this.mutex=mutex;
    }

}
