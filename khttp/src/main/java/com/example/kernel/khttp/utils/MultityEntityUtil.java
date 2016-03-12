package com.example.kernel.khttp.utils;

/**
 * Created by kernel on 15/3/1.
 */
public class MultityEntityUtil {

    /**
     * Header key
     */
    public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";

    /**
     * Special sign
     */
    public static final String CRLF = "\r\n";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String SPACE = " ";
    public static final String EQUAL = "=";
    public static final String DOUBLE_MINUS = "--";

    /**
     * Content Disposition value
     */
    public static final String CONTENT_DISPOSITION_FORMAT_TEXT = "form-data;name=\"%s\"";
    public static final String CONTENT_DISPOSITION_FORMAT_STEAM = "form-data;name=\"%s\";filename=\"%s\"";

    /**
     * Content type value
     */
    public static final String CONTENT_TYPE_TEXT = "text/plain";
    public static final String CONTENT_TYPE_STREAM = "application/octet-stream";
    public static final String CONTENT_TYPE_MULTITY_FORMAT = "multipart/form-data;boundary=%s";
    public static final String CONTENT_TYPE_TEXT_FORMAT = "text/plain;charset=%s";

    /**
     * Content transfer encoding value
     */
    public static final String BINARY = "binary";
    public static final String EIGHT_BITS = "8bit";

    /** Length byte*/
    public static final long LENGTH_CRLF_BYTE = CRLF.getBytes().length;


}