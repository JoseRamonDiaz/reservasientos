/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author José Ramón Díaz
 */
public class Connection extends Thread {

    private DataInputStream in;
    private DataOutputStream out;
    private Socket clientSocket;

    //Se recibe el socket cliente con el que se conecto
    public Connection(Socket aClientSocket) {
        try {
            //Se guarda el socket cliente
            clientSocket = aClientSocket;
            //Se guarda en un buffered reader el inputStream del cliente

            in = new DataInputStream(clientSocket.getInputStream());
            //in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new DataOutputStream(clientSocket.getOutputStream());

            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            for (;;) {
                //Se lee el mensaje entrante del cliente
                String message = in.readUTF();
                
                ServerController.getInstance().processMessage(message,out);
                //Se obtiene la instancia del MulticasServer
                //MulticastServer ms = MulticastServer.getInstance();
                //Se difunde el mensaje a todo el grupo
                //ms.sendMulticast(message);
            }
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
