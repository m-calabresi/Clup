package com.android.clup.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public final class Model {
    private static class ModelHelper {
        @NonNull
        private static final Model INSTANCE = new Model();
    }

    private static final int INVALID_INDEX = -1;

    @Nullable
    private List<Shop> shops;
    private int selectedShopIndex;
    private int selectedDayIndex;
    private int selectedHourIndex;

    private Model() {
        this.shops = null;
        this.selectedShopIndex = INVALID_INDEX;
        this.selectedDayIndex = INVALID_INDEX;
        this.selectedHourIndex = INVALID_INDEX;
    }

    public static Model getInstance() {
        return ModelHelper.INSTANCE;
    }

    public void setShops(@NonNull final List<Shop> shops) {
        this.shops = shops;
    }

    @Nullable
    public List<Shop> getShops() {
        return this.shops;
    }

    public void setSelectedShopIndex(final int selectedShopIndex) {
        this.selectedShopIndex = selectedShopIndex;
    }

    @NonNull
    public Shop getSelectedShop() {
        if (this.selectedShopIndex != INVALID_INDEX)
            return Objects.requireNonNull(this.shops).get(this.selectedShopIndex);
        throw new NullPointerException("No index was set before calling this method, did you call 'setSelectedShopIndex'?");
    }

    public void resetSelectedShopIndex() {
        this.selectedShopIndex = INVALID_INDEX;
    }

    public void setSelectedDayIndex(final int selectedDayIndex) {
        this.selectedDayIndex = selectedDayIndex;
    }

    public AvailableDay getSelectedDay() {
        if (this.selectedDayIndex != INVALID_INDEX)
            return getSelectedShop().getAvailableDays().get(this.selectedDayIndex);
        throw new NullPointerException("No index was set before calling this method, did you call 'setSelectedDayIndex'?");
    }

    public void resetSelectedDayIndex() {
        this.selectedDayIndex = INVALID_INDEX;
    }

    public void setSelectedHourIndex(final int selectedHourIndex) {
        this.selectedHourIndex = selectedHourIndex;
    }

    public String getSelectedHour() {
        if (this.selectedHourIndex != INVALID_INDEX)
            return getSelectedDay().getHours().get(this.selectedHourIndex);
        throw new NullPointerException("No index was set before calling this method, did you call 'setSelectedHourIndex'?");
    }

    public void resetSelectedHourIndex() {
        this.selectedHourIndex = INVALID_INDEX;
    }
}
