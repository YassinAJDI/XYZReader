package com.example.xyzreader.ui.articlelist;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xyzreader.data.model.Article;
import com.example.xyzreader.ui.articlelist.ArticleListFragment.ArticleItemsClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import timber.log.Timber;

/**
 * @author Yassin Ajdi
 * @since 4/16/2019.
 */
public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Article> mArticleList;
    private ArticleItemsClickListener listener;

    public ArticlesAdapter(ArticleItemsClickListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return ArticleViewHolder.create(parent, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ArticleViewHolder) holder).bindTo(mArticleList.get(position));
    }

//    private Date parsePublishedDate() {
//        try {
//            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
//            return dateFormat.parse(date);
//        } catch (ParseException ex) {
//            Log.e(TAG, ex.getMessage());
//            Log.i(TAG, "passing today's date");
//            return new Date();
//        }
//    }

    @Override
    public int getItemCount() {
        return mArticleList != null ? mArticleList.size() : 0;
    }


    public void submitList(List<Article> articles) {
        Timber.d("data changed rebind viewholders");
        mArticleList = articles;
        notifyDataSetChanged();
    }
}
