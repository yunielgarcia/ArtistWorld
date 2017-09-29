package com.mycompany.artistworld.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.mycompany.artistworld.R;
import com.mycompany.artistworld.fragments.HomeFragment;
import com.mycompany.artistworld.model.Content;
import com.mycompany.artistworld.model.Project;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ProjectDetailActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
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
}
