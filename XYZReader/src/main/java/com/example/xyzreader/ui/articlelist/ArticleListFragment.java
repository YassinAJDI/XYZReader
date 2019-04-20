package com.example.xyzreader.ui.articlelist;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.TransitionInflater;

import com.example.xyzreader.R;
import com.example.xyzreader.data.model.Article;
import com.example.xyzreader.databinding.FragmentArticleListBinding;
import com.example.xyzreader.ui.ArticlesViewModel;
import com.example.xyzreader.ui.HomeActivity;
import com.example.xyzreader.ui.details.ArticlesPagerFragment;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass, representing a list of Articles. This fragment has different
 * presentations for handset and tablet-size devices. On handsets, the fragment presents a list of
 * items, which when touched, lead to a {@link ArticlesPagerFragment} representing item details. On tablets, the
 * fragment presents a grid of items as cards.
 */
public class ArticleListFragment extends Fragment {

    private ArticlesViewModel mViewModel;
    private FragmentArticleListBinding mBinding;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    public ArticleListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        // Enable FragmentManager logging
//        FragmentManager.enableDebugLogging(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(
                    TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
        postponeEnterTransition();
        // Inflate the layout for this fragment
        mBinding = FragmentArticleListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = HomeActivity.obtainViewModel(getActivity());
        setupListAdapter(view);
        prepareTransitions();
        if (savedInstanceState == null) {
            updateRefreshingUI(true);
        }
        mBinding.getRoot().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mBinding.getRoot().getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });
    }

    private void prepareTransitions() {
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                Integer selectedPosition = mViewModel.getCurrentSelectedPosition();
                // Locate the ViewHolder for the clicked position.
                RecyclerView.ViewHolder selectedViewHolder = mBinding.recyclerView
                        .findViewHolderForAdapterPosition(selectedPosition);
                if (selectedViewHolder == null || selectedViewHolder.itemView == null) {
                    return;
                }

                // Map the first shared element name to the child ImageView.
                sharedElements.put(names.get(0),
                        selectedViewHolder.itemView.findViewById(R.id.thumbnail));
            }
        });
    }

    private void setupListAdapter(View view) {
        RecyclerView recyclerView = mBinding.recyclerView;
        final ArticlesAdapter adapter = new ArticlesAdapter(clickListener);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sglm);
        recyclerView.setAdapter(adapter);

        // observe articles list LiveData
        mViewModel.getArticlesListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Article>>() {
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
//        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

    public interface ArticleItemsClickListener {
        void onClick(View sharedView, String sharedElementName, int selectedPosition);
    }

    public ArticleItemsClickListener clickListener = new ArticleItemsClickListener() {
        @Override
        public void onClick(View sharedView, String sharedElementName, int selectedPosition) {
            // save current selected article inside ViewModel
            mViewModel.setCurrentPosition(selectedPosition);
            // add shared element transitions extras
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(sharedView, sharedElementName)
                    .build();
            // navigate to destination & pass shared element
            NavHostFragment.findNavController(ArticleListFragment.this).navigate(
                    R.id.action_article_list_dest_to_articles_pager_dest,
                    null,
                    null,
                    extras);
        }
    };
}
