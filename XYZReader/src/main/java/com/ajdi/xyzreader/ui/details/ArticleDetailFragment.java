package com.ajdi.xyzreader.ui.details;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ShareCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ajdi.xyzreader.R;
import com.ajdi.xyzreader.data.model.Article;
import com.ajdi.xyzreader.databinding.FragmentArticleDetailBinding;
import com.ajdi.xyzreader.ui.HomeActivity;
import com.ajdi.xyzreader.utils.GlideApp;
import com.ajdi.xyzreader.utils.UiUtils;
import com.google.android.material.appbar.AppBarLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import timber.log.Timber;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link HomeActivity} in two-pane mode (on
 * tablets) or a {@link ArticlesPagerFragment} on handsets.
 */
public class ArticleDetailFragment extends Fragment {

    public static final String ARG_ARTICLE_DATA = "ARG_ARTICLE_DATA";

    private FragmentArticleDetailBinding mBinding;
    private Article mArticle;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(Article article) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_ARTICLE_DATA, article);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ARTICLE_DATA)) {
            mArticle = getArguments().getParcelable(ARG_ARTICLE_DATA);
        }
        setHasOptionsMenu(true);
    }

    public HomeActivity getActivityCast() {
        return (HomeActivity) getActivity();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        mBinding = FragmentArticleDetailBinding.inflate(inflater, container, false);
        // Article picture shared transition
        ViewCompat.setTransitionName(mBinding.photo, String.valueOf(mArticle.getId()));

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Sharing fab button
        mBinding.shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        setupToolbar();
        insetLayout();
        populateUi();
    }

    private void insetLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            final CoordinatorLayout coordinatorLayout = mBinding.drawInsetsFrameLayout;
            ViewCompat.setOnApplyWindowInsetsListener(coordinatorLayout, new OnApplyWindowInsetsListener() {
                @Override
                public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                    // apply insets for toolbar. inset the toolbar down by the status bar height
                    ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) mBinding.toolbar.getLayoutParams();
                    lpToolbar.topMargin = insets.getSystemWindowInsetTop();
                    mBinding.toolbar.setLayoutParams(lpToolbar);

                    // apply insets for fab button
                    ViewGroup.MarginLayoutParams fabLayoutParams = (ViewGroup.MarginLayoutParams) mBinding.shareFab.getLayoutParams();
                    fabLayoutParams.bottomMargin += insets.getSystemWindowInsetBottom(); // portrait
                    fabLayoutParams.rightMargin += insets.getSystemWindowInsetRight(); // landscape

                    // clear this listener so insets aren't re-applied
                    v.setOnApplyWindowInsetsListener(null);
                    return insets.consumeSystemWindowInsets();
                }
            });
            ViewCompat.requestApplyInsets(coordinatorLayout);
        }
    }

    private void setupToolbar() {
        final Toolbar toolbar = mBinding.toolbar;
        getActivityCast().setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(ArticleDetailFragment.this).navigateUp();
            }
        });

        if (getActivityCast().getSupportActionBar() != null) {
            getActivityCast().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            handleCollapsedToolbarTitle();
        }
    }

    /**
     * sets the title on the toolbar only if the toolbar is collapsed
     */
    private void handleCollapsedToolbarTitle() {
        mBinding.appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                // verify if the toolbar is completely collapsed and set the movie name as the title
                if (scrollRange + verticalOffset == 0) {
                    mBinding.collapsingToolbar.setTitle(mArticle.getTitle());
                    isShow = true;
                } else if (isShow) {
                    // display an empty string when toolbar is expanded
                    mBinding.collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }


    private void populateUi() {
        // title
        mBinding.articleTitle.setText(mArticle.getTitle());
        // publish date
        Date publishedDate = UiUtils.parsePublishedDate(mArticle.getPublished_date());
        mBinding.articleByline.setText(UiUtils.formatArticleByline(publishedDate, mArticle.getAuthor()));
        // article body
        mBinding.articleBody.setText(
                Html.fromHtml(mArticle.getBody().replaceAll("(\r\n|\n)", "<br />")));
        // article image
        GlideApp.with(this)
                .asBitmap()
                .load(mArticle.getPhoto_url())
                .dontAnimate()
                .placeholder(R.color.photo_placeholder)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object
                            model, Target<Bitmap> target, boolean isFirstResource) {
                        scheduleStartPostponedTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        // Generate palette synchronously
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette p) {
                                Palette.Swatch swatch = UiUtils.getDominantColor(p);
                                if (swatch != null) {
                                    mBinding.metaBar.setBackgroundColor(swatch.getRgb());
                                    mBinding.collapsingToolbar.setContentScrimColor(swatch.getRgb());
                                    if (mBinding.cardContentContainer != null) {
                                        mBinding.cardContentContainer.setStrokeColor(swatch.getRgb());
                                    }
                                }
                            }
                        });
                        scheduleStartPostponedTransition();
                        return false;
                    }
                })
                .into(mBinding.photo);

        mBinding.executePendingBindings();
    }

    private void scheduleStartPostponedTransition() {
        mBinding.getRoot().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mBinding.getRoot().getViewTreeObserver().removeOnPreDrawListener(this);
                getParentFragment().startPostponedEnterTransition();
                return true;
            }
        });
    }
}
