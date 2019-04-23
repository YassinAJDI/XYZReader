package com.ajdi.xyzreader.ui.details;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.transition.TransitionInflater;
import androidx.viewpager.widget.ViewPager;

import com.ajdi.xyzreader.R;
import com.ajdi.xyzreader.data.model.Article;
import com.ajdi.xyzreader.ui.ArticlesViewModel;
import com.ajdi.xyzreader.ui.HomeActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

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
        prepareSharedElementTransition();
    }

    private void prepareSharedElementTransition() {
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                // Locate the image view at the primary fragment (the ImageFragment
                // that is currently visible). To locate the fragment, call
                // instantiateItem with the selection position.
                // At this stage, the method will simply return the fragment at the
                // position and will not create a new one.
                Integer selectedPosition = mViewModel.getCurrentSelectedPosition();
                Fragment currentFragment = (Fragment) mPagerAdapter.instantiateItem(mPager, selectedPosition);
                View view = currentFragment.getView();
                if (view == null) {
                    return;
                }

                // Map the first shared element name to the child ImageView.
                sharedElements.put(names.get(0), view.findViewById(R.id.photo));
            }
        });
    }

    private void setupPagerAdapter(View view) {
        // Initialize with the child fragment manager.
        mPagerAdapter = new ArticlesPagerAdapter(getChildFragmentManager());
        mPager = view.findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        // observe pager list
        mViewModel.getArticlesListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles != null) {
                    mPagerAdapter.submitList(articles);
                    // select item at current position
                    mPager.setCurrentItem(mViewModel.getCurrentSelectedPosition(), false);
                }
            }
        });

        // save current selected item in viewModel
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mViewModel.setCurrentPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
