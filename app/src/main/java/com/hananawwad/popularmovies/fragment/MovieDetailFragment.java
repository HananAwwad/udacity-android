package com.hananawwad.popularmovies.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hananawwad.popularmovies.R;
import com.hananawwad.popularmovies.activity.DetailActivity;
import com.hananawwad.popularmovies.model.MovieModel;
import com.hananawwad.popularmovies.model.Review;
import com.hananawwad.popularmovies.model.Trailer;
import com.hananawwad.popularmovies.provider.FavoriteMovieContentProvider;
import com.hananawwad.popularmovies.task.CommonAsyncTask;
import com.hananawwad.popularmovies.task.ReviewsAsyncTask;
import com.hananawwad.popularmovies.task.TrailersAsyncTask;
import com.hananawwad.popularmovies.util.CommonUtil;
import com.hananawwad.popularmovies.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Bind(R.id.movie_detail_play_trailer_image_view)
    ImageView mPlayTrailerImageView;

    @Bind({R.id.appbar, R.id.inc_movie_detail})
    List<View> viewContainers;

    @Bind(R.id.inc_no_selected_movie)
    View noSelectedMovieView;

    @Bind(R.id.movie_detail_trailer_container)
    LinearLayout mTrailerLinearLayout;

    @Bind(R.id.detail_movie_reviews_container)
    LinearLayout mReviewLinearLayout;

    @Bind(R.id.empty_trailer_list)
    TextView mDetailMovieEmptyTrailers;

    @Bind(R.id.empty_review_list)
    TextView mDetailMovieEmptyReviews;

    @Bind(R.id.movie_detail_trailer_progress_bar)
    ProgressBar mTrailersProgressBar;

    @Bind(R.id.movie_detail_review_progress_bar)
    ProgressBar mReviewsProgressBar;

    @Nullable
    @Bind(R.id.favorite_fab)
    FloatingActionButton mFavoriteFab;

    private MovieModel mMovieModel;
    private Bitmap posterImage;
    private boolean mAddedInFavorite;

    private Trailer mMainTrailer;
    private ArrayList<Trailer> mTrailers;
    private ArrayList<Review> mReviews;

    private TrailersAsyncTask trailersAsyncTask;
    private ReviewsAsyncTask reviewsAsyncTask;


    public static MovieDetailFragment newInstance(Bundle bundle) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public MovieDetailFragment() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.share_movie:
                openShareIntent(mMainTrailer);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            posterImage = getArguments().getParcelable(Constants.POSTER_IMAGE_KEY);
            mMovieModel = getArguments().getParcelable(Constants.MOVIE_DETAIL_KEY);
            if (mMovieModel != null) {
                mAddedInFavorite = FavoriteMovieContentProvider.getMovieData(getActivity(), mMovieModel.id) != null;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "Calling on create view on detail fragment" + mMovieModel);
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);
        if (getActivity() instanceof DetailActivity) {
            DetailActivity detailActivity = (DetailActivity) getActivity();
            detailActivity.setToolbar(toolbar, true, false);
        }
        if (savedInstanceState != null) {
            mTrailers = savedInstanceState.getParcelableArrayList(Constants.TRAILERS);
            mReviews = savedInstanceState.getParcelableArrayList(Constants.REVIEWS);
            mAddedInFavorite = savedInstanceState.getBoolean(Constants.FAVORITE);
            mMainTrailer = savedInstanceState.getParcelable(Constants.MAIN_TRAILER);
            addTrailerViews(mTrailers);
            addReviewViews(mReviews);
        } else {
            executeTasks(mMovieModel);
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

        switchFabIcon();

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
        toggleVisibleFab(!noMovieData);
        noSelectedMovieView.bringToFront();
        noSelectedMovieView.setVisibility(noMovieData ? View.VISIBLE : View.GONE);
        for (View view : viewContainers) {
            view.setVisibility(noMovieData ? View.GONE : View.VISIBLE);
        }
    }

    /*
    show fab depending on movie
     */
    private void toggleVisibleFab(boolean showFab) {
        if (mFavoriteFab != null) {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) mFavoriteFab.getLayoutParams();
            p.setAnchorId(showFab ? R.id.appbar : View.NO_ID);
            mFavoriteFab.setLayoutParams(p);
            mFavoriteFab.setVisibility(showFab ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Runs tasks in background and get movie trailers and reviews
     */
    private void executeTasks(MovieModel mMovieData) {

        if (mMovieData == null) {
            return;
        }

        trailersAsyncTask = new TrailersAsyncTask(mMovieData.id, mTrailersProgressBar, new CommonAsyncTask.FetchDataListener<Trailer>() {
            @Override
            public void onFetchData(ArrayList<Trailer> resultList) {
                mTrailers = resultList;
                addTrailerViews(mTrailers);
            }
        });

        reviewsAsyncTask = new ReviewsAsyncTask(mMovieData.id, mReviewsProgressBar, new CommonAsyncTask.FetchDataListener<Review>() {
            @Override
            public void onFetchData(ArrayList<Review> resultList) {
                mReviews = resultList;
                addReviewViews(mReviews);
            }
        });

        trailersAsyncTask.execute();
        reviewsAsyncTask.execute();

    }

    private void addTrailerViews(List<Trailer> resultList) {

        final LayoutInflater inflater = LayoutInflater.from(getActivity());

        boolean emptyList = resultList == null || resultList.isEmpty();

        if (resultList != null && !resultList.isEmpty()) {
            mMainTrailer = resultList.get(0);
            mPlayTrailerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openYouTubeIntent(mMainTrailer.key);
                }
            });
            for (Trailer trailer : resultList) {
                final String key = trailer.key;
                final View trailerView = inflater.inflate(R.layout.list_item_trailer, mTrailerLinearLayout, false);
                ImageView trailerImage = ButterKnife.findById(trailerView, R.id.trailer_poster_image_view);
                ImageView playImage = ButterKnife.findById(trailerView, R.id.play_trailer_image_view);
                playImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openYouTubeIntent(key);
                    }
                });

                Picasso.with(getActivity())
                        .load(String.format(Constants.YOU_TUBE_IMG_URL, trailer.key))
                        .placeholder(R.drawable.ic_movie_placeholder)
                        .error(R.drawable.ic_movie_placeholder)
                        .into(trailerImage);
                mTrailerLinearLayout.addView(trailerView);
            }
        }
        mDetailMovieEmptyTrailers.setVisibility(emptyList ? View.VISIBLE : View.GONE);

    }

    private void addReviewViews(List<Review> resultList) {

        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        boolean emptyList = resultList == null || resultList.isEmpty();

        if (!emptyList) {
            for (Review review : resultList) {
                final View reviewView = inflater.inflate(R.layout.list_item_review, mReviewLinearLayout, false);
                TextView reviewAuthor = ButterKnife.findById(reviewView, R.id.list_item_review_author_text_view);
                TextView reviewContent = ButterKnife.findById(reviewView, R.id.list_item_review_content_text_view);
                reviewAuthor.setText(review.author);
                reviewContent.setText(review.content);
                mReviewLinearLayout.addView(reviewView);
            }
        }
        mDetailMovieEmptyReviews.setVisibility(emptyList ? View.VISIBLE : View.GONE);
    }


    /**
     * Switch FAB icons
     */
    private void switchFabIcon() {
        mFavoriteFab.setImageResource(mAddedInFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_outline);
    }

    private void openYouTubeIntent(String key) {
        Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOU_TUBE_VIDEO_URL + key));
        youTubeIntent.putExtra("force_fullscreen", true);
        startActivity(youTubeIntent);
    }

    private void openShareIntent(Trailer trailer) {
        if (trailer != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, Constants.YOU_TUBE_VIDEO_URL + trailer.key);
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, mMovieModel.title);
            startActivity(Intent.createChooser(intent, getActivity().getString(R.string.share)));
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.FAVORITE, mAddedInFavorite);
        outState.putParcelableArrayList(Constants.TRAILERS, mTrailers);
        outState.putParcelableArrayList(Constants.REVIEWS, mReviews);
    }

    @OnClick(R.id.favorite_fab)
    public void toggleFavorite() {
        Log.d(TAG, "Favorite icon clicked");
        int resultMsg;
        if (!mAddedInFavorite) {
            FavoriteMovieContentProvider.putMovieData(getActivity(), mMovieModel);
            resultMsg = R.string.added_to_favorite;
            Log.d(TAG, "toggleFavorite() called to add favorite movie");
        } else {
            FavoriteMovieContentProvider.deleteMovieData(getActivity(), mMovieModel.id);
            resultMsg = R.string.removed_from_favorite;
            Log.d(TAG, "toggleFavorite() called to delete from favorite movie list");
        }
        mAddedInFavorite = !mAddedInFavorite;
        Snackbar.make(getView(), resultMsg, Snackbar.LENGTH_SHORT).show();
        switchFabIcon();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (trailersAsyncTask != null) {
            trailersAsyncTask.cancel(true);
            trailersAsyncTask = null;
        }

        if (reviewsAsyncTask != null) {
            reviewsAsyncTask.cancel(true);
            reviewsAsyncTask = null;
        }
    }

}
