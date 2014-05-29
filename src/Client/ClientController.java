/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Model.RequestMessage;
import Model.Seat;
import Model.User;
import com.google.gson.Gson;

/**
 *
 * @author Yussel
 */
public class ClientController {
    private TCPClient tcpClient;
    private Gson gson = new Gson();
    private User user;
    private Seat seats[][];
    
    public ClientController(ReservationWindow reservationWindow){
        gson = new Gson();
        tcpClient = new TCPClient(reservationWindow);
        login();
    }
    
    public ClientController(){
        gson = new Gson();
        tcpClient = new TCPClient(null);
    }
    
    private void login(){
        RequestMessage rm = new RequestMessage(RequestMessage.LOGIN, null);
        String jsonMsg = gson.toJson(rm);
        
        //Envía petición al servidor
        tcpClient.sendMessage(jsonMsg);
        
        //Recibe respeusta del servidor
        String msg = tcpClient.recieveMessage();
        
        user = gson.fromJson(msg, User.class);
        
        System.out.println("User id: "+getUser().getId());
    }
    
    public Seat[][] getSeats(){
        if(seats == null){
            RequestMessage rm = new RequestMessage(RequestMessage.GET_SEATS, null);

            String jsonMsg = gson.toJson(rm);

            //Envía petición al servidor
            tcpClient.sendMessage(jsonMsg);

            //Recibe respeusta del servidor
            String msg = tcpClient.recieveMessage();

            seats = gson.fromJson(msg, Seat[][].class);
        }
        
        return seats;
    }
    
    public Seat[] getReservation(int id){
        
        RequestMessage rm = new RequestMessage(RequestMessage.GET_RESERVATION, gson.toJson(id));

        String jsonMsg = gson.toJson(rm);

        //Envía petición al servidor
        tcpClient.sendMessage(jsonMsg);

        //Recibe respeusta del servidor
        String msg = tcpClient.recieveMessage();

        Seat s[] = gson.fromJson(msg, Seat[].class);
        
        return s;
    }
    
    public boolean preReserveSeat(Seat seat){
        String jsonSeat = gson.toJson(seat);
        String jsonUser = gson.toJson(getUser());
        
        RequestMessage rm = new RequestMessage(RequestMessage.PRE_RESERVE_SEAT, jsonSeat, jsonUser);
        
        String jsonMsg = gson.toJson(rm);
        
        //Envía petición al servidor
        tcpClient.sendMessage(jsonMsg);
        
        //Recibe respeusta del servidor
        String msg = tcpClient.recieveMessage();
        
        boolean preReserved = gson.fromJson(msg, boolean.class);
        
        return preReserved;
    }
    
    public boolean cancel_preReserveSeat(Seat seat){
        String jsonSeat = gson.toJson(seat);
        String jsonUser = gson.toJson(getUser());
        
        RequestMessage rm = new RequestMessage(RequestMessage.CANCEL_PRE_RESERVE_SEAT, jsonSeat, jsonUser);
        
        String jsonMsg = gson.toJson(rm);
        
        //Envía petición al servidor
        tcpClient.sendMessage(jsonMsg);
        
        //Recibe respeusta del servidor
        String msg = tcpClient.recieveMessage();
        
        boolean canceled = gson.fromJson(msg, boolean.class);
        
        return canceled;
    }
    
    public boolean confirmReservations(){
        String jsonUser = gson.toJson(getUser());
        
        RequestMessage rm = new RequestMessage(RequestMessage.CONFIRM_RESERVATION, jsonUser);
        
        String jsonMsg = gson.toJson(rm);
        
        //Envía petición al servidor
        tcpClient.sendMessage(jsonMsg);
        
        //Recibe respeusta del servidor
        String msg = tcpClient.recieveMessage();
        
        boolean confirmed = gson.fromJson(msg, boolean.class);
        
        return confirmed;
    }
    
    public boolean cancelReservations(){
        String jsonUser = gson.toJson(getUser());
        
        RequestMessage rm = new RequestMessage(RequestMessage.CANCEL_RESERVATION, jsonUser);
        
        String jsonMsg = gson.toJson(rm);
        
        //Envía petición al servidor
        tcpClient.sendMessage(jsonMsg);
        
        //Recibe respeusta del servidor
        String msg = tcpClient.recieveMessage();
        
        boolean canceled = gson.fromJson(msg, boolean.class);
        
        return canceled;
    }
    
    public boolean cancelReservedSeats(int id, Seat[] seats){
        String jsonUser = gson.toJson(new User(id));
        String jsonSeats = gson.toJson(seats);
        
        RequestMessage rm = new RequestMessage(RequestMessage.CANCEL_RESERVED_SEAT, jsonUser, jsonSeats);
        
        String jsonMsg = gson.toJson(rm);
        
        //Envía petición al servidor
        tcpClient.sendMessage(jsonMsg);
        
        //Recibe respeusta del servidor
        String msg = tcpClient.recieveMessage();
        
        boolean canceled = gson.fromJson(msg, boolean.class);
        
        return canceled;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }
    
    public void close(){
        tcpClient.close();
    }
}
