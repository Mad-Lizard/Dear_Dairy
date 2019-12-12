package com.hfad.deardairy.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hfad.deardairy.Adapters.DataAdapter;
import com.hfad.deardairy.Dialogs.DataDeleteDialog;
import com.hfad.deardairy.Db.ViewModels.DataViewModel;
import com.hfad.deardairy.Db.Models.DataWithTitle;
import com.hfad.deardairy.Dialogs.DeleteDialogCall;
import com.hfad.deardairy.R;
import com.hfad.deardairy.Db.ViewModels.TitleViewModel;

import java.util.List;

public class DataActivity extends AppCompatActivity {
    private String dateTitle;
    private String titleName;
    private int titleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().getExtras().get("date") != null) {
            dateTitle = (String) getIntent().getExtras().get("date");
        }
        if(getIntent().getExtras().get("title") != null) {
            titleName = (String)getIntent().getExtras().get("title");
            TitleViewModel titleViewModel = ViewModelProviders.of(this).get(TitleViewModel.class);
            titleId = titleViewModel.getTitleId(titleName);
            Log.v("intent", String.valueOf(titleId));
        }
        buildRecyclerView();
    }

    public void buildRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.date_data_recycler);
        final DataAdapter adapter = new DataAdapter(this);
        if(dateTitle != null) {
            DataViewModel mDataViewModel = ViewModelProviders.of(this).get(DataViewModel.class);
            mDataViewModel.getDateDatasWithTitle(dateTitle).observe(this, new Observer<List<DataWithTitle>>() {
                @Override
                public void onChanged(List<DataWithTitle> dataWithTitles) {
                    adapter.setDatas(dataWithTitles);
                    if (adapter.getItemCount() == 0) { setNotification(); }
                }
            });
        }
        if(titleName != null) {
            DataViewModel mDataViewModel = ViewModelProviders.of(this).get(DataViewModel.class);
            mDataViewModel.getDatasWithTitle(titleId).observe(this, new Observer<List<DataWithTitle>>() {
                @Override
                public void onChanged(List<DataWithTitle> dataWithTitles) {
                    adapter.setDatas(dataWithTitles);
                    if (adapter.getItemCount() == 0) { setNotification(); }
                }
            });
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(new DataAdapter.onItemClickListener() {

            @Override
            public void onItemClick(String title, String date) {
                Intent intent = new Intent(getApplicationContext(), DairyActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("title", title);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(String date, String title) {
                TitleViewModel titleViewModel = new TitleViewModel(getApplication());
                int titleId = titleViewModel.getTitleId(title);
                DataDeleteDialog dialog = new DeleteDialogCall().OpenDeleteDialog(titleId, date);
                dialog.show(getSupportFragmentManager(), "delete_dialog");
            }

            @Override
            public void onCopyClick(String text) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("dairy_text", text);
                clipboard.setPrimaryClip(clip);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Текст скопирован в буфер обмена",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void setNotification() {
            Toast toast = Toast.makeText(this,
                    "Записи отсутствуют",
                    Toast.LENGTH_SHORT);
            toast.show();
    }
}
