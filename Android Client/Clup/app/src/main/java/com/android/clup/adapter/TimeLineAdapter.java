package com.android.clup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.clup.R;
import com.android.clup.ui.Utils;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_FOOTER = -1;

    @NonNull
    private List<String> customersNames;

    public static class TimeLineViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        public final TimelineView timelineView;
        @NonNull
        public final CardView cardView;
        @NonNull
        public final TextView textView;

        public TimeLineViewHolder(@NonNull final View v, final int viewType) {
            super(v);
            this.timelineView = v.findViewById(R.id.timeline);
            this.timelineView.initLine(viewType);

            this.cardView = v.findViewById(R.id.timeline_card_view);

            this.textView = v.findViewById(R.id.text_timeline);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(@NonNull final View v) {
            super(v);
        }
    }

    public TimeLineAdapter(@NonNull final List<String> customersNames) {
        super();
        this.customersNames = customersNames;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final RecyclerView.ViewHolder viewHolder;

        if (viewType == VIEW_TYPE_FOOTER) {
            final View footerView = layoutInflater.inflate(R.layout.item_footer, parent, false);
            viewHolder = new TimeLineAdapter.FooterViewHolder(footerView);
        } else {
            final View timelineView = layoutInflater.inflate(R.layout.item_timeline, parent, false);
            viewHolder = new TimeLineViewHolder(timelineView, viewType);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() != VIEW_TYPE_FOOTER) {
            final TimeLineViewHolder viewHolder = (TimeLineViewHolder) holder;

            if (position == this.customersNames.size() - 1) {
                viewHolder.textView.setText(this.customersNames.get(position));
                Utils.highlightEndLine(viewHolder);
            } else if (position == this.customersNames.size()) {
                viewHolder.textView.setText(R.string.text_you);
                Utils.highlightAll(viewHolder);
            } else {
                viewHolder.textView.setText(this.customersNames.get(position));
                // reset the color to inactive in order to prevent weird glitches with the timeline colors
                Utils.unHighlightEndLine(viewHolder);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == this.customersNames.size() + 1)
            return VIEW_TYPE_FOOTER;
        return TimelineView.getTimeLineViewType(position, this.customersNames.size() + 1);
    }

    @Override
    public int getItemCount() {
        return this.customersNames.size() + 2;
    }

    public void setCustomersNames(@NonNull final List<String> customersNames) {
        this.customersNames = customersNames;
        notifyDataSetChanged();
    }
}
