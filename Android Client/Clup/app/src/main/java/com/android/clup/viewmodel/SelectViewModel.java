package com.android.clup.viewmodel;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.clup.R;
import com.android.clup.model.AvailableDay;
import com.android.clup.model.Model;
import com.android.clup.model.Shop;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class SelectViewModel extends ViewModel {
    private final Model model;
    private final MutableLiveData<Integer> groupIdLiveData;

    public SelectViewModel() {
        this.model = Model.getInstance();
        this.groupIdLiveData = new MutableLiveData<>();
    }

    /**
     * Return the shop selected by the user.
     */
    public Shop getSelectedShop() {
        return this.model.getSelectedShop();
    }

    /**
     * Reset the index corresponding to the shop selected by the user.
     * This is done to allow the user to select another shop by going back in MapActivity and pick
     * another shop.
     */
    public void resetSelectedShopPosition() {
        this.model.resetSelectedShopIndex();
    }

    /**
     * Return the day selected by the user.
     */
    public AvailableDay getSelectedDay() {
        return this.model.getSelectedDay();
    }

    /**
     * Store index corresponding to the day selected by the user.
     */
    public void setSelectedDayPosition(final int position) {
        this.model.setSelectedDayIndex(position);
    }

    /**
     * Reset the index corresponding to the day selected by the user.
     */
    public void resetSelectedDayPosition() {
        this.model.resetSelectedDayIndex();
    }

    /**
     * Return the hour selected by the user.
     */
    public String getSelectedHour() {
        return this.model.getSelectedHour();
    }

    /**
     * Store index corresponding to the hour selected by the user.
     */
    public void setSelectedHourPosition(final int position) {
        this.model.setSelectedHourIndex(position);
    }

    /**
     * Reset the index corresponding to the shop selected by the user.
     */
    public void resetSelectedHourPosition() {
        this.model.resetSelectedHourIndex();
    }

    /**
     * Utility method to fill a ChipGroup with chips that contains values specified by hours.
     */
    public static void setHourChips(@NonNull final ChipGroup parent, @NonNull final List<String> hours) {
        parent.post(() -> {
            if (parent.getChildCount() > 0)
                parent.removeAllViews();

            final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

            for (int i = 0; i < hours.size(); i++) {
                final String foodName = hours.get(i);
                final Chip chip = (Chip) layoutInflater.inflate(R.layout.item_hour, parent, false);
                chip.setText(foodName);

                parent.addView(chip);
            }
        });
    }

    /**
     * A MutableLiveData that reacts to chips selection.
     */
    public MutableLiveData<Integer> getGroupIdLiveData() {
        return this.groupIdLiveData;
    }

    /**
     * Set the current value of MutableLiveData to the given group ID, all observers will be notified
     * that this group is now the only one containing a selected chip.
     */
    public void setGroupTagLiveData(@NonNull final Object groupTag) {
        this.groupIdLiveData.setValue((int) groupTag);
    }
}
