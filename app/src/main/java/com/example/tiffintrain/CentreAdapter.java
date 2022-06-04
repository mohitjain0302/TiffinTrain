package com.example.tiffintrain;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CentreAdapter extends ArrayAdapter<TiffinCentre> {

    private Context mContext ;
    public CentreAdapter(Activity context, ArrayList<TiffinCentre> centres) {
        super(context, 0, centres);
        mContext = context ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.centre_list_item, parent, false);
        }

        TiffinCentre currentCentre = getItem(position);

        TextView name =(TextView) listItemView.findViewById(R.id.name_text_view);
        name.setText(currentCentre.getName());

        TextView address = (TextView) listItemView.findViewById(R.id.address_text_view);
        address.setText(currentCentre.getAddress());

        ImageView tiffinCentreImageInList = listItemView.findViewById(R.id.tiffin_centre_image_in_list);
        if(currentCentre.getMyTiffinCentreImageUrl()!=null){
            Picasso.with(mContext).load(currentCentre.getMyTiffinCentreImageUrl()).fit().centerCrop().into(tiffinCentreImageInList);
        }


        return listItemView;
    }

}
