package com.dxzmpk.time_saver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {

    private int resourceId;

    public ItemAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<Item> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Item item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView title = (TextView)view.findViewById(R.id.title_item);
        TextView link = (TextView)view.findViewById(R.id.link_item);
        title.setText(item.getOrderedTitle(position + 1));
        link.setText(item.getLink());
        return view;
    }
}
