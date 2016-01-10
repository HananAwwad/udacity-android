package com.hananawwad.popularmovies.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hananawwad.popularmovies.R;
import com.hananawwad.popularmovies.activity.MainActivity;
import com.hananawwad.popularmovies.adapter.MovieGridAdapter;
import com.hananawwad.popularmovies.loaders.FavoritesLoader;
import com.hananawwad.popularmovies.model.MovieModel;
import com.hananawwad.popularmovies.loaders.MoviesLoader;
import com.hananawwad.popularmovies.util.Constants;
import com.hananawwad.popularmovies.util.DeviceUtil;
import com.hananawwad.popularmovies.util.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.Callback} interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<MovieModel>> {

    private static final String TAG = "MainFragment";

    @Bind(R.id.main_movie_grid_recycle_view)
    RecyclerView recyclerView;

    @Bind(R.id.main_movie_sw_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @Bind(R.id.main_grid_empty_container)
    LinearLayout noInternetLayout;

    @Bind(R.id.inc_no_movies)
    View noMoviesView;

    private MainActivity mainActivity;
    private MovieGridAdapter movieGridAdapter;
    private ArrayList<MovieModel> movieModels;

    public MainFragment() {
    }

    public interface Callback {
        void onItemSelected(MovieModel movieData, Bitmap posterBitmap, View view, int position);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        movieModels = new ArrayList<>();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String sortType;
        boolean result;
        switch (item.getItemId()) {
            case R.id.sort_by_popularity_desc:
                sortType = Constants.SORT_BY_POPULARITY_DESC;
                result = true;
                break;
            case R.id.sort_by_rates_desc:
                sortType = Constants.SORT_BY_RATING_DESC;
                result = true;
                break;
            case R.id.show_favorites:
                sortType = Constants.SHOW_FAVORITES;
                result = true;
                break;
            default:
                sortType = Constants.SORT_BY_POPULARITY_DESC;
                result = super.onOptionsItemSelected(item);
                break;
        }

        item.setChecked(true);
        PreferenceUtil.savePrefs(getActivity(), Constants.MODE_VIEW, sortType);
        restartLoader();
        refreshLayout.setRefreshing(true);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "Calling on create view on main fragment");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        int gridColumns = getResources().getInteger(R.integer.grid_columns);
        refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), gridColumns);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        int colorPrimaryLight = ContextCompat.getColor(getActivity(), (R.color.colorPrimaryTransparent));
        movieGridAdapter = new MovieGridAdapter(movieModels, colorPrimaryLight, (Callback) getActivity());
        recyclerView.setAdapter(movieGridAdapter);
        refreshLayout.setOnRefreshListener(this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<List<MovieModel>> onCreateLoader(int id, Bundle args) {
        return PreferenceUtil.getPrefs(getActivity(),Constants.MODE_VIEW,Constants.SORT_BY_POPULARITY_DESC).equals(Constants.SHOW_FAVORITES)?
                new FavoritesLoader(getActivity()): new MoviesLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<MovieModel>> loader, List<MovieModel> data) {
        Log.d(TAG, "Finish loading the movies data with movies.");
        refreshLayout.setRefreshing(false);
        movieModels = (ArrayList<MovieModel>) data;
        movieGridAdapter.addMovies(data);
        if (data == null || data.isEmpty()) {
            if (!DeviceUtil.isOnline(getActivity())) {
                noInternetLayout.setVisibility(View.VISIBLE);
            } else {
                toggleShowEmptyMovie(false);
            }
        } else {
            toggleShowEmptyMovie(true);
        }

        if (mainActivity != null && mainActivity.getSelectedPosition() != -1) {
            recyclerView.scrollToPosition(mainActivity.getSelectedPosition());
        }

        Snackbar.make(getView(), data == null ? R.string.movies_not_found : R.string.movies_data_loaded, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onLoaderReset(Loader<List<MovieModel>> loader) {
        refreshLayout.setRefreshing(false);
        movieGridAdapter.addMovies(null);
    }


    private void toggleShowEmptyMovie(boolean showMovieGrid) {
        noMoviesView.setVisibility(showMovieGrid ? View.GONE : View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onRefresh() {
        restartLoader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
