package com.csatimes.dojma.herald;

import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.csatimes.dojma.R;
import com.csatimes.dojma.models.HeraldItem;
import com.csatimes.dojma.utilities.DHC;
import com.facebook.drawee.view.SimpleDraweeView;
import com.like.LikeButton;
import com.like.OnLikeListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;

import static android.content.Intent.EXTRA_TEXT;
import static com.csatimes.dojma.articles.ArticleViewerActivity.readArticle;

/**
 * @author Rushikesh Jogdand.
 */
public class HeraldViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, OnLikeListener {

    private final TextView titleTv;
    private final TextView dateTv;
    private final SimpleDraweeView heraldSdv;
    private final LikeButton favLb;
    private HeraldItem item;

    HeraldViewHolder(final View itemView) {
        super(itemView);

        heraldSdv = itemView.findViewById(R.id.item_format_herald_image);
        dateTv = itemView.findViewById(R.id.item_format_herald_date);
        titleTv = itemView.findViewById(R.id.item_format_herald_title);
        favLb = itemView.findViewById(R.id.item_format_herald_heart);
        final ImageButton shareIb = itemView.findViewById(R.id.item_format_herald_share);

        favLb.setOnLikeListener(this);
        itemView.setOnClickListener(this);
        shareIb.setOnClickListener(this);

    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public void onClick(final View view) {
        if (view.getId() == R.id.item_format_herald_share) {
            final Intent shareIntent = new Intent((Intent.ACTION_SEND));
            shareIntent.setType(DHC.MIME_TYPE_PLAINTEXT);
            shareIntent.putExtra(EXTRA_TEXT, item.getTitle_plain() + " at " + item.getUrl());
            view.getContext().startActivity(Intent.createChooser(shareIntent,
                    view.getContext().getString(R.string.share_prompt)));
            return;
        }
        readArticle(view.getContext(), Integer.parseInt(item.getPostID()));
    }

    @Override
    public void liked(final LikeButton likeButton) {
        item.setFav(true);
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(item);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void unLiked(final LikeButton likeButton) {
        item.setFav(false);
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(item);
        realm.commitTransaction();
        realm.close();
    }

    @SuppressWarnings("FeatureEnvy")
    void populate(@NonNull final HeraldItem item) {
        this.item = item;
        dateTv.setText(item.getFormattedDate());
        heraldSdv.setImageURI(Uri.parse(item.getThumbnailUrl()));
        favLb.setLiked(item.isFav());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            titleTv.setText(Html.fromHtml(item.getTitle(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            titleTv.setText(Html.fromHtml(item.getTitle()));
        }
    }
}
