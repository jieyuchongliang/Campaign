package com.fujisoft.campaign.unit;

import android.os.SystemClock;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

import javax.net.ssl.SSLHandshakeException;

class RetryHandler implements HttpRequestRetryHandler {
    private static final int RETRY_SLEEP_TIME_MILLIS = 1500;
    private static HashSet<Class<?>> exceptionWhitelist = new HashSet<Class<?>>();
    private static HashSet<Class<?>> exceptionBlacklist = new HashSet<Class<?>>();

    static {
        exceptionWhitelist.add(NoHttpResponseException.class);
        exceptionWhitelist.add(UnknownHostException.class);
        exceptionWhitelist.add(SocketException.class);

        exceptionBlacklist.add(InterruptedIOException.class);
        exceptionBlacklist.add(SSLHandshakeException.class);
    }

    private final int maxRetries;

    public RetryHandler(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public boolean retryRequest(IOException exception, int executionCount,
                                HttpContext context) {
        boolean retry = true;

        Boolean b = (Boolean) context
                .getAttribute(ExecutionContext.HTTP_REQ_SENT);
        boolean sent = (b != null && b.booleanValue());

        if (executionCount > maxRetries) {
            retry = false;
        } else if (exceptionBlacklist.contains(exception.getClass())) {
            retry = false;
        } else if (exceptionWhitelist.contains(exception.getClass())) {
            retry = true;
        } else if (!sent) {
            retry = true;
        }

        if (retry) {
            HttpUriRequest currentReq = (HttpUriRequest) context
                    .getAttribute(ExecutionContext.HTTP_REQUEST);
            String requestType = currentReq.getMethod();
            retry = !requestType.equals("POST");
        }

        if (retry) {
            SystemClock.sleep(RETRY_SLEEP_TIME_MILLIS);
        } else {
            exception.printStackTrace();
        }

        return retry;
    }
}