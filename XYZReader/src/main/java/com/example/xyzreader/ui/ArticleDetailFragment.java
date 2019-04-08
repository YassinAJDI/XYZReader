package com.example.xyzreader.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.utils.UiUtils;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NavUtils;
import androidx.core.app.ShareCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.palette.graphics.Palette;
import timber.log.Timber;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment";

    public static final String ARG_ITEM_ID = "item_id";

    private Cursor mCursor;
    private long mItemId;
    private View mRootView;

    private ImageView mPhotoView;
    private boolean mIsCard = false;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // make window fullscreen
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        mIsCard = getResources().getBoolean(R.bool.detail_is_card);
        setHasOptionsMenu(true);
    }

    public ArticleDetailActivity getActivityCast() {
        return (ArticleDetailActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");

        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        mPhotoView = mRootView.findViewById(R.id.photo);
        mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });
        setupToolbar();
        bindViews();
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.d("onActivityCreated");

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        LoaderManager.getInstance(this).initLoader(0, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume");
    }

    @Override
    public void onDestroyView() {
        Timber.d("onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Timber.d("onDetach");
        super.onDetach();
    }

    private void setupToolbar() {
        Timber.d("setupToolbar");
        final Toolbar toolbar = mRootView.findViewById(R.id.toolbar);
        getActivityCast().setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("onClick");
                NavUtils.navigateUpFromSameTask(getActivityCast());
            }
        });

        if (getActivityCast().getSupportActionBar() != null) {
            Timber.d("Has toolbar");
            getActivityCast().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                handleCollapsedToolbarTitle();
        }

        // inset the toolbar down by the status bar height
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            final CoordinatorLayout coordinatorLayout = mRootView.findViewById(R.id.draw_insets_frame_layout);
            coordinatorLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    Timber.d("onApplyWindowInsets");
                    ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                    lpToolbar.topMargin = insets.getSystemWindowInsetTop();
                    toolbar.setLayoutParams(lpToolbar);
                    // clear this listener so insets aren't re-applied
                    coordinatorLayout.setOnApplyWindowInsetsListener(null);
                    return insets.consumeSystemWindowInsets();
                }
            });
            ViewCompat.requestApplyInsets(toolbar);
        }

    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        TextView titleView = mRootView.findViewById(R.id.article_title);
        TextView bylineView = mRootView.findViewById(R.id.article_byline);
        TextView bodyView = mRootView.findViewById(R.id.article_body);


        bodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));

        // TODO: 4/6/2019 improve this using fonts
        if (mCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            Date publishedDate = parsePublishedDate();
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {
                bylineView.setText(Html.fromHtml(
                        DateUtils.getRelativeTimeSpanString(
                                publishedDate.getTime(),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString()
                                + " by <font color='#ffffff'>"
                                + mCursor.getString(ArticleLoader.Query.AUTHOR)
                                + "</font>"));

            } else {
                // If date is before 1902, just show the string
                bylineView.setText(Html.fromHtml(
                        outputFormat.format(publishedDate) + " by <font color='#ffffff'>"
                                + mCursor.getString(ArticleLoader.Query.AUTHOR)
                                + "</font>"));

            }
            bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY).replaceAll("(\r\n|\n)", "<br />")));

            GlideApp.with(mPhotoView.getContext())
                    .asBitmap()
                    .load(mCursor.getString(ArticleLoader.Query.PHOTO_URL))
                    .placeholder(R.color.photo_placeholder)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
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
                                        mRootView.findViewById(R.id.meta_bar)
                                                .setBackgroundColor(swatch.getRgb());
//                                        holder.titleView.setTextColor(swatch.getTitleTextColor());
//                                        holder.subtitleView.setTextColor(swatch.getBodyTextColor());
                                    }
                                }
                            });
                            return false;
                        }
                    })
                    .into(mPhotoView);
        }
    }

    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(@NonNull androidx.loader.content.Loader<Cursor> loader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(@NonNull androidx.loader.content.Loader<Cursor> cursorLoader) {
        mCursor = null;
//        bindViews();
    }
}
