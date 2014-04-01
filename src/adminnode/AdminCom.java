package adminnode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import regularnode.RegularNode;

/**
 * This class handles the communication between admin node and regular nodes.
 *
 */
public class AdminCom extends Thread {

    ArrayList<Double> temps;
    TemperatureSystemInfo tsi;
    DatagramSocket serverSocket = null;
    ArrayList<Integer> states;

    public AdminCom(TemperatureSystemInfo tsi) {
        super();
        this.tsi = tsi;

        temps = new ArrayList<>();
        //add dummy values
        for (int i = 0; i < tsi.getNoNodes(); i++) {
            temps.add(RegularNode.invalidValue);
        }

//        states = new ArrayList<>();
//        //add dummy values
//        for (int i = 0; i < tsi.getNoNodes(); i++) {
//            states.add(0);
//        }

    }

    /**
     * Parses the message from the regular node, and stores it in the array
     *
     * @param s the message from the regular node
     */
    private void addTempToPosition(String s) {
        int ID;
        String[] tmp;
        tmp = s.split(":");

        ID = Integer.parseInt(tmp[0].trim());
        synchronized (temps) {
            temps.set(ID, new Double(tmp[1]));
        }

        //states.set(ID, 1);
    }

    /**
     * The purpose of this method is to handle interruptions in the situation
     * when the current thread is blocked by an I/O operation, namely the socket
     * waiting for messages
     */
    @Override
    public void interrupt() {
        try {
            serverSocket.close();
        } finally {
            super.interrupt();
        }
    }

    /**
     * Waits for messages from the regular nodes and stores them in the array.
     */
    @Override
    public void run() {

        System.out.println("[ADMIN]Temperature listener on admin has started.");
        String s;
        try {
            serverSocket = new DatagramSocket(tsi.getPort());
        } catch (SocketException ex) {
            Logger.getLogger(AdminCom.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] receiveData = new byte[1024];

        //This loop runs as long as the current thread is not interrupted.
        while (!Thread.currentThread().isInterrupted()) {

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                serverSocket.receive(receivePacket);
            } catch (IOException ex) {
                //Logger.getLogger(RegularNodeListener.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("[ADMIN]SOCKET CLOSED - FAILURE MESSAGE RECEIVED");
            }

            s = new String(receivePacket.getData());
            System.out.println("[ADMIN]Server received message " + s);
            addTempToPosition(s);

        }

        System.out.println("[ADMIN]Server exiting nicely.");

    }

    public TemperatureSystemInfo getTsi() {
        return tsi;
    }

    public void setTsi(TemperatureSystemInfo tsi) {
        this.tsi = tsi;
    }
}
