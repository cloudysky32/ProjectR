package com.projectr.code.http;

import org.apache.http.conn.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.tsccm.*;
import org.apache.http.params.*;
/*
public class HttpClientFactory {

	public static DefaultHttpClient getThreadSafeClient() {

		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();

		HttpParams params = client.getParams();

//		params.setParameter("http.protocol.expect-continue", false);
//		params.setParameter("http.connection.timeout", 5000);
//		params.setParameter("http.socket.timeout", 5000);

		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);

		return client;
	}
}
*/

public class HttpClientFactory {
	private static DefaultHttpClient client;

	public synchronized static DefaultHttpClient getThreadSafeClient() {
		
		if (client != null) {
			return client;
		}
		
		client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();
		
		HttpParams params = client.getParams();
		
//		params.setParameter("http.protocol.expect-continue", false);
//		params.setParameter("http.connection.timeout", 5000);
//		params.setParameter("http.socket.timeout", 5000);

		
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
		return client;
	}
}
