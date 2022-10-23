import edu.uv.cs.twcam.pls.rpc.RemoteException;

import java.util.List;
interface VMCatalog{
   public void putVM(String host, VM vm) throws RemoteException;
   public List<VM> getVMs(String host) throws RemoteException;
}