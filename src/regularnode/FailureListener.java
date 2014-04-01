package regularnode;

import adminnode.AdminCom;
import adminnode.KeepAliveBroadcaster;
import adminnode.TemperatureSystemInfo;
import adminnode.UserCom;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Listens for messages: message sent by the user to name the current node as
 * the new admin node message sent by the new admin node to announce it's
 * presence
 *
 */
public class FailureListener extends Thread {

    TemperatureSystemInfo tsi;
    KeepAliveBroadcaster kab;
    MulticastSocket socket;
    AdminCom ac;
    UserCom uc;
    boolean isAdmin;
    boolean isAlive;

    /**
     *
     * @param tsi various config info
     */
    public FailureListener(TemperatureSystemInfo tsi) {

        this.tsi = tsi;
        try {
            socket = new MulticastSocket(tsi.getFailurePort()); // must bind receive side
        } catch (IOException ex) {
            Logger.getLogger(FailureListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            socket.joinGroup(InetAddress.getByName(tsi.getMulticastAddress()));
        } catch (UnknownHostException ex) {
            Logger.getLogger(FailureListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FailureListener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * handles the messages it receives
     */
    @Override
    public void run() {


        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        String s;

        try {


            //socket.setSoTimeout(2000);

            while (true) {

                socket.receive(receivePacket);

                s = new String(receivePacket.getData());

                if (s.toLowerCase().trim().equals("admin")) {

                    DatagramPacket confirmation =
                            new DatagramPacket("confirmation".getBytes(), "confirmation".getBytes().length);

                    confirmation.setAddress(receivePacket.getAddress());
                    confirmation.setPort(8000);

                    socket.send(confirmation);

                    if (isAdmin == false) {
                        socket.send(confirmation);

                        kab = new KeepAliveBroadcaster(kab.getTsi());
                        kab.setSocket(socket);
                        kab.start();

                        ac = new AdminCom(ac.getTsi());
                        ac.start();

                        uc = new UserCom(uc.getTsi(), ac, uc.getRc(), kab, uc.getFl());
                        uc.start();

                        isAdmin = true;
                    }

                }
                if (s.toLowerCase().trim().equals("alive")) {
                    synchronized (tsi) {

                        tsi.setIP((receivePacket.getAddress().toString()).substring(1));
                        if(isAlive==true){
                        System.out.println("[REGULAR] Received keep alive message "
                                + (receivePacket.getAddress().toString()).substring(1));
                        }
                    }
                }


            }

        } catch (SocketTimeoutException e) {
        } catch (IOException ex) {
            Logger.getLogger(FailureListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TemperatureSystemInfo getTsi() {
        return tsi;
    }

    public void setTsi(TemperatureSystemInfo tsi) {
        this.tsi = tsi;
    }

    public KeepAliveBroadcaster getKab() {
        return kab;
    }

    public void setKab(KeepAliveBroadcaster kab) {
        this.kab = kab;
    }

    public MulticastSocket getSocket() {
        return socket;
    }

    public void setSocket(MulticastSocket socket) {
        this.socket = socket;
    }

    public AdminCom getAc() {
        return ac;
    }

    public void setAc(AdminCom ac) {
        this.ac = ac;
    }

    public UserCom getUc() {
        return uc;
    }

    public void setUc(UserCom uc) {
        this.uc = uc;
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isIsAlive() {
        return isAlive;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }
    
    
}
