package com.demo;

import java.io.InputStream;
import java.net.*;

public class UrlUtility{
    private static String mLastMessage = GlobalData.EMPTY_LINE;
    static String getLastMessage(){
        return(mLastMessage);
    }
    
    static InputStream getInputStreamFromUrl(String Url){
        InputStream result = null;
        
        try{
            result = new URL(Url).openStream();
        }catch(Exception E){
            mLastMessage = "[ERR] URL getUrlInputStream - " + E.getMessage();
        }
        
        return(result);
    }
    
    static boolean isUriAvailable(String Uri){
        boolean result = true;
        try{
            new URL(Uri).openConnection().connect();
        }catch(Exception E){
            mLastMessage = "[ERR] URL connect - " + E.getMessage();
            result = false;
        }

        return(result);
    }
    
    static int getUriSize(String Uri){
        int size = GlobalData.ERROR_FILE_SIZE;
        
        try{
            size = new URL(Uri).openConnection().getContentLength();
        }catch(Exception E){
            mLastMessage = "[ERR] URL getContentLength - " + E.getMessage();
        }

        return(size);
    }

    static String getUriType(String Uri){
        String type = GlobalData.EMPTY_LINE;
        try{
            type = new URL(Uri).openConnection().getContentType();
        }catch(Exception E){
            mLastMessage = "[ERR] URL getContentType - " + E.getMessage();
        }
        
        return(type);
    }
}
