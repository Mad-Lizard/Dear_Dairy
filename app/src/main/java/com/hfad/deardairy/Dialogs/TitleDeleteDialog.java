package com.hfad.deardairy.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.hfad.deardairy.R;
import com.hfad.deardairy.Db.ViewModels.TitleViewModel;

public class TitleDeleteDialog extends AppCompatDialogFragment {
    private TextView titleView;
    private TitleViewModel titleViewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_dialog, null);

        //get title name
        Bundle bundle = getArguments();
        final String title = bundle.getString("title");

        titleView = view.findViewById(R.id.title_delete);
        titleView.setText(title);
        titleViewModel = ViewModelProviders.of(this).get(TitleViewModel.class);

        builder.setView(view)
                .setTitle("Удалить тему дневника?")
                .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        titleViewModel.deleteTitle(title);
                    }
                });
        return builder.create();
    }
}
