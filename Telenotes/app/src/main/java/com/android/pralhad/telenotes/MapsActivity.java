package com.android.pralhad.telenotes;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pralhad.models.Restaurant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import android.net.Uri;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Restaurant restaurant = null;

    private TextView mName, mAddress, mRating, mType;
    private ImageView mCallImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mName = (TextView) findViewById(R.id.r_name);
        mAddress = (TextView) findViewById(R.id.r_address);
        mRating = (TextView) findViewById(R.id.r_rating);
        mType = (TextView) findViewById(R.id.r_type);
        mCallImage = (ImageView) findViewById(R.id.call_Image);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        restaurant = (new Gson()).fromJson(intent.getStringExtra(RestaurantsActivity.RESTAURANT_KEY),
                Restaurant.class);

        if (restaurant != null) {
            mName.setText(restaurant.getName());
            mAddress.setText(restaurant.getAddress());
            mRating.setText(restaurant.getRating());
            mType.setText(restaurant.getType());
            final String telephone = restaurant.getTelephone();
            if (telephone != null || telephone != "") {
                mCallImage.setVisibility(View.VISIBLE);
                mCallImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + telephone));
                        startActivity(intent);
                    }
                });
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker to the restaurant location and move the camera
        if (restaurant != null) {
            LatLng location = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            mMap.addMarker(new MarkerOptions().position(location).title(restaurant.getName()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
        }
    }
}
