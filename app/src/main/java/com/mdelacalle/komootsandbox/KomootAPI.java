package com.mdelacalle.komootsandbox;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.mdelacalle.komootsandbox.model.Tour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.mdelacalle.komootsandbox.RequestTokenAsyncTask.ACCESS_TOKEN;
import static com.mdelacalle.komootsandbox.RequestTokenAsyncTask.USER_NAME;

public class KomootAPI {

    public static void getRoutesByUser(Activity parentActivity, KomootAPIListener listener){

        SharedPreferences preferences = parentActivity.getSharedPreferences("user_info", 0);

        final String currentToken = preferences.getString(ACCESS_TOKEN, null);
        final String user = preferences.getString(USER_NAME, null);
        final String refreshToken = preferences.getString(RequestTokenAsyncTask.REFRESH_TOKEN, null);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://external-api.komoot.de/v007/users/" + user + "/tours/")
                .get()
                .addHeader("Authorization", "Bearer " + currentToken)
                .addHeader("cache-control", "no-cache")
                .build();
        try {
            Response response = client.newCall(request).execute();


            String result = response.body().string();
            Log.e("***", result);
            JSONObject resultJson = new JSONObject(result);
            //Extract data from JSON Response
            String error = resultJson.has("error") ? resultJson.getString("error") : null;

            if(error == null){
                JSONObject embedded = resultJson.getJSONObject("_embedded");

                JSONArray tours = embedded.getJSONArray("tours");

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.delete(Tour.class);

                for(int i = 0; i < tours.length();i++){

                  JSONObject toursJSONObject = tours.getJSONObject(i);
                    Tour tour = new Tour();
                    tour.setId(toursJSONObject.getString("id"));
                    tour.setStatus(toursJSONObject.getString("status"));
                    tour.setType(toursJSONObject.getString("type"));
                    tour.setName(toursJSONObject.getString("name"));
                    tour.setDistance(toursJSONObject.getString("distance"));
                    tour.setDuration(toursJSONObject.getString("duration"));
                    tour.setSport(toursJSONObject.getString("sport"));
                    tour.setMap_image(toursJSONObject.getString("map_image"));
                    tour.setMap_image_preview(toursJSONObject.getString("map_image_preview"));
                    realm.copyToRealm(tour);
                }

                realm.commitTransaction();
                realm.close();
                listener.onSuccess();


            }else{
                //we refresh the token
                String refreshTokenUrl =  RequestTokenAsyncTask.getRefreshTokenUrl(refreshToken);
                new RequestTokenAsyncTask(parentActivity).execute(refreshTokenUrl);

                Log.e("***", "We are refresing the token");
                Thread.sleep(4000);
                Log.e("***", "The token must be refresed, we retry the method");
                getRoutesByUser(parentActivity,listener);

            }
        } catch (IOException e) {
            e.printStackTrace();
            listener.onError();
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onError();
        } catch (InterruptedException e) {
            e.printStackTrace();
            listener.onError();
        }

    }

}
