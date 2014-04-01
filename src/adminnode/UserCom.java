package adminnode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import regularnode.FailureListener;
import regularnode.RegularCom;

/**
 * Class that handles the communication with the user.
 */
public class UserCom extends Thread {

    TemperatureSystemInfo tsi;
    AdminCom ac;
    RegularCom rc;
    KeepAliveBroadcaster kab;
    FailureListener fl;
    ServerSocket listenSocket;

    /**
     *
     * @param tsi
     * @param ac
     * @param rc
     * @param kab
     * @param fl - threads to be passed to the thred that handles requests
     */
    public UserCom(TemperatureSystemInfo tsi, AdminCom ac, RegularCom rc, 
            KeepAliveBroadcaster kab, FailureListener fl) {

        this.tsi = tsi;
        this.ac = ac;
        this.rc = rc;
        this.kab = kab;
        this.fl = fl;
    }

    /**
     * Listens for a connection from the user. Starts a new UserComServerThread to handle the 
     * requests once a connection is established 
     */
    @Override
    public void run() {
        listenSocket = null;
        try {
            
            listenSocket = new ServerSocket(7001);
            System.out.println("[ADMIN]Starting user communications");
            //ServerSocket listenSocket = new ServerSocket(tsi.getUserPort());
            while(!this.isInterrupted()){
                Socket clientSocket = listenSocket.accept();
                UserComServerThread ucst = new UserComServerThread(clientSocket, tsi, ac, rc, kab,fl,this);
                ucst.start();
                System.out.println("[ADMIN]Started new thread to talk with user");
            }
            
        } catch (IOException ex) {
            //Logger.getLogger(UserCom.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("[ADMIN]Closed communication with user");
        }
        
        System.out.println("[ADMIN]UserCom exits nicely");
    } 

    /**
     * Forcefully closes the socket in case an interrupt() call is made
     */
    @Override  
    public void interrupt(){  
       try{  
          listenSocket.close();  
       }  
        catch (IOException ex) {  
        }       finally{  
         super.interrupt();  
       }  
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

    /**
     *
     * @return
     */
    public RegularCom getRc() {
        return rc;
    }

    /**
     *
     * @param rc
     */
    public void setRc(RegularCom rc) {
        this.rc = rc;
    }

    /**
     *
     * @return
     */
    public FailureListener getFl() {
        return fl;
    }

    /**
     *
     * @param fl
     */
    public void setFl(FailureListener fl) {
        this.fl = fl;
    }

    
}
