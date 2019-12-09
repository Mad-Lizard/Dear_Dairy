package com.hfad.deardairy.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hfad.deardairy.R;
import com.hfad.deardairy.Dialogs.TitleDeleteDialog;
import com.hfad.deardairy.Dialogs.TitleDialog;
import com.hfad.deardairy.Adapters.TitleListAdapter;
import com.hfad.deardairy.Db.Models.TitleModel;
import com.hfad.deardairy.Db.ViewModels.TitleViewModel;

import java.util.List;

public class TitleSelectionActivity extends AppCompatActivity {
    private FloatingActionButton floatingButton;
    private TitleViewModel mTitleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_selection);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        floatingButton = (FloatingActionButton)findViewById(R.id.add_title_button);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTitleDialog();
            }
        });

        buildRecyclerView();
    }

    public void buildRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.title_name_recycler);
        final TitleListAdapter adapter = new TitleListAdapter(this);

        mTitleViewModel = new TitleViewModel(getApplication());
        mTitleViewModel.getAllTitles().observe(this, new Observer<List<TitleModel>>(){
            @Override
            public void onChanged(final List<TitleModel> titleModels) {
                adapter.setTitles(titleModels);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(new TitleListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(String title) {
                Intent intent = new Intent(recyclerView.getContext(), DataActivity.class);
                intent.putExtra("title", title);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(String title) {
               openDeleteDialog(title);
            }
        });
    }

    public void openTitleDialog() {
        TitleDialog titleDialog = new TitleDialog();
        titleDialog.show(getSupportFragmentManager(), "title dialog");
    }

    public void openDeleteDialog(String title) {
        TitleDeleteDialog deleteDialog = new TitleDeleteDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        deleteDialog.setArguments(bundle);
        deleteDialog.show(getSupportFragmentManager(), "delete dialog");
    }
}
