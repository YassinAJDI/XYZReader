package com.example.xyzreader.ui.details;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.transition.TransitionInflater;
import androidx.viewpager.widget.ViewPager;

import com.example.xyzreader.R;
import com.example.xyzreader.data.model.Article;
import com.example.xyzreader.ui.ArticlesViewModel;
import com.example.xyzreader.ui.HomeActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A simple {@link Fragment} subclass, representing a single Article detail screen, letting you swipe between articles
 */
public class ArticlesPagerFragment extends Fragment {

    private ArticlesViewModel mViewModel;
    private ArticlesPagerAdapter mPagerAdapter;
    private ViewPager mPager;

    public ArticlesPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            postponeEnterTransition();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(
                    TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_articles_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = HomeActivity.obtainViewModel(getActivity());
        setupPagerAdapter(view);
        selectCurrentItem();
    }

    private void selectCurrentItem() {
        // TODO: 4/19/2019 use single live event instead
        mViewModel.getCurrentSelectedArticlePosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer selectedPosition) {
                mPager.setCurrentItem(selectedPosition, false);
            }
        });
    }

    private void setupPagerAdapter(View view) {
        // Enable FragmentManager logging
//        FragmentManager.enableDebugLogging(true);
        // Initialize with the child fragment manager.
        mPagerAdapter = new ArticlesPagerAdapter(getChildFragmentManager());
        mPager = view.findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        mViewModel.getArticlesListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles != null) {
                    mPagerAdapter.submitList(articles);
                }
            }
        });
    }
}
