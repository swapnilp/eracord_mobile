package com.example.vidhiraj.sample;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 12/11/2016.
 */

public class OffClassesAdapter extends RecyclerView.Adapter<OffClassesAdapter.ViewHolder>{

    private List<OffClassesData> timeTableDatas;
    Context context;

    public OffClassesAdapter(Context context,List<OffClassesData> timeTableDatas){
        this.context=context;
        this.timeTableDatas=timeTableDatas;
    }

    @Override
    public OffClassesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType){
        View itemLayoutView= LayoutInflater.from(parent.getContext()).inflate(
                R.layout.off_class_row,null);
        OffClassesAdapter.ViewHolder viewHolder=new OffClassesAdapter.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OffClassesAdapter.ViewHolder viewHolder, final int position){

        final int pos=position;
        viewHolder.name.setText(timeTableDatas.get(position).getName());
        viewHolder.off_date.setText(timeTableDatas.get(position).getDate());
        viewHolder.teacher_name.setText(timeTableDatas.get(position).getTeacher_name());

    }
    @Override
    public int getItemCount(){
        return timeTableDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView off_date;
        public TextView teacher_name;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            name = (TextView) itemLayoutView.findViewById(R.id.name);
            off_date = (TextView) itemLayoutView.findViewById(R.id.date);
            teacher_name = (TextView) itemLayoutView.findViewById(R.id.teacher_name);
        }

    }
    public List<OffClassesData> getStudentist() {
        return timeTableDatas;
    }

}
