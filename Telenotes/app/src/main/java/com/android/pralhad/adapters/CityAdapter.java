package com.android.pralhad.adapters;

/**
 * Created by Pralhad on 3/22/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.pralhad.models.City;
import com.android.pralhad.telenotes.R;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

    private ArrayList<City> cityList;

    public class CityViewHolder extends RecyclerView.ViewHolder {
        public TextView name, state;

        public CityViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            state = (TextView) view.findViewById(R.id.state);
        }
    }

    public CityAdapter() {
        this.cityList = new ArrayList<City>();
    }

    public CityAdapter(ArrayList<City> cityList) {
        this.cityList = cityList;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_list_row, parent, false);

        return new CityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        if(cityList != null && position < cityList.size()) {
            City city = cityList.get(position);
            holder.name.setText(city.getName());
            holder.state.setText(city.getState());
        }
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public void updateData(ArrayList<City> cities) {
        cityList = null;
        cityList = cities;
        notifyDataSetChanged();
    }

    public String getCity(int position) {
        if(cityList != null) {
            return cityList.get(position).getName();
        }
        return "NA";
    }
}