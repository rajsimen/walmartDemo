package com.walmart.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.walmart.android.R;
import com.walmart.android.dom.Friend;
import com.walmart.android.listener.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajkumar
 * Nallusamy on 5/19/2017.
 */

public class FriendsListViewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FriendsListAdapter friendsListAdapter;
    private RecyclerView friendsListView;
    //private final View.OnClickListener mOnClickListener = new MyOnClickListener();
    private JSONArray friendslist;
    private ArrayList<Friend> friends = new ArrayList<Friend>();
    private String jsondata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

       Intent intent = getIntent();
        jsondata = intent.getStringExtra("jsondata"); // fetching the data from HomeScreenActivityar);
        //Log.v("",jsondata);
        loadValues(jsondata,0,30);


        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView) ;

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendsListAdapter = new FriendsListAdapter();
        mRecyclerView.setAdapter(friendsListAdapter);

        friendsListAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                friends.add(null);
                friendsListAdapter.notifyItemInserted(friends.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");

                        //Remove loading item
                        friends.remove(friends.size() - 1);
                        friendsListAdapter.notifyItemRemoved(friends.size());

                        //Load data
                        int index = friends.size();
                        int end = index + 20;
                        loadValues(jsondata,index,end);

                        friendsListAdapter.notifyDataSetChanged();
                        friendsListAdapter.setLoaded();
                    }
                }, 5000);
            }
        });
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.friend_name);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    class FriendsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;

        public FriendsListAdapter() {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        @Override
        public int getItemViewType(int position) {
            return friends.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(FriendsListViewActivity.this).inflate(R.layout.raw_content_friend, parent, false);
                view.setOnClickListener(FriendsListViewActivity.this);
                return new UserViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(FriendsListViewActivity.this).inflate(R.layout.layout_loading_item, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserViewHolder) {
                Friend friend = friends.get(position);
                UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.tvName.setText(friend.getName());
            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return friends == null ? 0 : friends.size();
        }

        public void setLoaded() {
            isLoading = false;
        }
    }
        protected void loadValues(String jsondata,int start, int count){
            try {
                // Stored the value in the POJO Friend class
                friendslist = new JSONArray(jsondata);
                // store value till 30 count
                for (int l=start; l < count; l++) {
                    Friend friend=new Friend(friendslist.getJSONObject(l).getString("name"));
                    Log.v("Name of Friends : ",friend.getName());
                    friends.add(friend);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
}
