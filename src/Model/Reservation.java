/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.ArrayList;

/**
 *
 * @author Yussel
 */
public class Reservation {
    private int id;
    private ArrayList<Seat> seats;

    public Reservation(int id) {
        this.id = id;
        seats = new ArrayList();
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the seats
     */
    public ArrayList<Seat> getSeats() {
        return seats;
    }

    /**
     * @param seats the seats to set
     */
    public void setSeats(ArrayList<Seat> seats) {
        this.seats = seats;
    }
    
    @Override
    public boolean equals(Object o){
        if(o!=null){
            if(o instanceof Reservation){
                return ((Reservation)o).getId()==id;
            }
        }
        
        return false;
    }
}
