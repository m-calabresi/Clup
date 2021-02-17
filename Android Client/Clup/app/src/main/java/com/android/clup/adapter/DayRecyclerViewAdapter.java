package com.android.clup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.android.clup.R;
import com.android.clup.model.AvailableDay;
import com.android.clup.model.Shop;
import com.android.clup.viewmodel.SelectViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class DayRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_FOOTER = 0;
    private static final int VIEW_TYPE_DAY = 1;

    @NonNull
    private final LifecycleOwner lifecycleOwner;
    @NonNull
    private final SelectViewModel viewModel;

    private final Shop shop;

    public class DayViewHolder extends RecyclerView.ViewHolder {

        private final TextView dayTextView;
        private final ChipGroup timeChipGroup;

        public DayViewHolder(@NonNull final View v) {
            super(v);
            this.dayTextView = v.findViewById(R.id.day_text_view);

            this.timeChipGroup = v.findViewById(R.id.time_chip_group);
            this.timeChipGroup.setTag(View.generateViewId());

            // when a chip is clicked, notifies all other groups that they need to uncheck their chips
            final ChipGroup.OnCheckedChangeListener timeChipGroupOnCheckedChangeListener = (group, checkedId) -> {
                // prevents the propagation of triggers when all other groups are cleared
                if (checkedId != View.NO_ID) {
                    viewModel.setGroupTagLiveData(group.getTag());

                    final Chip selectedChip = group.findViewById(checkedId);
                    final String selectedTime = selectedChip.getText().toString();
                    final int selectedTimeIndex = shop.getAvailableDays().get(getBindingAdapterPosition()).getTimes().indexOf(selectedTime);

                    viewModel.setSelectedDay(getBindingAdapterPosition());
                    viewModel.setSelectedTime(selectedTimeIndex);
                }
            };

            // when a group receives a notification telling to clear its chips, it does so only if it isn't the original caller
            final Observer<Integer> groupTagLiveDataObserver = groupTag -> {
                if (((int) this.timeChipGroup.getTag() != groupTag))
                    this.timeChipGroup.clearCheck();
            };

            this.timeChipGroup.setOnCheckedChangeListener(timeChipGroupOnCheckedChangeListener);
            viewModel.getGroupIdLiveData().observe(lifecycleOwner, groupTagLiveDataObserver);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(@NonNull final View v) {
            super(v);
        }
    }

    public DayRecyclerViewAdapter(@NonNull final LifecycleOwner lifecycleOwner, @NonNull final SelectViewModel viewModel) {
        super();
        this.lifecycleOwner = lifecycleOwner;
        this.viewModel = viewModel;
        this.shop = viewModel.getSelectedShop();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final RecyclerView.ViewHolder viewHolder;

        if (viewType == VIEW_TYPE_FOOTER) {
            final View footerView = layoutInflater.inflate(R.layout.item_footer, parent, false);
            viewHolder = new FooterViewHolder(footerView);
        } else {
            final View dayView = layoutInflater.inflate(R.layout.item_day, parent, false);
            viewHolder = new DayViewHolder(dayView);
        }
        return viewHolder;
    }

    // TODO try to test on a device
    @Override
    public int getItemViewType(final int position) {
        if (position == this.shop.getAvailableDays().size())
            return VIEW_TYPE_FOOTER;
        return VIEW_TYPE_DAY;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (position != this.shop.getAvailableDays().size()) {
            final DayViewHolder viewHolder = (DayViewHolder) holder;
            final AvailableDay availableDay = this.shop.getAvailableDays().get(position);

            viewHolder.dayTextView.post(() -> viewHolder.dayTextView.setText(availableDay.getDate().formatted()));
            SelectViewModel.setTimeChips(viewHolder.timeChipGroup, availableDay.getTimes());
        }
    }

    // TODO try to test on a device
    @Override
    public int getItemCount() {
        return this.shop.getAvailableDays().size() + 1;
    }
}
