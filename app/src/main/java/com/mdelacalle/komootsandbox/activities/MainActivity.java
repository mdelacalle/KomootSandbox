package com.mdelacalle.komootsandbox.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mdelacalle.komootsandbox.R;
import com.mdelacalle.komootsandbox.RequestTokenAsyncTask;
import com.mdelacalle.komootsandbox.data.KomootAPI;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.mdelacalle.komootsandbox.RequestTokenAsyncTask.ACCESS_TOKEN;
import static com.mdelacalle.komootsandbox.RequestTokenAsyncTask.USER_NAME;

public class MainActivity extends AppCompatActivity implements KomootAPIListener {

    private static final String REDIRECT_URI = "https://komoot.redirecturl";
    private static final String RESPONSE_TYPE_VALUE ="code";
    /*********************************************/

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();

        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Realm.init(
                        getApplicationContext());
                RealmConfiguration config = new RealmConfiguration
                        .Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();
                Realm.setDefaultConfiguration(config);

                SharedPreferences preferences = MainActivity.this.getSharedPreferences("user_info", 0);

                final String currentToken = preferences.getString(ACCESS_TOKEN, null);
                final String user = preferences.getString(USER_NAME, null);
                final String refreshToken = preferences.getString(RequestTokenAsyncTask.REFRESH_TOKEN, null);


                if (currentToken == null && refreshToken == null) {
                    //we start the authorizathion process
                    webView = (WebView) findViewById(R.id.web_view);
                    webView.requestFocus(View.FOCUS_DOWN);

                    webView.setWebViewClient(new WebViewClient() {
                        @SuppressWarnings("deprecation")
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
                            //This method will be called when the Auth proccess redirect to our RedirectUri.
                            //We will check the url looking for our RedirectUri.
                            if (authorizationUrl.startsWith(REDIRECT_URI)) {

                                Uri uri = Uri.parse(authorizationUrl);
                                //If the user doesn't allow authorization to our application, the authorizationToken Will be null.
                                String authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE);
                                if (authorizationToken == null) {
                                    return true;
                                }

                                //Generate URL for requesting Access Token
                                String accessTokenUrl = RequestTokenAsyncTask.getAccessTokenUrl(authorizationToken);
                                //We make the request in a AsyncTask
                                new RequestTokenAsyncTask(MainActivity.this).execute(accessTokenUrl);


                                Log.i("***", "We are refreshing the token");
                                try {
                                    Thread.sleep(4000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Log.e("***", "The token must be refreshed, we retry the method");
                                new Thread(new Runnable() {
                                    public void run() {
                                        KomootAPI.getRoutesByUser(MainActivity.this, MainActivity.this);
                                    }
                                }).start();

                            } else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        findViewById(R.id.splash).setVisibility(View.GONE);
                                        findViewById(R.id.web_view).setVisibility(View.VISIBLE);
                                    }
                                });
                                //Default behaviour

                                webView.loadUrl(authorizationUrl);
                            }
                            return true;
                        }
                    });


                    String authUrl = "https://auth.komoot.de/oauth/authorize?client_id=g3m-j2mhh4&response_type=code&redirect_uri=https://komoot.redirecturl&scope=profile";

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.splash).setVisibility(View.GONE);
                            findViewById(R.id.web_view).setVisibility(View.VISIBLE);
                        }
                    });

                    webView.loadUrl(authUrl);
                } else {
                    //We try to connect, we get all the tour, if we have right token we continue to next activity
                    //Otherwise we refresh the token using the refresh_token
                    new Thread(new Runnable() {
                        public void run() {
                            KomootAPI.getRoutesByUser(MainActivity.this, MainActivity.this);
                        }
                    }).start();
                }
            }
        },500);



    }

    @Override
    public void onSuccess() {
        Intent intent = new Intent(this, ListToursActivity.class);
        startActivity(intent);
    }

    @Override
    public void onError() {
            //Error activity
        Intent intent = new Intent(this, ErrorActivity.class);
        startActivity(intent);
    }
}
