package com.ajdi.xyzreader.ui.articlelist;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajdi.xyzreader.data.model.Article;
import com.ajdi.xyzreader.ui.articlelist.ArticleListFragment.ArticleItemsClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Yassin Ajdi
 * @since 4/16/2019.
 */
public class ArticlesAdapter extends RecyclerView.Adapter<ArticleViewHolder> {

    private List<Article> mArticleList;
    private ArticleItemsClickListener listener;

    public ArticlesAdapter(ArticleItemsClickListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return ArticleViewHolder.create(parent, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        holder.bindTo(mArticleList.get(position));
    }

    @Override
    public int getItemCount() {
        return mArticleList != null ? mArticleList.size() : 0;
    }


    public void submitList(List<Article> articles) {
        mArticleList = articles;
        notifyDataSetChanged();
    }
}
