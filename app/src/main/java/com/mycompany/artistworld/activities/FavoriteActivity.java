package com.mycompany.artistworld.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mycompany.artistworld.R;
import com.mycompany.artistworld.adapter.CustomCursorAdapter;
import com.mycompany.artistworld.data.ArtistWorldContract;
import com.mycompany.artistworld.model.Project;
import com.mycompany.artistworld.rest.IdeaApiInterface;
import com.mycompany.artistworld.rest.ServiceGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mycompany.artistworld.fragments.HomeFragment.IDEA_SELECTED;

public class FavoriteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, CustomCursorAdapter.FavListItemClickListener{

    @BindView(R.id.pb_fav_loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.recyclerViewFavorites)
    RecyclerView recyclerView;
    @BindView(R.id.adView)
    AdView mAdView;

    Project projectSelected;

    // Constants for logging and referring to a unique loader
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;

    // Member variables for the adapter and RecyclerView
    private CustomCursorAdapter mAdapter;

    IdeaApiInterface ideaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        ButterKnife.bind(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new CustomCursorAdapter(this, this);
        recyclerView.setAdapter(mAdapter);

        ideaService = ServiceGenerator.createService(IdeaApiInterface.class);

         /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }

    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddTaskActivity,
     * so this restarts the loader to re-query the underlying data for any changes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        showIdeaDataView();

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // implement to load data
                try {
                    return getContentResolver().query(ArtistWorldContract.ProjectEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            ArtistWorldContract.ProjectEntry.COLUMN_TIMESTAMP);
                } catch (Exception e){
                    Log.e(TAG, "Failed to asynchronosly load data");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.swapCursor(data);
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClickI(String selectedSlug) {
        //Toast.makeText(this, selectedSlug, Toast.LENGTH_SHORT).show();
        showProgressLoader();

        //load the idea here and then if success go to detail
        Call<Project> call = ideaService.getIdea(selectedSlug);
        call.enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()){
                    projectSelected = response.body();
                    goToDetailView(projectSelected);
                } else {
                    if (response.code() == 401){
                        Toast.makeText(getBaseContext(), "Unauthenticated", Toast.LENGTH_SHORT).show();
                    } else if (response.code() >= 400){
                        Toast.makeText(getBaseContext(), "Client Error " + response.code() + " " + response.message() , Toast.LENGTH_LONG).show();
                    }
                    showIdeaDataView();
                }
            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                showIdeaDataView();
            }
        });
    }

    private void showProgressLoader(){
        mLoadingIndicator.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void showIdeaDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void goToDetailView(Project project){
        Intent intent = new Intent(this, ProjectDetailActivity.class);
        Bundle extra = new Bundle();
        extra.putParcelable(IDEA_SELECTED, project);
        intent.putExtras(extra);
        startActivity(intent);
    }
}













