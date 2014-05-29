/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Model.*;
import com.google.gson.Gson;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yussel
 */
public class ServerController {
    
    private static ServerController serverController;
    private int session_id=0;
    private Seat seats[][];
    private Gson gson;
    private ArrayList<Reservation> reservations;
    
    private ServerController(){
        createSeats();
        gson = new Gson();
        reservations = new ArrayList<>();
    }
    
    public static ServerController getInstance(){
        if(serverController == null){
            serverController = new ServerController();
            return serverController;
        }
        else{
            return serverController; 
        }
    }
    
    public void processMessage(String message,DataOutputStream out){
        try {
            System.out.print("Mensaje process "+message);
            RequestMessage reqMsg = gson.fromJson(message, RequestMessage.class);
            
            switch(reqMsg.getMessageType()){
                case RequestMessage.LOGIN:
                {
                    User user = new User(session_id++);
                    out.writeUTF(gson.toJson(user)+'\n');
                }
                    break;
                case RequestMessage.CONFIRM_RESERVATION:
                {
                    String data1 = reqMsg.getData1();
                  
                    User user = gson.fromJson(data1, User.class);
                    
                    boolean reserved = confirmReservations(user);
                    
                    out.writeUTF(gson.toJson(reserved)+'\n');
                }
                    break;
                case RequestMessage.CANCEL_RESERVATION:
                {
                    String data1 = reqMsg.getData1();
                  
                    User user = gson.fromJson(data1, User.class);
                    
                    boolean canceled = cancelReservations(user);
                    
                    out.writeUTF(gson.toJson(canceled)+'\n');
                }
                    break;
                case RequestMessage.GET_SEATS:
                    out.writeUTF(gson.toJson(seats)+'\n');
                    break;
                case RequestMessage.PRE_RESERVE_SEAT:
                {
                    String data1 = reqMsg.getData1();
                    String data2 = reqMsg.getData2();
                    
                    Seat seat = gson.fromJson(data1, Seat.class);
                    User user = gson.fromJson(data2, User.class);
                    
                    int row = seat.getRow()-'A';
                    int seatNumber = seat.getSeatNumber()-1;
                    
                    boolean reserved = preReserveSeat(row, seatNumber, user);
                    
                    out.writeUTF(gson.toJson(reserved)+'\n');
                }
                    break;
                case RequestMessage.CANCEL_PRE_RESERVE_SEAT:
                {
                    String data1 = reqMsg.getData1();
                    String data2 = reqMsg.getData2();
                    
                    Seat seat = gson.fromJson(data1, Seat.class);
                    User user = gson.fromJson(data2, User.class);
                    
                    int row = seat.getRow()-'A';
                    int seatNumber = seat.getSeatNumber()-1;
                    
                    boolean canceled = cancel_preReserveSeat(row, seatNumber, user);
                    
                    out.writeUTF(gson.toJson(canceled)+'\n');
                }
                    break;
                case RequestMessage.GET_RESERVATION:
                {
                    String data1 = reqMsg.getData1();

                    int id = gson.fromJson(data1, int.class);
                    
                    Seat s[] = getReservationSeats(id);
                    
                    out.writeUTF(gson.toJson(s)+'\n');
                }
                    break;
                case RequestMessage.CANCEL_RESERVED_SEAT:
                {
                    String data1 = reqMsg.getData1();
                    String data2 = reqMsg.getData2();
                    
                    Seat s[] = gson.fromJson(data2, Seat[].class);
                    User user = gson.fromJson(data1, User.class);
                    
                    boolean canceled = cancel_reservedSeats(user, s);
                    
                    out.writeUTF(gson.toJson(canceled)+'\n');
                }
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createSeats(){
        seats = new Seat[10][10];
        
        for (int nRow = 0; nRow < 10; nRow++) {
            for (int seatNum = 0; seatNum < 10; seatNum++) {
                seats[nRow][seatNum] = new Seat((char)(nRow+'A'), seatNum+1);
            }
        }
    }
    
    private synchronized boolean confirmReservations(User user){
        
        Reservation reservation = new Reservation(user.getId());
        int index;
        
        if( (index = reservations.indexOf(reservation)) >= 0){
            reservation = reservations.get(index);
            
            CancelReservationTask cancelTask;
            if( (cancelTask = reservation.getCancelTask()) != null){
                cancelTask.remove();
            }
            
            for(Seat seat : reservation.getSeats()){
                seat.setState(Seat.RESERVED);
                int row = seat.getRow()-'A';
                int seatNumber = seat.getSeatNumber()-1;
                
                seats[row][seatNumber].setState(Seat.RESERVED);
                
                String jsonSeat = gson.toJson(seat);
                String jsonUser = gson.toJson(user);

                RequestMessage rm = new RequestMessage(RequestMessage.CONFIRM_RESERVATION, jsonSeat, jsonUser);

                String jsonMsg = gson.toJson(rm);

                //Se obtiene la instancia del MulticasServer
                MulticastServer ms = MulticastServer.getInstance();
                //Se difunde el mensaje a todo el grupo
                ms.sendMulticast(jsonMsg);
            }
            
            return true;
        }
        
        return false;
    }
    
    public synchronized boolean cancelReservations(User user){
        Reservation reservation = new Reservation(user.getId());
        int index;
        
        if( (index = reservations.indexOf(reservation)) >= 0){
            reservation = reservations.get(index);
            
            if(!reservation.getSeats().isEmpty() && reservation.getSeats().get(0).getState() == Seat.PRE_RESERVED){
                for(Seat seat : reservation.getSeats()){
                    seat.setState(Seat.AVAILABLE);
                    int row = seat.getRow()-'A';
                    int seatNumber = seat.getSeatNumber()-1;

                    seats[row][seatNumber].setState(Seat.AVAILABLE);

                    String jsonSeat = gson.toJson(seat);
                    String jsonUser = gson.toJson(user);

                    RequestMessage rm = new RequestMessage(RequestMessage.CANCEL_RESERVATION, jsonSeat, jsonUser);

                    String jsonMsg = gson.toJson(rm);

                    //Se obtiene la instancia del MulticasServer
                    MulticastServer ms = MulticastServer.getInstance();
                    //Se difunde el mensaje a todo el grupo
                    ms.sendMulticast(jsonMsg);
                }

                reservations.remove(index);
                return true;
            }
            
        }
        
        return false;
    }
    
    private synchronized boolean preReserveSeat(int row, int seatNumber, User user){
        Seat seat = seats[row][seatNumber];
        if(seat.getState() == Seat.AVAILABLE){
            
            String jsonSeat = gson.toJson(seat);
            String jsonUser = gson.toJson(user);
        
            RequestMessage rm = new RequestMessage(RequestMessage.PRE_RESERVE_SEAT, jsonSeat, jsonUser);

            String jsonMsg = gson.toJson(rm);
            
            seat.setState(Seat.PRE_RESERVED);
            
            addReservation(user, seat);
            
            //Se obtiene la instancia del MulticasServer
            MulticastServer ms = MulticastServer.getInstance();
            //Se difunde el mensaje a todo el grupo
            ms.sendMulticast(jsonMsg);
            
            
            
            
            
            return true;
        }
        else{
            return false;
        }
    }
    
    private synchronized boolean cancel_preReserveSeat(int row, int seatNumber, User user){
        Seat seat = seats[row][seatNumber];
        
        if(seat.getState() == Seat.PRE_RESERVED){
            String jsonSeat = gson.toJson(seat);
            String jsonUser = gson.toJson(user);
        
            RequestMessage rm = new RequestMessage(RequestMessage.CANCEL_PRE_RESERVE_SEAT, jsonSeat, jsonUser);

            String jsonMsg = gson.toJson(rm);
            
            removeReservation(user, seat);
            seat.setState(Seat.AVAILABLE);
            
            //Se obtiene la instancia del MulticasServer
            MulticastServer ms = MulticastServer.getInstance();
            //Se difunde el mensaje a todo el grupo
            ms.sendMulticast(jsonMsg);
            
            
            
            return true;
        }
        return false;
    }
    
    private void addReservation(User user, Seat seat){
        Reservation reservation = new Reservation(user.getId());
        int index;
        
        if( (index = reservations.indexOf(reservation)) >= 0){
            reservation = reservations.get(index);
            reservation.getSeats().add(seat);
        }
        else{
            reservation.getSeats().add(seat);
            reservation.setCancelTask( new CancelReservationTask(user.getId()) );
            reservations.add(reservation);
        }
    }
    
    private void removeReservation(User user, Seat seat){
        Reservation reservation = new Reservation(user.getId());
        int index;
        
        if( (index = reservations.indexOf(reservation)) >= 0){
            reservation = reservations.get(index);
            
            reservation.getSeats().remove(seat);
            
            if(reservation.getSeats().isEmpty()){
                reservations.remove(index);
            }
        }
    }
    
    private Seat[] getReservationSeats(int id){
        Reservation reservation = new Reservation(id);
        Seat s[]=null;
        int index;
        
        if( (index = reservations.indexOf(reservation)) >= 0){
            reservation = reservations.get(index);
            
            if(!reservation.getSeats().isEmpty() && reservation.getSeats().get(0).getState() == Seat.RESERVED){
                s = new Seat[reservation.getSeats().size()];
                reservation.getSeats().toArray(s);
            }
        }
        return s;
    }

    private synchronized boolean cancel_reservedSeats(User user, Seat[] s){
        Reservation reservation = new Reservation(user.getId());
        int index;
        
        if( (index = reservations.indexOf(reservation)) >= 0){
            reservation = reservations.get(index);
            
            for(Seat seat : s){
                int i = reservation.getSeats().indexOf(seat);
                if(i >=0){
                    Seat seatToCancel = reservation.getSeats().get(i);
                    
                    if(seatToCancel.getState() == Seat.RESERVED){
                        seatToCancel.setState(Seat.AVAILABLE);
                        int row = seatToCancel.getRow()-'A';
                        int seatNumber = seatToCancel.getSeatNumber()-1;

                        seats[row][seatNumber].setState(Seat.AVAILABLE);

                        String jsonSeat = gson.toJson(seatToCancel);
                        String jsonUser = gson.toJson(user);
                        
                        reservation.getSeats().remove(i);

                        RequestMessage rm = new RequestMessage(RequestMessage.CANCEL_PRE_RESERVE_SEAT, jsonSeat, jsonUser);

                        String jsonMsg = gson.toJson(rm);

                        //Se obtiene la instancia del MulticasServer
                        MulticastServer ms = MulticastServer.getInstance();
                        //Se difunde el mensaje a todo el grupo
                        ms.sendMulticast(jsonMsg);
                    }
                }
            }
            if(reservation.getSeats().isEmpty())
                reservations.remove(index);
            
            return true;
        }
        
        return false;
    }
}
