package csfyp.cs_fyp_android.lib;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by ray on 18/1/2017.
 */

public class SSL {
    private static String serverCert ;

    public static void setServerCert(InputStream is) throws java.io.IOException{
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }
        serverCert= total.toString();

        tm = new X509TrustManager() {

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {

                X509Certificate[] trustedCerts = new X509Certificate[1];
                try{
                    InputStream is = new ByteArrayInputStream(serverCert.getBytes(StandardCharsets.UTF_8));
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    X509Certificate cert = (X509Certificate)cf.generateCertificate(is);
                    is.close();
                    trustedCerts[0] = cert;
                }catch(Exception e){
                    e.printStackTrace();
                }
                return trustedCerts;
            }

            @Override
            public void checkClientTrusted( X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                boolean match = false;
                try{
                    InputStream is = new ByteArrayInputStream(serverCert.getBytes(StandardCharsets.UTF_8));
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    X509Certificate cert = (X509Certificate)cf.generateCertificate(is);
                    is.close();
                    for(X509Certificate c : chain){
                        if(c.equals(cert)){
                            match = true;
                        }
                    }
                }catch(Exception e){
                    throw new CertificateException();
                }

                if(!match)
                    throw new CertificateException();
            }

        };

    }


    public static java.lang.String getServerCert() {
        return serverCert;
    }

    public static SSLSocketFactory getNewSSL(){
        SSLContext sslContext = null;
        KeyManagerFactory keyManagerFactory=null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[] { tm }, null);
        }catch(Exception e){

        }
        return sslContext.getSocketFactory();
    }


    private static X509TrustManager tm;

    public static X509TrustManager getTm() {
        return tm;
    }
}
