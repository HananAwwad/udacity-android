package com.hananawwad.popularmovies.activity;

import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.hananawwad.popularmovies.R;
import com.hananawwad.popularmovies.fragment.MainFragment;
import com.hananawwad.popularmovies.fragment.MovieDetailFragment;
import com.hananawwad.popularmovies.model.MovieModel;
import com.hananawwad.popularmovies.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {

    private static final String TAG = "MainActivity";
    private int selectedPosition = -1;
    private boolean mTwoPane = true;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setElevation(0f);
        if (savedInstanceState != null) {
            selectedPosition = savedInstanceState.getInt(Constants.POSITION_KEY);
        }

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                replaceMovieDetailFragment(MovieDetailFragment.newInstance(new Bundle()));
                initStetho();
            } else {
                selectedPosition = savedInstanceState.getInt(Constants.POSITION_KEY);
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

    }

    private void replaceMovieDetailFragment(MovieDetailFragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.movie_detail_container, fragment, MovieDetailFragment.TAG).commit();
    }

    private void initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    @Override
    public void onItemSelected(MovieModel movieData, Bitmap posterBitmap, View view, int position) {
        Log.d(TAG, "Calling on item selected and opening the detail activity");
        selectedPosition = position;
        if (mTwoPane) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Bundle args = new Bundle();
            args.putParcelable(Constants.MOVIE_DETAIL_KEY, movieData);
            args.putParcelable(Constants.POSTER_IMAGE_KEY, posterBitmap);
            MovieDetailFragment fragment = MovieDetailFragment.newInstance(args);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fragment.setSharedElementReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.trans_move));
                fragment.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));
                fragmentTransaction.addSharedElement(view, Constants.POSTER_IMAGE_VIEW_KEY);
            }
            fragmentTransaction.replace(R.id.movie_detail_container, fragment, MovieDetailFragment.TAG).commit();
        } else {
            ActivityOptions options = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                options = ActivityOptions.
                        makeSceneTransitionAnimation(this, view, Constants.POSTER_IMAGE_VIEW_KEY);
            }
            Intent openDetailIntent = new Intent(this, DetailActivity.class);
            openDetailIntent.putExtra(Constants.MOVIE_DETAIL_KEY, movieData);
            openDetailIntent.putExtra(Constants.POSTER_IMAGE_KEY, posterBitmap);
            if (options != null) {
                startActivity(openDetailIntent, options.toBundle());
            } else {
                startActivity(openDetailIntent);
            }
        }

    }
    public int getSelectedPosition() {
        return selectedPosition;
    }
}
