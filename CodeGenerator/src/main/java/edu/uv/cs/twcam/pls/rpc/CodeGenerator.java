package edu.uv.cs.twcam.pls.rpc;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class CodeGenerator {

    private static Utils util = new Utils();

    private static void clientTaskSkeleton(PrintWriter pw, String interfaceName) {
        try {
            pw.println("import java.net.*;");
            pw.println("import java.io.*;");
            pw.println("import java.lang.reflect.*;");
            pw.println("import java.util.concurrent.*;");
            pw.println("import java.util.Optional;");
            pw.println("import java.util.stream.Stream;");
            pw.println("");
            //
            pw.println("class TrataCliente implements Runnable{");
            pw.println("   private Socket s;");
            pw.println("   private " + interfaceName + " impl;");
            pw.println("   TrataCliente(Socket s, " + interfaceName + " impl){");
            pw.println("      this.s = s;");
            pw.println("      this.impl = impl;");
            pw.println("   }");
            pw.println("   public void run(){");
            pw.println("      try{");
            pw.println("         ObjectInputStream in = new ObjectInputStream(s.getInputStream());");
            pw.println("         String method = (String)in.readObject();");
            Class<?> c = Class.forName(interfaceName);
            Method[] methods = c.getMethods();
            pw.println("        String address = s.getRemoteSocketAddress().toString();");
            pw.println("        Class<?> c = Class.forName(\"" + interfaceName + "\");");
            for (int i = 0; i < methods.length; i++) {
                Method m = methods[i];
                System.out.println("Generating code to deal with method call :" + m.getName());
                if (i == 0)
                    pw.println("          if (\"" + m.getName() + "\".equals(method)){");
                else
                    pw.println("          else if (\"" + m.getName() + "\".equals(method)){");

                Type returnType = m.getGenericReturnType();
                Class<?>[] parameterTypes = m.getParameterTypes();

                pw.println(
                        "           Optional<Method> opt = Stream.of(c.getMethods()).filter(m -> m.getName().equals(method)).findFirst();");
                pw.println("           if (opt.isPresent()) {");
                pw.println(
                        "              System.out.println(\"[\" + address + \"] Method \" + method + \" invoked in the server\");");
                pw.println("              Method m = opt.get();");
                pw.println("              int nparams = m.getParameterTypes().length;");
                pw.println("              Object[] params = new Object[nparams];");
                for (int p = 0; p < parameterTypes.length; p++)
                    pw.println("              params[" + p + "] = in.readObject();");
                pw.println(
                        "              System.out.println(\"[\" + address + \"] Deserialized \" + nparams + \" arguments\");");
                if (returnType != Void.TYPE) {
                    pw.println("              " + returnType.getTypeName() + " ret  = (" + returnType.getTypeName()
                            + ")m.invoke(impl, params);");
                    pw.println("              ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());");
                    pw.println("              out.writeObject(ret);");
                    pw.println("              out.flush();");
                    pw.println("              out.close();");
                } else
                    pw.println("              m.invoke(impl, params);");
                pw.println("              System.out.println(\"[\" + address + \"] Method \" + method + \" ends\");");
                pw.println("           }else");
                pw.println(
                        "              System.out.println(\"[\" + address + \"] Method \" + method + \" not found\");");
                pw.println("         }");
            }
            pw.println("         in.close();");
            pw.println("         s.close();");
            pw.println("      }catch(Exception ex){");
            pw.println("         System.err.println(ex.toString());");
            pw.println("      }");
            pw.println("   }");
            pw.println("}");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void mainClassSkeleton(PrintWriter pw, String interfaceName) {
        String class_name = interfaceName + "_skel";
        pw.println("class " + class_name + "{");
        pw.println("   private " + interfaceName + " impl;");
        pw.println("   private int port;");
        pw.println("   private  ExecutorService pool;");
        pw.println("   " + class_name + "(" + interfaceName + " impl, int port){");
        pw.println("       this.impl = impl;");
        pw.println("       this.port = port;");
        pw.println("       pool = Executors.newSingleThreadExecutor();");
        pw.println("   }");
        pw.println("   public void start(){");
        pw.println("      try{");
        pw.println("         ServerSocket s = new ServerSocket(port);");
        pw.println("         while (true){");
        pw.println("            Socket client = s.accept();");
        pw.println("            pool.execute(new TrataCliente(client,impl));");
        pw.println("         }");
        pw.println("      }catch(Exception ex){System.err.println(ex.toString());}");
        pw.println("      finally{ pool.shutdown(); }");
        pw.println("   }");
        pw.println("}");
        pw.flush();
        pw.close();
    }

    private static void startClassStub(PrintWriter pw, String interfaceName) {
        String class_name = interfaceName + "_stub";
        pw.println("import java.net.*;");
        pw.println("import java.io.*;");
        pw.println("import java.util.List;");
        pw.println("");
        pw.println("public class "+class_name+" implements " + interfaceName + "{");
        pw.println("    private String host;");
        pw.println("    private int port;");
        pw.println(class_name + "(String h, int p){");
        pw.println("    this.host = h;");
        pw.println("    this.port = p;");
        pw.println("}");
    }

    private static void generateMethodStub(PrintWriter pw, Method m) {
        String methodName = m.getName();
        String methodType = m.getGenericReturnType().getTypeName();
        Parameter parameters[] = m.getParameters();
        Class<?> classObject = m.getReturnType();
        Class<?>[] classException = m.getExceptionTypes();

        // Creo el metodo con sus argumentos y tipos
        pw.println("@Override");
        pw.print("public " + methodType + " " + methodName + "(");
        int index = 0;
        for (Parameter p : parameters) {
            pw.print(util.getType(p.toString()));
            index++;
            if (index != parameters.length) {
                pw.print(", ");
            }
        }

        // Valido si el metodo tiene alguna exception por capturar
        boolean hasException = false;
        String typeException = "";

        if (classException != null) {
            for (Class<?> exception : classException) {
                hasException = true;
                typeException = exception.getName().toString();
            }
        }

        // Si tiene una excepcion por defecto, diferente a Exception o Remote, la
        // capturo y la pongo
        if (hasException) {
            pw.println(") throws " + util.getType(typeException) + " {");
        } else {
            pw.println(") {");
        }

        // Creo el bloque Try
        pw.println("try {");
        pw.println("Socket socket = new Socket(this.host,this.port);");
        pw.println("ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());");
        pw.println("out.writeObject(\"" + methodName + "\");");
        pw.println("out.flush();");
        for (Parameter parameter : parameters) {
            pw.println("out.writeObject(" + parameter.getName() + ");");
        }
        pw.println("out.flush();");
        if (classObject != Void.TYPE) {
            pw.println("ObjectInputStream in = new ObjectInputStream(socket.getInputStream());");
            pw.println(util.getType(methodType) + " aux;");
            pw.println("aux =(" + util.getType(methodType) + ")in.readObject();");
            pw.println("in.close();");
        }
        pw.println("out.close();");
        pw.println("socket.close();");
        if (classObject != Void.TYPE) {
            pw.println("return aux;");
        }
        // Si tiene una excepcion por defecto, diferente a Exception o Remote, la
        // capturo y la pongo
        if (hasException) {
            pw.println("}catch("+util.getType(typeException)+ " e){");
            pw.println("throw new "+util.getType(typeException)+" (e.toString());");
            pw.println("}");
            pw.println("}");
        } else {
            pw.println("}catch(Exception e){");
            pw.println("System.out.println(e.toString());");
            pw.println("}");
            pw.println("}");
        }
    }

    private static void closeClassStub(PrintWriter pw) {
        pw.println("}");
        pw.flush();
        pw.close();
    }

    public static void main(String[] args) {
        try {
            String interfaceName = args[0];
            String className = interfaceName + "_stub";
            Class<?> c = Class.forName(interfaceName);
            PrintWriter pw = new PrintWriter(new FileWriter(className + ".java"));
            startClassStub(pw, interfaceName);
            Method[] methods = c.getMethods();
            for (Method m : methods)
                generateMethodStub(pw, m);
            closeClassStub(pw);

            String classNameSkel = interfaceName + "_skel";
            PrintWriter pwSkel = new PrintWriter(new FileWriter(classNameSkel + ".java"));
            clientTaskSkeleton(pwSkel, interfaceName);
            mainClassSkeleton(pwSkel, interfaceName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
