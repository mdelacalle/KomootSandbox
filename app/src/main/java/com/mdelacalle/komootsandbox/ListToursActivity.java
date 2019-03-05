package com.mdelacalle.komootsandbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.mdelacalle.komootsandbox.model.Tour;

import io.realm.Realm;
import io.realm.RealmResults;

public class ListToursActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tours);
        
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Tour> tours = realm.where(Tour.class).findAll();

       RecyclerView listTours = findViewById(R.id.list_tours);
       ToursAdapter toursAdapter = new ToursAdapter(tours);

    }
}
