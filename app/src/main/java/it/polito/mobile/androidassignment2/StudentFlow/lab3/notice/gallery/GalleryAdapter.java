package it.polito.mobile.androidassignment2.StudentFlow.lab3.notice.gallery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


import it.polito.mobile.androidassignment2.R;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

/**
 * Created by mark9 on 09/06/15.
 */
final class GalleryAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> urls = new ArrayList<String>();

    public GalleryAdapter(Context context, String[] signedUrls) {
        this.context = context;
        for (String u : signedUrls) {
            urls.add(u);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SquareImageView view = (SquareImageView) convertView;
        if (view == null) {
            view = new SquareImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        // Get the image URL for the current position.
        String url = getItem(position);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load(url) //
                .placeholder(R.drawable.placeholder) //
                .error(R.drawable.error) //
                .fit() //
                .tag(context) //
                .into(view);

        return view;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public String getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
