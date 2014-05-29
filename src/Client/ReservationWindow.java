/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Model.RequestMessage;
import Model.Seat;
import Model.User;
import com.google.gson.Gson;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Yussel
 */
public class ReservationWindow extends javax.swing.JFrame {

    private ClientController clientController;
    private boolean isFirstPreReservedSeat = true;
    private Stack stack = new Stack();

    /**
     * Creates new form ReservationWindow
     */
    public ReservationWindow() {
        lookAndFeel();
        initComponents();
        clientController = new ClientController(this);
        initInterface();
    }

    //Actualiza el estado de los asientos.
    public void update(String msg) {
        Gson gson = new Gson();
        System.out.print("Mensaje update " + msg);
        RequestMessage reqMsg = gson.fromJson(msg, RequestMessage.class);
        switch (reqMsg.getMessageType()) {
            case RequestMessage.PRE_RESERVE_SEAT: {
                String data1 = reqMsg.getData1();
                String data2 = reqMsg.getData2();

                Seat seat = gson.fromJson(data1, Seat.class);
                User user = gson.fromJson(data2, User.class);

                if (clientController.getUser().getId() != user.getId()) {
                    int row = seat.getRow() - 'A';
                    int seatNumber = seat.getSeatNumber() - 1;

                    setSeatIcon(seatLabels[row][seatNumber], Seat.PRE_RESERVED);
                    clientController.getSeats()[row][seatNumber].setState(Seat.PRE_RESERVED);
                }
            }
            break;
            case RequestMessage.CANCEL_PRE_RESERVE_SEAT: {
                String data1 = reqMsg.getData1();
                String data2 = reqMsg.getData2();

                Seat seat = gson.fromJson(data1, Seat.class);
                User user = gson.fromJson(data2, User.class);

                if (clientController.getUser().getId() != user.getId()) {
                    int row = seat.getRow() - 'A';
                    int seatNumber = seat.getSeatNumber() - 1;

                    setSeatIcon(seatLabels[row][seatNumber], Seat.AVAILABLE);
                    clientController.getSeats()[row][seatNumber].setState(Seat.AVAILABLE);
                }
                break;
            }
            case RequestMessage.CONFIRM_RESERVATION: {
                String data1 = reqMsg.getData1();
                String data2 = reqMsg.getData2();

                Seat seat = gson.fromJson(data1, Seat.class);
                User user = gson.fromJson(data2, User.class);

                if (clientController.getUser().getId() != user.getId()) {
                    int row = seat.getRow() - 'A';
                    int seatNumber = seat.getSeatNumber() - 1;

                    setSeatIcon(seatLabels[row][seatNumber], Seat.RESERVED);
                    clientController.getSeats()[row][seatNumber].setState(Seat.RESERVED);
                }
            }
            break;
            case RequestMessage.CANCEL_RESERVATION: {
                String data1 = reqMsg.getData1();
                String data2 = reqMsg.getData2();

                Seat seat = gson.fromJson(data1, Seat.class);
                User user = gson.fromJson(data2, User.class);

                if (clientController.getUser().getId() != user.getId()) {
                    int row = seat.getRow() - 'A';
                    int seatNumber = seat.getSeatNumber() - 1;

                    setSeatIcon(seatLabels[row][seatNumber], Seat.AVAILABLE);
                    clientController.getSeats()[row][seatNumber].setState(Seat.AVAILABLE);
                }
            }
            break;
        }
    }

    private void setSeatIcon(javax.swing.JLabel seatLabel, int state) {
        switch (state) {
            case Seat.AVAILABLE:
                seatLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif")));
                break;
            case Seat.PRE_RESERVED:
                seatLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_PreReserved_0.gif")));
                break;
            case Seat.RESERVED:
                seatLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Reserved_0.gif")));
                break;
            case Seat.SELECTED:
                seatLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Selected_0.gif")));
        }
    }

    private void initInterface() {
        fillSeatMatrix();
        initSeatIcons();
    }

    private void initSeatIcons() {
        Seat seats[][] = clientController.getSeats();

        for (int row = 0; row < 10; row++) {
            for (int num = 0; num < 10; num++) {
                Seat seat = seats[row][num];

                if (seat.getState() != Seat.AVAILABLE) {
                    setSeatIcon(seatLabels[row][num], seat.getState());
                }
            }
        }
    }

    private void preReserveSeat(int row, int numseat) {
        Seat seat = clientController.getSeats()[row][numseat];
        JLabel seatLabel = seatLabels[row][numseat];

        if (seat.getState() == Seat.SELECTED) {
            cancel_preReserveSeat(seat, seatLabel);
        } else if (seat.getState() == Seat.AVAILABLE && !hasReachedLimitSeats() && clientController.preReserveSeat(seat)) {
            stack.push(seat);
            stack.push(seatLabel);
            seat.setState(Seat.SELECTED);
            setSeatIcon(seatLabel, Seat.SELECTED);
            if (isFirstPreReservedSeat) {
                showRemainingTime();
            }
            isFirstPreReservedSeat = false;
        } else {
            if(!hasReachedLimitSeats())
                JOptionPane.showMessageDialog(this, "Este asiento ya fue reservado por otra persona, elija otro.");
            else
                JOptionPane.showMessageDialog(this, "Solo puede reservar hasta 5 asientos por sesión");
        }
    }

    private void cancel_preReserveSeat(Seat seat, JLabel seatLabel) {
        if (clientController.cancel_preReserveSeat(seat)) {
            seat.setState(Seat.AVAILABLE);
            setSeatIcon(seatLabel, Seat.AVAILABLE);
            stack.remove(seat);
            stack.remove(seatLabel);
        }
    }

