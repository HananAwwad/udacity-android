package com.hananawwad.popularmovies.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hananawwad.popularmovies.R;
import com.hananawwad.popularmovies.fragment.MainFragment;
import com.hananawwad.popularmovies.model.MovieModel;
import com.hananawwad.popularmovies.util.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 11/21/2015.
 */
public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.ViewHolder> {

    public static final String TAG = "MovieGridAdapter";
    private List<MovieModel> movieList = new ArrayList<MovieModel>();
    private Calendar calendar;
    private int defaultColor;
    private MainFragment.Callback mainFragmentCallback;


    public MovieGridAdapter(ArrayList<MovieModel> movieList, int defaultColor, MainFragment.Callback mainFragmentCallback) {
        this.movieList = movieList;
        this.calendar = Calendar.getInstance();
        this.defaultColor = defaultColor;
        this.mainFragmentCallback = mainFragmentCallback;

    }

    public void addMovies(List<MovieModel> data) {
        Log.d(TAG, "Adding movies to the adapter.");
        if (data == null) {
            data = new ArrayList<>();
        }
        movieList = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_column, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MovieGridAdapter.ViewHolder holder, final int position) {

        Log.d(TAG, "Binding movie to the corresponding view.");
        final MovieModel movieData = movieList.get(position);

        holder.gridItemLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bitmap posterBitmap = ((BitmapDrawable) holder.posterImageView.getDrawable()).getBitmap();
                mainFragmentCallback.onItemSelected(movieData, posterBitmap, holder.posterImageView, position);
            }
        });

        holder.gridItemLayout.setContentDescription(holder.gridItemLayout.getContext().getString(R.string.a11y_movie_title,
                movieData.title));

        if (movieData != null) {
            if (movieData.getFormattedDate() != null)
                calendar.setTime(movieData.getFormattedDate());
            holder.releaseDateTextView.setText(String.valueOf(calendar.get(Calendar.YEAR)));
            holder.releaseDateTextView.setContentDescription(holder.releaseDateTextView.getContext().getString(
                    R.string.a11y_movie_year, String.valueOf(calendar.get(Calendar.YEAR))));
        }

        String imageUrl = Constants.IMAGE_MOVIE_URL + Constants.IMAGE_SIZE_W185 + movieData.posterPath;
        final RelativeLayout container = holder.titleLayout;

        Picasso.with(holder.posterImageView.getContext()).load(imageUrl).placeholder(R.drawable.ic_movie_placeholder).
                into(holder.posterImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap posterBitmap = ((BitmapDrawable) holder.posterImageView.getDrawable()).getBitmap();
                        Palette.from(posterBitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                container.setBackgroundColor(ColorUtils.setAlphaComponent(palette.getMutedColor(defaultColor), 190)); //Opacity
                            }
                        });
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    @Override
    public int getItemCount() {
        return movieList == null ? 0 : movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.grid_item_release_date_text_view)
        TextView releaseDateTextView;

        @Bind(R.id.grid_item_poster_image_view)
        ImageView posterImageView;

        @Bind(R.id.grid_item_title_container)
        RelativeLayout titleLayout;

        @Bind(R.id.grid_item_container)
        FrameLayout gridItemLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
