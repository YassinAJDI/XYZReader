package com.ajdi.xyzreader.ui.articlelist;

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
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.TransitionInflater;

import com.ajdi.xyzreader.R;
import com.ajdi.xyzreader.data.model.Article;
import com.ajdi.xyzreader.databinding.FragmentArticleListBinding;
import com.ajdi.xyzreader.ui.ArticlesViewModel;
import com.ajdi.xyzreader.ui.HomeActivity;
import com.ajdi.xyzreader.ui.details.ArticlesPagerFragment;
import com.ajdi.xyzreader.utils.ItemOffsetDecoration;

import org.jetbrains.annotations.NotNull;

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
    private AtomicBoolean enterTransitionStarted;

    public ArticleListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Enable FragmentManager logging
//        FragmentManager.enableDebugLogging(true);
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
        if (savedInstanceState == null) {
            updateRefreshingUI(true);
        }
        scrollToPosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        insetLayout();
    }

    private void insetLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mBinding.coordinator.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    // inset toolbar by status bar height
                    ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams)
                            mBinding.toolbar.getLayoutParams();
                    lpToolbar.topMargin = insets.getSystemWindowInsetTop();
                    mBinding.toolbar.setLayoutParams(lpToolbar);

                    // inset the recyclerview bottom by the navigation bar
                    mBinding.recyclerView.setPadding(
                            mBinding.recyclerView.getPaddingLeft() + insets.getSystemWindowInsetLeft(), // landscape
                            mBinding.recyclerView.getPaddingTop(),
                            mBinding.recyclerView.getPaddingRight() + insets.getSystemWindowInsetRight(), // landscape
                            mBinding.recyclerView.getPaddingBottom() + insets.getSystemWindowInsetBottom());

                    // clear this listener so insets aren't re-applied
                    v.setOnApplyWindowInsetsListener(null);
                    return insets.consumeSystemWindowInsets();
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
                // layout manager children), or it's not completely visible
                if (viewAtPosition == null
                        || !(layoutManager.isViewPartiallyVisible(viewAtPosition, false, true)
                        || layoutManager.isViewPartiallyVisible(viewAtPosition, true, true))) {
                    mBinding.recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
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
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
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
            }
        });
    }

    private void updateRefreshingUI(boolean isRefreshing) {
        mBinding.swipeRefreshLayout.setRefreshing(isRefreshing);
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
            // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
            int selectedPosition = mViewModel.getCurrentSelectedPosition();
            if (selectedPosition != position) {
                return;
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return;
            }
            scheduleStartPostponedTransition();
        }
    };

    private void scheduleStartPostponedTransition() {
        mBinding.recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mBinding.recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });
    }
}
