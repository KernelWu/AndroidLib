package com.example.kernel.khttp.entity;

import com.example.kernel.khttp.response.Response.ProgressListener;

import org.apache.http.entity.AbstractHttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class MultiPartEntity extends AbstractHttpEntity {
    private ArrayList<Part> mParts;
    private Boundary mBoundary;

    // 文件上传进度回调接口
    private ProgressListener mProgressListener;

    // 用于设置每个FilePart上传进度的公用接口
    private ProgressListener innerFilePublicProgressListener;
    // 文件已上传的大小
    private long filesUploadCompleteSize = 0;
    // 要上传的文件的总大小
    private long filesTotalSize = 0;

    public MultiPartEntity(Boundary boundary) {
        this.mBoundary = boundary;
        this.mParts = new ArrayList<Part>();
        innerFilePublicProgressListener = new ProgressListener() {
            @Override
            public void onProgress(long completeSize, long totalSize) {
                filesUploadCompleteSize += completeSize;
                if(mProgressListener != null) {
                    mProgressListener.onProgress(filesUploadCompleteSize, filesTotalSize);
                }
            }
        };
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public long getContentLength() {
        long contentLength = 0;
        for(Part part : mParts) {
            contentLength += part.getContentLength();
        }
        contentLength += mBoundary.getEndBoundaryByte().length;
        return contentLength;
    }

    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        return null;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        filesTotalSize = getFilePartLength();
        for(Part part : mParts) {
            part.writeTo(outputStream);
        }
        outputStream.write(mBoundary.getEndBoundaryByte());
    }

    private long getFilePartLength() {
        long contentLength = 0;
        for(Part part : mParts) {
            if(part instanceof  FilePart) {
                contentLength += part.getContentLength();
            }
        }
        return contentLength;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    public void addPart(Part part) {
        for(Part p : mParts) {
            if(p == part) {
                throw new IllegalArgumentException("The part existed");
            }
        }
        if(part instanceof  FilePart) {
            ((FilePart)part).setProgressListener(innerFilePublicProgressListener);
        }
        this.mParts.add(part);
    }

    public void setProgressListener(ProgressListener listener) {
        mProgressListener = listener;
    }

    public ProgressListener getProgressListener() {
        return mProgressListener;
    }

}
