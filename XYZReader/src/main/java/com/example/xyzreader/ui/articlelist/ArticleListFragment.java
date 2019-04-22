package com.example.xyzreader.ui.articlelist;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import java.util.concurrent.atomic.AtomicBoolean;

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
    private AtomicBoolean enterTransitionStarted;

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
        FragmentManager.enableDebugLogging(true);
        // Inflate the layout for this fragment
        mBinding = FragmentArticleListBinding.inflate(inflater, container, false);
        mViewModel = HomeActivity.obtainViewModel(getActivity());
        prepareTransitions();
        setupListAdapter();

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if (savedInstanceState == null) {
//            updateRefreshingUI(true);
//        }

        scrollToPosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        insetLayout();
    }

    private void insetLayout() {
        // inset the toolbar down by the status bar height
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//            final AppBarLayout appBarLayout = mBinding.appbar;
//            ViewCompat.setOnApplyWindowInsetsListener(appBarLayout, new OnApplyWindowInsetsListener() {
//                @Override
//                public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
//                    Timber.d("onApplyWindowInsets");
//                    ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) appBarLayout.getLayoutParams();
//                    lpToolbar.topMargin = insets.getSystemWindowInsetTop();
////                    appBarLayout.setPadding(0, insets.getStableInsetTop(), 0, 0);
//                    appBarLayout.setLayoutParams(lpToolbar);
//                    // clear this listener so insets aren't re-applied
//                    v.setOnApplyWindowInsetsListener(null);
//                    return insets;
//                }
//            });
            mBinding.coordinator.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    Timber.d("onApplyWindowInsets");
                    ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams)
                            mBinding.appbarLayout.getLayoutParams();
                    lpToolbar.topMargin = insets.getSystemWindowInsetTop();
//                    appBarLayout.setPadding(0, insets.getStableInsetTop(), 0, 0);
                    mBinding.appbarLayout.setLayoutParams(lpToolbar);
                    // clear this listener so insets aren't re-applied
                    v.setOnApplyWindowInsetsListener(null);
                    return insets;
                }
            });
            ViewCompat.requestApplyInsets(mBinding.coordinator);
        }
    }

    private void scrollToPosition() {
        mBinding.recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mBinding.recyclerView.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = mBinding.recyclerView.getLayoutManager();
                View viewAtPosition = layoutManager.findViewByPosition(mViewModel.getCurrentSelectedPosition());
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
//                boolean isPartiallyVisible = ;
//                boolean isFullyVisible = ;
                if (viewAtPosition == null
                        || !(layoutManager.isViewPartiallyVisible(viewAtPosition, false, true)
                        || layoutManager.isViewPartiallyVisible(viewAtPosition, true, true))) {
                    mBinding.recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            Timber.d("post animation");
                            layoutManager.scrollToPosition(mViewModel.getCurrentSelectedPosition());
                        }
                    });
                }
            }
        });
    }

    private void prepareTransitions() {
        enterTransitionStarted = new AtomicBoolean();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(
                    TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }

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

    private void setupListAdapter() {
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
                postponeEnterTransition();
                if (articles != null) {
                    Timber.d("articles: " + articles.size());
                    updateRefreshingUI(false);
                    adapter.submitList(articles);
                }
                Timber.d("getArticlesListLiveData");
            }
        });
    }

    private void updateRefreshingUI(boolean isRefreshing) {
//        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

    public interface ArticleItemsClickListener {
        void onClick(View sharedView, String sharedElementName, int selectedPosition);

        void onLoadCompleted(int position);
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

        @Override
        public void onLoadCompleted(int position) {
            Timber.d("onLoadCompleted");
            Timber.d("enterTransitionStarted: " + enterTransitionStarted);
            // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
            int selectedPosition = mViewModel.getCurrentSelectedPosition();
            if (selectedPosition != position) {
                Timber.d("current selected pos= " + selectedPosition + ". adapter pos= " + position);
                return;
            }
            if (enterTransitionStarted.getAndSet(true)) {
                Timber.d("transation already started");
                return;
            }
            scheduleStartPostponedTransition();
        }
    };

    private void scheduleStartPostponedTransition() {
        Timber.d("scheduleStartPostponedTransition");
        mBinding.recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mBinding.recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                Timber.d("startPostponedEnterTransition");
                return true;
            }
        });
    }
}
