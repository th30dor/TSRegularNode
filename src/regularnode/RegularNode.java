package regularnode;

import adminnode.AdminCom;
import adminnode.KeepAliveBroadcaster;
import adminnode.TemperatureSystemInfo;
import adminnode.UserCom;

public class RegularNode {

     static public Double invalidValue;
    /**
     * Reads the config file and starts the threads.
     * @param args the name of the config file 
     */
    public static void main(String[] args) {

        String fn;
        fn = args[0];

        invalidValue = -1000.0;
        
        TemperatureSystemInfo tsi = new TemperatureSystemInfo(fn);
        tsi.read();

        RegularCom node;
        node = new RegularCom(tsi);
        node.start();
        
        KeepAliveBroadcaster kab = new KeepAliveBroadcaster(tsi);
        AdminCom ac = new AdminCom(tsi);
                
        
        FailureListener fl = new FailureListener(tsi);
        UserCom us = new UserCom(tsi,ac,node,kab,fl);   
        
        fl.setKab(kab);
        fl.setAc(ac);
        fl.setUc(us);
        fl.start();
        
        System.out.println("[REGULAR]Client started");
        
        
        if (tsi.getType().toLowerCase().equals("admin")) {

            fl.setIsAdmin(true);
            
            ac.start();
            us.start();
            
            kab.setSocket(fl.getSocket());
            kab.start();            
            
            System.out.println("[ADMIN]Server started");
        }        

    }
}
