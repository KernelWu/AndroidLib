package com.example.kernel.khttp.entity;


import com.example.kernel.khttp.utils.MultityEntityUtil;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by kernel on 15/3/1.
 */
public class StringPart extends BasicPart{
    private HeaderProvider mHeaderProvider;
    private Boundary mBoundary;

    private String name;
    private String value;
    private String encoding;

    public StringPart(Boundary boundary,final String name,final String value, final String encoding) {
        this.mBoundary = boundary;
        this.name = name;
        this.value = value;
        this.encoding = encoding;
        mHeaderProvider = new HeaderProvider() {
            @Override
            public String getContentDisposition() {
                return MultityEntityUtil.HEADER_CONTENT_DISPOSITION
                        + MultityEntityUtil.COLON + MultityEntityUtil.SPACE
                        + String.format(MultityEntityUtil.CONTENT_DISPOSITION_FORMAT_TEXT, name)
                        + MultityEntityUtil.CRLF;
            }

            @Override
            public String getContentType() {
                return MultityEntityUtil.HEADER_CONTENT_TYPE
                        + MultityEntityUtil.COLON + MultityEntityUtil.SPACE
                        + String.format(MultityEntityUtil.CONTENT_TYPE_TEXT_FORMAT, encoding)
                        + MultityEntityUtil.CRLF;
            }

            @Override
            public String getContentTransferEncoding() {
                return MultityEntityUtil.HEADER_CONTENT_TRANSFER_ENCODING
                        + MultityEntityUtil.COLON + MultityEntityUtil.SPACE
                        + MultityEntityUtil.EIGHT_BITS
                        + MultityEntityUtil.CRLF;
            }
        };
    }

    @Override
    public void writeTo(OutputStream outstream) {
        try {
            outstream.write(getBodyHeader());
            outstream.write(MultityEntityUtil.CRLF.getBytes());
            outstream.write(value.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getContentLength() {
        return getBodyHeader().length + MultityEntityUtil.LENGTH_CRLF_BYTE
                + value.getBytes().length;
    }

    @Override
    public HeaderProvider getHeaderProvide() {
        return mHeaderProvider;
    }

    @Override
    public Boundary getBoundary() {
        return mBoundary;
    }
}
