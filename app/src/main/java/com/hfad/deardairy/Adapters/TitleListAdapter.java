package com.hfad.deardairy.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.deardairy.Db.Models.TitleModel;
import com.hfad.deardairy.R;

import java.util.List;

public class TitleListAdapter extends RecyclerView.Adapter<TitleListAdapter.TitleViewHolder> {
    private onItemClickListener mListener;
    private final LayoutInflater layoutInflater;
    private List<TitleModel> titleList;

    public interface onItemClickListener {

        void onItemClick(String title);
        void onDeleteClick(String title);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mListener = listener;
    }

    class TitleViewHolder extends RecyclerView.ViewHolder{
        private final TextView titleItemView = itemView.findViewById(R.id.textView);;
        //private ImageView deleteImage;

        private TitleViewHolder(final View itemView, final onItemClickListener listener) {
            super(itemView);

            ImageView deleteImage = itemView.findViewById(R.id.image_delete);

            titleItemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        String title = titleItemView.getText().toString();
                        if(title != null){
                            listener.onItemClick(title);
                        }
                    }
                }
            });
            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        String title = titleItemView.getText().toString();
                        if(title != null){
                            listener.onDeleteClick(title);
                        }
                    }
                }
            });
        }
    }

    public TitleListAdapter(Context context) {layoutInflater = LayoutInflater.from(context);}

    @NonNull
    @Override
    public TitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.title_view, parent, false);
        return new TitleViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TitleViewHolder holder, int position) {
        if(titleList != null) {
            TitleModel current = titleList.get(position);
            String text = current.getName();
            holder.titleItemView.setText(text);
        } else {
            holder.titleItemView.setText("Нет заголовков");
        }
    }

    public void setTitles(List<TitleModel> titles) {
        titleList = titles;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(titleList != null) {
            return titleList.size();
        } else return 0;
    }
}
