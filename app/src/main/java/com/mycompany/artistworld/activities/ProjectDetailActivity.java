package com.mycompany.artistworld.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.mycompany.artistworld.R;
import com.mycompany.artistworld.data.ArtistWorldContract;
import com.mycompany.artistworld.data.ProjectDbHelper;
import com.mycompany.artistworld.fragments.HomeFragment;
import com.mycompany.artistworld.model.Content;
import com.mycompany.artistworld.model.Project;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProjectDetailActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    private Project mSelectedIdea;
    private List<String> mContentUrls;

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

    private SQLiteDatabase mDb;


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

        }

        // Create a DB helper (this will create the DB if run for the first time)
        ProjectDbHelper dbHelper = new ProjectDbHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding items
        mDb = dbHelper.getWritableDatabase();

        //DataTestUtil.insertFakeData(mDb);

        // Get all guest info from the database and save in a cursor
        Cursor cursor = getAllProjects();

        int totalInDb = cursor.getCount();
        Toast.makeText(this, "Total items" + totalInDb, Toast.LENGTH_LONG).show();

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

    private Cursor getAllProjects() {
        return mDb.query(
                ArtistWorldContract.ProjectEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                ArtistWorldContract.ProjectEntry.COLUMN_TIMESTAMP
        );
    }

    @OnClick(R.id.fab)
    public void voteAndAddToFavorite(){
        if (mSelectedIdea == null) return;

        //create a ContentValues object
        ContentValues cv = new ContentValues();
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_SLUG, mSelectedIdea.getmSlug());
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_TITLE, mSelectedIdea.getmTitle());
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_VOTE_WEIGHT, 5); //FOR NOW SINCE VOTE WEIGHT IS NO YET IMPLEMENTED
        cv.put(ArtistWorldContract.ProjectEntry.COLUMN_MAIN_CONTENT_PATH, mSelectedIdea.getmContents().get(0).getmDisplayImg());

        //insert data using contentProvider
        Uri uri = getContentResolver().insert(ArtistWorldContract.ProjectEntry.CONTENT_URI, cv);

        if (uri != null){
            Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();
        }
    }


}







