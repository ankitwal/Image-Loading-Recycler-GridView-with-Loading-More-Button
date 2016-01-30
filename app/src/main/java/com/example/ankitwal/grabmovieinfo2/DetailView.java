package com.example.ankitwal.grabmovieinfo2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class DetailView extends AppCompatActivity {

    TwoWayView videoList;
    TwoWayView likeList;
    ImageView headerImage;
    ImageView posterImage;
    TextView infoText;
    TextView title;
    String id;
    Context context;
    JSONObject movie,videos,similar;
    List<ListItem> items;
    List<YouTubeVideo> vItems;
    Boolean isFav;
    FloatingActionButton fab;
    int itemPosition;
    String posterPath,name;

    private newRecyclerAdapter adapter2;
    private VideoListAdapter adapterVideo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            if(savedInstanceState==null || savedInstanceState.isEmpty()) {



                setContentView(R.layout.details);
                Intent intent = getIntent();
                id = intent.getStringExtra("id");

                items = new ArrayList<ListItem>();
                vItems = new ArrayList<YouTubeVideo>();




                context = this;
                videoList = (TwoWayView) findViewById(R.id.VideoList);
                likeList = (TwoWayView) findViewById(R.id.LikeList);
                headerImage = (ImageView) findViewById(R.id.imageview_header);
                posterImage = (ImageView) findViewById(R.id.PosterImage);
                infoText = (TextView) findViewById(R.id.infoText);
                title = (TextView) findViewById(R.id.textView_title);
                fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));


                String[] value = {(id + "?api_key=4df263f48a4fe2621749627f5d001bf0"), (id + "/videos?api_key=4df263f48a4fe2621749627f5d001bf0"), (id + "/similar?api_key=4df263f48a4fe2621749627f5d001bf0&page=1")};
                GetAsync getMoive;
                getMoive = new GetAsync();
                getMoive.execute(value);
                adapter2 = new newRecyclerAdapter(this, items, true);
                adapterVideo = new VideoListAdapter(this, vItems);

                videoList.setAdapter(adapterVideo);
                likeList.setAdapter(adapter2);

                likeList.addOnItemTouchListener(
                        new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                System.out.println(items.get(position).id);
                                Intent i = new Intent(DetailView.this, DetailView.class);
                                i.putExtra("id", items.get(position).id);
                                startActivity(i);
                            }
                        })
                );

                videoList.addOnItemTouchListener(
                        new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                // Passing the movie the activity context caused a memory leak. Hence passed the Application Context
                                if (YouTubeIntents.canResolvePlayVideoIntent(getApplicationContext())) {
                                    startActivity(YouTubeStandalonePlayer.createVideoIntent(DetailView.this,
                                            getString(R.string.DEVELOPER_KEY), vItems.get(position).id));


                                }
                            }
                        })
                );

                likeList.setOrientation(TwoWayLayoutManager.Orientation.HORIZONTAL);
                likeList.setHasFixedSize(true);

                videoList.setOrientation(TwoWayLayoutManager.Orientation.HORIZONTAL);
                videoList.setHasFixedSize(true);



            }




    }
    public boolean onSupportNavigateUp() {

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        // Retrun intent to Main Activity to update its FAV list
        finish();
        return true;
    }


    public void onBackPressed (){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        // Retrun intent to Main Activity to update its FAV list
        finish();

    }


    class GetAsync extends AsyncTask<String, Void, Boolean> {

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;
        private static final String url="http://api.themoviedb.org/3/movie/";
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";



        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... urlFinal)
        {

            try {


                Log.d("request", "starting");

                movie = jsonParser.makeHttpRequest(url+urlFinal[0]);
                videos=jsonParser.makeHttpRequest(url+urlFinal[1]);
                similar=jsonParser.makeHttpRequest(url+urlFinal[2]);

                return true;


            } catch (Exception e)

            {
                e.printStackTrace();
            }

            return false;
        }

        protected void onPostExecute(Boolean val) {


            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (val)
            {
                try
                {
                    String imageUrl="http://image.tmdb.org/t/p/w500/";
                    String apiKey="?api_key=ffecbbe8a1ca091801b9e78d3ee1a073";
                    if(!movie.getString("backdrop_path").equals("null")) {
                        Picasso.with(context) //
                                .load(imageUrl + movie.getString("backdrop_path")+apiKey) //
                                .placeholder(R.drawable.blank) //
                                .error(R.drawable.error) //
                                .fit() //
                                .tag(context) //
                                .into(headerImage);
                    }
                    else{((ViewGroup) headerImage.getParent()).removeView(headerImage);}
                    if(!movie.getString("poster_path").equals("null")) {
                        Picasso.with(context) //
                                .load(imageUrl + movie.getString("poster_path")+apiKey) //
                                .placeholder(R.drawable.blank) //
                                .error(R.drawable.error) //
                                .fit() //
                                .tag(context) //
                                .into(posterImage);
                    }else{
                        ((ViewGroup) posterImage.getParent()).removeView(posterImage);
                    }

                    infoText.setText(movie.getString("overview"));
                    title.setText(movie.getString("original_title"));

                    posterPath=movie.getString("poster_path");
                    name=movie.getString("original_title");

                    JSONArray result=videos.getJSONArray("results");

                    //Populate the Video list with videos that have site value as YouTube
                    for (int i=0; i<result.length(); i++)
                    {
                        try
                        {

                                if(result.getJSONObject(i).getString("site").equals("YouTube")) {
                                    vItems.add(i, new YouTubeVideo(result.getJSONObject(i).getString("key"), result.getJSONObject(i).getString("name")));
                                }


                        }

                        catch (JSONException e)

                        {
                            e.printStackTrace();
                        }


                    }

                    adapterVideo.items=vItems;
                    adapterVideo.notifyDataSetChanged();

                    result=similar.getJSONArray("results");

                    //Populate the list of Similar Moview
                    for (int i=0; i<result.length(); i++)
                    {
                        try
                        {
                          items.add(i,  new ListItem(result.getJSONObject(i).getString("id"), result.getJSONObject(i).getString("poster_path"),result.getJSONObject(i).getString("original_title"), false, false));
                        }

                        catch (JSONException e)

                        {
                            e.printStackTrace();
                        }


                    }
                    adapter2.mValues=items;
                    adapter2.notifyDataSetChanged();

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
            // Favourite button listner is only set after JSON get to avoid Null values in Fav list.
            itemPosition=0;
            isFav = false;
            final List<ListItem> favList;
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(DetailView.this);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Gson gson = new Gson();
            String json = mPrefs.getString("Favs", "");
            favList = (List<ListItem>) gson.fromJson(json, new TypeToken<List<ListItem>>() {}.getType());
            if(favList!=null) {


                ListIterator<ListItem> litr = favList.listIterator();
                isFav = false;
                while (litr.hasNext()) {
                    if (litr.next().id.equals(id)) {
                        isFav = true;
                        fab.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        break;

                    }
                    itemPosition++; // Store position to avoid another itteration to find item in list.
                }
            }
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isFav) {
                        fab.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY)); // GRAY indicated the movie can be favourited
                        favList.remove(itemPosition);
                        System.out.println("Removing from position " + String.valueOf(itemPosition));
                        isFav = false;

                    } else {
                        fab.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN)); // GREEN indicates the Movie has been added to the favourites.
                        favList.add(favList.size(), new ListItem(id, posterPath, name, false, false));
                        System.out.println("Adding " );
                        isFav=true;
                        itemPosition=favList.size()-1;
                    }

                    // Update directly into persistant memory. It'll get updated when ever a detail activity returns view to Main Activity.
                    SharedPreferences mPrefs=PreferenceManager.getDefaultSharedPreferences(DetailView.this);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    Gson gson = new Gson();
                    String favString = gson.toJson(favList);
                    prefsEditor.putString("Favs", favString);
                    prefsEditor.apply();

                }
            });

        }

    }



}
