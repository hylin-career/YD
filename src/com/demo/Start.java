package com.demo;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.Arrays;
import java.io.*;

import org.apache.commons.io.FilenameUtils;

public class Start{
    private String mTaskDescription;
    
    private void _LogTaskDescription(String Description){
        mTaskDescription = Description;
        
        if(GlobalData.PRINT_DEBUG_INFO) System.out.println(
                "[TASK] " + mTaskDescription);
    }
    
    private String _GetTaskDescription(){
        return(mTaskDescription);
    }
    
    private void _error(String Reason){
        String message = "Program terminated abnormally"
                + GlobalData.NEWLINE
                + GlobalData.TAB + "TASK: " + mTaskDescription
                + GlobalData.NEWLINE
                + GlobalData.TAB + "REASON: " + Reason;
        System.out.println(message);
        System.exit(GlobalData.ABNORMAL_TERMINATION);
    }
    
    public void start(){
        String uri = "http://software-files-a.cnet.com/s/software/13/21/20/41/PhotoScape_V3.6.5.exe?token=1401471527_ea733f24e1ac69985c0411ec27de485b&fileName=PhotoScape_V3.6.5.exe";
        String fileName = "download\\data.dat";
        
        _LogTaskDescription("check uri validation");
        if(!UrlUtility.isUriAvailable(uri)){
            _error(UrlUtility.getLastMessage());
        }
        
        _LogTaskDescription("get uri object size");
        int fileSize = UrlUtility.getUriSize(uri);
        if(fileSize == GlobalData.ERROR_FILE_SIZE){
            _error(UrlUtility.getLastMessage());
        }
        System.out.println("url file size: " + fileSize);
        
        System.out.println("url content type: " + UrlUtility.getUriType(uri));
    }
    
    private void _doDownload(History H){
        boolean ok;
        
        _LogTaskDescription("以 URL.openStream 取得 InputStream");
        InputStream urlInputStream = UrlUtility.getInputStreamFromUrl(H.directUrl);
        if(urlInputStream == null){
            _error(UrlUtility.getLastMessage());
        }
        
        int offset = H.chunkCount * H.chunkSize;

        _LogTaskDescription("把 URL.openStream(InputStream) 利用 skip() 做位移");
        ok = FileIOUtility.positionInputStream(urlInputStream, offset);
        if(!ok){
            _error(FileIOUtility.getLastMessage());
        }

        ReadableByteChannel rbc = Channels.newChannel(urlInputStream);

        _LogTaskDescription("把本機端的 fileNameDL 檔案, 包成 FileChannel");
        FileChannel fc = FileIOUtility.getFileChannel(H.fileNameDL);
        if(fc == null){
            _error(FileIOUtility.getLastMessage());
        }

        _LogTaskDescription("把本機端的 fileNameDL 的 FileChannel 利用 position 做位移");
        ok = FileIOUtility.positionFileChannel(fc, offset);
        if(!ok){
            _error(FileIOUtility.getLastMessage());
        }

        while(!H.isDone){
            _LogTaskDescription("從伺服器複製 " + H.chunkSize + " 位元組，"
                    + "填入本機檔案 '" + H.fileNameDL + "'    區塊號碼 " + H.chunkCount);
            ok = FileIOUtility.transferFromReadableByteChannelToFileChannel(rbc, fc, H.chunkSize);
            if(!ok){
                _error(FileIOUtility.getLastMessage());
            }
            
            /* close local file */
            try{
                fc.close();
            }catch(Exception E){
                _error("本機檔案關閉失敗 - " + E.getMessage());
            }
            
            /* update and save history */
            H.chunkCount = H.chunkCount + 1;
            if(H.chunkCount == H.chunkTotal){
                H.isDone = true;
            }
            HistoryUtility.store(H);
        }
    }
    
