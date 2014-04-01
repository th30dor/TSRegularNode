package adminnode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import regularnode.FailureListener;
import regularnode.RegularCom;
import regularnode.RegularNode;

/**
 * Thread that handles requests from the user
 *
 */
public class UserComServerThread extends Thread {

    Socket clientSocket;
    TemperatureSystemInfo tsi;
    AdminCom ac;
    RegularCom rc;
    KeepAliveBroadcaster kab;
    FailureListener fl;
    UserCom uc;

    /**
     *
     * @param clientSocket
     * @param tsi   information taken from the config file
     * @param ac  
     * @param rc  
     * @param kab 
     * @param fl
     * @param uc    all the above are thread references, meant to be closed when the 
     *              appropriate message is received 
     */
    public UserComServerThread(Socket clientSocket, TemperatureSystemInfo tsi,
            AdminCom ac, RegularCom rc, KeepAliveBroadcaster kab, FailureListener fl, UserCom uc) {

        this.clientSocket = clientSocket;
        this.tsi = tsi;
        this.rc = rc;
        this.ac = ac;
        this.kab = kab;
        this.fl = fl;
        this.uc = uc;

    }

    /**
     * This method computes the mean of the temperatures the server receives
     *
     * @param al the array with the temperatures
     * @return the mean value of all the temperatures
     */
    public Double computeMean(ArrayList<Double> al) {

        Double mean = 0.0;
        int noElem = 0;

        synchronized (al) {
            for (int i = 0; i < al.size(); i++) {

                if (al.get(i) != RegularNode.invalidValue) {

                    mean = mean + al.get(i);
                    noElem++;
                }

            }
        }
        if (noElem != 0) {
            return mean / noElem;
        }

        return RegularNode.invalidValue;
    }

    /**
     *
     * The method "kills" the threads that are given as arguments. The method
     * re-starts them after a timeout of 10 seconds.
     *
     * @param rc    a RegularCom class that is to be terminated
     * @param ac    an AdminCom class that is to be terminated
     * @param kab   to be terminated
     * @param fl    to be terminated
     * @param uc    to be terminated
     */
    public void failureHandler(RegularCom rc, AdminCom ac, KeepAliveBroadcaster kab, FailureListener fl, UserCom uc) {

        //kill everything
        ac.interrupt();
        rc.interrupt();
        kab.interrupt();
        uc.interrupt();
        try {
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(UserComServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        fl.setIsAdmin(false);
        fl.setIsAlive(false);

        //in the eventuality that this node will wake up
        //make it a regular node
        tsi.setType("regular");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            Logger.getLogger(UserCom.class.getName()).log(Level.SEVERE, null, ex);
        }

        //only start the user node threads
        rc = new RegularCom(tsi);
        rc.start();
        fl.setIsAlive(true);

    }

    /**
     * Listens for a connection from the user. It receives 2 types of messages:
     * Request temp - replies to the user with the mean temp 
     * Failure message - simulates the failure of the admin node
     * State request - replies with a list of available nodes
     */
    @Override
    public void run() {
        try {
            System.out.println("[ADMIN]Accepted connection");
            DataInputStream in;
            DataOutputStream out;

            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            while (true) {

                String data;
                data = in.readUTF();

                if (data.equals("Request temp")) {

                    out.writeUTF(computeMean(ac.temps).toString());
                    System.out.println("[ADMIN]Answered user request");
                }

                if (data.toLowerCase().equals("die")) {

                    failureHandler(rc, ac, kab, fl, uc);
                    break;
                }

                if (data.toLowerCase().equals("state")) {

                    System.out.println("[ADMIN]Received request for node states");

                    String s = new String();
                    for (int i = 0; i < ac.temps.size(); i++) {
                        if(ac.temps.get(i) == RegularNode.invalidValue)
                            s = s + "0";
                        else
                            s = s + "1";
                    }

                    out.writeUTF(s);
                }

            }
        } catch (IOException ex) {
            System.out.println("[ADMIN]User machine disconnected");
        }


    }
}
