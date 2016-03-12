package com.example.kernel.khttp.entity;

/**
 * Created by kernel on 15/3/1.
 */
public abstract class BasicPart implements  Part{

    public interface HeaderProvider{
        public String getContentDisposition();
        public String getContentType();
        public String getContentTransferEncoding();
    }

    public abstract HeaderProvider getHeaderProvide();
    public abstract Boundary getBoundary();

    public byte[] getBodyHeader() {
        StringBuilder header = new StringBuilder();
        header.append(getBoundary().getStartBoundary());
        HeaderProvider mHeaderProvider = getHeaderProvide();
        header.append(mHeaderProvider.getContentDisposition());
        header.append(mHeaderProvider.getContentType());
        header.append(mHeaderProvider.getContentTransferEncoding());
        return header.toString().getBytes();
    }
}