    public void testDownload(boolean IsNew){
        String fileName = "v.flv";
        String url = "http://www.youtube.com/watch?v=A9HV5O8Un6k";
        
        if(!IsNew){
            String hash = CommonUtility.toHashKey(fileName, url);
            History h = HistoryUtility.load(hash);
            
            _DoDownload(h);
            System.out.println(_GetTaskDescription());
            
            return;
        }
        
        /* 建立新的下載任務歷史紀錄 */
        History h = HistoryUtility.Factory.createEmptyHistory();

        HistoryUtility.setFileName(h, "v.flv");

        h.isDone = false;
        
        h.url = url;
        h.directUrl = testWebDriver.kejFlvRetriever2(url);
        //h.directUrl = "http://software-files-a.cnet.com/s/software/13/21/20/41/PhotoSca"
        //        + "pe_V3.6.5.exe?lop=link&ptype=3001&ontid=2192&siteId=4&edId=3&pid"
        //        + "=13212041&psid=10703122&token=1401850377_129a6b084e6b272645d1d26"
        //        + "aa2099dec&fileName=PhotoScape_V3.6.5.exe";
        //h.directUrl = "http://r8---sn-3cu-3iie.googlevideo.com/videoplayback?ms=au"
        //        + "&id=o-AGTz32qhqEuXEuwJwvyA5CO6j42pSzWhn-xfDgjox4Ip"
        //        + "&mws=yes"
        //        + "&sparams=id%2Cip%2Cipbits%2Citag%2Csource%2Cupn%2Cexpire"
        //        + "&mt=1401789693"
        //        + "&sver=3"
        //        + "&expire=1401814348"
        //        + "&fexp=913434%2C916104%2C923341%2C930008%2C935640%2C938631%2C940000"
        //        + "&source=youtube"
        //        + "&upn=zp4kR2oqJuE"
        //        + "&ipbits=0"
        //        + "&key=yt5"
        //        + "&ip=182.234.128.53"
        //        + "&itag=5"
        //        + "&mv=m"
        //        + "&signature=115D5A2D8C0E49A0476979D433995DD6A0ADCAE6.3866DB61F02F867C4B5B4C4188762DDB1A225CA6"
        //        + "&title=B.o.B+feat.+Bruno+Mars+-+Nothing+on+you+-+Cover+by+%E9%98%BF%E7%A6%8F";        

        h.urlTitle = "Bunny Eating Raspberries!";

        _LogTaskDescription("讀取伺服器物件的大小(URL.openConnection.getContentLength)");
        int remoteObjectSize = UrlUtility.getUriSize(h.directUrl);
        if(remoteObjectSize == GlobalData.ERROR_FILE_SIZE){
            _error(UrlUtility.getLastMessage());
        }
        h.fileSize = remoteObjectSize;

        h.key = CommonUtility.toHashKey(h.fileName, h.url);

        int chunkTotal = remoteObjectSize / GlobalData.CHUNK_SIZE;
        if(chunkTotal * GlobalData.CHUNK_SIZE < remoteObjectSize){
            chunkTotal++;
        }
        h.chunkTotal = chunkTotal;
        h.chunkSize = GlobalData.CHUNK_SIZE;
        h.chunkCount = 0;

        _LogTaskDescription("記錄檔 '" + h.key + "' 的每個欄位已初始化");

        _LogTaskDescription("回存記錄檔 '" + h.key + "'");
        boolean storeOk = HistoryUtility.store(h);
        if(!storeOk){
            _error(HistoryUtility.getLastMessage());
        }
        
        _DoDownload(h);
        
        System.out.println(_GetTaskDescription());
        /* 
         * IF h.isDone
         * DELETE_IF_EXIST(filename);
         * RENAME(filename.dl, filename)
         */
    }
    
    private void _InitializeLocalFiles(History H){
        _LogTaskDescription("建立一個大小為 " + H.fileSize + " bytes 的本機檔案，"
                + "檔名 '" + H.fileNameDL + "'");
        boolean isCreateOk = FileIOUtility.createFile(H.fileNameDL, H.fileSize);
        if(!isCreateOk){
            _error(FileIOUtility.getLastMessage());
        }
    }
    
    private void _DoDownload(History H){
        if(H.isDone){
            _LogTaskDescription("下載完成");

            return;
        }
        
        if(!H.isDone && H.chunkCount == 0){
            _LogTaskDescription("初始化新的下載作業, '" + H.key + "'");
            _InitializeLocalFiles(H);
        }

        _LogTaskDescription("以 URL.openStream 取得 InputStream");
        InputStream urlInputStream = UrlUtility.getInputStreamFromUrl(H.directUrl);
        if(urlInputStream == null){
            _error(UrlUtility.getLastMessage());
        }

        int offset = H.chunkCount * H.chunkSize;
        _LogTaskDescription("把 URL.openStream.skip 位移到 " + offset + " byte(s)");
        boolean urlPosition = FileIOUtility.positionInputStream(urlInputStream, offset);
        if(!urlPosition){
            _error(FileIOUtility.getLastMessage());
        }

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        while(!H.isDone){
            int bufferSize = HistoryUtility.nextChunkSize(H);
            byte[] buffer = null;
            
            _LogTaskDescription("從伺服器讀入 " + bufferSize + " byte(s)");
            try{
                buffer = FileIOUtility.readFromInputStream(
                        urlInputStream, bufferSize, byteStream); 
            }catch(Exception E){
                _error(E.getMessage());
            }
        
            atomicFileSave(H, buffer);

            /* update and save history */
            HistoryUtility.updateChunkCount(H);
            HistoryUtility.store(H);
        }

        _LogTaskDescription("下載完成");
        
        /* RENAME */
        
    }

    private void atomicFileSave(History H, byte[] Data){
        _LogTaskDescription("開檔 '" + H.fileNameDL + "'");
        RandomAccessFile file = null;
        try{
            file = new RandomAccessFile(H.fileNameDL, "rw");
        }catch(Exception E){
            _error(E.getMessage());
        }
        
        int fileOffset = H.chunkCount * H.chunkSize;
        _LogTaskDescription("檔案 seek() 位移, offset=" + fileOffset + " byte(s)");
        try{
            file.seek(fileOffset);
        }catch(Exception E){
            _error(E.getMessage());
        }

        _LogTaskDescription("寫入本機檔案 '" + H.fileNameDL + "' , 區塊號碼 "
                + H.chunkCount + " , 大小 " + Data.length);
        try{
            file.write(Data);
        }catch(Exception E){
            _error(E.getMessage());
        }
        
        /* close local file */
        _LogTaskDescription("關閉檔案 '" + H.fileNameDL + "'");
        try{
            file.close();
        }catch(Exception E){
            _error(E.getMessage());
        }
    }

    public static void main(String[] args){
        //new Start().start();
        new Start().testDownload(true);
    }
}
