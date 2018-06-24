package com.example.nirma.moes5.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nirma.moes5.R;
import com.example.nirma.moes5.model.Post;

import java.util.List;

/**
 * Created by mvryan on 13/06/18.
 * this class handled to show the article
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.Holder> {

    private List<Post> posts;
    private ArticleListener listener;

    public ArticleAdapter(List<Post> posts, ArticleListener listener) {
        this.posts = posts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        holder.txtTitle.setText(posts.get(position).getTitle().getRendered());
        holder.txtSlug.setText(posts.get(position).getSlug());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(posts.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        LinearLayout item;
        TextView txtTitle, txtSlug;

        public Holder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txtSlug = itemView.findViewById(R.id.txt_slug);
        }
    }
}
