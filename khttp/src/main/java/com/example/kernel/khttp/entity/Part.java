package com.example.kernel.khttp.entity;


/**
 * Created by kernel on 15/3/1.
 */

import java.io.OutputStream;

public interface Part {
    public void writeTo(OutputStream outstream);
    public long getContentLength();
}
