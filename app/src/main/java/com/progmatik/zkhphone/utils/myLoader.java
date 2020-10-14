package com.progmatik.zkhphone.utils;

import android.app.Activity;
import android.os.AsyncTask;
import com.progmatik.zkhphone.classes.myKeyValuesList;
import com.progmatik.zkhphone.interfaces.RequestFinishListeners;

import org.json.JSONObject;

public class myLoader extends AsyncTask {

    private myHttpsRequest.sendMethod method = myHttpsRequest.sendMethod.smGET;
    private JSONObject postData;
    private myHttpsRequest httpsRequest;
    private myKeyValuesList params;
    private RequestFinishListeners requesFinishListeners;
    private String p12FileName;
    private String p12Password;
    private String subURL;
    private String signature;

    public myLoader(Activity activity ) { httpsRequest = new myHttpsRequest( activity ); }
    public void setParams( myKeyValuesList params ) { this.params = params; }
    public myKeyValuesList getParams()
    {
        return this.params;
    }

    public void setSendMethod( myHttpsRequest.sendMethod method ) { this.method = method; }
    public myHttpsRequest.sendMethod getSendMethod() { return this.method; }

    public void setPostData( JSONObject postData ) { this.postData = postData; }
    public JSONObject getPostData() { return this.postData; }

    public void setP12FileName( String p12FileName ) { this.p12FileName = p12FileName; }
    public String getP12FileName() { return this.p12FileName; }

    public void setP12Password( String p12Password ) { this.p12Password = p12Password; }
    public String getP12Password() { return this.p12Password; }

    public void setSubURL( String subURL ) { this.subURL = subURL; }
    public String getSubURL() { return this.subURL; }

    public void setSignature( String signature ) { this.signature = signature; }
    public String getSignature() { return this.signature; }

    public void setRequestFinishListeners(RequestFinishListeners requesFinishListeners) { this.requesFinishListeners = requesFinishListeners; }

    @Override
    protected Object doInBackground(Object[] o) {
        this.httpsRequest.setParams(params);
        this.httpsRequest.sendRequest(this.subURL, this.p12FileName, this.p12Password, this.method, this.postData, this.signature);
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {

        if (requesFinishListeners != null)
            requesFinishListeners.requestFinished(this.httpsRequest);

    }
}
