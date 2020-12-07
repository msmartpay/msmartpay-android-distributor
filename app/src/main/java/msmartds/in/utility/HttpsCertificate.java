package msmartds.in.utility;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

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
            InputStream is = this.context.getApplicationContext().getResources().openRawResource(R.raw.msmartpay_in);
            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer, StandardCharsets.UTF_8);
            String certificates = writer.toString();
            String certificateArray[] = certificates.split("-----BEGIN CERTIFICATE-----");

            // creating a KeyStore containing our trusted CAs
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
            for (int i = 1; i < certificateArray.length; i++) {
                certificateArray[i] = "-----BEGIN CERTIFICATE-----" + certificateArray[i];

                // generate input stream for certificate factory
                InputStream stream = IOUtils.toInputStream(certificateArray[i]);

                // CertificateFactory
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                // certificate
                Certificate ca;
                try {
                    ca = cf.generateCertificate(stream);
                } finally {
                    is.close();
                }

                ks.setCertificateEntry("ca" + i, ca);
            }
            // TrustManagerFactory
            String algorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
            // Create a TrustManager that trusts the CAs in our KeyStore
            tmf.init(ks);

            // Create a SSLContext with the certificate that uses tmf (TrustManager)
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            SSLSocketFactory localSSLSocketFactory = sslContext.getSocketFactory();
            return localSSLSocketFactory;

        } catch (Exception localException) {
            throw new AssertionError(localException);
        }
    }
}
