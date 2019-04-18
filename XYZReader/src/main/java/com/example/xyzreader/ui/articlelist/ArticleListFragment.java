package com.example.xyzreader.ui.articlelist;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.xyzreader.ui.ArticlesViewModel;
import com.example.xyzreader.ui.HomeActivity;
import com.example.xyzreader.R;
import com.example.xyzreader.data.model.Article;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleListFragment extends Fragment {

    private ArticlesViewModel mViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public ArticleListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = HomeActivity.obtainViewModel(getActivity());
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        setupListAdapter(view);
        if (savedInstanceState == null) {
            updateRefreshingUI(true);
        }
    }

    private void setupListAdapter(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        final ArticlesAdapter adapter = new ArticlesAdapter(clickListener);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sglm);
        recyclerView.setAdapter(adapter);

        // observe articles list LiveData
        mViewModel.getArticlesListLiveData().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles != null) {
                    Timber.d("articles: " + articles.size());
                    updateRefreshingUI(false);
                    adapter.submitList(articles);
                }
            }
        });
    }

    private void updateRefreshingUI(boolean isRefreshing) {
        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

    public interface ArticleItemsClickListener {
        void onClick(View sharedView, String sharedElementName);
    }

    public ArticleListActivity.ArticleItemsClickListener clickListener = new ArticleListActivity.ArticleItemsClickListener() {
        @Override
        public void onClick(View sharedView, String sharedElementName) {
//            Intent intent = new Intent(ArticleListActivity.this, ArticleDetailActivity.class);
////                Uri articleUri = ItemsContract.Items.buildItemUri(getItemId(adapterPosition));
////                intent.putExtra(ArticleDetailActivity.EXTRA_ARTICLE_URI, articleUri.toString());
////                    getString(R.string.article_photo_shared_transition)
//            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                    ArticleListActivity.this, sharedView, sharedElementName);
//            startActivity(intent, options.toBundle());

        }
    };
}
