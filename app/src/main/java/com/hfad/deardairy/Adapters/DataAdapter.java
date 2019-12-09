package com.hfad.deardairy.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.hfad.deardairy.Db.Models.DataWithTitle;
import com.hfad.deardairy.R;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DateDatasViewHolder> {
    private final LayoutInflater layoutInflater;
    private List<DataWithTitle> dataList;
    private onItemClickListener mListener;

    public interface onItemClickListener {
        void onItemClick(String title);
        void onDeleteClick(String dateit, String title);
        void onCopyClick(String text);
    }

    public void setOnItemClickListener(DataAdapter.onItemClickListener listener) {mListener = listener;}

    class DateDatasViewHolder extends RecyclerView.ViewHolder{
        private TextView titleView = itemView.findViewById(R.id.title_for_data);
        private TextView dateView = itemView.findViewById(R.id.date_title);
        private TextView dairyData = itemView.findViewById(R.id.dairy_data_for_date);

        private DateDatasViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);
            MaterialCardView cardDataDate = itemView.findViewById(R.id.date_data_card);
            ImageView deleteImage = itemView.findViewById(R.id.image_delete);
            ImageView copyImage = itemView.findViewById(R.id.image_copy);
            cardDataDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        String titleName = titleView.getText().toString();
                        if(titleName != null) {
                            listener.onItemClick(titleName);
                        }
                    }
                }
            });
            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        String dateName = dateView.getText().toString();
                        String titleName = titleView.getText().toString();
                        if(dateName != null) {
                            listener.onDeleteClick(dateName, titleName);
                        };
                    }
                }
            });
            copyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        String text = dairyData.getText().toString();
                        if(text != null) {
                            listener.onCopyClick(text);
                        }
                    }
                }
            });
        }
    }

    public DataAdapter(Context context) { layoutInflater = LayoutInflater.from(context);}

    @NonNull
    @Override
    public DateDatasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.data_view, parent, false);
        return new DateDatasViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DateDatasViewHolder holder, int position) {
        if (dataList != null) {
            String titleName = dataList.get(position).titles.get(0).getName();
            holder.titleView.setText(titleName);

            String dateName = dataList.get(position).data.getDate();
            holder.dateView.setText(dateName);

            String dairyText = dataList.get(position).data.getText();
            holder.dairyData.setText(dairyText);
        }
    }

    public void setDatas(List<DataWithTitle> datas) {
        dataList = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(dataList != null) {
            return dataList.size();
        } else return 0;
    }
}
