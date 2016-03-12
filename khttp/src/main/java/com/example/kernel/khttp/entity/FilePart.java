package com.example.kernel.khttp.entity;


import com.example.kernel.khttp.response.Response;
import com.example.kernel.khttp.utils.MultityEntityUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by kernel on 15/3/1.
 */
public class FilePart extends BasicPart{
    private HeaderProvider mHeaderProvider;
    private Boundary mBoundary;

    private String name;
    private String fileName;
    private File file;

    private Response.ProgressListener mProgressListener;

    public FilePart(Boundary boundary, final String name, final String fileName,
                    File file) {
        this.mBoundary = boundary;
        this.name = name;
        this.fileName = fileName;
        this.file = file;
        mHeaderProvider = new HeaderProvider() {
            @Override
            public String getContentDisposition() {
                return MultityEntityUtil.HEADER_CONTENT_DISPOSITION
                        + MultityEntityUtil.COLON + MultityEntityUtil.SPACE
                        + String.format(MultityEntityUtil.CONTENT_DISPOSITION_FORMAT_STEAM, name, fileName)
                        + MultityEntityUtil.CRLF;
            }

            @Override
            public String getContentType() {
                return MultityEntityUtil.HEADER_CONTENT_TYPE
                        + MultityEntityUtil.COLON + MultityEntityUtil.SPACE
                        + MultityEntityUtil.CONTENT_TYPE_STREAM
                        + MultityEntityUtil.CRLF;
            }

            @Override
            public String getContentTransferEncoding() {
                return MultityEntityUtil.HEADER_CONTENT_TRANSFER_ENCODING
                        + MultityEntityUtil.COLON + MultityEntityUtil.SPACE
                        + MultityEntityUtil.BINARY
                        + MultityEntityUtil.CRLF;
            }
        };
    }

    @Override
    public HeaderProvider getHeaderProvide() {
        return mHeaderProvider;
    }

    @Override
    public Boundary getBoundary() {
        return mBoundary;
    }

    @Override
    public void writeTo(OutputStream outstream) {
        try {
            outstream.write(getBodyHeader());
            outstream.write(MultityEntityUtil.CRLF.getBytes());
            FileInputStream fis = new FileInputStream(file);
            long completeSize = 0;
            long fileSize = file.length();
            int readLength = 0;
            byte[] buffer = new byte[2048];
            while((readLength = fis.read(buffer)) > 0) {
                outstream.write(buffer, 0, readLength);
                completeSize += readLength;
                if(mProgressListener != null) {
                    mProgressListener.onProgress(completeSize,
                            fileSize);
                }
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getContentLength() {
        return getBodyHeader().length + MultityEntityUtil.LENGTH_CRLF_BYTE
                + file.length();
    }

    /** 设置文件传输进度监听接口*/
    public void setProgressListener(Response.ProgressListener listener) {
        this.mProgressListener = listener;
    }

    public Response.ProgressListener getProgressListener() {
        return mProgressListener;
    }
}
