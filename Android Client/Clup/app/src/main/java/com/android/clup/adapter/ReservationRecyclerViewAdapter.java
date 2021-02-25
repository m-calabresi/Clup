package com.android.clup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.clup.R;
import com.android.clup.model.Reservation;

import java.util.List;

public class ReservationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_FOOTER = 0;
    private static final int VIEW_TYPE_DAY = 1;

    @NonNull
    private final OnRecyclerViewItemClickedCallback onRecyclerViewItemClickedCallback;
    @NonNull
    private final List<Reservation> reservations;

    public class ReservationViewHolder extends RecyclerView.ViewHolder {
        private final TextView shopNameTextView;
        private final TextView dateTimeTextView;
        private final TextView notificationTextView;

        public ReservationViewHolder(@NonNull final View itemView) {
            super(itemView);

            final View.OnClickListener reservationViewOnClickListener = view ->
                    onRecyclerViewItemClickedCallback.onRecyclerViewItemClicked(getBindingAdapterPosition());

            final CardView parent = itemView.findViewById(R.id.main_card_view);
            parent.setOnClickListener(reservationViewOnClickListener);

            this.shopNameTextView = parent.findViewById(R.id.shop_name_text_view);
            this.dateTimeTextView = parent.findViewById(R.id.date_time_text_view);
            this.notificationTextView = parent.findViewById(R.id.notification_text_view);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(@NonNull final View v) {
            super(v);
        }
    }

    public ReservationRecyclerViewAdapter(@NonNull final OnRecyclerViewItemClickedCallback callback,
                                          @NonNull final List<Reservation> reservations) {
        this.onRecyclerViewItemClickedCallback = callback;
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final RecyclerView.ViewHolder viewHolder;

        if (viewType == VIEW_TYPE_FOOTER) {
            final View footerView = layoutInflater.inflate(R.layout.item_footer, parent, false);
            viewHolder = new ReservationRecyclerViewAdapter.FooterViewHolder(footerView);
        } else {
            final View dayView = layoutInflater.inflate(R.layout.item_reservation, parent, false);
            viewHolder = new ReservationRecyclerViewAdapter.ReservationViewHolder(dayView);
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == this.reservations.size())
            return VIEW_TYPE_FOOTER;
        return VIEW_TYPE_DAY;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (position != this.reservations.size()) {
            final ReservationViewHolder viewHolder = (ReservationViewHolder) holder;
            final Reservation reservation = this.reservations.get(position);

            final String summary = viewHolder.itemView.getContext().getString(R.string.text_reservation_summary,
                    reservation.getDate().formatted(), reservation.getDate().getTime());

            viewHolder.shopNameTextView.setText(reservation.getShopName());
            viewHolder.dateTimeTextView.setText(summary);

            if (reservation.getTimeNotice() != Reservation.TimeNotice.NOT_SET &&
                    reservation.getTimeNotice() != Reservation.TimeNotice.DISABLED) {
                viewHolder.notificationTextView.setVisibility(View.VISIBLE);

                final String timeNoticeString = Reservation.TimeNotice
                        .toTimeString(viewHolder.itemView.getContext(), reservation.getTimeNotice());
                final String timeNoticeSummary = viewHolder.itemView.getContext()
                        .getString(R.string.text_notification_summary, timeNoticeString);

                viewHolder.notificationTextView.setText(timeNoticeSummary);
            } else
                viewHolder.notificationTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reservations.size() + 1;
    }
}
