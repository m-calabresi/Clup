package com.android.clup.viewmodel;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.android.clup.model.Model;

import net.glxn.qrgen.android.QRCode;

public class DetailsViewModel extends ViewModel {
    private static final int QR_CODE_SIZE = 1024;

    private final Model model;

    public DetailsViewModel() {
        this.model = Model.getInstance();
    }

    /**
     * Generates the qr-code associated to the given uuid using the specified color-scheme.
     */
    @NonNull
    public Bitmap generateQRCode(final int onColor, final int offColor) {
        // TODO replace with proper logic to retrieve the current uuid
        final String uuid = this.model.getReservations().get(0).getUuid();
        return QRCode.from(uuid).withSize(QR_CODE_SIZE, QR_CODE_SIZE).withColor(onColor, offColor).bitmap();
    }
}
