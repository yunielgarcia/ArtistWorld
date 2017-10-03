package com.mycompany.artistworld.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycompany.artistworld.R;
import com.mycompany.artistworld.data.ArtistWorldContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ygarcia on 10/2/2017.
 */

public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.FavViewHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;
    //reference to the interface
    final private FavListItemClickListener mFavOnClickListener;

    //interface for click handling
    public interface FavListItemClickListener {
        void onListItemClickI(String selectedSlug);
    }


    /**
     * Constructor for the CustomCursorAdapter that initializes the Context.
     *
     * @param mContext the current Context
     */
    public CustomCursorAdapter(Context mContext, FavListItemClickListener favListItemClickListener) {
        this.mContext = mContext;
        this.mFavOnClickListener = favListItemClickListener;
    }


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new FavViewHolder that holds the view for each task
     */
    @Override
    public FavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.fav_item_layout, parent, false);

        return new FavViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(FavViewHolder holder, int position) {

        // Indices for the _id, description, and priority columns
        int idIndex = mCursor.getColumnIndex(ArtistWorldContract.ProjectEntry._ID);
        int titleIndex = mCursor.getColumnIndex(ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_TITLE);
        int voteWeightIndex = mCursor.getColumnIndex(ArtistWorldContract.ProjectEntry.COLUMN_VOTE_WEIGHT);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        String title = mCursor.getString(titleIndex);
        String voteWeight = String.valueOf(mCursor.getInt(voteWeightIndex));


        //Set values
        holder.itemView.setTag(id);
        holder.titleView.setText(title);
        holder.favVoteWeightView.setText(voteWeight);
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    // Inner class for creating ViewHolders
    class FavViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        @BindView(R.id.fav_project_title)
        TextView titleView;
        @BindView(R.id.fav_vote_weight_tv)
        TextView favVoteWeightView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public FavViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int slugIndex = mCursor.getColumnIndex(ArtistWorldContract.ProjectEntry.COLUMN_PROJECT_SLUG);
            String slugSelected = mCursor.getString(slugIndex);

            mFavOnClickListener.onListItemClickI(slugSelected);
        }
    }
}
