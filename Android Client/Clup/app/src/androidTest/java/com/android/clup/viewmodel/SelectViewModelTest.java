package com.android.clup.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;

import com.android.clup.model.AvailableDay;
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

public class SelectViewModelTest {
    private SelectViewModel viewModel;

    @Before
    public void setUp() {
        try (final ActivityScenario<AuthActivity> scenario = ActivityScenario.launch(AuthActivity.class)) {
            scenario.onActivity(activity ->
                    this.viewModel = new ViewModelProvider(activity).get(SelectViewModel.class));
        }
    }

    @Test
    public void getSelectedShop() {
        // dummy list
        final LatLng coords = new LatLng(45.4659, 9.1914);
        final Date date = Date.fromString("11-02-2021");
        final AvailableDay availableDay = new AvailableDay(date, Arrays.asList("12:00", "18:00"));
        final List<AvailableDay> availableDays = Arrays.asList(availableDay, availableDay);
        final Shop shop = new Shop("Local shop", coords, availableDays);

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
        final AvailableDay availableDay = new AvailableDay(date, Arrays.asList("12:00", "18:00"));
        final List<AvailableDay> availableDays = Arrays.asList(availableDay, availableDay);
        final Shop shop = new Shop("Local shop", coords, availableDays);

        Model.getInstance().setSelectedShop(shop);
        Model.getInstance().setSelectedDay(availableDay);
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
        final AvailableDay availableDay = new AvailableDay(date, Arrays.asList(selectedTime, "18:00"));
        final List<AvailableDay> availableDays = Arrays.asList(availableDay, availableDay);
        final Shop shop = new Shop("Local shop", coords, availableDays);

        Model.getInstance().setSelectedShop(shop);
        Model.getInstance().setSelectedDay(availableDay);
        this.viewModel.setSelectedTime(0);

        assertEquals(selectedTime, Model.getInstance().getSelectedTime());
    }

    @Test(expected = NullPointerException.class)
    public void resetSelectedTime() {
        this.viewModel.resetSelectedTime();
        Model.getInstance().getSelectedTime();
    }

    @Test
    public void setGroupTagLiveData() {
        // handler is needed: can't assign value to LiveData on a background thread
        new Handler(Looper.getMainLooper()).post(() -> {
            final int tag = 1;
            this.viewModel.setGroupTagLiveData(tag);

            assert this.viewModel.getGroupTagLiveData().getValue() != null;
            final int actualTag = (int) this.viewModel.getGroupTagLiveData().getValue();

            assertEquals(tag, actualTag);
        });
    }
}