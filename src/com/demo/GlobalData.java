package com.demo;

public class GlobalData{
    /* global constants */
    
    static final int ERROR_FILE_SIZE = -1;
    
    static final String EMPTY_LINE = "";
    static final String EMPTY_STRING = EMPTY_LINE;
    static final String EMPTY_STRING_QUOTED = EMPTY_STRING + '"' + '"';
    static final String TAB = "    ";
    
    static final String NEWLINE = System.getProperty("line.separator");
    static final int CHUNK_SIZE = 1048576;

    static final String URL_CONTENT_TYPE_BINARY_STREAM = "application/octet-stream";

    static final String PROPERTIES_MARK_NAME = GlobalData.class.getCanonicalName();
    static final String PROPERTIES_MARK_VALUE = "E7785506-E315-4A48-BEB1-119FA763E899";
    
    static final int NORMAL_TERMINATION = 0;
    static final int ABNORMAL_TERMINATION = 1;
    
    static final String DOWNLOADING_FILE_NAME_EXTENSION = ".DL";
    
    /* global variables */
    
    static boolean PRINT_DEBUG_INFO = true;
}
