package com.example.andreeamocean.tasks.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.BaseColumns;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.CursorAdapter;

import com.example.andreeamocean.tasks.R;
import com.example.andreeamocean.tasks.location.FetchLocationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by andreeamocean on 8/8/15.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SearchView.OnQueryTextListener {

    ArrayList<Address> addressOutput;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap map;
    private AddressResultReceiver mResultReceiver;
    private android.support.v7.widget.SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map = mapFragment.getMap();
        mResultReceiver = new AddressResultReceiver(new Handler());
        buildGoogleApiClient();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_task, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        // Theme the SearchView's AutoCompleteTextView drop down. For some reason this wasn't working in styles.xml
        SearchView.SearchAutoComplete autoCompleteTextView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);

        if (autoCompleteTextView != null) {
            autoCompleteTextView.setDropDownBackgroundResource(R.drawable.menu_dropdown_panel_example);
        }
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Address address = addressOutput.get(position);
                map.clear();
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(address.getLatitude(), address.getLongitude()))
                        .title("Hello world"));
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                map.animateCamera(cameraUpdate);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMyLocationEnabled(true);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        map.getMyLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Only start the service to fetch the address if GoogleApiClient is connected.
//        if (mGoogleApiClient.isConnected()) {
        startIntentService(newText);
//        }
        return true;
    }


    private void startIntentService(String addressName) {
        Intent intent = new Intent(this, FetchLocationIntentService.class);
        intent.putExtra(FetchLocationIntentService.RECEIVER, mResultReceiver);
        intent.putExtra(FetchLocationIntentService.ADDRESS_NAME, addressName);
        startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "address"});

            addressOutput = resultData.getParcelableArrayList(FetchLocationIntentService.LOCATION_ADDRESS);

            int size = addressOutput.size();
            final String[] from = new String[size];
            for (int i = 0; i < size; i++) {
                ArrayList<String> addressFragments = new ArrayList<String>();
                // Fetch the address lines using getAddressLine
                for (int j = 0; j < addressOutput.get(i).getMaxAddressLineIndex(); j++) {
                    addressFragments.add(addressOutput.get(i).getAddressLine(j));
                }
                c.addRow(new Object[]{i, TextUtils.join(System.getProperty("line.separator"), addressFragments)});
            }

            final int[] to = new int[]{android.R.id.text1};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(MapActivity.this,
                    android.R.layout.simple_list_item_1,
                    null,
                    new String[]{"address"},
                    to,
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            adapter.changeCursor(c);

            searchView.setSuggestionsAdapter(adapter);
        }
    }
}
