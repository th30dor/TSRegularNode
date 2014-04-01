package adminnode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Keeps sending messages with the address of the server using multicast.
 *
 */
public class KeepAliveBroadcaster extends Thread {

    /**
     *
     */
    public MulticastSocket socket;
    TemperatureSystemInfo tsi;

    /**
     *
     * @param tsi
     */
    public KeepAliveBroadcaster(TemperatureSystemInfo tsi) {

        this.tsi = tsi;

    }

    /**
     * Sends messages to the regular nodes using multicast every 5 seconds.
     *
     */
    @Override
    public void run() {

        DatagramPacket keepAlive =
                new DatagramPacket("alive".getBytes(), "alive".getBytes().length);

        while (!this.isInterrupted()) {
            try {

                keepAlive.setAddress(InetAddress.getByName(tsi.getMulticastAddress()));
                keepAlive.setPort(tsi.getFailurePort());
                socket.send(keepAlive);
                System.out.println("[ADMIN]Sending keepalive message");

            } catch (UnknownHostException ex) {
                Logger.getLogger(KeepAliveBroadcaster.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(KeepAliveBroadcaster.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                break;
            }
        }

    }

    /**
     *
     * @return
     */
    public MulticastSocket getSocket() {
        return socket;
    }

    /**
     *
     * @param socket
     */
    public void setSocket(MulticastSocket socket) {
        this.socket = socket;
    }

    /**
     *
     * @return
     */
    public TemperatureSystemInfo getTsi() {
        return tsi;
    }

    /**
     *
     * @param tsi
     */
    public void setTsi(TemperatureSystemInfo tsi) {
        this.tsi = tsi;
    }
}
