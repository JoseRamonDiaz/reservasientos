/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author José Ramón Díaz
 */
public class MulticastServer {

    private MulticastSocket s = null;
    private String grp = "228.5.6.7";
    private InetAddress group;
    private int port = 6789;
    private static MulticastServer multicastServer;

    private MulticastServer() {

        try {
            //Se crea el socket multicast con el puerto indicado
            s = new MulticastSocket(port);
            //Se obtiene la direccion por el nombre
            group = InetAddress.getByName(grp);
            //El socket multicast se une al grupo
            s.joinGroup(group);
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
    
    //Obtiene la instancia del MulticastServer
    static MulticastServer getInstance() {
        //Si es la primera vez que se solicita se crea uno nuevo
        //Para seguir el patron singleton
        if (multicastServer == null) {
            multicastServer = new MulticastServer();
            return multicastServer;
        } else {
            return multicastServer;
        }
    }

    //Recibe el mensaje y lo envía al grupo
    public void sendMulticast(String message) {

        try {
            //Obtiene los bytes del mensaje
            byte[] m = message.getBytes("UTF-8");
            DatagramPacket messageOut;

            try {
                //Crea el datagrama
                messageOut = new DatagramPacket(m, m.length, group, port);
                //Envía el mensaje
                s.send(messageOut);
            } catch (SocketException ex) {
                Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            }

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //Abandona el grupo
    public void close() {
        try {
            s.leaveGroup(group);
        } catch (IOException ex) {
            Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Este metodo solo es para pruebas
    void readMessages() {
        byte[] buffer = new byte[1000];
        for (;;) {
            try {
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                s.receive(messageIn);
                System.out.println("Received:" + new String(messageIn.getData()));
            } catch (IOException ex) {
                Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
