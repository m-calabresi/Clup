package com.android.clup.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.clup.R;
import com.android.clup.model.AvailableDay;
import com.android.clup.model.AvailableSlot;
import com.android.clup.viewmodel.SelectViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;
import java.util.stream.Collectors;

public class SelectFragment extends Fragment {
    @NonNull
    public static final String KEY_POSITION = "position";

    private SelectViewModel viewModel;
    private AvailableDay availableDay;

    private ChipGroup timeChipGroup;

    private final ChipGroup.OnCheckedChangeListener timeChipGroupOnCheckedChangeListener = (group, checkedId) -> {
        // check if the change has been triggered by a valid chip
        if (checkedId != View.NO_ID) {
            // when a chip is selected, retrieve the corresponding time
            final Chip selectedChip = group.findViewById(checkedId);
            final String selectedTime = selectedChip.getText().toString();

            this.viewModel.setSelectedTime(selectedTime);
            this.viewModel.setVisibilityStatusLiveData(true);
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

        final List<String> times = this.availableDay.getAvailableSlots().stream().map(AvailableSlot::getTime).collect(Collectors.toList());
        Utils.setTimeChips(this.timeChipGroup, times);

        return parent;
    }

    /**
     * Called every time the current {@code Fragment} is swiped away.
     */
    @Override
    public void onPause() {
        super.onPause();

        // clear the selected chip
        this.timeChipGroup.clearCheck();
        this.viewModel.setVisibilityStatusLiveData(false);
    }
}
