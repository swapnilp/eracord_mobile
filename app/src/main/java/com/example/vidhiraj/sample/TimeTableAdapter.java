package com.example.vidhiraj.sample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Lenovo on 11/30/2016.
 */

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.ViewHolder>{

    private List<TimeTableData> timeTableDatas;
        Context context;

public TimeTableAdapter(List<TimeTableData> timeTableDatas){
        this.context=context;
        this.timeTableDatas=timeTableDatas;
        }

@Override
public ViewHolder onCreateViewHolder(ViewGroup parent,
        int viewType){
        View itemLayoutView= LayoutInflater.from(parent.getContext()).inflate(
        R.layout.timetable_row, parent, false);
        ViewHolder viewHolder=new ViewHolder(itemLayoutView);
        return viewHolder;
        }

@Override
public void onBindViewHolder(ViewHolder viewHolder,final int position){

final int pos=position;
        viewHolder.className.setText(timeTableDatas.get(position).getClassName());
    viewHolder.startTime.setText(timeTableDatas.get(position).getStartTime());
    viewHolder.endTime.setText(timeTableDatas.get(position).getEndTime());
    viewHolder.classDivision.setText(timeTableDatas.get(position).getClassDivision());
    viewHolder.subject.setText(timeTableDatas.get(position).getSubject());

        }

// Return the size arraylist
@Override
public int getItemCount(){
        return timeTableDatas.size();
        }

public static class ViewHolder extends RecyclerView.ViewHolder {

    public TextView className;
    public TextView startTime;
    public TextView endTime;
    public TextView classDivision;
    public TextView subject;

    public ViewHolder(View itemLayoutView) {
        super(itemLayoutView);

        className = (TextView) itemLayoutView.findViewById(R.id.name);
        startTime = (TextView) itemLayoutView.findViewById(R.id.start_time);
        endTime = (TextView) itemLayoutView.findViewById(R.id.end_time);
        classDivision = (TextView) itemLayoutView.findViewById(R.id.class_div);
        subject = (TextView) itemLayoutView.findViewById(R.id.subject);

    }

}

    // method to access in activity after updating selection
    public List<TimeTableData> getStudentist() {
        return timeTableDatas;
    }

}
