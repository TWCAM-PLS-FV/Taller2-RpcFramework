package edu.uv.cs.twcam.pls.rpc;

public class Utils {    
    public String getType(String arg){
        int i=arg.lastIndexOf(".");
        if(i!=-1){
            String st= arg.substring(i+1, arg.length());
            return st;
        }
        else{
            return arg;
        }
    }
}
