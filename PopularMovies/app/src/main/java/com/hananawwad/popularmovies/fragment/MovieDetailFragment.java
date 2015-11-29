package com.hananawwad.popularmovies.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hananawwad.popularmovies.R;
import com.hananawwad.popularmovies.activity.DetailActivity;
import com.hananawwad.popularmovies.model.MovieModel;
import com.hananawwad.popularmovies.util.CommonUtil;
import com.hananawwad.popularmovies.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**

 */
public class MovieDetailFragment extends Fragment {

    public static final String TAG = "MovieDetailFragment";

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.movie_detail_poster_image_view)
    ImageView posterMovie;

    @Bind(R.id.movie_detail_backdrop_image_view)
    ImageView backdropMovie;

    @Bind(R.id.movie_detail_rate_image_view)
    ImageView rateImageView;

    @Bind(R.id.movie_detail_rate_value_text_view)
    TextView rateTextView;

    @Bind(R.id.movie_detail_title_text_view)
    TextView movieTitle;

    @Bind(R.id.movie_detail_year_text_view)
    TextView movieYear;

    @Bind(R.id.movie_detail_synopsys_data_text_view)
    TextView movieSynopsis;

    @Bind({R.id.appbar, R.id.inc_movie_detail})
    List<View> viewContainers;

    @Bind(R.id.inc_no_selected_movie)
    View noSelectedMovieView;

    private MovieModel mMovieModel;
    private Bitmap posterImage;

    public static MovieDetailFragment newInstance(Bundle bundle) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public MovieDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            posterImage = getArguments().getParcelable(Constants.POSTER_IMAGE_KEY);
            mMovieModel = getArguments().getParcelable(Constants.MOVIE_DETAIL_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "Calling on create view on detail fragment");
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);
        if (getActivity() instanceof DetailActivity) {
            DetailActivity detailActivity = (DetailActivity) getActivity();
            detailActivity.setToolbar(toolbar, true, false);
        }
        initView(mMovieModel);
        return view;


    }

    private void initView(MovieModel movieModel) {
        Log.d(TAG, "Init view in movie details fragment");
        if (movieModel == null) {
            showNoMovieSelectedView(true);
            return;
        }
        showNoMovieSelectedView(false);
        String imageUrl = Constants.IMAGE_MOVIE_URL + Constants.IMAGE_SIZE_W185 + movieModel.posterPath;
        Picasso.with(getActivity())
                .load(imageUrl)
                .placeholder(R.drawable.ic_movie_placeholder)
                .error(R.drawable.ic_movie_placeholder)
                .into(backdropMovie);

        posterMovie.setImageBitmap(posterImage);
        rateImageView.setImageResource(CommonUtil.getMovieRateIcon(movieModel.voteAverage, true));
        rateImageView.setContentDescription(getString(R.string.a11y_movie_title, movieModel.title));

        movieTitle.setText(movieModel.title);
        movieTitle.setContentDescription(getString(R.string.a11y_movie_title, movieModel.title));

        rateTextView.setText(String.format("%d/10", Math.round(movieModel.voteAverage)));
        rateTextView.setContentDescription(getString(R.string.a11y_movie_rate, String.format("%d/10", Math.round(movieModel.voteAverage))));

        movieSynopsis.setText(movieModel.overview);
        movieSynopsis.setContentDescription(getString(R.string.a11y_movie_overview, movieModel.overview));

        if (movieModel.getFormattedDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(movieModel.getFormattedDate().getTime());
            movieYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
            movieYear.setContentDescription(getString(R.string.a11y_movie_year, String.valueOf(calendar.get(Calendar.YEAR))));
        }
    }

    /**
     * Show/hide empty message and containers depending on movie data
     *
     * @param noMovieData
     */
    private void showNoMovieSelectedView(boolean noMovieData) {
        noSelectedMovieView.bringToFront();
        noSelectedMovieView.setVisibility(noMovieData ? View.VISIBLE : View.GONE);
        for (View view : viewContainers) {
            view.setVisibility(noMovieData ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
