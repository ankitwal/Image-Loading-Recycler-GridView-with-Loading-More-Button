package com.example.ankitwal.grabmovieinfo2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

//import com.eden.youtubesample.content.YouTubeContent;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VideoListAdapter extends  RecyclerView.Adapter<VideoListAdapter.ViewHolder> implements YouTubeThumbnailView.OnInitializedListener {

    private Context mContext;
    private Map<View, YouTubeThumbnailLoader> mLoaders;
    List<YouTubeVideo> items;

    public VideoListAdapter(final Context context, List<YouTubeVideo> list)
    {
        mContext = context.getApplicationContext();
        mLoaders = new HashMap<>();
        items=list;
    }


    @Override
    public VideoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            //Create the row
            final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.ytimage, parent, false);
            return new VideoListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoListAdapter.ViewHolder holder, int position) {


        //The item at the current position
        final YouTubeVideo item = items.get(position);


        //Set the title
        holder.title.setText(item.title);

        //Initialise the thumbnail
        holder.thumb.setTag(item.id);
        holder.thumb.initialize("AIzaSyBrmPMce50AARH9T88zmgMDWhnoMezErCM", this);

    }







    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }




    @Override
    public void onInitializationSuccess(YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
        mLoaders.put(view, loader);
        loader.setVideo((String) view.getTag());
    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView thumbnailView, YouTubeInitializationResult errorReason) {
        final String errorMessage = errorReason.toString();
        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
    }


     static class ViewHolder extends RecyclerView.ViewHolder{
        YouTubeThumbnailView thumb;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.videoTitle);
            thumb = (YouTubeThumbnailView) itemView.findViewById(R.id.imageView_thumbnail);
        }
    }
}
