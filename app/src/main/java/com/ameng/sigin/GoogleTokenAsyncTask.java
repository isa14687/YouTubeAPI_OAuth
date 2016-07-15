package com.ameng.sigin;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.services.youtube.YouTube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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


    @Override
    protected String doInBackground(Void... params) {
        String token = "";
        try {
            token = GoogleAuthUtil.getToken(
                    context,
                    name,
                    SCOPE
            );
//            final HttpTransport transport = AndroidHttp.newCompatibleTransport();
//            com.google.api.client.json.JsonFactory jsonFactory =new JacksonFactory();
//            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, YouTubeScopes.all());
//            new com.google.api.services.youtube.YouTube.Builder(transport, jsonFactory, credential)
//                    .setApplicationName("Google-YouTubeAndroidSample/1.0").build();
//            youTube = new YouTube.Builder(transport, jsonFactory, credential)
//                    .setApplicationName("com.ameng.sigin")
//                    .build();


            Log.e("token", token.toString());

        } catch (IOException transientEx) {
            // Network or server error, try later
            Log.e("IOException", transientEx.toString());
        } catch (UserRecoverableAuthException e) {
            // Recover (with e.getIntent())
//            Intent recover = e.getIntent();
            Log.e("UserRecoverableAuthException", e.toString());
//            Intent recover = e.getIntent();
//            context.startActivityForResult(recover, REQUEST_CODE_TOKEN_AUTH);
        } catch (GoogleAuthException authEx) {
            // The call is not ever expected to succeed
            // assuming you have already verified that
            // Google Play services is installed.
            Log.e("GoogleAuthException", authEx.toString());
        }
        doTranslate(token);

        return token;
    }


    @Override
    protected void onPostExecute(String token) {
        Log.e("Access token retrieved:", token + "");


//        googleTokenOnFinsh.onGoogleTokenOnFinsh(token);
    }

    //    public void getGoogleTokenOnFinsh(GoogleTokenOnFinsh googleTokenOnFinsh){
//        this.googleTokenOnFinsh=googleTokenOnFinsh;
//    }
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
}
