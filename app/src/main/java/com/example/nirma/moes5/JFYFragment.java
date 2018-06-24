package com.example.nirma.moes5;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nirma.moes5.model.Post;
import com.example.nirma.moes5.network.Network;
import com.example.nirma.moes5.network.Routes;
import com.example.nirma.moes5.utils.ArticleAdapter;
import com.example.nirma.moes5.utils.ArticleListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class JFYFragment extends Fragment implements ArticleListener {

    private RecyclerView lstArtcicles;
    private ArticleAdapter adapter;
    private Routes routes;
    private List<Post> posts;

    public JFYFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jfy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        posts = new ArrayList<>();
        lstArtcicles = view.findViewById(R.id.lst_article);
        lstArtcicles.setLayoutManager(new LinearLayoutManager(getContext()));
        routes = Network.requestWp().create(Routes.class);
        adapter = new ArticleAdapter(posts, this);
        lstArtcicles.setAdapter(adapter);
        requestArticle();
    }

    private void requestArticle() {
        routes.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    posts.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(Post post) {
        Intent i = new Intent(getContext(), ArticleDetailActivity.class);
        i.putExtra("title", post.getTitle().getRendered());
        i.putExtra("content", post.getContent().getRendered());
        startActivity(i);
    }
}
