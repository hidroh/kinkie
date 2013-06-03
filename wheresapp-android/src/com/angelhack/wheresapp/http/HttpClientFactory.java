package com.angelhack.wheresapp.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

/**
 * Base class to handle all HTTP requests. Responses will be return through the
 * call back objects
 */
public class HttpClientFactory {
	
private static final String TAG = HttpClientFactory.class.getName();
	
	private static final String USER_AGENT = "User-Agent";
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";
	
	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
	private static final int DEFAULT_MAX_CONNECTIONS = 5;

	private static final int SECOND_IN_MILLIS = (int) DateUtils.SECOND_IN_MILLIS;

	/**
     * Generate and return a {@link AsyncHttpClient} configured for general use,
     * including setting an application-specific user-agent string, session time
     * out and a {@link PersistentCookieStore}
     */
	public static AsyncHttpClient getAsynHttpClient(Context context) {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		String userAgent = buildUserAgent(context);
		asyncHttpClient.setUserAgent(userAgent);

		asyncHttpClient.setTimeout(10 * SECOND_IN_MILLIS);
		
		final HttpClient client = asyncHttpClient.getHttpClient();
		if (client instanceof DefaultHttpClient) {
			((DefaultHttpClient) client).addRequestInterceptor(generateHttpRequestInterceptor());
			((DefaultHttpClient) client).addResponseInterceptor(generateHttpResponseInterceptor());
		}
		
		return asyncHttpClient;
	}
	
	/**
	 * Implementation of the Apache {@link DefaultHttpClient} that is configured
	 * with reasonable default settings and registered schemes for Android, and
	 * also lets the user add {@link HttpRequestInterceptor} classes.
	 */
	public static AndroidHttpClient getAndroidHttpClient(Context context) {
		final String userAgent = buildUserAgent(context);
		final AndroidHttpClient client = AndroidHttpClient.newInstance(userAgent, context);
		return client;
	}

    /**
     * Generate and return a {@link HttpClient} configured for general use,
     * including setting an application-specific user-agent string.
     */
    public static HttpClient getDefaultHttpClient(Context context) {
        final HttpParams params = new BasicHttpParams();
        
        // Use generous timeouts for slow mobile networks
        ConnManagerParams.setTimeout(params, 20 * SECOND_IN_MILLIS);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(DEFAULT_MAX_CONNECTIONS));
        ConnManagerParams.setMaxTotalConnections(params, DEFAULT_MAX_CONNECTIONS);

        HttpConnectionParams.setConnectionTimeout(params, 20 * SECOND_IN_MILLIS);
        HttpConnectionParams.setSoTimeout(params, 20 * SECOND_IN_MILLIS);
        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSocketBufferSize(params, DEFAULT_SOCKET_BUFFER_SIZE);
        
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(params, buildUserAgent(context));
        
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ThreadSafeClientConnManager conman = new ThreadSafeClientConnManager(params, schemeRegistry);

        final DefaultHttpClient client = new DefaultHttpClient(conman, params);
        client.addRequestInterceptor(generateHttpRequestInterceptor());
        client.addResponseInterceptor(generateHttpResponseInterceptor());

        return client;
    }
	
	/**
     * Build and return a user-agent string that can identify this application
     * to remote servers. Contains the package name and version code.
     */
	private static String buildUserAgent(Context context) {
		try {
			final PackageManager manager = context.getPackageManager();
			final PackageInfo info = manager.getPackageInfo(
					context.getPackageName(), 0);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				return info.packageName + "/" 
						+ info.versionName + " ("
						+ info.versionCode + ") ("
						+ "Android " + Build.VERSION.RELEASE + ") ("
						+ Build.MANUFACTURER + ") ("
						+ Build.MODEL + ") ("
						+ Build.DISPLAY + ") (serial number "
						+ Build.SERIAL + ")";
			} else {
				return info.packageName + "/" 
						+ info.versionName + " ("
						+ info.versionCode + ") ("
						+ "Android " + Build.VERSION.RELEASE + ") ("
						+ Build.MANUFACTURER + ") ("
						+ Build.MODEL + ") ("
						+ Build.DISPLAY + ")";
			}
		} catch (NameNotFoundException e) {
			return null;
		}
	}
	
	private static HttpRequestInterceptor generateHttpRequestInterceptor() {
		return new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) {
            	StringBuilder builder = new StringBuilder();
            	Header[] headers = request.getAllHeaders();
            	for (int i = 0; i < headers.length; i++) {
					builder.append(headers[i].getName());
					builder.append(": ");
					builder.append(headers[i].getValue());
					builder.append(" ... ");
				}
            	Log.d(TAG, "HTTP request:\n" + builder.toString());
            }
        };
	}
	
	private static HttpResponseInterceptor generateHttpResponseInterceptor() {
		return new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context) {
                StringBuilder builder = new StringBuilder();
                Header[] headers = response.getAllHeaders();
                for (int i = 0; i < headers.length; i++) {
                	builder.append(headers[i].getName());
                	builder.append(": ");
                	builder.append(headers[i].getValue());
                	builder.append(" ... ");
				}
                Log.d(TAG, "HTTP response:\n" + builder.toString());
                
                /*
                final StatusLine statusLine = response.getStatusLine();
                final HttpEntity entity = response.getEntity();
                final Header type = entity.getContentType();
                final Header encoding = entity.getContentEncoding();
                
                if (encoding != null) {
                	LogUtils.d(TAG, "StatusCode: " + statusLine.getStatusCode()
							+ " ContentLength: " + entity.getContentLength()
							+ " bytes, contentEncoding: " + encoding.getValue()
							+ ", contentType: " + ((type == null) ? "unknown" : type.getValue()));
                	
                	// Inflate any responses compressed with gzip                	
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }*/
            }
        };
	}

    /**
     * Simple {@link HttpEntityWrapper} that inflates the wrapped
     * {@link HttpEntity} by passing it through {@link GZIPInputStream}.
     */
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
