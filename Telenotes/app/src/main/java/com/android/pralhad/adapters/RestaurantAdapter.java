package com.android.pralhad.adapters;

/**
 * Created by Pralhad on 3/22/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.pralhad.models.Restaurant;
import com.android.pralhad.telenotes.R;

import java.util.ArrayList;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private ArrayList<Restaurant> restaurantList;

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {
        public TextView name, address, rating, type;

        public RestaurantViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.restaurant_name);
            address = (TextView) view.findViewById(R.id.restaurant_address);
            rating = (TextView) view.findViewById(R.id.restaurant_rating);
            type = (TextView) view.findViewById(R.id.restaurant_type);
        }
    }

    public RestaurantAdapter() {
        restaurantList = new ArrayList<Restaurant>();
    }

    public RestaurantAdapter(ArrayList<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_list_row, parent, false);

        return new RestaurantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        if (restaurantList != null && position < restaurantList.size()) {
            Restaurant restaurant = restaurantList.get(position);
            holder.name.setText(restaurant.getName());
            holder.address.setText(restaurant.getAddress());
            holder.rating.setText(restaurant.getRating());
            holder.type.setText(restaurant.getType());
        }
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public void updateData(ArrayList<Restaurant> restaurants) {
        restaurantList = null;
        restaurantList = restaurants;
        notifyDataSetChanged();
    }

    public Restaurant getRestaurant(int position) {
        if(restaurantList != null) {
            return restaurantList.get(position);
        }
        return null;
    }
}