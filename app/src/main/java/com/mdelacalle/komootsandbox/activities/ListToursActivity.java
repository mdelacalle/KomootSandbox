package com.mdelacalle.komootsandbox.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mdelacalle.komootsandbox.R;
import com.mdelacalle.komootsandbox.model.Tour;
import com.mdelacalle.komootsandbox.ui.ToursAdapter;

import io.realm.Realm;
import io.realm.RealmResults;

public class ListToursActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.loading).setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tours);
        findViewById(R.id.loading).setVisibility(View.GONE);
        
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Tour> tours = realm.where(Tour.class).findAll();

       RecyclerView listTours = findViewById(R.id.list_tours);
       ToursAdapter toursAdapter = new ToursAdapter(tours,true);
       toursAdapter.addParentActivity(ListToursActivity.this);
       listTours.setHasFixedSize(true);
       LinearLayoutManager layoutManager = new LinearLayoutManager(ListToursActivity.this);
       listTours.setLayoutManager(layoutManager);
       DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ListToursActivity.this,
                layoutManager.getOrientation());

       listTours.addItemDecoration(dividerItemDecoration);
       listTours.setAdapter(toursAdapter);

    }
}
