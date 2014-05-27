/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Esta clase se conectará con el cliente y recibirá sus mensajes
 *
 * @author José Ramón Díaz
 */
public class TCPServer {

    public static void main(String args[]) {
        try {
            int serverPort = 7896;
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while (true) {
                //Se acepta la solicitud de conexion del cliente
                Socket clientSocket = listenSocket.accept();
                //Se procesa el mensaje en la calse Connection
                new Connection(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }
}
