package com.example.weathery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "result";
    private ArrayList<LocationsModel> locationsModelArrayList;
    private CyclerViewLocations cyclerViewLocations;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SearchView searchView = findViewById(R.id.sv);

        recyclerView = findViewById(R.id.listView);
        locationsModelArrayList = new ArrayList<>();
        cyclerViewLocations = new CyclerViewLocations(this, locationsModelArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cyclerViewLocations);


        if (locationsModelArrayList != null && locationsModelArrayList.size() > 0) {
            locationsModelArrayList.clear();
        }

        final Intent intent2 = getIntent();
        final ArrayList<String> ListLocation = intent2.getStringArrayListExtra("Saved_locations");
        Log.d(TAG, "random : " + ListLocation);
        for (String location:ListLocation) {
            locationsModelArrayList.add(new LocationsModel(location));
        }

        //ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,ListLocation);
        //savedLocations.setAdapter(adapter);

        /*savedLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SearchActivity.this, ListLocation.get(i), Toast.LENGTH_SHORT).show();
            }
        });*/

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //getCity(query);
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("mCity",query);
                intent.putExtra("BooleanData",false);
                startActivity(intent);
                finish();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
    }
}
