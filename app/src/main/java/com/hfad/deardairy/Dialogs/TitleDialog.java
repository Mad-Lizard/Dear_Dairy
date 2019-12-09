package com.hfad.deardairy.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.hfad.deardairy.Db.Models.TitleModel;
import com.hfad.deardairy.R;
import com.hfad.deardairy.Db.ViewModels.TitleViewModel;

public class TitleDialog extends AppCompatDialogFragment {
    private EditText addTitle;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_title_dialog, null);
        final TitleViewModel titleViewModel = ViewModelProviders.of(this).get(TitleViewModel.class);

        builder.setView(view)
                .setTitle(R.string.add_title_dialog)
                .setPositiveButton(R.string.save_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(titleViewModel.getTitlesCount() < 8) {
                            TitleModel titleModel = new TitleModel();
                            String titleName = addTitle.getText().toString();
                            titleModel.setName(titleName);
                            titleViewModel.insert(titleModel);
                        } else {
                            TooManyTitlesDialog dialog = new TooManyTitlesDialog();
                            dialog.show(getFragmentManager(), "too many titiles");
                        }
                    }
                });
        addTitle = view.findViewById(R.id.edit_title);

        return builder.create();
    }
}
