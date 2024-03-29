package com.hfad.deardairy.Activities;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.ViewModelProviders;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hfad.deardairy.Db.Models.DataModel;
import com.hfad.deardairy.Db.ViewModels.DataViewModel;
import com.hfad.deardairy.Dropbox_access.DropboxActivity;
import com.hfad.deardairy.R;
import com.hfad.deardairy.Db.Models.TitleModel;
import com.hfad.deardairy.Db.ViewModels.TitleViewModel;

import java.util.Date;
import java.util.List;

public class DairyActivity extends DropboxActivity implements AdapterView.OnItemSelectedListener {

    private AppCompatSpinner titleSpinner;
    private EditText textInput;
    private int titleId;
    private String dateTitle;
    private Button saveButton;
    private List<String> titles;
    DataViewModel mDataViewModel;
    TitleViewModel mTitleViewModel;
    DataModel current;
    ClipboardManager clipboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy);
        mDataViewModel = ViewModelProviders.of(this).get(DataViewModel.class);
        mTitleViewModel = ViewModelProviders.of(this).get(TitleViewModel.class);
        textInput = findViewById(R.id.text_input_field);

        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ImageView copyImage = findViewById(R.id.copy_image);
        copyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = textInput.getText().toString();
                ClipData clip = ClipData.newPlainText("dairy_text", text);
                clipboard.setPrimaryClip(clip);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Текст скопирован в буфер обмена",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        ImageView pasteImage = findViewById(R.id.paste_image);
        pasteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                String pasteData = item.getText().toString();
                if(pasteData != null) {
                    textInput.setText(pasteData);
                } else {
                    String pasteUri = item.getUri().toString();
                    if(pasteUri != null) {
                        textInput.setText(pasteUri);
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Невозможно вставить данные из буфера обмена",
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
        try {
            dateTitle = getIntent().getExtras().get("date").toString();
        } catch (Exception e) {
            SharedPreferences preferences = getSharedPreferences("dropbox", MODE_PRIVATE);
            dateTitle = preferences.getString("date", null);
        }

        titleSpinner = findViewById(R.id.title_spinner);
        titleSpinner.setOnItemSelectedListener(this);
        loadTitleSpinner();

        if(getIntent().getExtras().get("title") != null) {
            String title = getIntent().getExtras().get("title").toString();
            int spinnerPosition = titles.indexOf(title);
            titleSpinner.setSelection(spinnerPosition);

        }

        TextView dateView = findViewById(R.id.date_title);
        dateView.setText(dateTitle);

        saveButton = findViewById(R.id.save_button);
    }

    private void loadTitleSpinner() {
        titles = mTitleViewModel.getTitleNames();
        if (titles.size() < 1) {
            titles = mTitleViewModel.getReservedTitle();
        }
        ArrayAdapter<String> titleAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                titles);
        titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        titleSpinner.setAdapter(titleAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String title = parent.getItemAtPosition(position).toString();
        TitleModel titleModel = mTitleViewModel.getTitle(title);
        titleId = titleModel.getTitleId();
        //set text to title if exists
        current = mDataViewModel.getByTitleAndDate(titleId, dateTitle);
        if(current != null) {
            String dairyText = current.getText();
            textInput.setText(dairyText);
            saveButton.setText("Обновить");
        } else {
            textInput.setText(null);
            saveButton.setText("Сохранить");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        String title = adapterView.getItemAtPosition(0).toString();
        TitleModel titleModel = mTitleViewModel.getTitle(title);
        titleId = titleModel.getTitleId();
    }

    public void onSaveClicked(View view) {
        if(current == null) {
            DataModel model = new DataModel();
            model.setTitle(titleId);
            model.setText(textInput.getText().toString());
            model.setDate(dateTitle);
            mDataViewModel.insert(model);
            Toast toast = Toast.makeText(this, "Запись сохранена", Toast.LENGTH_LONG);
            toast.show();
        } else {
            current.setText(textInput.getText().toString());
            mDataViewModel.update(current);
            Toast toast = Toast.makeText(this, "Запись обновлена", Toast.LENGTH_LONG);
            toast.show();
        }
        finish();
    }
}
