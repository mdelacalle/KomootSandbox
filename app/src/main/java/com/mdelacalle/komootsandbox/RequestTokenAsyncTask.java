package com.mdelacalle.komootsandbox;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.ParseException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestTokenAsyncTask extends AsyncTask<String, Void, Boolean> {

    public static final String ACCESS_TOKEN = "access_token";
    public static final String USER_NAME = "user_name";
    public static final String REFRESH_TOKEN = "refresh_token";

    /*CONSTANT FOR THE AUTHORIZATION PROCESS*/
    // client_id:     g3m-j2mhh4
    // client_secret: aezoh3ahdeiquaephahgaathe
    //https://auth.komoot.de/oauth/authorize
    /****FILL THIS WITH YOUR INFORMATION*********/
    //This is the public api key of our application
    private static final String API_KEY = "g3m-j2mhh4";
    private static final String SECRET_KEY = "aezoh3ahdeiquaephahgaathe";
    //This is the private api key of our application
    private static final String REDIRECT_URI = "https://komoot.redirecturl";
    /*********************************************/
    //These are constants used for build the urls
    private static final String ACCESS_TOKEN_URL = "https://auth.komoot.de/oauth/token";
    private static final String REFRESH_TOKEN_URL = "https://auth.komoot.de/oauth/token?refresh_token";

    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE_VALUE ="code";

    private static final String REDIRECT_URI_PARAM = "redirect_uri";
    /*---------------------------------------*/
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token" ;

    Activity _activity;
    public RequestTokenAsyncTask(Activity activity) {
        _activity = activity;
    }


    @Override
    public void onPreExecute(){
        //   pd = ProgressDialog.show(MainActivity.this, "", MainActivity.this.getString(R.string.loading),true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Boolean doInBackground(String... urls) {
        if(urls.length>0) {
            String url = urls[0];

            try {

                OkHttpClient client = new OkHttpClient();
                String userCredentials = API_KEY + ":" + SECRET_KEY;
                String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
                RequestBody reqbody = RequestBody.create(null, new byte[0]);
                Request request = new Request.Builder()
                        .url(url)
                        .post(reqbody)
                        .addHeader("Authorization", basicAuth)
                        .addHeader("cache-control", "no-cache")
                        .build();

                Response response = client.newCall(request).execute();

                if (response != null) {
                    //If status is OK 200
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        Log.e("*** RESULT", result);
                        //Convert the string result to a JSON Object
                        JSONObject resultJson = new JSONObject(result);
                        //Extract data from JSON Response
                        int expiresIn = resultJson.has("expires_in") ? resultJson.getInt("expires_in") : 0;
                        String accessToken = resultJson.has("access_token") ? resultJson.getString("access_token") : null;
                        String user = resultJson.has("username") ? resultJson.getString("username") : null;
                        String refreshToken = resultJson.has("refresh_token") ? resultJson.getString("refresh_token") : null;


                        if (expiresIn > 0 && accessToken != null) {
                            Log.e("Authorize", "This is the access Token: " + accessToken + ". It will expires in " + expiresIn + " secs");

                            //Calculate date of expiration
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.SECOND, expiresIn);
                            long expireDate = calendar.getTimeInMillis();

                            ////Store both expires in and access token in shared preferences
                            SharedPreferences preferences = _activity.getSharedPreferences("user_info", 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putLong("expires", expireDate);
                            editor.putString(USER_NAME, user);
                            editor.putString(ACCESS_TOKEN, accessToken);
                            editor.putString(REFRESH_TOKEN, refreshToken);
                            editor.commit();

                            return true;
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("Authorize", "Error Http response " + e.getLocalizedMessage());
            } catch (ParseException e) {
                Log.e("Authorize", "Error Parsing Http response " + e.getLocalizedMessage());
            } catch (JSONException e) {
                Log.e("Authorize", "Error Parsing Http response " + e.getLocalizedMessage());
            }
        }
        return false;
    }

    @Override
    public void onPostExecute(Boolean status){

        if(status){
            Log.e("****", "STATUS:"+status);
        }
    }

    /**
     * Method that generates the url for get the access token from the Service
     * @return Url
     */
    public static String getAccessTokenUrl(String authorizationToken){
        return ACCESS_TOKEN_URL
                +QUESTION_MARK
                +GRANT_TYPE_PARAM+EQUALS+GRANT_TYPE
                +AMPERSAND
                +RESPONSE_TYPE_VALUE+EQUALS+authorizationToken
                +AMPERSAND
                +REDIRECT_URI_PARAM+EQUALS+REDIRECT_URI;
    }


    public static String getRefreshTokenUrl(String currentToken){
        return REFRESH_TOKEN_URL+EQUALS+currentToken
                +AMPERSAND
                +GRANT_TYPE_PARAM+EQUALS+GRANT_TYPE_REFRESH_TOKEN;
    }



}
