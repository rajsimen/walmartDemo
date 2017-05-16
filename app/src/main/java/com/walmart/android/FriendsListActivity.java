package com.walmart.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.walmart.android.dom.Friend;
import com.walmart.android.listener.OnLoadMoreListener;
import com.walmart.android.utils.FriendsListAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by rajkumar
 * Nallusamy on 5/16/2017.
 */


public class FriendsListActivity extends AppCompatActivity {

    private RecyclerView friendsListView;
    private FriendsListAdapter friendsListAdapter;
    private JSONArray friendslist;
    private ArrayList<Friend> friends = new ArrayList<Friend>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        Intent intent = getIntent();
        String jsondata = intent.getStringExtra("jsondata"); // fetching the data from HomeScreenActivity



        try {
            // Stored the value in the POJO Friend class
            friendslist = new JSONArray(jsondata);
            // store value till 30 count
            for (int l=0; l < 30; l++) {
                Friend friend=new Friend(friendslist.getJSONObject(l).getString("name"));
                friends.add(friend);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Recycler View
        friendsListView=(RecyclerView)findViewById(R.id.recyclerView) ;

        // initialize the adapter
        friendsListAdapter=new FriendsListAdapter(friends,friendsListView);
        friendsListView.setAdapter(friendsListAdapter);

        friendsListAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override public void onLoadMore() {
                Log.e("haint", "Load More");
                friends.add(null);
                friendsListAdapter.notifyItemInserted(friends.size() - 1);
                //Load more data for reyclerview
                new Runnable() {
                    @Override public void run() {
                        Log.e("haint", "Load More 2");
                        //Remove loading item
                        friends.remove(friends.size() - 1);
                        friendsListAdapter.notifyItemRemoved(friends.size());
                        //Load data
                        int index = friends.size();
                        int end = index + 20;
                        // Add the Next Count in to the Array

                            for (int j = index; j < end; j++) {
                                Friend friend=new Friend(friendslist.getJSONObject(j).getString("name"));
                                friends.add(friend);
                            }



                        friendsListAdapter.notifyDataSetChanged();
                        friendsListAdapter.setLoaded();
                    }
                }, 5000;
            }
        });
    }

}
