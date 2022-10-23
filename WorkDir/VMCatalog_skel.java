import java.net.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.concurrent.*;
import java.util.Optional;
import java.util.stream.Stream;

class TrataCliente implements Runnable{
   private Socket s;
   private VMCatalog impl;
   TrataCliente(Socket s, VMCatalog impl){
      this.s = s;
      this.impl = impl;
   }
   public void run(){
      try{
         ObjectInputStream in = new ObjectInputStream(s.getInputStream());
         String method = (String)in.readObject();
        String address = s.getRemoteSocketAddress().toString();
        Class<?> c = Class.forName("VMCatalog");
          if ("putVM".equals(method)){
           Optional<Method> opt = Stream.of(c.getMethods()).filter(m -> m.getName().equals(method)).findFirst();
           if (opt.isPresent()) {
              System.out.println("[" + address + "] Method " + method + " invoked in the server");
              Method m = opt.get();
              int nparams = m.getParameterTypes().length;
              Object[] params = new Object[nparams];
              params[0] = in.readObject();
              params[1] = in.readObject();
              System.out.println("[" + address + "] Deserialized " + nparams + " arguments");
              m.invoke(impl, params);
              System.out.println("[" + address + "] Method " + method + " ends");
           }else
              System.out.println("[" + address + "] Method " + method + " not found");
         }
          else if ("getVMs".equals(method)){
           Optional<Method> opt = Stream.of(c.getMethods()).filter(m -> m.getName().equals(method)).findFirst();
           if (opt.isPresent()) {
              System.out.println("[" + address + "] Method " + method + " invoked in the server");
              Method m = opt.get();
              int nparams = m.getParameterTypes().length;
              Object[] params = new Object[nparams];
              params[0] = in.readObject();
              System.out.println("[" + address + "] Deserialized " + nparams + " arguments");
              java.util.List<VM> ret  = (java.util.List<VM>)m.invoke(impl, params);
              ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
              out.writeObject(ret);
              out.flush();
              out.close();
              System.out.println("[" + address + "] Method " + method + " ends");
           }else
              System.out.println("[" + address + "] Method " + method + " not found");
         }
         in.close();
         s.close();
      }catch(Exception ex){
         System.err.println(ex.toString());
      }
   }
}
class VMCatalog_skel{
   private VMCatalog impl;
   private int port;
   private  ExecutorService pool;
   VMCatalog_skel(VMCatalog impl, int port){
       this.impl = impl;
       this.port = port;
       pool = Executors.newSingleThreadExecutor();
   }
   public void start(){
      try{
         ServerSocket s = new ServerSocket(port);
         while (true){
            Socket client = s.accept();
            pool.execute(new TrataCliente(client,impl));
         }
      }catch(Exception ex){System.err.println(ex.toString());}
      finally{ pool.shutdown(); }
   }
}
