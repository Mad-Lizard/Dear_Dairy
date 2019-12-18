package com.hfad.deardairy.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.hfad.deardairy.Db.ViewModels.DataViewModel;
import com.hfad.deardairy.R;

public class DataDeleteDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_dialog, null);

        TextView dialogText = view.findViewById(R.id.title_delete);
        dialogText.setText("Вы точно хотите удалить эту запись?");

        Bundle bundle = getArguments();
        final int titleId = bundle.getInt("titleId");
        final String date = bundle.getString("date");

        final DataViewModel mDataViewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        builder.setView(view)
              //  .setTitle("Удалить запись")
                .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDataViewModel.deleteData(titleId, date);
                    }
                });
        return builder.create();
    }
}
