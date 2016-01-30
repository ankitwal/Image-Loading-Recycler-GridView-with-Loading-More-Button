package com.example.ankitwal.grabmovieinfo2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
//import com.example.ankitwal.grabmovieinfo2.ItemFragment.OnListFragmentInteractionListener;
import com.squareup.picasso.Picasso;
import java.util.List;
import static android.widget.ImageView.ScaleType.CENTER_CROP;



public class newRecyclerAdapter extends RecyclerView.Adapter<newRecyclerAdapter.ViewHolder> { // Adapter for all image lists


    private final Context context;
    boolean horizontal;


    public List<ListItem> mValues;

    public newRecyclerAdapter(Context context, List<ListItem> items,boolean horizontal) {
        mValues = items;
        this.horizontal=horizontal;
        this.context=context;
    }


    private class VIEW_TYPES {
        public static final int Header = 1;
        public static final int Normal = 2;
        public static final int Footer = 3;
    }

    @Override
    public int getItemViewType(int position) {

        if(mValues.get(position).isHeader)
            return VIEW_TYPES.Header;
        else if(mValues.get(position).isFooter)
            return VIEW_TYPES.Footer;      // footer is a dummy ListItem with grabage data used to display loadMore
        else
            return VIEW_TYPES.Normal;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case VIEW_TYPES.Normal:
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
                return new ViewHolder(view);
            case VIEW_TYPES.Footer:
                View footerView= LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
                return new ViewHolder(footerView);
            default:

                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
                return new ViewHolder(view);


        }

    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {

        String url = mValues.get(position).url;
        System.out.println("URL" + url);
        url="http://image.tmdb.org/t/p/w500" + url+"?api_key=ffecbbe8a1ca091801b9e78d3ee1a073";

        holder.id=mValues.get(position).id;
        if(!mValues.get(position).isFooter)
        {
            holder.textV.setText(mValues.get(position).title);
            holder.image.setScaleType(CENTER_CROP);

            if(!mValues.get(position).url.equals("null"))
            {
                // Trigger the download of the URL asynchronously into the image view.
                Picasso.with(context) //
                        .load(url) //
                        .placeholder(R.drawable.blank) //
                        .error(R.drawable.error) //
                        .fit() //
                        .tag(context) //
                        .into(holder.image);
            }
            else
            {
                // Prevents the Picasso library from attempting to redownload a movie with no poster URL
                //Prevents flickering images.

                holder.image.setImageResource(R.drawable.error);
            }

            


        }
        else{


        }




    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

     static class ViewHolder extends RecyclerView.ViewHolder {

        SquaredImageView image;
        public String id;
        View footerView;
        TextView textV;



        public ViewHolder(View view) {
            super(view);

            footerView = view.findViewById(R.id.button);     // Gets initialised to null is the Item is a movie item, but is never accessed.
            image = (SquaredImageView) view.findViewById(R.id.gridImage);
            textV = (TextView) view.findViewById(R.id.detailTitle);



        }


    }

}
