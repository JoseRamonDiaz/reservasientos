/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author Yussel
 */
public class Seat {
    public static final int AVAILABLE=0;
    public static final int PRE_RESERVED=1;
    public static final int RESERVED=2;
    public static final int SELECTED=3;
    
    private int state;
    private char row;
    private int seatNumber;

    public Seat(char row, int seatNumber) {
        this.row = row;
        this.seatNumber = seatNumber;
        state = AVAILABLE;
    }

    /**
     * @return the state
     */
    public int getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * @return the row
     */
    public char getRow() {
        return row;
    }

    /**
     * @return the seatNumber
     */
    public int getSeatNumber() {
        return seatNumber;
    }
    
    @Override
    public boolean equals(Object o){
        if(o!=null){
            if(o instanceof Seat){
                return (((Seat)o).getRow()==row && ((Seat)o).getSeatNumber() == seatNumber);
            }
        }
        
        return false;
    }
}
