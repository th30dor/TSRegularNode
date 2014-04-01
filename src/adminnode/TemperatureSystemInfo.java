package adminnode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that read the configuration file and stores the various information.
 *
 */
public class TemperatureSystemInfo {

    String filename;
    int ID;
    String IP;
    int port;
    String type;
    int noNodes;
    int userPort;
    int failurePort;
    String multicastAddress;

    /**
     *
     * @param filename the name of the config file
     */
    public TemperatureSystemInfo(String filename) {

        this.filename = filename;

    }

    /**
     * read the configuration file and stores the various information
     */
    public void read() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;

            //read the ID
            line = br.readLine();
            ID = Integer.parseInt(line);

            //read the IP
            line = br.readLine();
            IP = line;

            //read the port
            line = br.readLine();
            port = Integer.parseInt(line);

            //read the type
            line = br.readLine();
            type = line;

            line = br.readLine();
            noNodes = Integer.parseInt(line);

            line = br.readLine();
            userPort = Integer.parseInt(line);

            line = br.readLine();
            failurePort = Integer.parseInt(line);

            line = br.readLine();
            multicastAddress = line;

            br.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TemperatureSystemInfo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TemperatureSystemInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNoNodes() {
        return noNodes;
    }

    public void setNoNodes(int noNodes) {
        this.noNodes = noNodes;
    }

    public int getUserPort() {
        return userPort;
    }

    public void setUserPort(int userPort) {
        this.userPort = userPort;
    }

    public int getFailurePort() {
        return failurePort;
    }

    public void setFailurePort(int failurePort) {
        this.failurePort = failurePort;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public void setMulticastAddress(String multicastAddress) {
        this.multicastAddress = multicastAddress;
    }
}
