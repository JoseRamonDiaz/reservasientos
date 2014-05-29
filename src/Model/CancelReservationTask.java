/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Server.ServerController;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Yussel
 */
public class CancelReservationTask extends TimerTask{
    private int id;
    private Timer timer;
    
    public CancelReservationTask(int id){
        this.id = id;
        this.timer= new Timer();
        timer.schedule(this, 60000);
    }
    
    @Override
    public void run() {
        ServerController.getInstance().cancelReservations(new User(id));
        System.out.println("Reservación: "+id+" cancelada.");
        timer.cancel();
    }
    
    public void remove(){
        timer.cancel();
    }
    
}
