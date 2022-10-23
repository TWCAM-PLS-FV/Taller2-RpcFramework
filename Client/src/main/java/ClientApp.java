import java.io.BufferedReader;
import java.io.FileReader;

import edu.uv.cs.twcam.pls.rpc.RemoteException;

class ClientApp {
    public static void main(String[] args) {
        String host = args[0];
        System.out.println(args);
        int port = Integer.parseInt(args[1]);
        VMCatalog client = new VMCatalog_stub(host, port);
        try {
            client.putVM("compute0", new VM("vm1", 4));
            client.putVM("compute0", new VM("vm2", 2));
            client.putVM("compute1", new VM("vm3", 8));
            System.out.println(client.getVMs("compute0"));
            System.out.println(client.getVMs("compute1"));
            client.putVM("compute0", new VM("vm4", 6));
            System.out.println(client.getVMs("compute0"));
        } catch (RemoteException ex) {
            System.out.println("Error invoking the remote service");
            ex.printStackTrace();
        }
    }
}