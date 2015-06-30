package it.polito.mobile.androidassignment2.StudentFlow.lab3.notice.gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.s3client.AmazonUrlSigner;


/**
 * Created by mark9 on 09/06/15.
 */
public class GalleryActivity extends FragmentActivity {

    String[] urlsArray;
    String[] signedUrls;
    Context context;
    private List<AsyncTask<?,?,?>> pendingTasks = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_gallery);
        urlsArray = getIntent().getStringArrayExtra("picturesUrls");

        if (savedInstanceState == null) {
            if (urlsArray == null) { throw new RuntimeException("no images"); }
            AsyncTask<Void, Void, String> t = new AsyncTask<Void, Void, String>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    signedUrls = new String[urlsArray.length];
                    context = GalleryActivity.this;
                }

                @Override
                protected String doInBackground(Void... params) {
                    signedUrls = AmazonUrlSigner.getUrls(context, urlsArray);
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {

                    super.onPostExecute(s);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.sample_content, GridFragment.newInstance(signedUrls))
                            .commit();
                }
            };
            t.execute();
            pendingTasks.add(t);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        for(AsyncTask<?,?,?> t : pendingTasks){
            if(t.getStatus()== AsyncTask.Status.PENDING
                    || t.getStatus()== AsyncTask.Status.RUNNING)
                t.cancel(true);
        }
        pendingTasks.clear();
    }



    void showImage(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "image/*");
        startActivity(intent);
    }

    public static class GridFragment extends Fragment {
        private static String SIGNED_URLS="signedUrls";

        public static GridFragment newInstance(String[] signedUrls) {
            Bundle arguments = new Bundle();
            arguments.putStringArray(SIGNED_URLS, signedUrls);

            GridFragment fragment = new GridFragment();
            fragment.setArguments(arguments);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final GalleryActivity activity = (GalleryActivity) getActivity();
            Bundle arguments = getArguments();
            String[] signedUrls = arguments.getStringArray(SIGNED_URLS);
            final GalleryAdapter adapter = new GalleryAdapter(activity, signedUrls);



            GridView gv = (GridView) LayoutInflater.from(activity)
                    .inflate(R.layout.fragment_gridview, container, false);
            gv.setAdapter(adapter);
            final Context context = getActivity();
            gv.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {
                    final Picasso picasso = Picasso.with(context);
                    if (i == SCROLL_STATE_IDLE || i == SCROLL_STATE_TOUCH_SCROLL) {
                        picasso.resumeTag(context);
                    } else {
                        picasso.pauseTag(context);
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                }
            });
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url = adapter.getItem(position);
                    activity.showImage(url);
                }
            });

            return gv;
        }
    }

}
