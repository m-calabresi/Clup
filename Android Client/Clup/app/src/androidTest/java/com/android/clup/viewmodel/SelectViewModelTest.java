package com.android.clup.viewmodel;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;

import com.android.clup.model.AvailableDay;
import com.android.clup.model.AvailableSlot;
import com.android.clup.model.Date;
import com.android.clup.model.Model;
import com.android.clup.model.Shop;
import com.android.clup.ui.auth.AuthActivity;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SelectViewModelTest {
    private SelectViewModel viewModel;

    @Before
    public void setUp() {
        try (final ActivityScenario<AuthActivity> scenario = ActivityScenario.launch(AuthActivity.class)) {
            scenario.onActivity(activity ->
                    this.viewModel = new ViewModelProvider(activity).get(SelectViewModel.class));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void setVisibilityStatusLiveData() {
        // not initialized value results in an exception
        this.viewModel.getVisibilityStatusLiveData().getValue();

        this.viewModel.setVisibilityStatusLiveData(true);

        assert this.viewModel.getVisibilityStatusLiveData().getValue() != null;
        assertTrue(this.viewModel.getVisibilityStatusLiveData().getValue());

        this.viewModel.setVisibilityStatusLiveData(false);
        assertFalse(this.viewModel.getVisibilityStatusLiveData().getValue());
    }

    @Test
    public void getSelectedShop() {
        // dummy list
        final LatLng coords = new LatLng(45.4659, 9.1914);
        final Date date = Date.fromString("11-02-2021");
        final AvailableSlot availableSlot1 = new AvailableSlot("12:30", Arrays.asList("Marco", "Giacomo"));
        final AvailableSlot availableSlot2 = new AvailableSlot("13:30", Arrays.asList("Giovanni", "Aldo"));
        final List<AvailableSlot> availableSlots = Arrays.asList(availableSlot1, availableSlot2);
        final AvailableDay availableDay = new AvailableDay(date, availableSlots);
        final List<AvailableDay> availableDays = Arrays.asList(availableDay, availableDay);
        final Shop shop = new Shop("1234567", "Local shop", coords, availableDays);

        Model.getInstance().setSelectedShop(shop);

        assertEquals(Model.getInstance().getSelectedShop(), this.viewModel.getSelectedShop());
    }

    @Test(expected = NullPointerException.class)
    public void resetSelectedShop() {
        this.viewModel.resetSelectedShop();
        this.viewModel.getSelectedShop();
    }

    @Test
    public void setSelectedDay() {
        // dummy list
        final LatLng coords = new LatLng(45.4659, 9.1914);
        final Date date = Date.fromString("11-02-2021");
        final AvailableSlot availableSlot1 = new AvailableSlot("12:30", Arrays.asList("Marco", "Giacomo"));
        final AvailableSlot availableSlot2 = new AvailableSlot("13:30", Arrays.asList("Giovanni", "Aldo"));
        final List<AvailableSlot> availableSlots = Arrays.asList(availableSlot1, availableSlot2);
        final AvailableDay availableDay = new AvailableDay(date, availableSlots);
        final List<AvailableDay> availableDays = Arrays.asList(availableDay, availableDay);
        final Shop shop = new Shop("1234567", "Local shop", coords, availableDays);

        Model.getInstance().setSelectedShop(shop);
        //Model.getInstance().setSelectedDay(availableDay);
        this.viewModel.setSelectedDay(0);

        assertEquals(availableDay, Model.getInstance().getSelectedDay());
    }

    @Test(expected = NullPointerException.class)
    public void resetSelectedDay() {
        this.viewModel.resetSelectedDay();
        Model.getInstance().getSelectedDay();
    }

    @Test
    public void setSelectedTime() {
        final String selectedTime = "11:35";

        // dummy list
        final LatLng coords = new LatLng(45.4659, 9.1914);
        final Date date = Date.fromString("11-02-2021");
        final AvailableSlot availableSlot1 = new AvailableSlot(selectedTime, Arrays.asList("Marco", "Giacomo"));
        final AvailableSlot availableSlot2 = new AvailableSlot("13:30", Arrays.asList("Giovanni", "Aldo"));
        final List<AvailableSlot> availableSlots = Arrays.asList(availableSlot1, availableSlot2);
        final AvailableDay availableDay = new AvailableDay(date, availableSlots);
        final List<AvailableDay> availableDays = Arrays.asList(availableDay, availableDay);
        final Shop shop = new Shop("1234567", "Local shop", coords, availableDays);

        Model.getInstance().setSelectedShop(shop);
        Model.getInstance().setSelectedDay(availableDay);
        this.viewModel.setSelectedTime(availableDay.getAvailableSlots().get(0).getTime());

        assertEquals(selectedTime, Model.getInstance().getSelectedTime());
    }

    @Test(expected = NullPointerException.class)
    public void resetSelectedTime() {
        this.viewModel.resetSelectedTime();
        Model.getInstance().getSelectedTime();
    }
}