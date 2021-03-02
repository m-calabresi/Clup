package com.android.clup.adapter;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TimeLineAdapterTest {
    private TimeLineAdapter adapter;
    private List<String> customersNames;

    @Before
    public void setUp() {
        this.customersNames = Arrays.asList("Marco", "Giovanni", "Giulia", "Anna");
        this.adapter = new TimeLineAdapter(this.customersNames);
    }

    @Test
    public void getItemViewType() {
        for (int i = 0; i < this.customersNames.size(); i++)
            assertNotEquals(-1, this.adapter.getItemViewType(i));

        assertEquals(-1, this.adapter.getItemViewType(this.customersNames.size() + 1));
    }

    @Test
    public void getItemCount() {
        assertEquals(this.customersNames.size() + 2, this.adapter.getItemCount());
    }
}