package com.example.ankitwal.grabmovieinfo2;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


public class ItemFragment extends Fragment {


    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 3;
    private OnListFragmentInteractionListener mListener;

    private newRecyclerAdapter adapter2;
    private RecyclerView mRecyclerView;
    private int pageNo;
    boolean LoadButtonflag;
    boolean isFavs;



    public JSONArray result;
    List<ListItem> items;


    public ItemFragment()
    {

    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int pos)
    {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt("position", pos);
        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = 3;
            isFavs=(getArguments().getInt("position"))==1?true:false;
        }
        pageNo=0;LoadButtonflag=true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);


        items= new ArrayList<ListItem>();
        String[] value = {"now_playing?api_key=4df263f48a4fe2621749627f5d001bf0&page="};
        if(!isFavs) {
            GetAsync getMoives;
            getMoives = new GetAsync();
            getMoives.execute(value);
        }
        else{

        items=((MainActivity)getActivity()).favs;
        }


        GridLayoutManager manager = new GridLayoutManager(this.getContext(), 3);
        RecyclerView recyclerView = (RecyclerView) view;
        adapter2=new newRecyclerAdapter(getContext(),ItemFragment.this.items,false);

        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (items.get(position).isFooter) {
                    return 3;
                } else {
                    return 1;
                }
            }


        });


        recyclerView.setLayoutManager(manager);
        mRecyclerView=recyclerView;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter2);


        //newWay
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {


                        mListener.onListFragmentInteraction(items.get(position).id);
                    }
                })
        );

        return view;
    }

    public void loadMore(){
        String[] value = {"now_playing?api_key=4df263f48a4fe2621749627f5d001bf0&page="};
        GetAsync getMoives;getMoives=new GetAsync();
        getMoives.execute(value);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public  void updateContents(List<ListItem> ls)
    {
        items=ls;
        adapter2.mValues=items;
        adapter2.notifyDataSetChanged();
    }



    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name

        void onListFragmentInteraction(String id);
    }

    class GetAsync extends AsyncTask<String, Void, JSONObject> {

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private static final String url="http://api.themoviedb.org/3/movie/";
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";

        @Override
        protected void onPreExecute() {
            pageNo++;
            pDialog = new ProgressDialog(ItemFragment.this.getActivity());
            pDialog.setMessage("Attempting...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... urlFinal) {

            try {


                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest((url+urlFinal[0]+String.valueOf(pageNo)));

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject json) {


            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null)
            {
                try
                {
                    ItemFragment.this.result=json.getJSONArray("results");
                    int size=items.size()==0?0:items.size()-1;
                    for (int i=0; i<ItemFragment.this.result.length(); i++)
                    {
                        try
                        {
                            ListIterator<ListItem> litr = items.listIterator();
                            boolean alreadyContains=false;
                            // Remove Duplicate movies from upComing List
                            while(litr.hasNext())
                            {
                                if(litr.next().id.equals(ItemFragment.this.result.getJSONObject(i).getString("id")))
                                {
                                    alreadyContains = true;
                                }

                            }

                            if(!alreadyContains) {
                                ItemFragment.this.items.add(size, new ListItem(ItemFragment.this.result.getJSONObject(i).getString("id"), ItemFragment.this.result.getJSONObject(i).getString("poster_path"),ItemFragment.this.result.getJSONObject(i).getString("original_title"), false, false));
                                size++;
                            }

                        }

                        catch (JSONException e)

                        {
                            e.printStackTrace();
                        }


                    }

                    if(LoadButtonflag){ItemFragment.this.items.add(size,new ListItem("Load More","xxx","xxx",false,true));LoadButtonflag=false;}
                    adapter2.mValues=items;
                    ItemFragment.this.adapter2.notifyDataSetChanged();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }


        }

    }

}
