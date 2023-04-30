package com.shehzad.aboutphone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shehzad.aboutphone.R;
import com.shehzad.aboutphone.model.InfoModel;

import java.util.ArrayList;

public class InfoAdapter extends BaseAdapter {
    Context context;
    ArrayList<InfoModel> infoModels;

    public InfoAdapter(Context context, ArrayList<InfoModel> infoModels) {
        this.context = context;
        this.infoModels = infoModels;
    }


    @Override
    public int getCount() {
        return infoModels.size();
    }

    @Override
    public Object getItem(int i) {
        return infoModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        TextView key, value;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.info_list_item, parent, false);
        }

        key = view.findViewById(R.id.keyText);
        value = view.findViewById(R.id.valueText);

        InfoModel model = infoModels.get(position);
        key.setText(model.getKey());
        value.setText(model.getValue());

        return view;
    }
}
