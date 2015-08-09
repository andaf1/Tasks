package com.example.andreeamocean.tasks.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by andreeamocean on 8/8/15.
 */
public class FetchLocationIntentService extends IntentService {

    public static final String ADDRESS_NAME = "com.example.andreeamocean.tasks.ADDRES_NAME";
    public static final String LOCATION_ADDRESS = "com.example.andreeamocean.tasks.LOCATION_ADDRESS";
    public static final String RECEIVER = "com.example.andreeamocean.tasks.RECEIVER";

    protected ResultReceiver mReceiver;

    public FetchLocationIntentService() {
        super("FetchLocationIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Get the address passed to this service through an extra.
        String addressName = intent.getStringExtra(ADDRESS_NAME);
        mReceiver = intent.getParcelableExtra(RECEIVER);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(addressName, 5);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        // Handle case where no address was found.
        if (!addresses.isEmpty()) {
//            Address address = addresses.get(0);
//            ArrayList<LatLng> addressFragments = new ArrayList<LatLng>();
//            LatLng location;
//            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                location=new LatLng(address.getLatitude(), address.getLongitude());
//                addressFragments.add(location);
//            }
            deliverResultToReceiver(0, (ArrayList<Address>) addresses);
        }

    }


    private void deliverResultToReceiver(int resultCode, ArrayList<Address> addresses) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(LOCATION_ADDRESS, addresses);
        mReceiver.send(resultCode, bundle);
    }
}
