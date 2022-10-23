import java.net.*;
import java.io.*;
import java.util.List;

public class VMCatalog_stub implements VMCatalog {
    private String host;
    private int port;

    VMCatalog_stub(String h, int p) {
        this.host = h;
        this.port = p;
    }

    @Override
    public void putVM(String arg0, VM arg1) throws Exception {
        try {
            Socket socket = new Socket(this.host, this.port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject("putVM");
            out.flush();
            out.writeObject(arg0);
            out.writeObject(arg1);
            out.flush();
            out.close();
            socket.close();
        } catch (Exception e) {
            throw new Exception(e.toString());
        }
    }

    @Override
    public java.util.List<VM> getVMs(String arg0) throws Exception {
        try {
            Socket socket = new Socket(this.host, this.port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject("getVMs");
            out.flush();
            out.writeObject(arg0);
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            List<VM> aux;
            aux = (List<VM>) in.readObject();
            in.close();
            out.close();
            socket.close();
            return aux;
        } catch (Exception e) {
            throw new Exception(e.toString());
        }
    }
}
