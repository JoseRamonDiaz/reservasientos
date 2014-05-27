/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author Yussel
 */
public class RequestMessage {
    public static final int LOGIN = 0;
    public static final int LOGOUT = 1;
    public static final int CONFIRM_RESERVATION = 2;
    public static final int CANCEL_RESERVATION = 3;
    public static final int PRE_RESERVE_SEAT = 4;
    public static final int GET_SEATS = 5;
    public static final int CANCEL_PRE_RESERVE_SEAT = 6;
    
    
    private int messageType;
    private String data1;
    private String data2;

    public RequestMessage(int messageType, String data1) {
        this.messageType = messageType;
        this.data1 = data1;
    }
        
    public RequestMessage(int messageType, String data1, String data2) {
        this.messageType = messageType;
        this.data1 = data1;
        this.data2 = data2;
    }

    /**
     * @return the messageType
     */
    public int getMessageType() {
        return messageType;
    }

    /**
     * @param messageType the messageType to set
     */
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    /**
     * @return the data
     */
    public String getData1() {
        return data1;
    }

    /**
     * @param data the data to set
     */
    public void setData1(String data1) {
        this.data1 = data1;
    }

    /**
     * @return the data2
     */
    public String getData2() {
        return data2;
    }

    /**
     * @param data2 the data2 to set
     */
    public void setData2(String data2) {
        this.data2 = data2;
    }
    
    
}
