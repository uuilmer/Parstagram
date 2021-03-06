package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.parstagram.utils.EndlessRecyclerViewScrollListener;
import com.example.parstagram.models.Post;
import com.example.parstagram.adapters.PostAdapter;
import com.example.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    List<Post> posts;
    RecyclerView rvPosts;
    PostAdapter adapter;
    SwipeRefreshLayout refreshLayout;
    EndlessRecyclerViewScrollListener scrolling;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    // Set an adapter for the RecyclerView and query for posts whenever this fragment is opened.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        posts = new ArrayList<>();
        rvPosts = view.findViewById(R.id.rv_posts);
        refreshLayout = view.findViewById(R.id.swipeContainer);

        // We need to assign the same layout manager to both RecyclerView and EnlessScrollListener, so they are syncronized
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // As we scroll, get the next page
        scrolling = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(page);
            }
        };

        rvPosts.addOnScrollListener(scrolling);

        adapter = new PostAdapter(view.getContext(), posts);
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(linearLayoutManager);

        queryPosts(0);

        // When we refresh, delete the posts we have now and replace with a more updated set
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                posts.clear();
                queryPosts(0);
            }
        });
    }

    // Retrieve a list of all the posts
    public void queryPosts(int page) {
        ParseQuery<Post> q = ParseQuery.getQuery(Post.class);
        q.include(Post.KEY_USER);

        // The first Post that we need from page x is x * 5 + 1, so set skip(lower bound) as x * 5
        // We need 5 so set limit(upper bound) as x * 5 + 5
        q.setLimit(5 * page + 5);
        q.setSkip(5 * page);

        q.addDescendingOrder("createdAt");
        q.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error getting posts", Toast.LENGTH_SHORT).show();

                    // Reset refreshing progress bar to false
                    refreshLayout.setRefreshing(false);
                    return;
                }
                posts.addAll(objects);
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
    }
}