package com.android.clup.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;

import com.android.clup.api.SMSAuthService;
import com.android.clup.model.Model;
import com.android.clup.model.Preferences;
import com.android.clup.ui.auth.AuthActivity;
import com.android.clup.ui.auth.SuccessFragment;

import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AuthViewModelTest {
    private AuthViewModel viewModel;
    private AuthActivity activity;

    @Before
    public void setUp() {
        try (final ActivityScenario<AuthActivity> scenario = ActivityScenario.launch(AuthActivity.class)) {
            scenario.onActivity(activity -> {
                this.activity = activity;
                this.viewModel = new ViewModelProvider(activity).get(AuthViewModel.class);
            });
        }
    }

    @Test
    public void switchTo() {
        // handler is needed: can't assign value to LiveData on a background thread
        new Handler(Looper.getMainLooper()).post(() -> {
            this.viewModel.switchTo(SuccessFragment.class);
            assertEquals(SuccessFragment.class, this.viewModel.getNextFragmentLiveData().getValue());
        });
    }

    @Test
    public void setUsername() {
        final String completeName = "Name Surname";
        this.viewModel.setUsername(completeName);

        assertEquals(Model.getInstance().getFullname(), completeName);
        assertEquals(Model.getInstance().getFriendlyName(), completeName.split(" ")[0]);
    }

    @Test
    public void setPhoneNumber() {
        final String phoneNumber1 = "+1 (123) 4567-890";
        final String phoneNumber2 = "+112 345-6789-012";
        final String phoneNumber3 = "+11234567890";

        final String expected13 = "11234567890";
        final String expected2 = "1123456789012";

        this.viewModel.setPhoneNumber(phoneNumber1);
        assertEquals(expected13, this.viewModel.getPhoneNumber());

        this.viewModel.setPhoneNumber(phoneNumber3);
        assertEquals(expected13, this.viewModel.getPhoneNumber());

        this.viewModel.setPhoneNumber(phoneNumber2);
        assertEquals(expected2, this.viewModel.getPhoneNumber());

    }

    @Test
    public void isValidUsername() {
        // no empty string or numbers/special chars
        assertFalse(this.viewModel.isValidUsername(""));
        assertFalse(this.viewModel.isValidUsername("-"));
        assertFalse(this.viewModel.isValidUsername("'"));

        // no whitespaces, no single name
        assertFalse(this.viewModel.isValidUsername(" "));
        assertFalse(this.viewModel.isValidUsername("a"));
        assertFalse(this.viewModel.isValidUsername("aa"));
        assertFalse(this.viewModel.isValidUsername("aaa"));

        assertFalse(this.viewModel.isValidUsername("  "));
        assertFalse(this.viewModel.isValidUsername(" a"));
        assertFalse(this.viewModel.isValidUsername(" aa"));
        assertFalse(this.viewModel.isValidUsername(" aaa"));

        assertFalse(this.viewModel.isValidUsername("   "));
        assertFalse(this.viewModel.isValidUsername("a "));
        assertFalse(this.viewModel.isValidUsername("aa "));
        assertFalse(this.viewModel.isValidUsername("aaa "));

        assertFalse(this.viewModel.isValidUsername(" a "));
        assertFalse(this.viewModel.isValidUsername(" aa "));
        assertFalse(this.viewModel.isValidUsername(" aaa "));

        // no name with less than 2 letters and surname with less than 3 letters
        assertFalse(this.viewModel.isValidUsername("a b"));
        assertFalse(this.viewModel.isValidUsername("a bb"));
        assertFalse(this.viewModel.isValidUsername("a bbb"));
        assertFalse(this.viewModel.isValidUsername("aa b"));
        assertFalse(this.viewModel.isValidUsername("aa bb"));
        assertFalse(this.viewModel.isValidUsername("aaa b"));
        assertFalse(this.viewModel.isValidUsername("aaa bb"));
        // ok: name with at least 2 letters and surname with at least 3 letters
        assertTrue(this.viewModel.isValidUsername("aa bbb"));
        assertTrue(this.viewModel.isValidUsername("aaa bbb"));

        // TRIM TEST + no name with less than 2 letters and surname with less than 3 letters
        assertFalse(this.viewModel.isValidUsername(" a b"));
        assertFalse(this.viewModel.isValidUsername(" a bb"));
        assertFalse(this.viewModel.isValidUsername(" a bbb"));
        assertFalse(this.viewModel.isValidUsername(" aa b"));
        assertFalse(this.viewModel.isValidUsername(" aa bb"));
        assertFalse(this.viewModel.isValidUsername(" aaa b"));
        assertFalse(this.viewModel.isValidUsername(" aaa bb"));
        // TRIM TEST + ok: name with at least 2 letters and surname with at least 3 letters
        assertTrue(this.viewModel.isValidUsername(" aa bbb"));
        assertTrue(this.viewModel.isValidUsername(" aaa bbb"));

        // TRIM TEST + no name with less than 2 letters and surname with less than 3 letters
        assertFalse(this.viewModel.isValidUsername("a b "));
        assertFalse(this.viewModel.isValidUsername("a bb "));
        assertFalse(this.viewModel.isValidUsername("a bbb "));
        assertFalse(this.viewModel.isValidUsername("aa b "));
        assertFalse(this.viewModel.isValidUsername("aa bb "));
        assertFalse(this.viewModel.isValidUsername("aaa b "));
        assertFalse(this.viewModel.isValidUsername("aaa bb "));
        // TRIM TEST + ok: ok: name with at least 2 letters and surname with at least 3 letters
        assertTrue(this.viewModel.isValidUsername("aa bbb "));
        assertTrue(this.viewModel.isValidUsername("aaa bbb "));

        // TRIM TEST + no name with less than 2 letters and surname with less than 3 letters
        assertFalse(this.viewModel.isValidUsername(" a b "));
        assertFalse(this.viewModel.isValidUsername(" a bb "));
        assertFalse(this.viewModel.isValidUsername(" a bbb "));
        assertFalse(this.viewModel.isValidUsername(" aa b "));
        assertFalse(this.viewModel.isValidUsername(" aa bb "));
        assertFalse(this.viewModel.isValidUsername(" aaa b "));
        assertFalse(this.viewModel.isValidUsername(" aaa bb "));
        // TRIM TEST + ok: name with at least 2 letters and surname with at least 3 letters
        assertTrue(this.viewModel.isValidUsername(" aa bbb "));
        assertTrue(this.viewModel.isValidUsername(" aaa bbb "));

        // no name-surname
        assertFalse(this.viewModel.isValidUsername("a-b"));
        assertFalse(this.viewModel.isValidUsername("a-bb"));
        assertFalse(this.viewModel.isValidUsername("a-bbb"));
        assertFalse(this.viewModel.isValidUsername("aa-b"));
        assertFalse(this.viewModel.isValidUsername("aa-bb"));
        assertFalse(this.viewModel.isValidUsername("aa-bbb"));
        assertFalse(this.viewModel.isValidUsername("aaa-b"));
        assertFalse(this.viewModel.isValidUsername("aaa-bb"));
        assertFalse(this.viewModel.isValidUsername("aaa-bbb"));

        // second surname always ok (as soon as first name and first surname are ok)
        assertFalse(this.viewModel.isValidUsername("a b c"));
        assertFalse(this.viewModel.isValidUsername("a b cc"));
        assertFalse(this.viewModel.isValidUsername("a b ccc"));
        assertFalse(this.viewModel.isValidUsername("a bb c"));
        assertFalse(this.viewModel.isValidUsername("a bb cc"));
        assertFalse(this.viewModel.isValidUsername("a bb ccc"));
        assertFalse(this.viewModel.isValidUsername("a bbb c"));
        assertFalse(this.viewModel.isValidUsername("a bbb cc"));
        assertFalse(this.viewModel.isValidUsername("a bbb ccc"));

        assertFalse(this.viewModel.isValidUsername("aa b c"));
        assertFalse(this.viewModel.isValidUsername("aa b cc"));
        assertFalse(this.viewModel.isValidUsername("aa b ccc"));
        assertFalse(this.viewModel.isValidUsername("aa bb c"));
        assertFalse(this.viewModel.isValidUsername("aa bb cc"));
        assertFalse(this.viewModel.isValidUsername("aa bb ccc"));
        assertTrue(this.viewModel.isValidUsername("aa bbb c"));
        assertTrue(this.viewModel.isValidUsername("aa bbb cc"));
        assertTrue(this.viewModel.isValidUsername("aa bbb ccc"));

        assertFalse(this.viewModel.isValidUsername("aaa b c"));
        assertFalse(this.viewModel.isValidUsername("aaa b cc"));
        assertFalse(this.viewModel.isValidUsername("aaa b ccc"));
        assertFalse(this.viewModel.isValidUsername("aaa bb c"));
        assertFalse(this.viewModel.isValidUsername("aaa bb cc"));
        assertFalse(this.viewModel.isValidUsername("aaa bb ccc"));
        assertTrue(this.viewModel.isValidUsername("aaa bbb c"));
        assertTrue(this.viewModel.isValidUsername("aaa bbb cc"));
        assertTrue(this.viewModel.isValidUsername("aaa bbb ccc"));

        //first name can contain one or more -
        assertTrue(this.viewModel.isValidUsername("-a bbb ccc"));
        assertTrue(this.viewModel.isValidUsername("a- bbb ccc"));
        assertTrue(this.viewModel.isValidUsername("-aa bbb ccc"));
        assertTrue(this.viewModel.isValidUsername("a-a bbb ccc"));
        assertTrue(this.viewModel.isValidUsername("aa- bbb ccc"));
        assertTrue(this.viewModel.isValidUsername("-- bbb ccc"));

        // first surname can contain one - or one ' as second character
        assertTrue(this.viewModel.isValidUsername("aa b-bb ccc"));
        assertTrue(this.viewModel.isValidUsername("aa b'bb ccc"));

        // no more than one - or ' are allowed
        assertFalse(this.viewModel.isValidUsername("aa b--bb ccc"));
        assertFalse(this.viewModel.isValidUsername("aa b''bb ccc"));

        //no other positions are allowed for - and '
        assertFalse(this.viewModel.isValidUsername("aa -bbb ccc"));
        assertFalse(this.viewModel.isValidUsername("aa 'bbb ccc"));
        assertFalse(this.viewModel.isValidUsername("aa bb-b ccc"));
        assertFalse(this.viewModel.isValidUsername("aa bb'b ccc"));
        assertFalse(this.viewModel.isValidUsername("aa bbb- ccc"));
        assertFalse(this.viewModel.isValidUsername("aa bbb' ccc"));

        // second surname can't contain any - or '
        assertFalse(this.viewModel.isValidUsername("aa bbb -ccc"));
        assertFalse(this.viewModel.isValidUsername("aa bbb 'ccc"));
        assertFalse(this.viewModel.isValidUsername("aa bbb c-cc"));
        assertFalse(this.viewModel.isValidUsername("aa bbb c'cc"));
        assertFalse(this.viewModel.isValidUsername("aa bbb cc-c"));
        assertFalse(this.viewModel.isValidUsername("aa bbb cc'c"));
        assertFalse(this.viewModel.isValidUsername("aa bbb ccc-"));
        assertFalse(this.viewModel.isValidUsername("aa bbb ccc'"));
    }

    @Test
    public void toLocale() {
        final int it = 39;
        final int fake = 123456;

        assertEquals("IT", this.viewModel.toLocale(it));
        assertEquals(SMSAuthService.DEFAULT_LOCALE, this.viewModel.toLocale(fake));
    }

    @Test
    public void setNameFragmentButtonVisibilityStatus() {
        // handler is needed: can't assign value to LiveData on a background thread
        new Handler(Looper.getMainLooper()).post(() -> {
            final boolean status = true;
            this.viewModel.setNameFragmentButtonVisibilityStatus(status);
            assertEquals(status, this.viewModel.getNameFragmentButtonVisibilityStatus().getValue());
        });
    }

    @Test
    public void setCodeFragmentButtonVisibilityStatus() {
        // handler is needed: can't assign value to LiveData on a background thread
        new Handler(Looper.getMainLooper()).post(() -> {
            final boolean status = true;
            this.viewModel.setCodeFragmentButtonVisibilityStatus(status);
            assertEquals(status, this.viewModel.getCodeFragmentButtonVisibilityStatus().getValue());
        });
    }

    @Test
    public void getDefaultCountryCode() {
        final String expectedLocale = Locale.getDefault().getCountry().toLowerCase();
        final String actualLocale = this.viewModel.toLocale(this.viewModel.getDefaultCountryCode(activity)).toLowerCase();

        assertEquals(expectedLocale, actualLocale);
    }

    @Test
    public void getTheme() {
        assertEquals(Preferences.getTheme(), this.viewModel.getTheme());
    }
}