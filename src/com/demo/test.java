package com.demo;

import java.io.*;
import java.nio.*;
import java.util.*;

public class test{
    public void testFileIO(){
        String fileName = "resizable";
        int size = 512;
        try{
            FileOutputStream fos = new FileOutputStream(fileName);
            
            byte[] data = new byte[size];
            for(int i = 0; i < data.length; i++){
                data[i] = 'A';
            }
            fos.write(data, 0, data.length);
            
            fos.close();
        }catch(Exception E){
            System.out.println(  E.getMessage()  );
            System.out.println(E);
        }
    }
    
    public void testStringHashCode(){
        String letterPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789";
        int poolLen = letterPool.length();
        
        int CASE_NUM = 20;

        Random random = new Random(5566);
        
        for(int i = 0; i < CASE_NUM; i++){
            int randomStringLen = 1 + random.nextInt(80);
            
            String randomString = ""; 
            for(int x = 0; x < randomStringLen; x++){
                int poolIndex = random.nextInt(poolLen);
                char c = letterPool.charAt(poolIndex);
                randomString = randomString + c;
            }
            
            String hash = CommonUtility.toHashKey(randomString, "");
            System.out.println(hash + " : " + randomString);
        }
    }
    
    public void testProperties(){
        File f = new File("config.ini");
        Properties props = PropertiesUtility.load(f);
        
        /* 檢查 Properties 是否讀取成功 */
        if(  !props.containsKey(GlobalData.PROPERTIES_MARK_NAME)
                || !props.get(GlobalData.PROPERTIES_MARK_NAME).equals(GlobalData.PROPERTIES_MARK_VALUE) )
        {
            System.out.println("[NOT PASS] test Properties, file:'" + f + "'");
            System.out.println(GlobalData.TAB + "REASON: " + PropertiesUtility.getLastMessage());
            
            System.exit(GlobalData.ABNORMAL_TERMINATION);
        }
        
        System.out.println("[PASS] test Properties, file:'" + f + "'");
        System.out.println("keySet = " + props.keySet());
    }
    
    public void testHistory(){
        History h = HistoryUtility.Factory.createEmptyHistory();
        
    }
    
    public static void main(String[] args){
        //new test().testFileIO();
        //new test().testProperties();
        //new test().testStringHashCode();
        new test().testHistory();
    }
}
