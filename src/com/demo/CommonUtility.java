package com.demo;

import java.io.IOException;

public class CommonUtility{
    static String toHashKey(String FileName, String Url){
        String code = Integer.toHexString(  (FileName + Url).hashCode()  );
        
        final String HEX_PREFIX = "00000000";
        final int HASH_CODE_LEN = 8;
        
        int pos = (HEX_PREFIX + code).length() - HASH_CODE_LEN;
        String result = (HEX_PREFIX + code).substring(pos).toUpperCase();
        
        return(result);
    }
    
    static void pauseAnyKey(){
        System.out.print("Press any key...");
        try{
            System.in.read();
        } catch(IOException e){
            e.printStackTrace();
        }
        System.out.println();
    }

    static void pause(double Second){
        try{
            int t = (int)(Second * 1000);
            Thread.sleep(t);
        }catch(InterruptedException E){
            /* do nothing */
        }
    }

    static void disableVerboseLoggingReport(){
        String[] loggingOffClasses = new String[]{
                "com.gargoylesoftware.htmlunit",
                "org.apache.http",
                "com.gargoylesoftware.htmlunit.WebClient",
                "com.gargoylesoftware.htmlunit.javascript",
                "com.gargoylesoftware.htmlunit.javascript.StrictErrorReporter",
                "com.gargoylesoftware.htmlunit.IncorrectnessListenerImpl",
        };

        for(String className : loggingOffClasses){
            java.util.logging.Logger.getLogger(className)
                    .setLevel(java.util.logging.Level.OFF);
        }
    }
}
