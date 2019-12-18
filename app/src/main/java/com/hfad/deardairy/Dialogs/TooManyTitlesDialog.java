package com.hfad.deardairy.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class TooManyTitlesDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Тем не может быть больше 7")
                .setPositiveButton("Ясно", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }
}
