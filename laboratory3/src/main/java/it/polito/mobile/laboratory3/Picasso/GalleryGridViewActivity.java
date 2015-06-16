package it.polito.mobile.laboratory3.Picasso;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import it.polito.mobile.laboratory3.R;

public class GalleryGridViewActivity extends FragmentActivity {

    private FrameLayout sampleContent;
    String[] urlsArray;
    String[] signedUrls;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_gallery_gridview);
        sampleContent = (FrameLayout) findViewById(R.id.sample_content);
        urlsArray = getIntent().getStringArrayExtra("picturesUrls");

        if (savedInstanceState == null) {
            if (urlsArray == null) { throw new RuntimeException("picturesUrls param is required"); }
            new SignUrls().execute();
        }
    }

    private class SignUrls extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            signedUrls = new String[urlsArray.length];
            context = GalleryGridViewActivity.this;
        }

        @Override
        protected String doInBackground(Void... params) {
            signedUrls = Data.getUrls(context, urlsArray);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.sample_content, GridFragment.newInstance(signedUrls))
                    .commit();
        }
    }

    void showDetails(String url) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sample_content, DetailFragment.newInstance(url))
                .addToBackStack(null)
                .commit();
    }

    public static class GridFragment extends Fragment {
        public static GridFragment newInstance(String[] signedUrls) {
            Bundle arguments = new Bundle();
            arguments.putStringArray("signedUrls", signedUrls);

            GridFragment fragment = new GridFragment();
            fragment.setArguments(arguments);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final GalleryGridViewActivity activity = (GalleryGridViewActivity) getActivity();
            Bundle arguments = getArguments();
            String[] signedUrls = arguments.getStringArray("signedUrls");
            final GalleryGridViewAdapter adapter = new GalleryGridViewAdapter(activity, signedUrls);



            GridView gv = (GridView) LayoutInflater.from(activity)
                    .inflate(R.layout.activity_gallery_gridview, container, false);
            gv.setAdapter(adapter);
            gv.setOnScrollListener(new ScrollListener(getActivity()));
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url = adapter.getItem(position);
                    activity.showDetails(url);
                }
            });

            return gv;
        }
    }

    public static class DetailFragment extends Fragment {
        private static final String KEY_URL = "picasso:url";

        public static DetailFragment newInstance(String url) {
            Bundle arguments = new Bundle();
            arguments.putString(KEY_URL, url);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Activity activity = getActivity();

            View view = LayoutInflater.from(activity)
                    .inflate(R.layout.list_detail, container, false);

            TextView urlView = (TextView) view.findViewById(R.id.url);
            ImageView imageView = (ImageView) view.findViewById(R.id.photo);

            Bundle arguments = getArguments();
            String url = arguments.getString(KEY_URL);

            urlView.setText(url);
            Picasso.with(activity)
                    .load(url)
                    .fit()
                    .tag(activity)
                    .into(imageView);

            return view;
        }
    }
}
