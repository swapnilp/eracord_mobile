package com.eracordapp.teacher.sample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class PresentyAdapter extends RecyclerView.Adapter<PresentyAdapter.ViewHolder> {
    private List<PresentyData> stList;
    Context context;
    TextView totalpresent, totalabsent;

    public PresentyAdapter(Context context, List<PresentyData> students, TextView totalPresent, TextView totalAbsent) {
        this.context = context;
        this.stList = students;
        this.totalpresent = totalPresent;
        this.totalabsent = totalAbsent;
    }

    @Override
    public PresentyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.presenty_catalog_row, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        final int pos = position;
        viewHolder.tvName.setText(stList.get(position).getName());
        viewHolder.chkSelected.setChecked(stList.get(position).isSelected());

        viewHolder.chkSelected.setTag(stList.get(position));
        viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                PresentyData contact = (PresentyData) cb.getTag();
                contact.setSelected(cb.isChecked());
                stList.get(pos).setSelected(cb.isChecked());

                int present = Integer.parseInt(totalpresent.getText().toString());
                int absent = Integer.parseInt(totalabsent.getText().toString());
                if(cb.isChecked()) {
                    totalpresent.setText(Integer.toString(present+1));
                    totalabsent.setText(Integer.toString(absent-1));
                } else {
                    totalpresent.setText(Integer.toString(present-1));
                    totalabsent.setText(Integer.toString(absent+1));
                }
            }
        });

    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return stList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvEmailId;
        public CheckBox chkSelected;
        public PointsData singlestudent;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            tvName = (TextView) itemLayoutView.findViewById(R.id.tvName);
            chkSelected = (CheckBox) itemLayoutView.findViewById(R.id.pr_chkSelected);
        }

    }
    // method to access in activity after updating selection
    public List<PresentyData> getStudentist() {
        return stList;
    }

}