package com.mdelacalle.komootsandbox.data;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.mdelacalle.komootsandbox.RequestTokenAsyncTask;
import com.mdelacalle.komootsandbox.activities.KomootAPIListener;
import com.mdelacalle.komootsandbox.model.Coordinate;
import com.mdelacalle.komootsandbox.model.Image;
import com.mdelacalle.komootsandbox.model.Tour;
import com.mdelacalle.komootsandbox.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.mdelacalle.komootsandbox.RequestTokenAsyncTask.ACCESS_TOKEN;
import static com.mdelacalle.komootsandbox.RequestTokenAsyncTask.USER_NAME;

public class KomootAPI {

    public static void getRouteById(Activity parentActivity, KomootAPIListener listener, String id ){


        SharedPreferences preferences = parentActivity.getSharedPreferences("user_info", 0);

        final String currentToken = preferences.getString(ACCESS_TOKEN, null);
        final String user = preferences.getString(USER_NAME, null);
        final String refreshToken = preferences.getString(RequestTokenAsyncTask.REFRESH_TOKEN, null);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://external-api.komoot.de/v007/tours/" + id)
                .get()
                .addHeader("Authorization", "Bearer " + currentToken)
                .addHeader("cache-control", "no-cache")
                .build();

        try {
            Response response = client.newCall(request).execute();



            String result = response.body().string();

            Log.e("***", result);

            JSONObject resultJson = new JSONObject(result);

            String error = resultJson.has("error") ? resultJson.getString("error") : null;

            if(error == null){


                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                Tour tour = realm.where(Tour.class).equalTo("id", id).findFirst();

                JSONObject links = resultJson.getJSONObject("_links");
                JSONObject coordinateLink = links.getJSONObject("coordinates");

                JSONObject _embedded = resultJson.getJSONObject("_embedded");
                JSONObject creatorObj = _embedded.getJSONObject("creator");

                String username = creatorObj.getString("username");
                JSONObject avatarObj = creatorObj.getJSONObject("avatar");
                String src = avatarObj.getString("src");
                String displayName = creatorObj.getString("display_name");

                User creator = realm.where(User.class).equalTo("username",username).findFirst();
                if(creator==null){
                    creator = realm.createObject(User.class,username);
                }
                Image image = realm.createObject(Image.class);
                image.setSrc(src);
                realm.copyToRealm(image);
                creator.setAvatar(image);
                creator.setDisplayName(displayName);
                realm.copyToRealmOrUpdate(creator);
                tour.setUser(creator);



                Request requestCoordinates = new Request.Builder()
                        .url(coordinateLink.getString("href"))
                        .get()
                        .addHeader("Authorization", "Bearer " + currentToken)
                        .addHeader("cache-control", "no-cache")
                        .build();

                Response responseCoordinates = client.newCall(requestCoordinates).execute();

                String resultCoordinates = responseCoordinates.body().string();
                JSONObject resultJsonCoordinates = new JSONObject(resultCoordinates);
                JSONArray items = resultJsonCoordinates.getJSONArray("items");



                RealmList<Coordinate> coordinates =  new RealmList<>();
                realm.copyToRealm(coordinates);

                for (int i = 0; i < items.length(); i++ ){
                    JSONObject coordinateJson = items.getJSONObject(i);
                    Coordinate coordinate = realm.createObject(Coordinate.class);
                    coordinate.setLongitude(coordinateJson.getDouble("lng"));
                    coordinate.setLatitude(coordinateJson.getDouble("lat"));
                    coordinate.setAltitude(coordinateJson.getDouble("alt"));
                    coordinate.setTimestamp(coordinateJson.getLong("t"));
                    realm.copyToRealm(coordinate);
                    coordinates.add(coordinate);
                }

                tour.setCoordinates(coordinates);
                Log.i("***", tour.getName());
                realm.copyToRealmOrUpdate(tour);
                realm.commitTransaction();
                realm.close();
                listener.onSuccess();

            }else{
                String refreshTokenUrl =  RequestTokenAsyncTask.getRefreshTokenUrl(refreshToken);
                new RequestTokenAsyncTask(parentActivity).execute(refreshTokenUrl);

                Log.e("***", "We are refreshing the token");
                Thread.sleep(4000);
                Log.e("***", "The token must be refreshed, we retry the method");
                getRouteById(parentActivity,listener,id);
            }


        }catch (IOException e){
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
                    Image image = new Image();
                    image.setSrc(toursJSONObject.getJSONObject("map_image").getString("src"));
                    tour.setMap_image(image);
                    Image imagePreview = new Image();
                    imagePreview.setSrc(toursJSONObject.getJSONObject("map_image_preview").getString("src"));
                    tour.setMap_image_preview(imagePreview);
                    realm.copyToRealm(tour);
                }

                realm.commitTransaction();
                realm.close();
                listener.onSuccess();


            }else{
                //we refresh the token
                String refreshTokenUrl =  RequestTokenAsyncTask.getRefreshTokenUrl(refreshToken);
                new RequestTokenAsyncTask(parentActivity).execute(refreshTokenUrl);

                Log.e("***", "We are refreshing the token");
                Thread.sleep(4000);
                Log.e("***", "The token must be refreshed, we retry the method");
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
