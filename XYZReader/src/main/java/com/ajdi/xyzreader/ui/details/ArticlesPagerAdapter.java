package com.ajdi.xyzreader.ui.details;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ajdi.xyzreader.data.model.Article;

import java.util.List;

import timber.log.Timber;

/**
 * @author Yassin Ajdi
 * @since 4/18/2019.
 */
class ArticlesPagerAdapter extends FragmentStatePagerAdapter {

    private List<Article> mArticleList;

    public ArticlesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Article article = mArticleList.get(position);
        Timber.d("getItem ==> Article ID: %s", article.getId());
        return ArticleDetailFragment.newInstance(article);
    }

    @Override
    public int getCount() {
        return mArticleList != null ? mArticleList.size() : 0;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }

    public void submitList(List<Article> articles) {
        mArticleList = articles;
        notifyDataSetChanged();
    }
}
