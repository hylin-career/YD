package com.demo;

import java.io.*;
import java.util.*;

public class PropertiesUtility{
    private static String mLastMessage = GlobalData.EMPTY_LINE;
    static String getLastMessage(){
        return(mLastMessage);
    }

    static Properties load(File F){
        //Properties props = new Properties();
        //
        ///* create an empty properties file, if not exist */
        //if(  !F.exists()  ){
        //    if(  !createFile(F)  ){
        //        mLastMessage = "PropertiesUtility::load - " + mLastMessage;
        //
        //        return(props);
        //    }
        //    
        //    /* initialize props */
        //    props.setProperty(GlobalConstants.PROPERTIES_MARK_NAME,
        //            GlobalConstants.PROPERTIES_MARK_VALUE);
        //    save(F, props);
        //}
        
        Properties props = new Properties();
        
        try{
            FileInputStream in = new FileInputStream(F);
            props.load(in);
            in.close();
        }catch(Exception E){
            mLastMessage = "[ERR] Properties load - " + E.getMessage();
            
            return(null);
        }
        
        return(props);
    }
    
    static boolean save(File F, Properties Props){
        try{
            FileOutputStream out = new FileOutputStream(F);
            Props.store(out, GlobalData.EMPTY_LINE);
            out.close();
        }catch(IOException E){
            mLastMessage = "[ERR] PropertiesUtility::save, "
                    + E.getMessage() + ", '" + F.getPath() + "'";
            
            return(false);
        }

        return(true);
    }
}
