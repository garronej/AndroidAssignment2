package it.polito.mobile.androidassignment2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.LoginActivity;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.OffersListsActivity;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.context.AppContext;

public class OfferArrayAdapter extends ArrayAdapter<Offer> {
    private final Context context;
    private final List<Offer> values;


    public List<Offer> getValue(){
        return this.values;
    }

    public OfferArrayAdapter(Context context, List<Offer> values) {
        super(context, R.layout.list_offer_layout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_offer_layout, parent, false);
        }
        TextView textView1 = (TextView) convertView.findViewById(R.id.firstLine);
        TextView textView2 = (TextView) convertView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);

        Offer offer = this.values.get(position);



        textView1.setText(offer.getTitle());
        textView2.setText(offer.getCompanyName() + ", " + offer.getLocation());


        int offerId = offer.getId();


        if( isFavourite(offerId)){



            if( isApplied(offerId)){

                imageView.setImageResource(R.drawable.offer_fav_app);


            }else{
                imageView.setImageResource(R.drawable.offer_fav);

            }

        }else{




            if( isApplied(offerId)){

                imageView.setImageResource(R.drawable.offer_app);
            }else{
                imageView.setImageResource(R.drawable.offer);
            }


        }



        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return values.get(position).getId();
    }


    public boolean isFavourite(int offerId){

        List<Offer> favOffers = null;

        try{
            favOffers = ((AppContext) this.context.getApplicationContext()).getSession().getFavoriteOffer();
        }catch(DataFormatException e){
            return false;
        }
        for( Offer offer : favOffers){

            if( offer.getId() == offerId )
                return true;

        }

        return false;

    }

    public boolean isApplied(int offerId){

        List<Offer> appliedOffers = null;

        try{
            appliedOffers = ((AppContext)this.context.getApplicationContext()).getSession().getAppliedOffers();
        }catch(DataFormatException e){
            return false;
        }
        for( Offer offer : appliedOffers){

            if( offer.getId() == offerId )
                return true;

        }

        return false;

    }





} 