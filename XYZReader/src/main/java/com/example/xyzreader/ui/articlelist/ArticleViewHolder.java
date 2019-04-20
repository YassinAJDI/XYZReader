package com.example.xyzreader.ui.articlelist;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.xyzreader.R;
import com.example.xyzreader.data.model.Article;
import com.example.xyzreader.databinding.ListItemArticleBinding;
import com.example.xyzreader.ui.articlelist.ArticleListFragment.ArticleItemsClickListener;
import com.example.xyzreader.utils.GlideApp;
import com.example.xyzreader.utils.UiUtils;
import com.google.android.material.card.MaterialCardView;

import timber.log.Timber;

/**
 * @author Yassin Ajdi
 * @since 4/16/2019.
 */
public class ArticleViewHolder extends RecyclerView.ViewHolder {

    private final ListItemArticleBinding binding;
    private ArticleItemsClickListener listener;

    public ArticleViewHolder(@NonNull ListItemArticleBinding binding, ArticleItemsClickListener listener) {
        super(binding.getRoot());

        this.binding = binding;
        this.listener = listener;
    }

    public static ArticleViewHolder create(ViewGroup parent, ArticleItemsClickListener listener) {
        // Inflate
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Create the binding
        ListItemArticleBinding binding =
                ListItemArticleBinding.inflate(layoutInflater, parent, false);
        return new ArticleViewHolder(binding, listener);
    }

    public void bindTo(final Article article) {
        // title
        binding.articleTitle.setText(article.getTitle());
//        Date publishedDate = parsePublishedDate();
//        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
//            holder.subtitleView.setText(Html.fromHtml(
//                    DateUtils.getRelativeTimeSpanString(
//                            publishedDate.getTime(),
//                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
//                            DateUtils.FORMAT_ABBREV_ALL).toString()));
//        } else {
//            holder.subtitleView.setText(Html.fromHtml(outputFormat.format(publishedDate)));
//        }
        // article thumbnail
        binding.thumbnail.setAspectRatio(article.getAspect_ratio());

        GlideApp.with(binding.getRoot().getContext())
                .asBitmap()
                .load(article.getThumb_url())
                .dontAnimate()
                .dontTransform()
                .placeholder(R.color.photo_placeholder)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(
                        (int) UiUtils.dipToPixels(binding.getRoot().getContext(), 6))))
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
                                    MaterialCardView cardView = (MaterialCardView) binding.getRoot();
                                    cardView.setCardBackgroundColor(swatch.getRgb());
                                    cardView.setStrokeColor(swatch.getRgb());
//                                        holder.titleView.setTextColor(swatch.getTitleTextColor());
//                                        holder.subtitleView.setTextColor(swatch.getBodyTextColor());
                                }
                            }
                        });
                        return false;
                    }
                })
                .into(binding.thumbnail);
        // Set the string value of the article id as the unique transition name for the view.
        ViewCompat.setTransitionName(binding.thumbnail, String.valueOf(article.getId()));

        // Article items click event
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = getAdapterPosition();
                Timber.d("Article Clicked at position: " + adapterPosition + " with ID of: " + article.getId());
                listener.onClick(binding.thumbnail, String.valueOf(article.getId()), adapterPosition);
            }
        });

        binding.executePendingBindings();
    }
}
