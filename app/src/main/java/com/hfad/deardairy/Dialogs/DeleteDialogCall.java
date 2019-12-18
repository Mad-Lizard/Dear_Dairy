package com.hfad.deardairy.Dialogs;

import android.os.Bundle;

public class DeleteDialogCall {
    public DataDeleteDialog OpenDeleteDialog(int titleId, String date) {
        DataDeleteDialog dataDeleteDialog = new DataDeleteDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("titleId", titleId);
        bundle.putString("date", date);
        dataDeleteDialog.setArguments(bundle);
        return dataDeleteDialog;
    }
}
