package com.android.pralhad.telenotes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pralhad.adapters.CityAdapter;
import com.android.pralhad.adapters.RestaurantAdapter;
import com.android.pralhad.models.City;
import com.android.pralhad.models.Restaurant;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.loopj.android.http.*;

import org.json.JSONObject;
import org.json.*;

import cz.msebera.android.httpclient.Header;

public class RestaurantsActivity extends AppCompatActivity {
    final int CITY = 0;
    final int RESTAURANT = 1;
    final static String RESTAURANT_KEY = "RestaurantObj";

    AppCompatActivity context;

    int mode;
    RecyclerView mRecyclerView;
    CityAdapter mCityAdapter;
    ArrayList<City> cityList;

    RestaurantAdapter mRestaurantAdapter;
    ArrayList<Restaurant> restaurantList;
    ProgressDialog mProgressDialog;

    EditText mCityEditText;
    TextView mCityTextView;
    Button mChangeButton;
    EditText mRestaurantEditText;

    TextView mErrorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        mode = CITY;

        mErrorTextView = (TextView) findViewById(R.id.error_textView);
        mCityTextView = (TextView) findViewById(R.id.city_textView);
        mChangeButton = (Button) findViewById(R.id.change_cityButton);

        mChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = CITY;
                mRecyclerView.setVisibility(View.GONE);
                mErrorTextView.setVisibility(View.GONE);
                mCityTextView.setVisibility(View.GONE);
                mChangeButton.setVisibility(View.GONE);
                mRestaurantEditText.setVisibility(View.GONE);
                mCityEditText.setVisibility(View.VISIBLE);
                mCityEditText.requestFocus();
            }
        });

        mCityEditText = (EditText) findViewById(R.id.city_editText);
        mCityEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                boolean searchTapped = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    String city = v.getText().toString();
                    if (actionId == EditorInfo.IME_ACTION_GO
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || actionId == EditorInfo.IME_ACTION_NEXT
                            || actionId == EditorInfo.IME_ACTION_SEND
                            || actionId == EditorInfo.IME_ACTION_SEARCH) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mCityEditText.getWindowToken(), 0);
                    }

                    searchCities(city);
                    searchTapped = true;
                }
                return searchTapped;
            }
        });

        mRestaurantEditText = (EditText) findViewById(R.id.restaurant_editText);
        mRestaurantEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean searchTapped = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    String placeName = v.getText().toString();
                    if (actionId == EditorInfo.IME_ACTION_GO
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || actionId == EditorInfo.IME_ACTION_NEXT
                            || actionId == EditorInfo.IME_ACTION_SEND
                            || actionId == EditorInfo.IME_ACTION_SEARCH) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mRestaurantEditText.getWindowToken(), 0);
                    }

                    searchRestaurants(mCityTextView.getText().toString(), placeName);
                    searchTapped = true;
                }
                return searchTapped;
            }
        });

        mCityAdapter = new CityAdapter();
        mRestaurantAdapter = new RestaurantAdapter();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_listView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final GestureDetector mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });


        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    if (mode == CITY) {
                        mCityTextView.setText(mCityAdapter.getCity((int) recyclerView.getChildAdapterPosition(child)));

                        mCityEditText.setVisibility(View.GONE);
                        mCityTextView.setVisibility(View.VISIBLE);
                        mChangeButton.setVisibility(View.VISIBLE);
                        mRestaurantEditText.setText("");
                        mRestaurantEditText.setVisibility(View.VISIBLE);

                        mode = RESTAURANT;
                        mRecyclerView.setAdapter(null);
                        searchRestaurants(mCityTextView.getText().toString(), "");

                    } else if (mode == RESTAURANT) {
                        Restaurant restaurant = mRestaurantAdapter.getRestaurant((int) recyclerView.getChildAdapterPosition(child));
                        if (restaurant != null) {
                            Intent intent = new Intent(context, MapsActivity.class);
                            Gson gson = new Gson();
                            intent.putExtra(RESTAURANT_KEY, gson.toJson(restaurant, Restaurant.class));
                            startActivity(intent);
                        }
                    }

                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    public void searchCities(String cityName) {

        String url = "http://api.v3.factual.com/t/places-us?filters={\"$or\":[{\"locality\":{\"$search\":\""
                + cityName +
                "\"}},{\"locality\":{\"$bw\":\""
                + cityName +
                "\"}}]}&select=locality,region&limit=50&KEY=faK0zvTKM26qSJc49xPkBpT0bO6nQjbJkoZNmQjI";

        if (isNetworkConnected()) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, new JsonHttpResponseHandler() {

                public void onStart() {
                    context.runOnUiThread(new Runnable() {
                        public void run() {
                            mProgressDialog = ProgressDialog.show(context, "Please wait", "Looking for cities", true);
                        }
                    });
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getString("status").equalsIgnoreCase("ok")) {
                            HashMap<String, String> uniqueCities = new HashMap<String, String>(50);
                            cityList = new ArrayList<City>();
                            JSONArray cities = response.getJSONObject("response").getJSONArray("data");
                            for (int i = 0; i < cities.length(); i++) {
                                JSONObject city = (JSONObject) cities.get(i);
                                if (!uniqueCities.containsKey(city.getString("locality"))) {
                                    uniqueCities.put(city.getString("locality"), city.getString("region"));
                                    cityList.add(new City(city.getString("locality"), city.getString("region")));
                                }
                            }

                            if (cityList.size() > 0) {
                                showData();
                                mCityAdapter.updateData(cityList);
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.setAdapter(mCityAdapter);
                                    }
                                });
                            } else {
                                showError(getString(R.string.no_cities));
                            }

                        } else {
                            showError(getString(R.string.no_cities));
                        }
                    } catch (Exception e) {
                        showError(getString(R.string.no_cities));
                    }
                    mProgressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }
            });
        } else {
            showNoNetworkDialog();
        }
    }

    public void searchRestaurants(String city, String query) {

        String url = "http://api.v3.factual.com/t/restaurants-us?filters={\"locality\":{\"$bw\":\""
                + city +
                "\"}}&select=cuisine,locality,address,latitude,longitude,name,tel,rating&q="
                + query +
                "&limit=50&KEY=faK0zvTKM26qSJc49xPkBpT0bO6nQjbJkoZNmQjI";

        if (isNetworkConnected()) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, new JsonHttpResponseHandler() {

                public void onStart() {
                    context.runOnUiThread(new Runnable() {
                        public void run() {
                            mProgressDialog = ProgressDialog.show(context, "Please wait", "Searching for restaurants", true);
                        }
                    });
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getString("status").equalsIgnoreCase("ok")) {
                            restaurantList = new ArrayList<Restaurant>();
                            JSONArray restaurants = response.getJSONObject("response").getJSONArray("data");
                            for (int i = 0; i < restaurants.length(); i++) {
                                JSONObject restaurant = (JSONObject) restaurants.get(i);

                                Restaurant place = new Restaurant();

                                if (restaurant.has("name"))
                                    place.setName(restaurant.getString("name"));

                                if (restaurant.has("address"))
                                    place.setAddress(restaurant.getString("address"));

                                if (restaurant.has("rating"))
                                    place.setRating(restaurant.getString("rating"));

                                if (restaurant.has("cuisine") && restaurant.getJSONArray("cuisine").length() > 0)
                                    place.setType(restaurant.getJSONArray("cuisine").getString(0));

                                place.setLatitude(restaurant.getDouble("latitude"));
                                place.setLongitude(restaurant.getDouble("longitude"));

                                if (restaurant.has("tel"))
                                    place.setTelephone(restaurant.getString("tel"));

                                restaurantList.add(place);

                            }
                            if (restaurantList.size() > 0) {
                                showData();
                                mRestaurantAdapter.updateData(restaurantList);
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.setAdapter(mRestaurantAdapter);
                                    }
                                });
                            } else {
                                showError(getString(R.string.no_restaurants));
                            }

                        } else {
                            showError(getString(R.string.no_restaurants));
                        }
                    } catch (Exception e) {
                        showError(getString(R.string.no_restaurants));
                    }
                    mProgressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {


                }
            });
        } else {
            showNoNetworkDialog();
        }
    }

    private void showData() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.GONE);
    }

    private void showError(String errorMsg) {
        mErrorTextView.setText(errorMsg);
        mRecyclerView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null);
    }

    private void showNoNetworkDialog() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.no_internet_header))
                        .setMessage(getString(R.string.no_internet))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurants, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /* noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }
}
