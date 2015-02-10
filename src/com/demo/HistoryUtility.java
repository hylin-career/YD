package com.demo;

import java.io.*;
import java.util.*;

public class HistoryUtility{
    private static String mLastMessage = GlobalData.EMPTY_LINE;
    static String getLastMessage(){
        return(mLastMessage);
    }

    static final String HISTORY_KEY           = "KEY"         ;
    static final String HISTORY_URL           = "URL"         ;
    static final String HISTORY_URL_TITLE     = "URL_TITLE"   ;
    static final String HISTORY_DIRECT_URL    = "DIRECT_URL"  ;
    static final String HISTORY_FILENAME      = "FILENAME"    ;
    static final String HISTORY_FILENAME_DL   = "FILENAME_DL" ;
    static final String HISTORY_FILE_SIZE     = "FILE_SIZE"   ;
    static final String HISTORY_IS_DONE       = "IS_DONE"     ;
    static final String HISTORY_CHUNK_SIZE    = "CHUNK_SIZE"  ;
    static final String HISTORY_CHUNK_TOTAL   = "CHUNK_TOTAL" ;
    static final String HISTORY_CHUNK_COUNT   = "CHUNK_COUNT" ;

    //    static History factory(
//            String YoutubeUrl, String DirectYoutubeUrl,
//            String YoutubeVideoTitle, String FileName, String FileNameDL,
//            int FileSize, boolean IsDone,
//            int ChunkSize, int ChunkTotal, int ChunkCount  )
//    {
//        History h = new History();
//        
//        h.key = CommonUtility.toHashKey(FileName, YoutubeUrl);
//        
//        h.youtubeUrl =        YoutubeUrl;
//        h.directYoutubeUrl =  DirectYoutubeUrl;
//        h.youtubeVideoTitle = YoutubeVideoTitle;
//        h.fileName =          FileName;
//        h.fileNameDL =        FileNameDL;
//        h.fileSize =          FileSize;
//        h.isDone =            IsDone; 
//        h.chunkSize =         ChunkSize;
//        h.chunkTotal =        ChunkTotal;
//        h.chunkCount =        ChunkCount;
//        
//        
//        return(h);
//    }

    private static final String HISTORY_STORE_DIRECTORY = ".hisdir";

    private static void bindPropertiesToHistory(Properties P, History H){
        H.key        = P.getProperty(HISTORY_KEY        );
        H.url        = P.getProperty(HISTORY_URL        );
        H.urlTitle   = P.getProperty(HISTORY_URL_TITLE  );
        H.directUrl  = P.getProperty(HISTORY_DIRECT_URL );
        H.fileName   = P.getProperty(HISTORY_FILENAME   );
        H.fileNameDL = P.getProperty(HISTORY_FILENAME_DL);
        H.fileSize   = Integer.parseInt    (  P.getProperty(HISTORY_FILE_SIZE  )  );
        H.isDone     = Boolean.parseBoolean(  P.getProperty(HISTORY_IS_DONE    )  );
        H.chunkSize  = Integer.parseInt    (  P.getProperty(HISTORY_CHUNK_SIZE )  );
        H.chunkTotal = Integer.parseInt    (  P.getProperty(HISTORY_CHUNK_TOTAL)  );
        H.chunkCount = Integer.parseInt    (  P.getProperty(HISTORY_CHUNK_COUNT)  );
    }
    
    private static void bindHistoryToProperties(History H, Properties P){
        P.setProperty(HISTORY_KEY         , "" + H.key       );
        P.setProperty(HISTORY_URL         , "" + H.url       );
        P.setProperty(HISTORY_URL_TITLE   , "" + H.urlTitle  );
        P.setProperty(HISTORY_DIRECT_URL  , "" + H.directUrl );
        P.setProperty(HISTORY_FILENAME    , "" + H.fileName  );
        P.setProperty(HISTORY_FILENAME_DL , "" + H.fileNameDL);
        P.setProperty(HISTORY_FILE_SIZE   , "" + H.fileSize  );
        P.setProperty(HISTORY_IS_DONE     , "" + H.isDone    );
        P.setProperty(HISTORY_CHUNK_SIZE  , "" + H.chunkSize );
        P.setProperty(HISTORY_CHUNK_TOTAL , "" + H.chunkTotal);
        P.setProperty(HISTORY_CHUNK_COUNT , "" + H.chunkCount);
    }
    
    private static boolean ensureDirectoryUsability(){
        File dir = new File(HISTORY_STORE_DIRECTORY);
        
        if(dir.exists() && dir.isDirectory()){
            return(true);
        }
        
        if(dir.exists() && !dir.isDirectory()){
            mLastMessage = "'" + HISTORY_STORE_DIRECTORY + "' 不是目錄，與檔案撞名啦！" ;
            return(false);
        }
        
        if(!dir.exists()){
            if(dir.mkdir()){
                return(true);
            }
            
            mLastMessage = "目錄 '" + HISTORY_STORE_DIRECTORY + "' 不存在，且無法建立目錄";
            return(false);
        }
        
        /* 理論上不可能執行到這行 */
        return(false);
    }
    
    static History load(String HashKey){
        if(!ensureDirectoryUsability()){
            return(null);
        }
        
        Properties props = PropertiesUtility.load(
                new File(HISTORY_STORE_DIRECTORY, HashKey)  );
        
        History h = Factory.createEmptyHistory();
        bindPropertiesToHistory(props, h);
        
        return(h);
    }
    
    static boolean store(History H){
        if(!ensureDirectoryUsability()){
            return(false);
        }

        /* 防保，避免粗心忘記填寫 key ... */
        if(H.key == null || H.key.length() == 0){
            H.key = CommonUtility.toHashKey(H.fileName, H.url);
        }
        
        Properties props = new Properties();
        bindHistoryToProperties(H, props);
        
        File f = new File(HISTORY_STORE_DIRECTORY, H.key);
        boolean ok = PropertiesUtility.save(f, props);
        
        if(!ok){
            mLastMessage = "HistoryUtility::store(History), "
                    + PropertiesUtility.getLastMessage();
            
            return(false);
        }
        
        return(true);
    }

    static int nextChunkSize(History H){
        int size = (H.chunkCount + 1 == H.chunkTotal)
                ? (H.fileSize % H.chunkSize)
                : H.chunkSize ;

        return(size);
    }
    
    static void setFileName(History H, String FileName){
        H.fileName = FileName;
        
        H.fileNameDL = HISTORY_STORE_DIRECTORY
                + File.separator
                + FileName + GlobalData.DOWNLOADING_FILE_NAME_EXTENSION;
    }
    
    static void updateChunkCount(History H){
        H.chunkCount++;

        if(H.chunkCount == H.chunkTotal){
            H.isDone = true;
        }
    }
    
    /* Factory as a static inner class */
    static class Factory{
        static History createEmptyHistory(){
            History h = new History();
            
            return(h);
        }
    }
}
