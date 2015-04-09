package cmpsc488.lockout.security;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.security.KeyStore;

import cmpsc488.lockout.ServerRequest;

/**
 * Created by Gal on 4/7/2015.
 */
public class SecureServerRequest extends ServerRequest {

    public static final String SECURE_SCHEME = "https://";
    public static final String SECURE_HOST = "146.186.64.168";
    public static final int SECURE_PORT = 6918;


    public SecureServerRequest() {
        setScheme(SECURE_SCHEME);
        setHost(SECURE_HOST);
        setPort(SECURE_PORT);
    }

    @Override
    public synchronized HttpClient getHTTPClient() {

        try {

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new LockOutSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 6917));
            registry.register(new Scheme("https", sf, 6918));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);

        } catch (Exception e) {

            return super.getHTTPClient();

        }
    }
}