package com.mycompany.artistworld.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mycompany.artistworld.R;
import com.mycompany.artistworld.model.Project;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ygarcia on 9/28/2017.
 */

public class IdeasRecyclerAdapter extends RecyclerView.Adapter<IdeasRecyclerAdapter.IdeaAdapterViewHolder>{

    public final static int LIST_IDEA_CARD = 0;
    public final static int LIST_SEARCH_RESULTS = 1;

    private int targetList;

    private List<Project> mIdeaData;
    //reference to the interface
    final private ListItemClickListener mOnClickListener;

    public IdeasRecyclerAdapter(ListItemClickListener mOnClickListener, int target) {
        this.mOnClickListener = mOnClickListener;//1- we'll initialize it from the activity interface implementation
        this.targetList = target;
    }

    //interface for click handling
    public interface ListItemClickListener{
        void onListItemClickI(Project clickedIdea);
    }


    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param parent   The ViewGroup that these ViewHolders are contained within.
     * @param viewType If your RecyclerView has more than one type of item (which ours doesn't) you
     *                 can use this viewType integer to provide a different layout. See
     *                 {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                 for more details.
     * @return a_ic new IdeaAdapterViewHolder that holds the View for each list item
     */
    @Override
    public IdeaAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem;

        if (targetList == LIST_IDEA_CARD){
            layoutIdForListItem = R.layout.idea_list_item;
        } else {
            layoutIdForListItem = R.layout.card_for_land_and_search;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new IdeaAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IdeaAdapterViewHolder holder, int position) {
        Project singleIdeaObj = mIdeaData.get(position);
        Context context = holder.mImageView.getContext();
        String idea_poster_path = singleIdeaObj.getmContents().get(0).getmDisplayImg();
        //main content
        Picasso.with(context)
                .load(idea_poster_path)
                .into(holder.mImageView);
        //title
        holder.mIdeaItem_tv.setText(singleIdeaObj.getmTitle());
        //creator
        String byCreator = "by: " + singleIdeaObj.getmIdeaCreator();
        holder.mCreator.setText(byCreator);
        //progress
        holder.mProgBar.setProgress(singleIdeaObj.getmVotePercent());
        //percent
        String strPercent = String.valueOf(singleIdeaObj.getmVotePercent()) + "%";
        holder.mPercent_tv.setText(strPercent);
    }

    @Override
    public int getItemCount() {
        if (null == mIdeaData) return 0;
        return mIdeaData.size();
    }

    /**
     * This method is used to set the idea on a IdeasAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param ideasData The new weather data to be displayed.
     */
    public void setIdeasData(List<Project> ideasData) {
        mIdeaData = ideasData;
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    class IdeaAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.title)
        TextView mIdeaItem_tv;
        @BindView(R.id.thumbnail)
        ImageView mImageView;
        @BindView(R.id.by)
        TextView mCreator;
        @BindView(R.id.prog_bar)
        ProgressBar mProgBar;
        @BindView(R.id.percent_tv)
        TextView mPercent_tv;

        IdeaAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }


        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Project clickedIdeaSelected = mIdeaData.get(adapterPosition);
            mOnClickListener.onListItemClickI(clickedIdeaSelected);
        }
    }
}
