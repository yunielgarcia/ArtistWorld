package com.mycompany.artistworld.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.crash.FirebaseCrash;
import com.mycompany.artistworld.R;
import com.mycompany.artistworld.data.ArtistWorldContract;
import com.mycompany.artistworld.data.Utils;
import com.mycompany.artistworld.fragments.HomeFragment;
import com.mycompany.artistworld.model.Content;
import com.mycompany.artistworld.model.Project;
import com.mycompany.artistworld.objects.IdeaVotePost;
import com.mycompany.artistworld.rest.IdeaApiInterface;
import com.mycompany.artistworld.rest.ServiceGenerator;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectDetailActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, RatingDialogListener, LoaderManager.LoaderCallbacks<Cursor> {

    private Project mSelectedIdea;
    private List<String> mContentUrls;

    // Constants for logging and referring to a unique loader
    private static final String TAG = ProjectDetailActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;

    private boolean votedAlready;
    private boolean isLoggedIn;
    private String slugIfvotedAlready;
    private int voteWeightvotedAlready;

    IdeaApiInterface ideaService;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.slider)
    SliderLayout mDemoSlider;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.idea_description)
    TextView ideaDescription;
    @BindView(R.id.detail_dash_title)
    TextView detailDashTitle;
    @BindView(R.id.detail_dash_by)
    TextView detailDashBy;
    @BindView(R.id.detail_prog_bar)
    ProgressBar progressBar;
    @BindView(R.id.detail_percent_tv)
    TextView votePercent;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.detail_vote_given_cont)
    RelativeLayout detailVoteGiven;
    @BindView(R.id.detail_vote_weight_tv)
    TextView detailVoteWeightGiven;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //enables the up arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            isLoggedIn = Utils.isLoggedIn(this);

            mSelectedIdea = extras.getParcelable(HomeFragment.IDEA_SELECTED);

            collapsingToolbarLayout.setTitle(mSelectedIdea.getmTitle());
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparent_color));

            mContentUrls = initializeContentArrayUrl();
            initSlider();

            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            //mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            //mDemoSlider.setDuration(9000);
            mDemoSlider.addOnPageChangeListener(this);
            mDemoSlider.stopAutoCycle();

            ideaDescription.setText(mSelectedIdea.getmDescription());
            detailDashTitle.setText(mSelectedIdea.getmTitle());
            detailDashBy.setText(mSelectedIdea.getmIdeaCreator());
            progressBar.setProgress(mSelectedIdea.getmVotePercent());
            votePercent.setText(String.valueOf(mSelectedIdea.getmVotePercent()));

            /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
            getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

            ideaService = ServiceGenerator.createService(IdeaApiInterface.class, Utils.getToken(this));

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    ////////////////////////////IMAGE SLIDER STARTS///////////////////////////////////////
    @Override//slider
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override//slider
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override//slider
    public void onPageSelected(int position) {

    }

    @Override//slider
    public void onPageScrollStateChanged(int state) {

    }

    private void initSlider() {
        for (String contentUrl : mContentUrls) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    //.description(name)
                    .image(contentUrl)
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);

            //add your extra information
