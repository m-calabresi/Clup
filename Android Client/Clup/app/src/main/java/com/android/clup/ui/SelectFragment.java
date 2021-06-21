package com.android.clup.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.clup.R;
import com.android.clup.adapter.TimeLineAdapter;
import com.android.clup.model.AvailableDay;
import com.android.clup.model.AvailableSlot;
import com.android.clup.viewmodel.SelectViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SelectFragment extends Fragment {
    @NonNull
    public static final String KEY_POSITION = "position";

    @Nullable
    private SelectViewModel viewModel;
    @Nullable
    private AvailableDay availableDay;

    @Nullable
    private ChipGroup timeChipGroup;

    @Nullable
    private RecyclerView recyclerView;
    @Nullable
    private TimeLineAdapter adapter;
    @Nullable
    private TextView queueTextView;

    @NonNull
    private final ChipGroup.OnCheckedChangeListener timeChipGroupOnCheckedChangeListener = (group, checkedId) -> {
        // check if the change has been triggered by a valid chip
        if (checkedId != View.NO_ID) {
            // when a chip is selected, retrieve the corresponding time
            final Chip selectedChip = group.findViewById(checkedId);
            final String selectedTime = selectedChip.getText().toString();

            this.viewModel.setSelectedTime(selectedTime);
            this.viewModel.setVisibilityStatusLiveData(true);

            final List<String> customersNames = this.availableDay.getAvailableSlotByTime(selectedTime).getEnqueuedCustomersNames();
            showQueue(customersNames);
        }
    };

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int position = 0;
        if (getArguments() != null)
            position = getArguments().getInt(KEY_POSITION);

        this.viewModel = new ViewModelProvider(requireActivity()).get(SelectViewModel.class);
        this.availableDay = this.viewModel.getSelectedShop().getAvailableDays().get(position);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        final View parent = inflater.inflate(R.layout.fragment_select, container, false);

        final CardView cardView = parent.findViewById(R.id.select_card_view);
        cardView.setBackgroundResource(R.drawable.rounded_view_background);

        this.timeChipGroup = parent.findViewById(R.id.time_chip_group);
        this.timeChipGroup.setOnCheckedChangeListener(timeChipGroupOnCheckedChangeListener);

        final List<String> times = Objects.requireNonNull(this.availableDay).getAvailableSlots()
                .stream()
                .map(AvailableSlot::getTime)
                .collect(Collectors.toList());
        Utils.setTimeChips(this.timeChipGroup, times);

        this.queueTextView = parent.findViewById(R.id.queue_text_view);

        this.recyclerView = parent.findViewById(R.id.timeline_recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        this.recyclerView.setHasFixedSize(true);

        this.adapter = new TimeLineAdapter(Collections.emptyList());
        this.recyclerView.setAdapter(this.adapter);

        return parent;
    }

    /**
     * Called every time the current {@code Fragment} is swiped away.
     */
    @Override
    public void onPause() {
        super.onPause();

        // clear the selected chip
        Objects.requireNonNull(this.timeChipGroup).clearCheck();
        Objects.requireNonNull(this.viewModel).setVisibilityStatusLiveData(false);

        // hide unused recyclerview
        hideQueue();
    }

    /**
     * Show the queue with all customers behind the user, plus the user himself.
     */
    private void showQueue(@NonNull final List<String> items) {
        Objects.requireNonNull(this.adapter).setCustomersNames(items);
        Objects.requireNonNull(this.recyclerView).setVisibility(View.VISIBLE);
        Objects.requireNonNull(this.queueTextView).setVisibility(View.VISIBLE);
    }

    /**
     * Hide the customers queue.
     */
    private void hideQueue() {
        Objects.requireNonNull(this.adapter).setCustomersNames(Collections.emptyList());
        Objects.requireNonNull(this.recyclerView).setVisibility(View.GONE);
        Objects.requireNonNull(this.queueTextView).setVisibility(View.GONE);
    }
}
