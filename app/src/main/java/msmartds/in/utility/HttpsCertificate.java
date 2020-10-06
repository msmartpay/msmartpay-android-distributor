package msmartds.in.utility;

import android.content.Context;

import java.io.BufferedInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import msmartds.in.R;


public class HttpsCertificate {
    private static char[] KEYSTORE_PASSWORD = {100, 101, 101, 112, 52, 49, 48, 57};
    Context context;

    public HttpsCertificate(Context paramContext) {
        this.context = paramContext;
    }

    public SSLSocketFactory newSslSocketFactory() {
        try {
            Certificate localCertificate = CertificateFactory.getInstance("X.509").generateCertificate(new BufferedInputStream(this.context.getApplicationContext().getResources().openRawResource(R.raw.msmartpay_in)));
            System.out.println("ca=" + ((X509Certificate) localCertificate).getSubjectDN());
            KeyStore localKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            localKeyStore.load(null, null);
            localKeyStore.setCertificateEntry("ca", localCertificate);
            TrustManagerFactory localTrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            localTrustManagerFactory.init(localKeyStore);
            SSLContext localSSLContext = SSLContext.getInstance("TLS");
            localSSLContext.init(null, localTrustManagerFactory.getTrustManagers(), null);
            SSLSocketFactory localSSLSocketFactory = localSSLContext.getSocketFactory();
            return localSSLSocketFactory;
        } catch (Exception localException) {
            throw new AssertionError(localException);
        }
    }
}