    private void fillSeatMatrix() {
        seatLabels = new javax.swing.JLabel[10][10];

        seatLabels[0][0] = seatA1;
        seatLabels[0][1] = seatA2;
        seatLabels[0][2] = seatA3;
        seatLabels[0][3] = seatA4;
        seatLabels[0][4] = seatA5;
        seatLabels[0][5] = seatA6;
        seatLabels[0][6] = seatA7;
        seatLabels[0][7] = seatA8;
        seatLabels[0][8] = seatA9;
        seatLabels[0][9] = seatA10;

        seatLabels[1][0] = seatB1;
        seatLabels[1][1] = seatB2;
        seatLabels[1][2] = seatB3;
        seatLabels[1][3] = seatB4;
        seatLabels[1][4] = seatB5;
        seatLabels[1][5] = seatB6;
        seatLabels[1][6] = seatB7;
        seatLabels[1][7] = seatB8;
        seatLabels[1][8] = seatB9;
        seatLabels[1][9] = seatB10;

        seatLabels[2][0] = seatC1;
        seatLabels[2][1] = seatC2;
        seatLabels[2][2] = seatC3;
        seatLabels[2][3] = seatC4;
        seatLabels[2][4] = seatC5;
        seatLabels[2][5] = seatC6;
        seatLabels[2][6] = seatC7;
        seatLabels[2][7] = seatC8;
        seatLabels[2][8] = seatC9;
        seatLabels[2][9] = seatC10;

        seatLabels[3][0] = seatD1;
        seatLabels[3][1] = seatD2;
        seatLabels[3][2] = seatD3;
        seatLabels[3][3] = seatD4;
        seatLabels[3][4] = seatD5;
        seatLabels[3][5] = seatD6;
        seatLabels[3][6] = seatD7;
        seatLabels[3][7] = seatD8;
        seatLabels[3][8] = seatD9;
        seatLabels[3][9] = seatD10;

        seatLabels[4][0] = seatE1;
        seatLabels[4][1] = seatE2;
        seatLabels[4][2] = seatE3;
        seatLabels[4][3] = seatE4;
        seatLabels[4][4] = seatE5;
        seatLabels[4][5] = seatE6;
        seatLabels[4][6] = seatE7;
        seatLabels[4][7] = seatE8;
        seatLabels[4][8] = seatE9;
        seatLabels[4][9] = seatE10;


        seatLabels[5][0] = seatF1;
        seatLabels[5][1] = seatF2;
        seatLabels[5][2] = seatF3;
        seatLabels[5][3] = seatF4;
        seatLabels[5][4] = seatF5;
        seatLabels[5][5] = seatF6;
        seatLabels[5][6] = seatF7;
        seatLabels[5][7] = seatF8;
        seatLabels[5][8] = seatF9;
        seatLabels[5][9] = seatF10;

        seatLabels[6][0] = seatG1;
        seatLabels[6][1] = seatG2;
        seatLabels[6][2] = seatG3;
        seatLabels[6][3] = seatG4;
        seatLabels[6][4] = seatG5;
        seatLabels[6][5] = seatG6;
        seatLabels[6][6] = seatG7;
        seatLabels[6][7] = seatG8;
        seatLabels[6][8] = seatG9;
        seatLabels[6][9] = seatG10;

        seatLabels[7][0] = seatH1;
        seatLabels[7][1] = seatH2;
        seatLabels[7][2] = seatH3;
        seatLabels[7][3] = seatH4;
        seatLabels[7][4] = seatH5;
        seatLabels[7][5] = seatH6;
        seatLabels[7][6] = seatH7;
        seatLabels[7][7] = seatH8;
        seatLabels[7][8] = seatH9;
        seatLabels[7][9] = seatH10;

        seatLabels[8][0] = seatI1;
        seatLabels[8][1] = seatI2;
        seatLabels[8][2] = seatI3;
        seatLabels[8][3] = seatI4;
        seatLabels[8][4] = seatI5;
        seatLabels[8][5] = seatI6;
        seatLabels[8][6] = seatI7;
        seatLabels[8][7] = seatI8;
        seatLabels[8][8] = seatI9;
        seatLabels[8][9] = seatI10;

        seatLabels[9][0] = seatJ1;
        seatLabels[9][1] = seatJ2;
        seatLabels[9][2] = seatJ3;
        seatLabels[9][3] = seatJ4;
        seatLabels[9][4] = seatJ5;
        seatLabels[9][5] = seatJ6;
        seatLabels[9][6] = seatJ7;
        seatLabels[9][7] = seatJ8;
        seatLabels[9][8] = seatJ9;
        seatLabels[9][9] = seatJ10;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        panelSeats = new javax.swing.JPanel();
        seatD1 = new javax.swing.JLabel();
        seatD2 = new javax.swing.JLabel();
        seatC9 = new javax.swing.JLabel();
        seatC10 = new javax.swing.JLabel();
        seatD5 = new javax.swing.JLabel();
        seatD3 = new javax.swing.JLabel();
        seatD4 = new javax.swing.JLabel();
        seatD9 = new javax.swing.JLabel();
        seatD8 = new javax.swing.JLabel();
        seatD7 = new javax.swing.JLabel();
        seatD6 = new javax.swing.JLabel();
        seatB8 = new javax.swing.JLabel();
        seatB9 = new javax.swing.JLabel();
        seatB10 = new javax.swing.JLabel();
        seatC1 = new javax.swing.JLabel();
        seatC2 = new javax.swing.JLabel();
        seatC3 = new javax.swing.JLabel();
        seatC4 = new javax.swing.JLabel();
        seatC5 = new javax.swing.JLabel();
        seatA1 = new javax.swing.JLabel();
        seatC6 = new javax.swing.JLabel();
        seatA3 = new javax.swing.JLabel();
        seatA2 = new javax.swing.JLabel();
        seatC8 = new javax.swing.JLabel();
        seatC7 = new javax.swing.JLabel();
        seatA5 = new javax.swing.JLabel();
        seatA4 = new javax.swing.JLabel();
        seatB5 = new javax.swing.JLabel();
        seatB3 = new javax.swing.JLabel();
        seatB4 = new javax.swing.JLabel();
        seatB1 = new javax.swing.JLabel();
        seatB2 = new javax.swing.JLabel();
        seatA9 = new javax.swing.JLabel();
        seatA10 = new javax.swing.JLabel();
        seatA7 = new javax.swing.JLabel();
        seatA8 = new javax.swing.JLabel();
        seatB7 = new javax.swing.JLabel();
        seatB6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        seatH10 = new javax.swing.JLabel();
        seatH6 = new javax.swing.JLabel();
        seatH1 = new javax.swing.JLabel();
        seatH2 = new javax.swing.JLabel();
        seatH3 = new javax.swing.JLabel();
        seatH4 = new javax.swing.JLabel();
        seatH5 = new javax.swing.JLabel();
        seatH7 = new javax.swing.JLabel();
        seatH8 = new javax.swing.JLabel();
        seatH9 = new javax.swing.JLabel();
        seatI10 = new javax.swing.JLabel();
        seatJ10 = new javax.swing.JLabel();
        seatJ9 = new javax.swing.JLabel();
        seatJ6 = new javax.swing.JLabel();
        seatJ8 = new javax.swing.JLabel();
        seatJ7 = new javax.swing.JLabel();
        seatJ5 = new javax.swing.JLabel();
        seatJ4 = new javax.swing.JLabel();
        seatJ3 = new javax.swing.JLabel();
        seatJ2 = new javax.swing.JLabel();
        seatJ1 = new javax.swing.JLabel();
        seatI9 = new javax.swing.JLabel();
        seatI8 = new javax.swing.JLabel();
        seatI7 = new javax.swing.JLabel();
        seatI6 = new javax.swing.JLabel();
        seatA6 = new javax.swing.JLabel();
        seatI4 = new javax.swing.JLabel();
        seatI5 = new javax.swing.JLabel();
        seatI2 = new javax.swing.JLabel();
        seatI3 = new javax.swing.JLabel();
        seatI1 = new javax.swing.JLabel();
        seatG3 = new javax.swing.JLabel();
        seatG2 = new javax.swing.JLabel();
        seatG5 = new javax.swing.JLabel();
        seatG4 = new javax.swing.JLabel();
        seatG6 = new javax.swing.JLabel();
        seatG7 = new javax.swing.JLabel();
        seatG8 = new javax.swing.JLabel();
        seatG9 = new javax.swing.JLabel();
        seatG10 = new javax.swing.JLabel();
        seatF5 = new javax.swing.JLabel();
        seatF4 = new javax.swing.JLabel();
        seatF3 = new javax.swing.JLabel();
        seatF2 = new javax.swing.JLabel();
        seatF1 = new javax.swing.JLabel();
        seatF10 = new javax.swing.JLabel();
        seatG1 = new javax.swing.JLabel();
        seatF8 = new javax.swing.JLabel();
        seatF9 = new javax.swing.JLabel();
        seatF6 = new javax.swing.JLabel();
        seatF7 = new javax.swing.JLabel();
        seatE5 = new javax.swing.JLabel();
        seatE4 = new javax.swing.JLabel();
        seatE1 = new javax.swing.JLabel();
        seatD10 = new javax.swing.JLabel();
        seatE3 = new javax.swing.JLabel();
        seatE2 = new javax.swing.JLabel();
        seatE7 = new javax.swing.JLabel();
        seatE8 = new javax.swing.JLabel();
        seatE9 = new javax.swing.JLabel();
        seatE10 = new javax.swing.JLabel();
        seatE6 = new javax.swing.JLabel();
        btn_confirm = new javax.swing.JButton();
        btn_cancel = new javax.swing.JButton();
        lblRemainingTime = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        panelSeats.setBackground(new java.awt.Color(255, 255, 255));
        panelSeats.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        seatD1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatD1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatD1MousePressed(evt);
            }
        });

        seatD2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatD2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatD2MousePressed(evt);
            }
        });

        seatC9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatC9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatC9MousePressed(evt);
            }
        });

        seatC10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatC10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatC10MousePressed(evt);
            }
        });

        seatD5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatD5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatD5MousePressed(evt);
            }
        });

        seatD3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatD3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatD3MousePressed(evt);
            }
        });

        seatD4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatD4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatD4MousePressed(evt);
            }
        });

        seatD9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatD9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatD9MousePressed(evt);
            }
        });

        seatD8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatD8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatD8MousePressed(evt);
            }
        });

        seatD7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatD7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatD7MousePressed(evt);
            }
        });

        seatD6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatD6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatD6MousePressed(evt);
            }
        });

        seatB8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatB8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatB8MousePressed(evt);
            }
        });

        seatB9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatB9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatB9MousePressed(evt);
            }
        });

        seatB10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatB10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatB10MousePressed(evt);
            }
        });

        seatC1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatC1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatC1MousePressed(evt);
            }
        });

        seatC2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatC2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatC2MousePressed(evt);
            }
        });

        seatC3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatC3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatC3MousePressed(evt);
            }
        });

        seatC4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatC4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatC4MousePressed(evt);
            }
        });

        seatC5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatC5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatC5MousePressed(evt);
            }
        });

        seatA1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatA1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatA1MousePressed(evt);
            }
        });

        seatC6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatC6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatC6MousePressed(evt);
            }
        });

        seatA3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatA3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatA3MousePressed(evt);
            }
        });

        seatA2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatA2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatA2MousePressed(evt);
            }
        });

        seatC8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatC8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatC8MousePressed(evt);
            }
        });

        seatC7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatC7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatC7MousePressed(evt);
            }
        });

        seatA5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatA5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatA5MousePressed(evt);
            }
        });

        seatA4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatA4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatA4MousePressed(evt);
            }
        });

        seatB5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatB5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatB5MousePressed(evt);
            }
        });

        seatB3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatB3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatB3MousePressed(evt);
            }
        });

        seatB4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatB4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatB4MousePressed(evt);
            }
        });

        seatB1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatB1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatB1MousePressed(evt);
            }
        });

        seatB2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatB2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatB2MousePressed(evt);
            }
        });

        seatA9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatA9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatA9MousePressed(evt);
            }
        });

        seatA10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatA10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatA10MousePressed(evt);
            }
        });

        seatA7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatA7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatA7MousePressed(evt);
            }
        });

        seatA8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatA8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatA8MousePressed(evt);
            }
        });

        seatB7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatB7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatB7MousePressed(evt);
            }
        });

        seatB6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatB6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatB6MousePressed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        seatH10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatH10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatH10MousePressed(evt);
            }
        });

        seatH6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatH6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatH6MousePressed(evt);
            }
        });

        seatH1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatH1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatH1MousePressed(evt);
            }
        });

        seatH2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatH2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatH2MousePressed(evt);
            }
        });

        seatH3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatH3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatH3MousePressed(evt);
            }
        });

        seatH4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatH4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatH4MousePressed(evt);
            }
        });

        seatH5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatH5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatH5MousePressed(evt);
            }
        });

        seatH7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatH7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatH7MousePressed(evt);
            }
        });

        seatH8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatH8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatH8MousePressed(evt);
            }
        });

        seatH9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatH9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatH9MousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(seatH1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seatH2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seatH3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seatH4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seatH5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seatH6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seatH7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seatH8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seatH9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seatH10))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(seatH10)
            .addComponent(seatH6)
            .addComponent(seatH1)
            .addComponent(seatH2)
            .addComponent(seatH3)
            .addComponent(seatH4)
            .addComponent(seatH5)
            .addComponent(seatH7)
            .addComponent(seatH8)
            .addComponent(seatH9)
        );

        seatI10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatI10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatI10MousePressed(evt);
            }
        });

        seatJ10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatJ10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatJ10MousePressed(evt);
            }
        });

        seatJ9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatJ9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatJ9MousePressed(evt);
            }
        });

        seatJ6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatJ6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatJ6MousePressed(evt);
            }
        });

        seatJ8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatJ8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatJ8MousePressed(evt);
            }
        });

        seatJ7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatJ7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatJ7MousePressed(evt);
            }
        });

        seatJ5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatJ5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatJ5MousePressed(evt);
            }
        });

        seatJ4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatJ4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatJ4MousePressed(evt);
            }
        });

        seatJ3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatJ3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatJ3MousePressed(evt);
            }
        });

        seatJ2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatJ2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatJ2MousePressed(evt);
            }
        });

        seatJ1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatJ1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatJ1MousePressed(evt);
            }
        });

        seatI9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatI9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatI9MousePressed(evt);
            }
        });

        seatI8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatI8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatI8MousePressed(evt);
            }
        });

        seatI7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatI7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatI7MousePressed(evt);
            }
        });

        seatI6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatI6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatI6MousePressed(evt);
            }
        });

        seatA6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatA6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatA6MousePressed(evt);
            }
        });

        seatI4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatI4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatI4MousePressed(evt);
            }
        });

        seatI5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatI5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatI5MousePressed(evt);
            }
        });

        seatI2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatI2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatI2MousePressed(evt);
            }
        });

        seatI3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatI3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatI3MousePressed(evt);
            }
        });

        seatI1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatI1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatI1MousePressed(evt);
            }
        });

        seatG3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatG3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatG3MousePressed(evt);
            }
        });

        seatG2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatG2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatG2MousePressed(evt);
            }
        });

        seatG5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatG5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatG5MousePressed(evt);
            }
        });

        seatG4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatG4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatG4MousePressed(evt);
            }
        });

        seatG6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatG6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatG6MousePressed(evt);
            }
        });

        seatG7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatG7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatG7MousePressed(evt);
            }
        });

        seatG8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatG8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatG8MousePressed(evt);
            }
        });

        seatG9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatG9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatG9MousePressed(evt);
            }
        });

        seatG10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatG10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatG10MousePressed(evt);
            }
        });

        seatF5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatF5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatF5MousePressed(evt);
            }
        });

        seatF4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatF4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatF4MousePressed(evt);
            }
        });

        seatF3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatF3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatF3MousePressed(evt);
            }
        });

        seatF2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatF2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatF2MousePressed(evt);
            }
        });

        seatF1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatF1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatF1MousePressed(evt);
            }
        });

        seatF10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatF10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatF10MousePressed(evt);
            }
        });

        seatG1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatG1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatG1MousePressed(evt);
            }
        });

        seatF8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatF8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatF8MousePressed(evt);
            }
        });

        seatF9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatF9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatF9MousePressed(evt);
            }
        });

        seatF6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatF6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatF6MousePressed(evt);
            }
        });

        seatF7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatF7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatF7MousePressed(evt);
            }
        });

        seatE5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatE5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatE5MousePressed(evt);
            }
        });

        seatE4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatE4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatE4MousePressed(evt);
            }
        });

        seatE1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatE1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatE1MousePressed(evt);
            }
        });

        seatD10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatD10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatD10MousePressed(evt);
            }
        });

        seatE3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatE3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatE3MousePressed(evt);
            }
        });

        seatE2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatE2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatE2MousePressed(evt);
            }
        });

        seatE7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatE7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatE7MousePressed(evt);
            }
        });

        seatE8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatE8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatE8MousePressed(evt);
            }
        });

        seatE9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatE9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatE9MousePressed(evt);
            }
        });

        seatE10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatE10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatE10MousePressed(evt);
            }
        });

        seatE6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SeatIcon_Availabe_0.gif"))); // NOI18N
        seatE6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                seatE6MousePressed(evt);
            }
        });

        javax.swing.GroupLayout panelSeatsLayout = new javax.swing.GroupLayout(panelSeats);
        panelSeats.setLayout(panelSeatsLayout);
        panelSeatsLayout.setHorizontalGroup(
            panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSeatsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelSeatsLayout.createSequentialGroup()
                            .addComponent(seatA1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatA2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatA3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatA4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatA5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatA6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatA7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatA8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatA9)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatA10))
                        .addGroup(panelSeatsLayout.createSequentialGroup()
                            .addComponent(seatB1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatB2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatB3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatB4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatB5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatB6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatB7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatB8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatB9)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatB10))
                        .addGroup(panelSeatsLayout.createSequentialGroup()
                            .addComponent(seatC1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatC2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatC3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatC4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatC5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatC6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatC7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatC8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatC9)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatC10))
                        .addGroup(panelSeatsLayout.createSequentialGroup()
                            .addComponent(seatD1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatD2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatD3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatD4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatD5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatD6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatD7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatD8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatD9)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(seatD10))
                        .addGroup(panelSeatsLayout.createSequentialGroup()
                            .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(panelSeatsLayout.createSequentialGroup()
                                    .addComponent(seatE1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatE2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatE3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatE4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatE5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatE6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatE7))
                                .addGroup(panelSeatsLayout.createSequentialGroup()
                                    .addComponent(seatF1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatF2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatF3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatF4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatF5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatF6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatF7))
                                .addGroup(panelSeatsLayout.createSequentialGroup()
                                    .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelSeatsLayout.createSequentialGroup()
                                            .addComponent(seatG1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(seatG2)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(seatG3)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(seatG4)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(seatG5)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(seatG6))
                                        .addGroup(panelSeatsLayout.createSequentialGroup()
                                            .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(seatI1)
                                                .addComponent(seatJ1))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(panelSeatsLayout.createSequentialGroup()
                                                    .addComponent(seatI2)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(seatI3)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(seatI4)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(seatI5)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(seatI6))
                                                .addGroup(panelSeatsLayout.createSequentialGroup()
                                                    .addComponent(seatJ2)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(seatJ3)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(seatJ4)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(seatJ5)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(seatJ6)))))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(seatG7)
                                        .addComponent(seatI7)
                                        .addComponent(seatJ7))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(panelSeatsLayout.createSequentialGroup()
                                    .addComponent(seatF8)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatF9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatF10))
                                .addGroup(panelSeatsLayout.createSequentialGroup()
                                    .addComponent(seatE8)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatE9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatE10))
                                .addGroup(panelSeatsLayout.createSequentialGroup()
                                    .addComponent(seatI8)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatI9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatI10))
                                .addGroup(panelSeatsLayout.createSequentialGroup()
                                    .addComponent(seatG8)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatG9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatG10))
                                .addGroup(panelSeatsLayout.createSequentialGroup()
                                    .addComponent(seatJ8)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatJ9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(seatJ10))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelSeatsLayout.setVerticalGroup(
            panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSeatsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(seatA3)
                    .addComponent(seatA4)
                    .addComponent(seatA5)
                    .addComponent(seatA6)
                    .addGroup(panelSeatsLayout.createSequentialGroup()
                        .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(seatA1)
                            .addComponent(seatA9)
                            .addComponent(seatA2)
                            .addComponent(seatA7)
                            .addComponent(seatA8)
                            .addComponent(seatA10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(seatB1)
                            .addComponent(seatB9)
                            .addComponent(seatB2)
                            .addComponent(seatB3)
                            .addComponent(seatB4)
                            .addComponent(seatB5)
                            .addComponent(seatB6)
                            .addComponent(seatB7)
                            .addComponent(seatB8)
                            .addComponent(seatB10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelSeatsLayout.createSequentialGroup()
                                .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(seatC8)
                                    .addComponent(seatC10))
                                .addGap(11, 11, 11)
                                .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(seatD10)
                                    .addComponent(seatD9)
                                    .addComponent(seatD8)))
                            .addGroup(panelSeatsLayout.createSequentialGroup()
                                .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(seatC1)
                                    .addComponent(seatC9)
                                    .addComponent(seatC2)
                                    .addComponent(seatC3)
                                    .addComponent(seatC4)
                                    .addComponent(seatC5)
                                    .addComponent(seatC6)
                                    .addComponent(seatC7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(seatD7)
                                    .addComponent(seatD6)
                                    .addComponent(seatD5)
                                    .addComponent(seatD4)
                                    .addComponent(seatD3)
                                    .addComponent(seatD2)
                                    .addGroup(panelSeatsLayout.createSequentialGroup()
                                        .addComponent(seatD1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(seatE2)
                                            .addComponent(seatE1)
                                            .addComponent(seatE3)
                                            .addComponent(seatE4)
                                            .addComponent(seatE5)
                                            .addComponent(seatE6)
                                            .addComponent(seatE7)
                                            .addComponent(seatE8)
                                            .addComponent(seatE9)
                                            .addComponent(seatE10))))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSeatsLayout.createSequentialGroup()
                        .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(seatF1)
                            .addComponent(seatF6)
                            .addComponent(seatF2)
                            .addComponent(seatF10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(seatG2)
                            .addComponent(seatG1)))
                    .addGroup(panelSeatsLayout.createSequentialGroup()
                        .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(seatF3)
                            .addComponent(seatF4)
                            .addComponent(seatF5)
                            .addComponent(seatF7)
                            .addComponent(seatF8)
                            .addComponent(seatF9))
                        .addGap(11, 11, 11)
                        .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(seatG9)
                            .addComponent(seatG8)
                            .addComponent(seatG7)
                            .addComponent(seatG5)
                            .addComponent(seatG4)
                            .addComponent(seatG3)
                            .addComponent(seatG6)
                            .addComponent(seatG10))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(seatI2)
                    .addComponent(seatI3)
                    .addComponent(seatI4)
                    .addComponent(seatI5)
                    .addComponent(seatI7)
                    .addComponent(seatI8)
                    .addComponent(seatI9)
                    .addComponent(seatI10)
                    .addComponent(seatI1)
                    .addComponent(seatI6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSeatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(seatJ6)
                    .addComponent(seatJ1)
                    .addComponent(seatJ2)
                    .addComponent(seatJ3)
                    .addComponent(seatJ4)
                    .addComponent(seatJ5)
                    .addComponent(seatJ7)
                    .addComponent(seatJ8)
                    .addComponent(seatJ9)
                    .addComponent(seatJ10))
                .addContainerGap())
        );

        btn_confirm.setText("Confirmar");
        btn_confirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_confirmActionPerformed(evt);
            }
        });

        btn_cancel.setText("Cancelar");
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });

        lblRemainingTime.setText(" ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(btn_confirm)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_cancel)
                .addGap(48, 48, 48))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRemainingTime)
                    .addComponent(panelSeats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblRemainingTime)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelSeats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_confirm)
                    .addComponent(btn_cancel))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void seatA2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatA2MousePressed
        preReserveSeat(0, 1);
    }//GEN-LAST:event_seatA2MousePressed

    private void seatA3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatA3MousePressed
        preReserveSeat(0, 2);
    }//GEN-LAST:event_seatA3MousePressed

    private void seatA4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatA4MousePressed
        preReserveSeat(0, 3);
    }//GEN-LAST:event_seatA4MousePressed

    private void seatA5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatA5MousePressed
        preReserveSeat(0, 4);
    }//GEN-LAST:event_seatA5MousePressed

    private void seatA6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatA6MousePressed
        preReserveSeat(0, 5);
    }//GEN-LAST:event_seatA6MousePressed

    private void seatA7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatA7MousePressed
        preReserveSeat(0, 6);
    }//GEN-LAST:event_seatA7MousePressed

    private void seatA8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatA8MousePressed
        preReserveSeat(0, 7);
    }//GEN-LAST:event_seatA8MousePressed

    private void seatA9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatA9MousePressed
        preReserveSeat(0, 8);
    }//GEN-LAST:event_seatA9MousePressed

    private void seatA10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatA10MousePressed
        preReserveSeat(0, 9);
    }//GEN-LAST:event_seatA10MousePressed

    private void seatB1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatB1MousePressed
        preReserveSeat(1, 0);
    }//GEN-LAST:event_seatB1MousePressed

    private void seatB2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatB2MousePressed
        preReserveSeat(1, 1);
    }//GEN-LAST:event_seatB2MousePressed

    private void seatB3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatB3MousePressed
        preReserveSeat(1, 2);
    }//GEN-LAST:event_seatB3MousePressed

    private void seatB4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatB4MousePressed
        preReserveSeat(1, 3);
    }//GEN-LAST:event_seatB4MousePressed

    private void seatB5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatB5MousePressed
        preReserveSeat(1, 4);
    }//GEN-LAST:event_seatB5MousePressed

    private void seatB6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatB6MousePressed
        preReserveSeat(1, 5);
    }//GEN-LAST:event_seatB6MousePressed

    private void seatB7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatB7MousePressed
        preReserveSeat(1, 6);
    }//GEN-LAST:event_seatB7MousePressed

    private void seatB8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatB8MousePressed
        preReserveSeat(1, 7);
    }//GEN-LAST:event_seatB8MousePressed

    private void seatB9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatB9MousePressed
        preReserveSeat(1, 8);
    }//GEN-LAST:event_seatB9MousePressed

    private void seatB10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatB10MousePressed
        preReserveSeat(1, 9);
    }//GEN-LAST:event_seatB10MousePressed

    private void seatC1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatC1MousePressed
        preReserveSeat(2, 0);
    }//GEN-LAST:event_seatC1MousePressed

    private void seatC2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatC2MousePressed
        preReserveSeat(2, 1);
    }//GEN-LAST:event_seatC2MousePressed

    private void seatC3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatC3MousePressed
        preReserveSeat(2, 2);
    }//GEN-LAST:event_seatC3MousePressed

    private void seatC4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatC4MousePressed
        preReserveSeat(2, 3);
    }//GEN-LAST:event_seatC4MousePressed

    private void seatC5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatC5MousePressed
        preReserveSeat(2, 4);
    }//GEN-LAST:event_seatC5MousePressed

    private void seatC6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatC6MousePressed
        preReserveSeat(2, 5);
    }//GEN-LAST:event_seatC6MousePressed

    private void seatC7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatC7MousePressed
        preReserveSeat(2, 6);
    }//GEN-LAST:event_seatC7MousePressed

    private void seatC8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatC8MousePressed
        preReserveSeat(2, 7);
    }//GEN-LAST:event_seatC8MousePressed

    private void seatC9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatC9MousePressed
        preReserveSeat(2, 8);
    }//GEN-LAST:event_seatC9MousePressed

    private void seatC10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatC10MousePressed
        preReserveSeat(2, 9);
    }//GEN-LAST:event_seatC10MousePressed

    private void seatD1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatD1MousePressed
        preReserveSeat(3, 0);
    }//GEN-LAST:event_seatD1MousePressed

    private void seatD2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatD2MousePressed
        preReserveSeat(3, 1);
    }//GEN-LAST:event_seatD2MousePressed

    private void seatD3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatD3MousePressed
        preReserveSeat(3, 2);
    }//GEN-LAST:event_seatD3MousePressed

    private void seatD4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatD4MousePressed
        preReserveSeat(3, 3);
    }//GEN-LAST:event_seatD4MousePressed

    private void seatD5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatD5MousePressed
        preReserveSeat(3, 4);
    }//GEN-LAST:event_seatD5MousePressed

    private void seatD6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatD6MousePressed
        preReserveSeat(3, 5);
    }//GEN-LAST:event_seatD6MousePressed

    private void seatD7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatD7MousePressed
        preReserveSeat(3, 6);
    }//GEN-LAST:event_seatD7MousePressed

    private void seatD8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatD8MousePressed
        preReserveSeat(3, 7);
    }//GEN-LAST:event_seatD8MousePressed

    private void seatD9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatD9MousePressed
        preReserveSeat(3, 8);
    }//GEN-LAST:event_seatD9MousePressed

    private void seatD10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatD10MousePressed
        preReserveSeat(3, 9);
    }//GEN-LAST:event_seatD10MousePressed

    private void seatE1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatE1MousePressed
        preReserveSeat(4, 0);
    }//GEN-LAST:event_seatE1MousePressed

    private void seatE2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatE2MousePressed
        preReserveSeat(4, 1);
    }//GEN-LAST:event_seatE2MousePressed

    private void seatE3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatE3MousePressed
        preReserveSeat(4, 2);
    }//GEN-LAST:event_seatE3MousePressed

    private void seatE4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatE4MousePressed
        preReserveSeat(4, 3);
    }//GEN-LAST:event_seatE4MousePressed

    private void seatE5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatE5MousePressed
        preReserveSeat(4, 4);
    }//GEN-LAST:event_seatE5MousePressed

    private void seatE6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatE6MousePressed
        preReserveSeat(4, 5);
    }//GEN-LAST:event_seatE6MousePressed

    private void seatE7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatE7MousePressed
        preReserveSeat(4, 6);
    }//GEN-LAST:event_seatE7MousePressed

    private void seatE8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatE8MousePressed
        preReserveSeat(4, 7);
    }//GEN-LAST:event_seatE8MousePressed

    private void seatE9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatE9MousePressed
        preReserveSeat(4, 8);
    }//GEN-LAST:event_seatE9MousePressed

    private void seatE10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatE10MousePressed
        preReserveSeat(4, 9);
    }//GEN-LAST:event_seatE10MousePressed

    private void seatF1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatF1MousePressed
        preReserveSeat(5, 0);
    }//GEN-LAST:event_seatF1MousePressed

    private void seatF2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatF2MousePressed
        preReserveSeat(5, 1);
    }//GEN-LAST:event_seatF2MousePressed

    private void seatF3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatF3MousePressed
        preReserveSeat(5, 2);
    }//GEN-LAST:event_seatF3MousePressed

    private void seatF4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatF4MousePressed
        preReserveSeat(5, 3);
    }//GEN-LAST:event_seatF4MousePressed

    private void seatF5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatF5MousePressed
        preReserveSeat(5, 4);
    }//GEN-LAST:event_seatF5MousePressed

    private void seatF6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatF6MousePressed
        preReserveSeat(5, 5);
    }//GEN-LAST:event_seatF6MousePressed

    private void seatF7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatF7MousePressed
        preReserveSeat(5, 6);
    }//GEN-LAST:event_seatF7MousePressed

    private void seatF8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatF8MousePressed
        preReserveSeat(5, 7);
    }//GEN-LAST:event_seatF8MousePressed

    private void seatF9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatF9MousePressed
        preReserveSeat(5, 8);
    }//GEN-LAST:event_seatF9MousePressed

    private void seatF10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatF10MousePressed
        preReserveSeat(5, 9);
    }//GEN-LAST:event_seatF10MousePressed

    private void seatG1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatG1MousePressed
        preReserveSeat(6, 0);
    }//GEN-LAST:event_seatG1MousePressed

    private void seatG2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatG2MousePressed
        preReserveSeat(6, 1);
    }//GEN-LAST:event_seatG2MousePressed

    private void seatG3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatG3MousePressed
        preReserveSeat(6, 2);
    }//GEN-LAST:event_seatG3MousePressed

    private void seatG4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatG4MousePressed
        preReserveSeat(6, 3);
    }//GEN-LAST:event_seatG4MousePressed

    private void seatG5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatG5MousePressed
        preReserveSeat(6, 4);
    }//GEN-LAST:event_seatG5MousePressed

    private void seatG6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatG6MousePressed
        preReserveSeat(6, 5);
    }//GEN-LAST:event_seatG6MousePressed

    private void seatG7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatG7MousePressed
        preReserveSeat(6, 6);
    }//GEN-LAST:event_seatG7MousePressed

    private void seatG8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatG8MousePressed
        preReserveSeat(6, 7);
    }//GEN-LAST:event_seatG8MousePressed

    private void seatG9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatG9MousePressed
        preReserveSeat(6, 8);
    }//GEN-LAST:event_seatG9MousePressed

    private void seatG10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatG10MousePressed
        preReserveSeat(6, 9);
    }//GEN-LAST:event_seatG10MousePressed

    private void seatH1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatH1MousePressed
        preReserveSeat(7, 0);
    }//GEN-LAST:event_seatH1MousePressed

    private void seatH2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatH2MousePressed
        preReserveSeat(7, 1);
    }//GEN-LAST:event_seatH2MousePressed

    private void seatH3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatH3MousePressed
        preReserveSeat(7, 2);
    }//GEN-LAST:event_seatH3MousePressed

    private void seatH4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatH4MousePressed
        preReserveSeat(7, 3);
    }//GEN-LAST:event_seatH4MousePressed

    private void seatH5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatH5MousePressed
        preReserveSeat(7, 4);
    }//GEN-LAST:event_seatH5MousePressed

    private void seatH7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatH7MousePressed
        preReserveSeat(7, 6);
    }//GEN-LAST:event_seatH7MousePressed

    private void seatH8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatH8MousePressed
        preReserveSeat(7, 7);
    }//GEN-LAST:event_seatH8MousePressed

    private void seatH9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatH9MousePressed
        preReserveSeat(7, 8);
    }//GEN-LAST:event_seatH9MousePressed

    private void seatH10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatH10MousePressed
        preReserveSeat(7, 9);
    }//GEN-LAST:event_seatH10MousePressed

    private void seatI1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatI1MousePressed
        preReserveSeat(8, 0);
    }//GEN-LAST:event_seatI1MousePressed

    private void seatI2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatI2MousePressed
        preReserveSeat(8, 1);
    }//GEN-LAST:event_seatI2MousePressed

    private void seatI3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatI3MousePressed
        preReserveSeat(8, 2);
    }//GEN-LAST:event_seatI3MousePressed

    private void seatI4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatI4MousePressed
        preReserveSeat(8, 3);
    }//GEN-LAST:event_seatI4MousePressed

    private void seatI5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatI5MousePressed
        preReserveSeat(8, 4);
    }//GEN-LAST:event_seatI5MousePressed

    private void seatI6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatI6MousePressed
        preReserveSeat(8, 5);
    }//GEN-LAST:event_seatI6MousePressed

    private void seatI7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatI7MousePressed
        preReserveSeat(8, 6);
    }//GEN-LAST:event_seatI7MousePressed

    private void seatI8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatI8MousePressed
        preReserveSeat(8, 7);
    }//GEN-LAST:event_seatI8MousePressed

    private void seatI9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatI9MousePressed
        preReserveSeat(8, 8);
    }//GEN-LAST:event_seatI9MousePressed

    private void seatJ1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatJ1MousePressed
        preReserveSeat(9, 0);
    }//GEN-LAST:event_seatJ1MousePressed

    private void seatJ2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatJ2MousePressed
        preReserveSeat(9, 1);
    }//GEN-LAST:event_seatJ2MousePressed

    private void seatJ3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatJ3MousePressed
        preReserveSeat(9, 2);
    }//GEN-LAST:event_seatJ3MousePressed

    private void seatJ4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatJ4MousePressed
        preReserveSeat(9, 3);
    }//GEN-LAST:event_seatJ4MousePressed

    private void seatJ5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatJ5MousePressed
        preReserveSeat(9, 4);
    }//GEN-LAST:event_seatJ5MousePressed

    private void seatJ6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatJ6MousePressed
        preReserveSeat(9, 5);
    }//GEN-LAST:event_seatJ6MousePressed

    private void seatJ7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatJ7MousePressed
        preReserveSeat(9, 6);
    }//GEN-LAST:event_seatJ7MousePressed

    private void seatJ8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatJ8MousePressed
        preReserveSeat(9, 7);
    }//GEN-LAST:event_seatJ8MousePressed

    private void seatJ9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatJ9MousePressed
        preReserveSeat(9, 8);
    }//GEN-LAST:event_seatJ9MousePressed

    private void seatJ10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatJ10MousePressed
        preReserveSeat(9, 9);
    }//GEN-LAST:event_seatJ10MousePressed

    private void seatI10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatI10MousePressed
        preReserveSeat(8, 9);
    }//GEN-LAST:event_seatI10MousePressed

    private void seatH6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatH6MousePressed
        preReserveSeat(7, 5);
    }//GEN-LAST:event_seatH6MousePressed

    private void seatA1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seatA1MousePressed
        preReserveSeat(0, 0);
    }//GEN-LAST:event_seatA1MousePressed

    private void btn_confirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_confirmActionPerformed
        if (clientController.confirmReservations()) {
            JOptionPane.showMessageDialog(this, "No. de reservación: " + clientController.getUser().getId(), "Asientos reservados", JOptionPane.INFORMATION_MESSAGE);
            new MainMenu().setVisible(true);
            dispose();
        }
    }//GEN-LAST:event_btn_confirmActionPerformed

    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        clientController.cancelReservations();
        new MainMenu().setVisible(true);
        dispose();
    }//GEN-LAST:event_btn_cancelActionPerformed
    /**
     * @param args the command line arguments
     */
    /*public static void main(String args[]) {
     /*
     * Set the Nimbus look and feel
     */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
     * If Nimbus (introduced in Java SE 6) is not available, stay with the
     * default look and feel. For details see
     * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
     */
    /*try {
     for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
     if ("Nimbus".equals(info.getName())) {
     javax.swing.UIManager.setLookAndFeel(info.getClassName());
     break;
     }
     }
     } catch (ClassNotFoundException ex) {
     java.util.logging.Logger.getLogger(ReservationWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
     } catch (InstantiationException ex) {
     java.util.logging.Logger.getLogger(ReservationWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
     } catch (IllegalAccessException ex) {
     java.util.logging.Logger.getLogger(ReservationWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
     } catch (javax.swing.UnsupportedLookAndFeelException ex) {
     java.util.logging.Logger.getLogger(ReservationWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
     }
     //</editor-fold>

     /*
     * Create and display the form
     */
    /*   java.awt.EventQueue.invokeLater(new Runnable() {

     @Override
     public void run() {
     new ReservationWindow().setVisible(true);
     }
     });
     }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_confirm;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblRemainingTime;
    private javax.swing.JPanel panelSeats;
    private javax.swing.JLabel seatA1;
    private javax.swing.JLabel seatA10;
    private javax.swing.JLabel seatA2;
    private javax.swing.JLabel seatA3;
    private javax.swing.JLabel seatA4;
    private javax.swing.JLabel seatA5;
    private javax.swing.JLabel seatA6;
    private javax.swing.JLabel seatA7;
    private javax.swing.JLabel seatA8;
    private javax.swing.JLabel seatA9;
    private javax.swing.JLabel seatB1;
    private javax.swing.JLabel seatB10;
    private javax.swing.JLabel seatB2;
    private javax.swing.JLabel seatB3;
    private javax.swing.JLabel seatB4;
    private javax.swing.JLabel seatB5;
    private javax.swing.JLabel seatB6;
    private javax.swing.JLabel seatB7;
    private javax.swing.JLabel seatB8;
    private javax.swing.JLabel seatB9;
    private javax.swing.JLabel seatC1;
    private javax.swing.JLabel seatC10;
    private javax.swing.JLabel seatC2;
    private javax.swing.JLabel seatC3;
    private javax.swing.JLabel seatC4;
    private javax.swing.JLabel seatC5;
    private javax.swing.JLabel seatC6;
    private javax.swing.JLabel seatC7;
    private javax.swing.JLabel seatC8;
    private javax.swing.JLabel seatC9;
    private javax.swing.JLabel seatD1;
    private javax.swing.JLabel seatD10;
    private javax.swing.JLabel seatD2;
    private javax.swing.JLabel seatD3;
    private javax.swing.JLabel seatD4;
    private javax.swing.JLabel seatD5;
    private javax.swing.JLabel seatD6;
    private javax.swing.JLabel seatD7;
    private javax.swing.JLabel seatD8;
    private javax.swing.JLabel seatD9;
    private javax.swing.JLabel seatE1;
    private javax.swing.JLabel seatE10;
    private javax.swing.JLabel seatE2;
    private javax.swing.JLabel seatE3;
    private javax.swing.JLabel seatE4;
    private javax.swing.JLabel seatE5;
    private javax.swing.JLabel seatE6;
    private javax.swing.JLabel seatE7;
    private javax.swing.JLabel seatE8;
    private javax.swing.JLabel seatE9;
    private javax.swing.JLabel seatF1;
    private javax.swing.JLabel seatF10;
    private javax.swing.JLabel seatF2;
    private javax.swing.JLabel seatF3;
    private javax.swing.JLabel seatF4;
    private javax.swing.JLabel seatF5;
    private javax.swing.JLabel seatF6;
    private javax.swing.JLabel seatF7;
    private javax.swing.JLabel seatF8;
    private javax.swing.JLabel seatF9;
    private javax.swing.JLabel seatG1;
    private javax.swing.JLabel seatG10;
    private javax.swing.JLabel seatG2;
    private javax.swing.JLabel seatG3;
    private javax.swing.JLabel seatG4;
    private javax.swing.JLabel seatG5;
    private javax.swing.JLabel seatG6;
    private javax.swing.JLabel seatG7;
    private javax.swing.JLabel seatG8;
    private javax.swing.JLabel seatG9;
    private javax.swing.JLabel seatH1;
    private javax.swing.JLabel seatH10;
    private javax.swing.JLabel seatH2;
    private javax.swing.JLabel seatH3;
    private javax.swing.JLabel seatH4;
    private javax.swing.JLabel seatH5;
    private javax.swing.JLabel seatH6;
    private javax.swing.JLabel seatH7;
    private javax.swing.JLabel seatH8;
    private javax.swing.JLabel seatH9;
    private javax.swing.JLabel seatI1;
    private javax.swing.JLabel seatI10;
    private javax.swing.JLabel seatI2;
    private javax.swing.JLabel seatI3;
    private javax.swing.JLabel seatI4;
    private javax.swing.JLabel seatI5;
    private javax.swing.JLabel seatI6;
    private javax.swing.JLabel seatI7;
    private javax.swing.JLabel seatI8;
    private javax.swing.JLabel seatI9;
    private javax.swing.JLabel seatJ1;
    private javax.swing.JLabel seatJ10;
    private javax.swing.JLabel seatJ2;
    private javax.swing.JLabel seatJ3;
    private javax.swing.JLabel seatJ4;
    private javax.swing.JLabel seatJ5;
    private javax.swing.JLabel seatJ6;
    private javax.swing.JLabel seatJ7;
    private javax.swing.JLabel seatJ8;
    private javax.swing.JLabel seatJ9;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JLabel seatLabels[][];

    private void showRemainingTime() {

        new Thread() {
            public void run() {
                int tiempoRestante = 60;
                lblRemainingTime.setText("Tiempo Restante: " + tiempoRestante);
                while(true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ReservationWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        tiempoRestante--;
                        lblRemainingTime.setText("Tiempo restante: " + tiempoRestante);
                        if(tiempoRestante <= 0){
                            restartReservations();
                            this.stop();
                        }
                }
            }

            private void restartReservations() {
                //clientController.cancelReservations();
                cancelAllPreReservations();
                isFirstPreReservedSeat = true;
                lblRemainingTime.setText(" ");
            }
            
            private void cancelAllPreReservations(){
                while(!stack.empty()){
                    JLabel seatLabel = (JLabel) stack.pop();
                    Seat seat = (Seat) stack.pop();
                    cancel_preReserveSeat(seat, seatLabel);
                }
            }
            
        }.start();


    }

    private void lookAndFeel() {
        try {
     for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
     if ("Nimbus".equals(info.getName())) {
     javax.swing.UIManager.setLookAndFeel(info.getClassName());
     break;
     }
     }
     } catch (ClassNotFoundException ex) {
     java.util.logging.Logger.getLogger(ReservationWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
     } catch (InstantiationException ex) {
     java.util.logging.Logger.getLogger(ReservationWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
     } catch (IllegalAccessException ex) {
     java.util.logging.Logger.getLogger(ReservationWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
     } catch (javax.swing.UnsupportedLookAndFeelException ex) {
     java.util.logging.Logger.getLogger(ReservationWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
     }
    }

    private boolean hasReachedLimitSeats() {
        return stack.size() >= 10;
    }
}
