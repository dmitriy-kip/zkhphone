package com.progmatik.zkhphone.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.progmatik.zkhphone.R;
import com.progmatik.zkhphone.classes.myKeyValuesList;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class myHttpsRequest implements myConsts
{
    public enum sendMethod { smPOST, smGET }
    public enum requestState { rqsIdle, rqsLoading, rqsError, rqsComplete }
    private KeyManagerFactory mgrFact = null;
    private Context myContext;
    private String lastError = "";
    private requestState state = requestState.rqsIdle;
    private String jsonResponse = "";

    private myKeyValuesList params;

    public myHttpsRequest(Activity activity) {
        myContext = activity;
    }

    public requestState getState() {
        return this.state;
    }

    public String getJSON() {
        return this.jsonResponse;
    }

    public String getLastError() {
        return this.lastError;
    }


    public void setParams( myKeyValuesList params )
    {
        this.params = params;

    }

    public myKeyValuesList getParams()
    {
        return this.params;
    }

    private class HttpsTrustManager implements X509TrustManager {
        private TrustManager[] trustManagers;
        private final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};

        @Override
        public void checkClientTrusted(
                X509Certificate[] x509Certificates, String s)
                throws java.security.cert.CertificateException {

        }

        @Override
        public void checkServerTrusted(
                X509Certificate[] x509Certificates, String s)
                throws java.security.cert.CertificateException {

        }

        public boolean isClientTrusted(X509Certificate[] chain) {
            return true;
        }

        public boolean isServerTrusted(X509Certificate[] chain) {
            return true;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return _AcceptedIssuers;
        }

        public void allowAllSSL(String p12FileName, String p12Password) {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

            });


            InputStream is = null;
            FileInputStream fis = null;
            if (p12FileName != null) {
                try {
                    fis = new FileInputStream(p12FileName);
                }  catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                is = myContext.getResources().openRawResource(R.raw.progmatik);
            }

            try {
                mgrFact = KeyManagerFactory.getInstance("X509");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }

            KeyStore clientStore = null;
            try {
                clientStore = KeyStore.getInstance("PKCS12");
            } catch (KeyStoreException e1) {
                e1.printStackTrace();
            }

            try {
                assert clientStore != null;
                clientStore.load(( p12FileName != null ? fis : is ), ( p12Password != null ? p12Password : PKCS_PASSWD ).toCharArray() );
            } catch (NoSuchAlgorithmException | CertificateException | IOException e1) {
                e1.printStackTrace();
            }

            try {
                mgrFact.init(clientStore, ( p12Password != null ? p12Password : PKCS_PASSWD ).toCharArray());
            } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }

            SSLContext context = null;
            if (trustManagers == null) {
                trustManagers = new TrustManager[]{new HttpsTrustManager()};
            }

            try {
                context = SSLContext.getInstance("TLS");
                context.init(mgrFact.getKeyManagers(), trustManagers, new SecureRandom());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }

            HttpsURLConnection.setDefaultSSLSocketFactory(context != null ? context.getSocketFactory() : null);
        }
    }

    /*
    * Отправка https-запроса
    * */
    public void sendRequest(String subURL, String p12FileName, String p12Password, sendMethod method, JSONObject postData, String signature ){
        String rqUrl = BASE_URL+subURL;
        if ( method.equals( sendMethod.smGET ) && params.getItems().size() >  0) {
            Integer i = 0;
            ArrayList<String> keys = params.getKeys();
            for ( i = 0; i < keys.size(); i++ ) {
                rqUrl += "&"+keys.get(i)+"="+ Uri.encode(params.getItem( keys.get(i) ).getValue());
            }
        }

        this.jsonResponse = "";

        HttpsURLConnection conn = null;
        this.state = requestState.rqsLoading;
        try {

            HttpsTrustManager httpsManager = new HttpsTrustManager();
            httpsManager.allowAllSSL(p12FileName, p12Password);

            BufferedReader reader;
            URL url = new URL( rqUrl );
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod( ( method.equals( sendMethod.smPOST ) ? "POST" : "GET" ) );
            conn.setRequestProperty("Connection", "Keep-Alive");
            if ( method.equals( sendMethod.smPOST ) && postData != null ) {
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept","application/json");
            }
            if ( signature != null ) {
                conn.setRequestProperty("X-Signature", signature);
            }

            conn.setReadTimeout(3000);
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();

            if ( method.equals( sendMethod.smPOST ) && postData != null ) {
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(URLEncoder.encode( postData.toString(), "UTF-8"));
                os.flush();
                os.close();
            }
            InputStream inputStream = conn.getInputStream();
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line.trim());
            }
            this.jsonResponse = buffer.toString();
            this.state = requestState.rqsComplete;
        } catch (Exception e) {
            //Log.d(TAG, "ошибка внутри DbConnection: " + e.getMessage());
            this.state = requestState.rqsError;
            this.lastError = e.getLocalizedMessage();
            //e.printStackTrace();
        } finally {
            if (conn != null){
                conn.disconnect();
            }
        }
    }


}