//                textSliderView.bundle(new Bundle());
//                textSliderView.getBundle()
//                        .putString("extra",name);

            mDemoSlider.addSlider(textSliderView);
        }
    }

    private List<String> initializeContentArrayUrl() {
        List<String> imgUrls = new ArrayList<String>() {
        };
        if (mSelectedIdea.getmContents().size() > 0) {
            for (Content content : mSelectedIdea.getmContents()) {
                imgUrls.add(content.getmDisplayImg());
            }
        }
        return imgUrls;
    }
    ////////////////////////////IMAGE SLIDER ENDS///////////////////////////////////////


    ///////////////////////////  VOTE DIALOG STARTS  //////////////////////
    @OnClick(R.id.fab)
    public void voteAttempt() {

        if (Utils.isLoggedIn(this)) {
            new AppRatingDialog.Builder()
                    .setPositiveButtonText("Submit")
                    .setNegativeButtonText("Cancel")
                    .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                    .setDefaultRating(1)
                    .setTitle("Rate this project")
                    .setDescription("Please select some stars and give your feedback")
                    //.setTitleTextColor(R.color.titleTextColor)
                    //.setDescriptionTextColor(R.color.contentTextColor)
                    .setHint("Please write your comment here ...")
                    //.setHintTextColor(R.color.hintTextColor)
                    //.setCommentTextColor(R.color.commentTextColor)
                    .setCommentBackgroundColor(R.color.input_field)
                    .setWindowAnimation(R.style.MyDialogFadeAnimation)
                    .create(ProjectDetailActivity.this)
                    .show();
        } else {
            Toast.makeText(this, "You need to be logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onPositiveButtonClicked(int rate, @NotNull String comment) {
        Toast.makeText(this, String.valueOf(rate), Toast.LENGTH_SHORT).show();
        if (votedAlready){
            //it's not gonna be implemented hiding vote button
        } else {
            vote(rate);
        }
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    private void vote(int rate){
        //load the idea here and then if success go to detail
        IdeaVotePost ideaVotePost = new IdeaVotePost(mSelectedIdea.getmUrl(), rate);

        Call<ResponseBody> call = ideaService.vote(ideaVotePost);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //mLoadingIndicator.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()){
                    Toast.makeText(getBaseContext(), "Success" , Toast.LENGTH_LONG).show();
                    //adicionar idea a favoritas aqui y update ui en esta actividad
                } else {
                    if (response.code() == 401){
                        Toast.makeText(getBaseContext(), "Unauthenticated", Toast.LENGTH_SHORT).show();
                    } else if (response.code() >= 400){
                        Toast.makeText(getBaseContext(), "Client Error " + response.code() + " " + response.message() , Toast.LENGTH_LONG).show();
                    }
                    //showIdeaDataView();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                //showIdeaDataView();
            }
        });
    }
    ///////////////////////////  VOTE DIALOG ENDS  //////////////////////










    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Constructs a selection clause that matches the Id (sourceId)
        String mSelectionClause = ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_SLUG + " = ?";

        // Moves the slug of the selected project to the selection arguments.
        String[] mSelectionArgs = {mSelectedIdea.getmSlug()};

        return new CursorLoader(getBaseContext(), ArtistWorldContract.ProjectEntry.CONTENT_URI,
                null, mSelectionClause, mSelectionArgs, ArtistWorldContract.ProjectEntry.COLUMN_TIMESTAMP);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Some providers return null if an error occurs, others throw an exception
        if (null == data) {
            Toast.makeText(getBaseContext(), "Sorry, an error happened.", Toast.LENGTH_SHORT).show();
        /*
         * Insert code here to handle the error. Be sure not to use the cursor! You may want to
         * call android.util.Log.e() to log this error.
         *
         */
            votedAlready = false;
            // If the Cursor is empty, the provider found no matches
        } else if (data.getCount() < 1) {
        /*
         * Insert code here to notify the user that the search was unsuccessful. This isn't necessarily
         * an error. You may want to offer the user the option to insert a new row, or re-type the
         * search term.
         */
            votedAlready = false;
        } else {
            // Insert code here to do something with the results
            votedAlready = true;
            //hide the fab since this project was already voted
            fab.setVisibility(View.INVISIBLE);
            //show info voted weight on this idea
            detailVoteGiven.setVisibility(View.VISIBLE);
            data.moveToFirst();
            int voteWeightIdx = data.getColumnIndex(ArtistWorldContract.ProjectEntry.COLUMN_VOTE_WEIGHT);
            voteWeightvotedAlready = data.getInt(voteWeightIdx);
            detailVoteWeightGiven.setText(String.valueOf(voteWeightvotedAlready));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void saveInFavorite(int voteWeight) {
        if (mSelectedIdea == null) return;

        //create a ContentValues object
        ContentValues cv = new ContentValues();
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_SLUG, mSelectedIdea.getmSlug());
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_TITLE, mSelectedIdea.getmTitle());
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_VOTE_WEIGHT, voteWeight); //FOR NOW SINCE VOTE WEIGHT IS NO YET IMPLEMENTED
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_MAIN_CONTENT_PATH, mSelectedIdea.getmContents().get(0).getmDisplayImg());

        //insert data using contentProvider
        Uri uri = getContentResolver().insert(ArtistWorldContract.ProjectEntry.CONTENT_URI, cv);

        if (uri != null) {
            Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();
        } else {
            FirebaseCrash.log("Failed to insert in favorite idea " + mSelectedIdea.getmTitle());
        }
    }



}







