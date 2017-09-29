package com.mycompany.artistworld.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mycompany.artistworld.R;
import com.mycompany.artistworld.activities.ProjectDetailActivity;
import com.mycompany.artistworld.adapter.IdeasRecyclerAdapter;
import com.mycompany.artistworld.model.Project;
import com.mycompany.artistworld.model.ProjectResponse;
import com.mycompany.artistworld.rest.IdeaApiInterface;
import com.mycompany.artistworld.rest.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment implements IdeasRecyclerAdapter.ListItemClickListener{
    public static final String HOME_LIST = "home_list";
    public static final String IDEA_SELECTED = "idea_selected";

    private Unbinder unbinder;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.recyclerview_idea)
    RecyclerView mRecyclerView;
    private List<Project> mHomeIdeas;
    private IdeasRecyclerAdapter mIdeasRecyclerAdapter;
    IdeaApiInterface ideaService;
    LinearLayoutManager mLayoutManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    // newInstance constructor for creating fragment with arguments
    public static HomeFragment newInstance(String title) {
        HomeFragment fragmentHome = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("titleParam", title);
        fragmentHome.setArguments(args);
        return fragmentHome;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.idea_list_fragment, container, false);

        unbinder = ButterKnife.bind(this, root);

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        boolean isTabletInLand = getResources().getBoolean(R.bool.isTabletInLand);

        if (isTablet && isTabletInLand){
            mLayoutManager = new GridLayoutManager(getContext(), 3);
        } else if (isTablet){
            mLayoutManager = new GridLayoutManager(getContext(), 2);
        } else {//is phone
            mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        // Use this setting to improve performance if you know that changes in content do not change the child layout size in the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mIdeasRecyclerAdapter = new IdeasRecyclerAdapter(this, IdeasRecyclerAdapter.LIST_IDEA_CARD);
        mRecyclerView.setAdapter(mIdeasRecyclerAdapter);

        ideaService = ServiceGenerator.createService(IdeaApiInterface.class);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //https://stackoverflow.com/questions/22505327/android-save-restore-fragment-state
        //https://stackoverflow.com/questions/15313598/once-for-all-how-to-correctly-save-instance-state-of-fragments-in-back-stack
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //probably orientation change
            mHomeIdeas = savedInstanceState.getParcelableArrayList(HOME_LIST);
            mIdeasRecyclerAdapter.setIdeasData(mHomeIdeas);
        } else {
            if (mHomeIdeas != null) {
                //returning from backstack, data is fine, do nothing
                showIdeaDataView();
                mIdeasRecyclerAdapter.setIdeasData(mHomeIdeas);
            } else {
                //newly created, compute data
                loadData();
            }
        }
    }

    private void loadData() {
        mLoadingIndicator.setVisibility(View.VISIBLE);

        Call<ProjectResponse> call = ideaService.getTrendingIdeas();
        call.enqueue(new Callback<ProjectResponse>() {
            @Override
            public void onResponse(Call<ProjectResponse> call, Response<ProjectResponse> response) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()){
                    mHomeIdeas = response.body().getmResults();
                    showIdeaDataView();
                    mIdeasRecyclerAdapter.setIdeasData(mHomeIdeas);
                } else {
                    if (response.code() == 401){
                        Toast.makeText(getContext(), "Unauthenticated", Toast.LENGTH_SHORT).show();
                    } else if (response.code() >= 400){
                        Toast.makeText(getContext(), "Client Error " + response.code() + " " + response.message() , Toast.LENGTH_LONG).show();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saving fragment's state
        outState.putParcelableArrayList(HOME_LIST, (ArrayList<Project>) mHomeIdeas);
    }

    @Override
    public void onListItemClickI(Project project) {
        Intent intent = new Intent(getContext(), ProjectDetailActivity.class);
        Bundle extra = new Bundle();
        extra.putParcelable(IDEA_SELECTED, project);
        intent.putExtras(extra);
        startActivity(intent);
    }
}
