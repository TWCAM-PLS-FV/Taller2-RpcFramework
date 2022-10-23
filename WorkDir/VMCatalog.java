import java.util.List;
interface VMCatalog{
   public void putVM(String host, VM vm) throws Exception;
   public List<VM> getVMs(String host) throws Exception;
}