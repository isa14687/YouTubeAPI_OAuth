package com.ameng.sigin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistListResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ameng on 2016/3/8.
 */
public class GoogleTokenAsyncTask extends AsyncTask<Void, Void, String> {
    String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    public static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private Context context;
    private String name;
    YouTube youTube;
//    private GoogleTokenOnFinsh googleTokenOnFinsh;

//    public interface GoogleTokenOnFinsh {
//        void onGoogleTokenOnFinsh(String token);
//    }


    public GoogleTokenAsyncTask(Context context, String name) {
        this.context = context;
        this.name = name;
    }


    @SuppressLint("LongLogTag")
    @Override
    protected String doInBackground(Void... params) {
        String token = "";
        try {
//            token = GoogleAuthUtil.getToken(
//                    context,
//                    name,
//                    YouTubeScopes.YOUTUBE
//            );
            ArrayList list = new ArrayList();
//            list.add(SCOPE);
//            Credential c = authorize(list, "ttt");
//            Log.e("c", c.getAccessToken());

            try {
                getTokenWithGoogleAccountCredential();
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            com.google.api.client.json.JsonFactory jsonFactory = new com.google.api.client.extensions.android.json.AndroidJsonFactory();
            // 使用youtube api 建立連線
//            youTube = new YouTube.Builder(transport, jsonFactory, null)
//                    .build();

//            GoogleCredential credential = new GoogleCredential().setAccessToken(token);
//            youTube = new YouTube.Builder(transport, jsonFactory, null)
//                    .build();


            Log.e("token", token.toString());

        } catch (IOException transientEx) {
            // Network or server error, try later
            Log.e("IOException", transientEx.toString());
        }
//        doTranslate(token);
        try {
//            設定Request
            YouTube.Playlists.List list = youTube.playlists()
                    .list("contentDetails")
                    .setMine(true);
//            若前面傳入的是 GoogleAccountCredential 不用 set ("access_token", token)
//                    .set("access_token", token);

//           取得 response
            PlaylistListResponse response = list.execute();
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("e", e.toString());
            return "";
        }
    }


    @Override
    protected void onPostExecute(String response) {
        JSONObject responseObj = null;
        try {
            responseObj = new JSONObject(response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("e", e.toString());
        }
        Log.e("jsonObj", responseObj.toString());
        try {
            Log.e("getToken", ((JSONObject) ((JSONArray) responseObj.get("items")).get(0)).get("id").toString());
            Intent intent = new Intent(context, YouTubeActivity.class);
            intent.putExtra("id", ((JSONObject) ((JSONArray) responseObj.get("items")).get(0)).get("id").toString());
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 以Http GET 方式建立連線
     */
    private void doTranslate(String token) {
        HttpURLConnection conn = null;
        try {
            // 建立連線
            URL url = new URL(
                    "https://www.googleapis.com/youtube/v3/channels?part=contentDetails&mine=true&access_token=" + token);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.connect();
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Log.e("log", conn.getInputStream().toString() + "");

            // 讀取資料
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "UTF-8"));
            String jsonString1;
            while (reader.readLine() != null) {
                jsonString1 = reader.readLine();
                Log.e("log", jsonString1);
            }
        } catch (Exception e) {
            Log.e("e", e.toString());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     *取得憑證
     * @throws IOException
     * @throws GoogleAuthException
     */
    public void getTokenWithGoogleAccountCredential() throws IOException, GoogleAuthException {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        com.google.api.client.json.JsonFactory jsonFactory = new com.google.api.client.extensions.android.json.AndroidJsonFactory();
        //add scope
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("https://www.googleapis.com/auth/userinfo.profile");
        // 使用 GoogleAccountCredential傳入 context 及 SCOPE
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, arrayList);
        //要設定 AccountName 否則會拿不到 token
        credential.setSelectedAccountName(name);
        Log.e("credential", credential.getToken());
        //傳入 YouTube.Builder
        youTube = new com.google.api.services.youtube.YouTube.Builder(transport, jsonFactory, credential)
                .build();
    }
}
