package com.mdelacalle.komootsandbox.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mdelacalle.komootsandbox.R;
import com.mdelacalle.komootsandbox.data.KomootAPI;
import com.mdelacalle.komootsandbox.model.Coordinate;
import com.mdelacalle.komootsandbox.model.Tour;

import org.glob3.mobile.generated.AltitudeMode;
import org.glob3.mobile.generated.Angle;
import org.glob3.mobile.generated.G3MContext;
import org.glob3.mobile.generated.GTask;
import org.glob3.mobile.generated.Geodetic3D;
import org.glob3.mobile.generated.LayerSet;
import org.glob3.mobile.generated.Mark;
import org.glob3.mobile.generated.MarksRenderer;
import org.glob3.mobile.generated.Sector;
import org.glob3.mobile.generated.TimeInterval;
import org.glob3.mobile.generated.URL;
import org.glob3.mobile.generated.URLTemplateLayer;
import org.glob3.mobile.specific.G3MBuilder_Android;
import org.glob3.mobile.specific.G3MWidget_Android;

import io.realm.Realm;
import io.realm.RealmList;

public class Map3DActivity extends AppCompatActivity implements KomootAPIListener{

    public static int STEP = 1000;
    private G3MWidget_Android _g3MWidget;
    private MarksRenderer _pathRenderer = new MarksRenderer(false);
    String _id;
    int _index;
    int _oldIndex;
    int _coordinateSize;
    private Realm _realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map3d_activity);
        _id = getIntent().getStringExtra("Tour");

        startGlob3();

        new Thread(new Runnable() {
            public void run() {
                _g3MWidget.getG3MContext().getThreadUtils().invokeInRendererThread(new GTask() {
                    @Override
                    public void run(G3MContext context) {
                        KomootAPI.getRouteById(Map3DActivity.this, Map3DActivity.this, _id);
                    }
                }, false);
            }
        }).start();

        findViewById(R.id.tools_panel).bringToFront();
        ImageView palante = (ImageView)findViewById(R.id.palante);
        palante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forward();
                palante.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        palante.setEnabled(true);
                    }
                },1200);



            }
        });





        palante.bringToFront();

        ImageView patras = (ImageView)findViewById(R.id.patras);
        patras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
                patras.setEnabled(false );
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        patras.setEnabled(true);
                    }
                },1200);
            }
        });
        patras.bringToFront();
        findViewById(R.id.switch_layer_panel).bringToFront();
        findViewById(R.id.avatar_panel).bringToFront();

    }

    private void startGlob3() {

        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.g3m);
        final G3MBuilder_Android builder = new G3MBuilder_Android(this);
        LayerSet layerSet = new LayerSet();
        final URLTemplateLayer baseLayer = URLTemplateLayer.newMercator("https://[1234].aerial.maps.cit.api.here.com/maptile/2.1/maptile/newest/satellite.day/{level}/{x}/{y}/256/png8?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg"
                , Sector.fullSphere(),false,2,18, TimeInterval.fromDays(30));

        final URLTemplateLayer terrainLayer = URLTemplateLayer.newMercator(
                "https://[abc].tiles.mapbox.com/v4/bobbysud.lff265id/{level}/{x}/{y}@2x.png?access_token=pk.eyJ1IjoiYm9iYnlzdWQiLCJhIjoiY2pvZjBzOThvMDJ1ZDNxbnYxcDRobTdkYiJ9.pG76GAr8S5J630_0WkwgAw"
                , Sector.fullSphere(),false,2,18, TimeInterval.fromDays(30));

        baseLayer.setEnable(false);
        layerSet.addLayer(baseLayer);
        terrainLayer.setEnable(true);
        layerSet.addLayer(terrainLayer);
        builder.setAtmosphere(true);
        builder.getPlanetRendererBuilder().setLayerSet(layerSet);
        builder.addRenderer(_pathRenderer);
        _g3MWidget = builder.createWidget();


        findViewById(R.id.switch_layer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(terrainLayer.isEnable()){
                    terrainLayer.setEnable(false);
                    baseLayer.setEnable(true);

                    ((ImageView)view).setImageDrawable(getResources().getDrawable(R.drawable.vect));

                }else{
                    terrainLayer.setEnable(true);
                    baseLayer.setEnable(false);

                    ((ImageView)view).setImageDrawable(getResources().getDrawable(R.drawable.imagery));

                }
            }
        });

        cl.addView(_g3MWidget);
        
    }

    @Override
    public void onSuccess() {

        _realm = Realm.getDefaultInstance();
        Tour tour = _realm.where(Tour.class).equalTo("id", _id).findFirst();
        RealmList<Coordinate> coordinates = tour.getCoordinates();
        _coordinateSize =coordinates.size();

        String path = tour.getUser().getAvatar().getSrcFixed();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(Map3DActivity.this).load(path).apply(RequestOptions.circleCropTransform()).into((ImageView) findViewById(R.id.avatar));
            }
        });


        for(int i = 0 ; i < _coordinateSize ; i+=100){

            if(i>_coordinateSize){
                i = _coordinateSize-1;
            }
            Coordinate coordinate = coordinates.get(i);

            Geodetic3D coordG3m = Geodetic3D.fromDegrees(coordinate.getLatitude(),coordinate.getLongitude(),0);
            final Mark mark = new Mark(

                    new URL("file:///iconk.png", false),  //
                    //new URL(Constants._iconsPath+"/"+_icon, false),  //
                    // new Geodetic3D(geometry.getPosition(), 40),
                    coordG3m,
                    AltitudeMode.RELATIVE_TO_GROUND,
                    0,
                    null,
                    false,
                    null,
                    true
            );
            _pathRenderer.addMark(mark);
        }




        _g3MWidget.getG3MWidget().setAnimatedCameraPosition(TimeInterval.fromSeconds(3),
                Geodetic3D.fromDegrees(
                        coordinates.get(0).getLatitude(),
                        coordinates.get(0).getLongitude(),
                        5000),
                Angle.fromDegrees(-bearing(
                        coordinates.get(0).getLatitude(),
                        coordinates.get(0).getLongitude(),
                        coordinates.get(1).getLatitude(),
                        coordinates.get(1).getLongitude())),
                Angle.fromDegrees(-90));
    }

    private void forward(){

        if((_index+ STEP) < _coordinateSize) {
            _index = _index + STEP;
        }else{
            _index = _coordinateSize-1;
        }

        Log.e("***", "INDEX:"+_index +", STEP:"+ STEP +" , size"+ _coordinateSize +" ----  ");
        goToCoordinateWithCurrentIndex();
    }

    private void back(){

        if(_index<STEP) {
            _index = 0;
        }else{
            _index = _index - STEP;
        }
        goToCoordinateWithCurrentIndex();
    }

    private void goToCoordinateWithCurrentIndex() {

        _realm = Realm.getDefaultInstance();
        RealmList<Coordinate> coordinates = _realm. where(Tour.class).equalTo("id",_id).findFirst().getCoordinates();

       try {


           _g3MWidget.getG3MWidget().setAnimatedCameraPosition( TimeInterval.fromSeconds(2),
                   Geodetic3D.fromDegrees(
                   coordinates.get(_index).getLatitude(),
                   coordinates.get(_index).getLongitude(),
                   3000));





       }catch (Exception e){
           // if we have any unexpected problem with indexes
           Intent intent = new Intent(this, ErrorActivity.class);
           startActivity(intent);
       }
    }



    protected static double bearing(double lat1, double lon1, double lat2, double lon2){
        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);
        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }



    @Override
    public void onError() {
        Intent intent = new Intent(this, ErrorActivity.class);
        startActivity(intent);
    }

}
