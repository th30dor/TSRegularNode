package regularnode;

import adminnode.TemperatureSystemInfo;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that handles the regular sending of temperatures to admin
 *
 */
public class RegularCom extends Thread {

    private Random gen;
    private int ID;
    private DatagramSocket aSocket;
    DatagramPacket request;
    TemperatureSystemInfo tsi;

    public RegularCom(TemperatureSystemInfo tsi) {

        this.tsi = tsi;
        gen = new Random();
        ID = tsi.getID();
        InetAddress server = null;
        int serverPort = tsi.getPort();
        byte[] inital = "0.0:1".getBytes();

        try {
            aSocket = new DatagramSocket();
            server = InetAddress.getByName(tsi.getIP());
        } catch (SocketException | UnknownHostException ex) {
            Logger.getLogger(RegularCom.class.getName()).log(Level.SEVERE, null, ex);
        }
        request = new DatagramPacket(inital, inital.length);
        request.setAddress(server);
        request.setPort(serverPort);


    }

    public double GetTemp() {

        return gen.nextGaussian() * 5 + 25;

    }

    /**
     * Sends a message with the temperature to the admin node.
     *
     * @param Temp
     */
    public void SendTemp(double Temp) {

        String message = Integer.toString(ID) + ":" + Double.toString(Temp);
        System.out.println("[REGULAR]Client " + ID + " Sending message " + message);

        synchronized (tsi) {
            try {
                request.setAddress(InetAddress.getByName(tsi.getIP()));
            } catch (UnknownHostException ex) {
                Logger.getLogger(RegularCom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            byte[] m = message.getBytes();
            request.setData(m);
            request.setLength(message.length());
            aSocket.send(request);
        } catch (IOException e) {
            System.out.println("IO" + e.getMessage());
        }

    }

    /**
     * Calls the method for sending messages to the admin node every 3 seconds.
     */
    @Override
    public void run() {

        System.out.println("[REGULAR]Temperature sender on regular node has started.");
        while (true) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                break;
            }
            SendTemp(GetTemp());


        }
        System.out.println("[REGULAR]Regular com exiting nicely");
    }

    public TemperatureSystemInfo getTsi() {
        return tsi;
    }

    public void setTsi(TemperatureSystemInfo tsi) {
        this.tsi = tsi;
    }
}
