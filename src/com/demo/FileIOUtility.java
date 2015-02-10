package com.demo;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class FileIOUtility{
    private static String mLastMessage = GlobalData.EMPTY_LINE;

    static String getLastMessage(){
        return(mLastMessage);
    }

    static boolean transferFromReadableByteChannelToFileChannel(
            ReadableByteChannel RBC, FileChannel FC, int Size)
    {
        try{
            FC.transferFrom(RBC, 0, Size);
        }catch(Exception E){
            mLastMessage = E.getMessage();
            
            return(false);
        }

        return(true);
    }
    
    static byte[] readFromInputStream(
            InputStream IStream, int Size, ByteArrayOutputStream BaOStream)
    {
        try{
            byte[] buffer = new byte[Size];
        
            while(true){
                int len = IStream.read(buffer, 0, Size);
                if(len <= 0) throw new Exception("IStream.read <= 0");
        
                BaOStream.write(buffer, 0, len);
                
                if(BaOStream.size() >= Size){
                    byte[] streamBytes = BaOStream.toByteArray();
                    byte[] sendOut = Arrays.copyOf(streamBytes, Size);
                    
                    byte[] remainder = new byte[streamBytes.length - Size];
                    for(int i = 0; i < remainder.length; i++){
                        remainder[i] = streamBytes[Size + i];
                    }
                    BaOStream.reset();
                    BaOStream.write(remainder);
                    
                    return(sendOut);
                }
            }
        }catch(Exception E){
            mLastMessage = "[ERR] FileIOUtility::readFromInputStream - "
                    + E.getMessage();
            return(new byte[0]);
        }
    }
    
    static byte[] readFromInputStream(InputStream IStream, int Size){
        byte[] bytes = new byte[Size];
        int readSize = -1;
        try{
            readSize = IStream.read(bytes, 0, Size);
        }catch(Exception E){
            mLastMessage = "[ERR] FileIOUtility::readFromInputStream, "
                    + "InputStream.read() - " + E.getMessage();
            return(new byte[0]);
        }
        
        byte[] result = Arrays.copyOf(bytes,readSize);
        
        return(result);
    }
    
    static boolean positionFileChannel(FileChannel FC, int Offset){
        try{
            FC.position(Offset);
        }catch(Exception E){
            mLastMessage = E.getMessage();
            
            return(false);
        }

        return(true);
    }
    
    static boolean positionInputStream(InputStream IS, int Offset){
        try{
            IS.skip(Offset);
        }catch(Exception E){
            mLastMessage = E.getMessage();
            
            return(false);
        }

        return(true);
    }

    static FileChannel getFileChannel(String FileName){
        try{
            FileChannel fc = new FileOutputStream(FileName).getChannel();
            
            return(fc);
        }catch(Exception E){
            mLastMessage = E.getMessage();
            
            return(null);
        }
    }
    
    static boolean createFile(File F){
        try{
            OutputStream out = new FileOutputStream(F);
            out.close();

            if(!F.exists()){
                throw new IOException("file created, but not exists");
            }
            
            return(true);
        }catch(IOException E){
            mLastMessage = "FileIOUtility::createFile() - "
                    + E.getMessage() + ", '" + F.getPath() + "'";
            
            return(false);
        }
    }

    static boolean createFile(String FileName, int Size){
        try{
            FileOutputStream fos = new FileOutputStream(FileName);
            
            byte[] data = new byte[Size];
            for(int i = 0; i < data.length; i++){
                data[i] = 'A';
            }
            fos.write(data, 0, data.length);
            
            fos.close();
        }catch(Exception E){
            mLastMessage = E.getMessage();
            
            return(false);
        }
        
        return(true);
    }
}
