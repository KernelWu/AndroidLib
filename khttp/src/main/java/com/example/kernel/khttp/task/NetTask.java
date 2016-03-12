package com.example.kernel.khttp.task;


import com.example.kernel.khttp.response.Response;

public interface NetTask {
    public Response work();
    public void cancel();
}
