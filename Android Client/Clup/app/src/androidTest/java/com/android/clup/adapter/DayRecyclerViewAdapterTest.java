package com.android.clup.adapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;

import com.android.clup.model.AvailableDay;
import com.android.clup.model.Date;
import com.android.clup.model.Model;
import com.android.clup.model.Shop;
import com.android.clup.ui.SelectActivity;
import com.android.clup.viewmodel.SelectViewModel;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DayRecyclerViewAdapterTest {
    private DayRecyclerViewAdapter adapter;
    private int size;

    @NonNull
    private List<Shop> generateShops() {
        // dummy list
        final LatLng coords1 = new LatLng(45.4659, 9.1914);
        final LatLng coords2 = new LatLng(45.698342, 9.204998);

        final Date date1 = Date.fromString("11-02-2021");
        final Date date2 = Date.fromString("12-02-2021");
        final Date date3 = Date.fromString("13-02-2021");

        final AvailableDay availableDay1 = new AvailableDay(date1, Arrays.asList("12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"));
        final AvailableDay availableDay2 = new AvailableDay(date2, Arrays.asList("16:00", "17:00", "18:00", "19:00"));
        final AvailableDay availableDay3 = new AvailableDay(date3, Arrays.asList("15:00", "16:00", "17:00", "20:00"));

        final List<AvailableDay> availableDays = Arrays.asList(availableDay1, availableDay2, availableDay3);

        final Shop shop1 = new Shop("Local shop", coords1, availableDays);
        final Shop shop2 = new Shop("Supermarket", coords2, availableDays);

        return Arrays.asList(shop1, shop2, shop1);
    }

    @Before
    public void setup() {
        final List<Shop> shops = generateShops();

        Model.getInstance().setShops(shops);
        Model.getInstance().setSelectedShop(shops.get(0));

        try (final ActivityScenario<SelectActivity> scenario = ActivityScenario.launch(SelectActivity.class)) {
            scenario.onActivity(activity -> {
                final SelectViewModel viewModel = new ViewModelProvider(activity).get(SelectViewModel.class);
                viewModel.setSelectedDay(0);

                this.size = shops.size();
                this.adapter = new DayRecyclerViewAdapter(activity, viewModel);
            });
        }
    }

    @Test
    public void getItemViewType() {
        for (int i = 0; i < this.size; i++)
            assertEquals(1, this.adapter.getItemViewType(i));
        assertEquals(0, this.adapter.getItemViewType(this.size));
    }

    @Test
    public void getItemCount() {
        assertEquals(this.size + 1, this.adapter.getItemCount());
    }
}