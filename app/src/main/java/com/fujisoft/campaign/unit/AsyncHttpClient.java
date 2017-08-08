package com.fujisoft.campaign.unit;

import android.content.Context;

import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.GZIPInputStream;

public class AsyncHttpClient {
    private static final int DEFAULT_MAX_CONNECTIONS = 100;
    private static final int DEFAULT_SOCKET_TIMEOUT = 30 * 1000;
    private static final int DEFAULT_MAX_RETRIES = 5;
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private static int maxConnections = DEFAULT_MAX_CONNECTIONS;
    private static int socketTimeout = DEFAULT_SOCKET_TIMEOUT;

    private final DefaultHttpClient httpClient;
    private final HttpContext httpContext;
    private ThreadPoolExecutor threadPool;
    private final Map<Context, List<WeakReference<Future<?>>>> requestMap;
    private final Map<String, String> clientHeaderMap;

    public AsyncHttpClient() {
        BasicHttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setTimeout(httpParams, socketTimeout);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
                new ConnPerRouteBean(maxConnections));
        ConnManagerParams.setMaxTotalConnections(httpParams,
                DEFAULT_MAX_CONNECTIONS);

        HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, socketTimeout);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams,
                DEFAULT_SOCKET_BUFFER_SIZE);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams
                .setUserAgent(
                        httpParams,
                        "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                                + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory
                .getSocketFactory(), 443));
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
                httpParams, schemeRegistry);

        httpContext = new SyncBasicHttpContext(new BasicHttpContext());
        httpClient = new DefaultHttpClient(cm, httpParams);
        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) {
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
                for (String header : clientHeaderMap.keySet()) {
                    request.addHeader(header, clientHeaderMap.get(header));
                }
            }
        });

        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context) {
                final HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(response
                                    .getEntity()));
                            break;
                        }
                    }
                }
            }
        });

        httpClient.setHttpRequestRetryHandler(new RetryHandler(
                DEFAULT_MAX_RETRIES));

        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        requestMap = new WeakHashMap<Context, List<WeakReference<Future<?>>>>();
        clientHeaderMap = new HashMap<String, String>();

    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public DefaultHttpClient getDefHttpClient() {
        return this.httpClient;
    }

    public HttpContext getHttpContext() {
        return this.httpContext;
    }

    public void setCookieStore(CookieStore cookieStore) {
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    public void setThreadPool(ThreadPoolExecutor threadPool) {
        this.threadPool = threadPool;
    }

    public void setUserAgent(String userAgent) {
        HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
    }

    public void setTimeout(int timeout) {
        final HttpParams httpParams = this.httpClient.getParams();
        ConnManagerParams.setTimeout(httpParams, timeout);
        HttpConnectionParams.setSoTimeout(httpParams, timeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
    }

    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.httpClient.getConnectionManager().getSchemeRegistry()
                .register(new Scheme("https", sslSocketFactory, 443));
    }

    public void addHeader(String header, String value) {
        clientHeaderMap.put(header, value);
    }

    public void setBasicAuth(String user, String pass) {
        AuthScope scope = AuthScope.ANY;
        setBasicAuth(user, pass, scope);
    }

    public void setBasicAuth(String user, String pass, AuthScope scope) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                user, pass);
        this.httpClient.getCredentialsProvider().setCredentials(scope,
                credentials);
    }

    public void cancelRequests(Context context, boolean mayInterruptIfRunning) {
        List<WeakReference<Future<?>>> requestList = requestMap.get(context);
        if (requestList != null) {
            for (WeakReference<Future<?>> requestRef : requestList) {
                Future<?> request = requestRef.get();
                if (request != null) {
                    request.cancel(mayInterruptIfRunning);
                }
            }
        }
        requestMap.remove(context);
    }

    public void get(String url, AsyncHttpResponseHandler responseHandler) {
        get(null, url, null, responseHandler);
    }

    public void get(String url, RequestParams params,
                    AsyncHttpResponseHandler responseHandler) {
        get(null, url, params, responseHandler);// modify by mm 2013-4-2
    }

    public void get(Context context, String url,
                    AsyncHttpResponseHandler responseHandler) {
        get(context, url, null, responseHandler);
    }

    public void get(Context context, String url, RequestParams params,
                    AsyncHttpResponseHandler responseHandler) {
        sendRequest(httpClient, httpContext,
                new HttpGet(getUrlWithQueryString(url, params)), null,
                responseHandler, context);
    }

    public void get(Context context, String url, Header[] headers,
                    RequestParams params, AsyncHttpResponseHandler responseHandler) {
        HttpUriRequest request = new HttpGet(getUrlWithQueryString(url, params));
        if (headers != null)
            request.setHeaders(headers);
        sendRequest(httpClient, httpContext, request, null, responseHandler,
                context);
    }

    public void post(String url, AsyncHttpResponseHandler responseHandler) {
        post(null, url, null, responseHandler);
    }

    public void post(String url, RequestParams params,
                     AsyncHttpResponseHandler responseHandler) {
        post(null, getUrlWithQueryString(url, params), params, responseHandler);// modify
        // by
        // mm
        // 2013-4-2
        // 加密
    }

    public void post(Context context, String url, RequestParams params,
                     AsyncHttpResponseHandler responseHandler) {
        post(context, url, paramsToEntity(params), null, responseHandler);
    }

    public void post(Context context, String url, HttpEntity entity,
                     String contentType, AsyncHttpResponseHandler responseHandler) {
        sendRequest(httpClient, httpContext,
                addEntityToRequestBase(new HttpPost(url), entity), contentType,
                responseHandler, context);
    }

    public void post(Context context, String url, Header[] headers,
                     RequestParams params, String contentType,
                     AsyncHttpResponseHandler responseHandler) {
        HttpEntityEnclosingRequestBase request = new HttpPost(url);
        if (params != null)
            request.setEntity(paramsToEntity(params));
        if (headers != null)
            request.setHeaders(headers);
        sendRequest(httpClient, httpContext, request, contentType,
                responseHandler, context);
    }

    public void post(Context context, String url, Header[] headers,
                     HttpEntity entity, String contentType,
                     AsyncHttpResponseHandler responseHandler) {
        HttpEntityEnclosingRequestBase request = addEntityToRequestBase(
                new HttpPost(url), entity);
        if (headers != null)
            request.setHeaders(headers);
        sendRequest(httpClient, httpContext, request, contentType,
                responseHandler, context);
    }

    public void put(String url, AsyncHttpResponseHandler responseHandler) {
        put(null, url, null, responseHandler);
    }

    public void put(String url, RequestParams params,
                    AsyncHttpResponseHandler responseHandler) {
        put(null, url, params, responseHandler);
    }

    public void put(Context context, String url, RequestParams params,
                    AsyncHttpResponseHandler responseHandler) {
        put(context, url, paramsToEntity(params), null, responseHandler);
    }

    public void put(Context context, String url, HttpEntity entity,
                    String contentType, AsyncHttpResponseHandler responseHandler) {
        sendRequest(httpClient, httpContext,
                addEntityToRequestBase(new HttpPut(url), entity), contentType,
                responseHandler, context);
    }

    public void put(Context context, String url, Header[] headers,
                    HttpEntity entity, String contentType,
                    AsyncHttpResponseHandler responseHandler) {
        HttpEntityEnclosingRequestBase request = addEntityToRequestBase(
                new HttpPut(url), entity);
        if (headers != null)
            request.setHeaders(headers);
        sendRequest(httpClient, httpContext, request, contentType,
                responseHandler, context);
    }

    public void delete(String url, AsyncHttpResponseHandler responseHandler) {
        delete(null, url, responseHandler);
    }

    public void delete(Context context, String url,
                       AsyncHttpResponseHandler responseHandler) {
        final HttpDelete delete = new HttpDelete(url);
        sendRequest(httpClient, httpContext, delete, null, responseHandler,
                context);
    }

    public void delete(Context context, String url, Header[] headers,
                       AsyncHttpResponseHandler responseHandler) {
        final HttpDelete delete = new HttpDelete(url);
        if (headers != null)
            delete.setHeaders(headers);
        sendRequest(httpClient, httpContext, delete, null, responseHandler,
                context);
    }

    protected void sendRequest(DefaultHttpClient client,
                               HttpContext httpContext, HttpUriRequest uriRequest,
                               String contentType, AsyncHttpResponseHandler responseHandler,
                               Context context) {
        if (contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }

        Future<?> request = threadPool.submit(new AsyncHttpRequest(client,
                httpContext, uriRequest, responseHandler));

        if (context != null) {
            // Add request to request map
            List<WeakReference<Future<?>>> requestList = requestMap
                    .get(context);
            if (requestList == null) {
                requestList = new LinkedList<WeakReference<Future<?>>>();
                requestMap.put(context, requestList);
            }

            requestList.add(new WeakReference<Future<?>>(request));
        }
    }

    public static String getUrlWithQueryString(String url, RequestParams params) {
        if (params != null) {
            // String vcode = "";
            // params.put("a", "1");
            String t = System.currentTimeMillis() / 1000 + "";
            params.put("e", t);
            String paramString = params.getParamString();
            url += "?" + paramString;

            List<BasicNameValuePair> paramsList = params.getParamsList();
            @SuppressWarnings("unused")
            String str = "";
            for (int i = 0; i < paramsList.size(); i++) {
                BasicNameValuePair b = paramsList.get(i);
                str += b.getValue();
            }
            // vcode = Utils.hmacSha1(str, Constants.KEY);
            // vcode = vcode.replace("\n", "");
            // try {
            // //params.put("key", URLEncoder.encode(vcode, "UTF-8"));
            // vcode = URLEncoder.encode(vcode, "UTF-8");
            // } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
            // }
            // Log.i("async", "vcode.lenght=="+vcode.length());
            // url += "&key="+vcode;
            // Log.i("async", "url==="+url);
        }
        return url;
    }

    private HttpEntity paramsToEntity(RequestParams params) {
        HttpEntity entity = null;

        if (params != null) {
            String vcode = "";
            String t = System.currentTimeMillis() + "";

            List<BasicNameValuePair> paramsList = params.getParamsList();
            for (int i = 0; i < paramsList.size(); i++) {
                BasicNameValuePair b = paramsList.get(i);
                if (b.getName().equals("r")) {
                    vcode = Utils.getMD5((Constants.KEY + b.getValue() + t)
                            .getBytes());
                }
            }
            params.put("vcode", vcode);
            params.put("t", t);

            entity = params.getEntity();
        }

        return entity;
    }

    private HttpEntityEnclosingRequestBase addEntityToRequestBase(
            HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {
        if (entity != null) {
            requestBase.setEntity(entity);
        }

        return requestBase;
    }

    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }
}