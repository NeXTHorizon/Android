package org.nhzcrypto.node;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.other.util.HttpUtil;

public class NodeContext {
    /**
     *  node IP
     */
    private String mIPStr;
    public void setIP(String ipStr){
        mIPStr = ipStr;
        mActive = false;
    }
    
    public String getIP(){
        return mIPStr;
    }

    /**
     * node information
     */
    private boolean mActive;
    private String mVersion;
    private int mBlocks;
    private int mTimestamp;
    public boolean isActive(){
        return mActive;
    }
    
    public String getVersion(){
        return mVersion;
    }

    public int getBlocks(){
        return mBlocks;
    }
    
    public int getTimestamp(){
        return mTimestamp;
    }

    /**
     * node update notify
     */
    public interface NodeUpdateListener{
        public void onUpdate(NodeContext node);
    }
    
    private NodeUpdateListener mNodeUpdateListener;
    public void setNodeUpdateListener(NodeUpdateListener l){
        mNodeUpdateListener = l;
    }
    
    public void updateAsync(){
        if ( null == mIPStr )
            return;

        new Thread(new Runnable(){
            @Override
            public void run() {
                String base_url = "http://" + mIPStr + ":7776";
                String httpUrl = String.format("%s/nhz?requestType=getState", base_url);
                try {
                    String response = HttpUtil.getHttp(httpUrl);
                    if ( response != null ){
                        JSONObject jsonObj = new JSONObject(response);
                        mVersion = jsonObj.getString("version");
                        mTimestamp = jsonObj.getInt("time");
                        Log.v("NodeContext", mIPStr + " time = " + mTimestamp);
                        String lastBlock = jsonObj.getString("lastBlock");
                        mBlocks = getBlockHeight(lastBlock);
                        mActive = true;
                        if ( null != mNodeUpdateListener )
                            mNodeUpdateListener.onUpdate(NodeContext.this);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mActive = false;
                if ( null != mNodeUpdateListener )
                    mNodeUpdateListener.onUpdate(NodeContext.this);
            }}).start();
    }
    
    public void update(){
        if ( null == mIPStr )
            return;

        String base_url = "http://" + mIPStr + ":7776";
        String httpUrl = String.format("%s/nhz?requestType=getState", base_url);
        AsyncHttpClient client = new AsyncHttpClient();
        try{
            client.get(httpUrl, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    String strResult = response;
                    JSONObject jsonObj;
                    try {
                        jsonObj = new JSONObject(strResult);
                        mVersion = jsonObj.getString("version");
                        mTimestamp = jsonObj.getInt("time");
                        Log.v("NodeContext", mIPStr + " time = " + mTimestamp);
                        String lastBlock = jsonObj.getString("lastBlock");
                        mBlocks = getBlockHeight(lastBlock);
                        mActive = true;
                        if ( null != mNodeUpdateListener )
                            mNodeUpdateListener.onUpdate(NodeContext.this);
                        return;
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    mActive = false;
                    if ( null != mNodeUpdateListener )
                        mNodeUpdateListener.onUpdate(NodeContext.this);
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    mActive = false;
                    if ( null != mNodeUpdateListener )
                        mNodeUpdateListener.onUpdate(NodeContext.this);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
            mActive = false;
            if ( null != mNodeUpdateListener )
                mNodeUpdateListener.onUpdate(NodeContext.this);
        }
    }
    
    private int getBlockHeight(String blockId){
        String base_url = "http://" + mIPStr + ":7776";
        String httpUrl = String.format(
                "%s/nhz?requestType=getBlock&&block=%s", 
                base_url, blockId);

        try {
            HttpURLConnection conn = (HttpURLConnection)new URL(httpUrl).openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while((line = br.readLine()) != null)
                sb.append(line);

            String strResult = sb.toString();
            JSONObject jsonObj;
            try {
                jsonObj = new JSONObject(strResult);
                int height = jsonObj.getInt("height");

                return height;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        

        return 0;
    }
    
    public NodeContext(){
        
    }
}