package com.mycompany.artistworld.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mycompany.artistworld.R;
import com.mycompany.artistworld.adapter.IdeasRecyclerAdapter;
import com.mycompany.artistworld.model.Project;
import com.mycompany.artistworld.model.ProjectResponse;
import com.mycompany.artistworld.rest.IdeaApiInterface;
import com.mycompany.artistworld.rest.ServiceGenerator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.mycompany.artistworld.fragments.HomeFragment.IDEA_SELECTED;

public class SearchActivity extends AppCompatActivity implements IdeasRecyclerAdapter.ListItemClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.recyclerview_search)
    RecyclerView mRecyclerView;
    private List<Project> mSearchIdeasResult;
    private IdeasRecyclerAdapter mIdeasRecyclerAdapter;
    IdeaApiInterface ideaService;
    LinearLayoutManager mLayoutManager;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //enables the up arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mLayoutManager);
        // Use this setting to improve performance if you know that changes in content do not change the child layout size in the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mIdeasRecyclerAdapter = new IdeasRecyclerAdapter(this, IdeasRecyclerAdapter.LIST_SEARCH_RESULTS);

        mRecyclerView.setAdapter(mIdeasRecyclerAdapter);

        ideaService = ServiceGenerator.createService(IdeaApiInterface.class);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search
            loadData(query);
        }
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        //https://developer.android.com/training/search/setup.html#create-sc
        SearchManager searchManager = (SearchManager) SearchActivity.this.getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                //text has changed , apply filtering
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                //perform the final search
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadData(String query) {
        mLoadingIndicator.setVisibility(View.VISIBLE);

        Call<ProjectResponse> call = ideaService.findIdeas(query, 1);
        call.enqueue(new Callback<ProjectResponse>() {
            @Override
            public void onResponse(Call<ProjectResponse> call, Response<ProjectResponse> response) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()){
                    mSearchIdeasResult = response.body().getmResults();
                    showIdeaDataView();
                    mIdeasRecyclerAdapter.setIdeasData(mSearchIdeasResult);
                } else {
                    if (response.code() == 401){
                        Toast.makeText(getBaseContext(), getString(R.string.unauthenticated), Toast.LENGTH_SHORT).show();
                    } else if (response.code() >= 400){
                        Toast.makeText(getBaseContext(), getString(R.string.client_error) + response.code() + " " + response.message() , Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectResponse> call, Throwable t) {
                // Log error here since request failed
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                Log.e(TAG, t.toString());
                showErrorMessage();
            }
        });
    }

    @Override
    public void onListItemClickI(Project clickedIdea) {
        Intent intent = new Intent(this, ProjectDetailActivity.class);
        Bundle extra = new Bundle();
        extra.putParcelable(IDEA_SELECTED, clickedIdea);
        intent.putExtras(extra);
        startActivity(intent);
    }

    private void showIdeaDataView() {
        /* First, make sure the error is invisible */
        //mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the idea list is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        // mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
}
