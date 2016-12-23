package com.example.vidhiraj.sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by vidhiraj on 10-08-2016.
 */
public class EraMyAdapter extends RecyclerView.Adapter<EraMyAdapter.ViewHolder> {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_ITEM = 1;
    private static String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
    private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java
    private String email, org_t_name;
    String url_icon;
    static Context context;
    static int drawer_selected_menu_pos = 0;

    static String colorWhite = "#ffffff";
    static String textcolor = "#000000";
    static String colorAccent = "#337ab7";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;
        TextView textView;
        ImageView profile, menu_icon;
        TextView email, org_t_name;
        LinearLayout drawer_selected;

        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            if (ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
                menu_icon = (ImageView) itemView.findViewById(R.id.menu_icon);
                drawer_selected=(LinearLayout) itemView.findViewById(R.id.drawer_items);
                Holderid = 1;

                drawer_selected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        drawer_selected_menu_pos = pos;
                        Intent intent1;
                        if (pos == 1) {
                            textView.setTextColor(context.getResources().getColor(R.color.bb_darkBackgroundColor));
                            intent1 = new Intent(context, ClassActivity.class);
                            context.startActivity(intent1);
                            ((Activity)(context)).finish();
                        } else if (pos == 2) {
                            textView.setTextColor(context.getResources().getColor(R.color.bb_darkBackgroundColor));
                            intent1 = new Intent(context, DailyCatalogActivity.class);
                            context.startActivity(intent1);
                            ((Activity)(context)).finish();
                        } else if (pos == 3) {
                            textView.setTextColor(context.getResources().getColor(R.color.bb_darkBackgroundColor));
                            intent1 = new Intent(context, StudentListActivity.class);
                            context.startActivity(intent1);
                            ((Activity)(context)).finish();
                         } else if(pos == 4){
                            textView.setTextColor(context.getResources().getColor(R.color.bb_darkBackgroundColor));
                            intent1 = new Intent(context, TimeTableActivity.class);
                            context.startActivity(intent1);
                            ((Activity)(context)).finish();
                        } else if(pos == 5) {
                            textView.setTextColor(context.getResources().getColor(R.color.bb_darkBackgroundColor));
                            intent1 = new Intent(context, OffClassesActivity.class);
                            context.startActivity(intent1);
                            ((Activity)(context)).finish();
                        } else if(pos == 6) {
                            textView.setTextColor(context.getResources().getColor(R.color.bb_darkBackgroundColor));
                            intent1 = new Intent(context, FeedbackActivity.class);
                            context.startActivity(intent1);
                            ((Activity)(context)).finish();
                        } else if(pos == 7) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            //shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Eracord App");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "Share eracord app link,  http://eracord.com");
                            context.startActivity(Intent.createChooser(shareIntent, "Share link using"));
                        } else if (pos == 8) {
                            textView.setTextColor(context.getResources().getColor(R.color.bb_darkBackgroundColor));
                            intent1 = new Intent(context, AndroidSpinnerExampleActivity.class);
                            context.startActivity(intent1);
                            ((Activity)(context)).finish();
                        }
                    }
                });
            } else {
                org_t_name = (TextView) itemView.findViewById(R.id.org_name);
                email = (TextView) itemView.findViewById(R.id.email);       // Creating Text View object from header.xml for email
                profile = (ImageView) itemView.findViewById(R.id.circleView);// Creating Image view object from header.xml for profile pic
                Holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }
        }
    }

    EraMyAdapter(Context context, String Titles[], int Icons[], String ORG_NAME, String Email, String url) { // MyAdapter Constructor with titles and icons parameter
        mNavTitles = Titles;                //have seen earlier
        mIcons = Icons;
        email = Email;
        org_t_name = ORG_NAME;
        url_icon = url;
        EraMyAdapter.context = context;
    }

    @Override
    public EraMyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.eracord_items, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
            return vhItem; // Returning the created object
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.eracord_header, parent, false); //Inflating the layout
            ViewHolder vhHeader = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
            return vhHeader; //returning the object created
        }
        return null;
    }

    @Override
    public void onBindViewHolder(EraMyAdapter.ViewHolder holder, int position) {
        if (holder.Holderid == 1) {                              // as the list view is going to be called after the header view so we decrement the
            try {
                String menu = mNavTitles[position - 1];
                menu = menu.replaceAll("\\s+","").toLowerCase();
                String uri = "@drawable/" + menu;  // where myresource (without the extension) is the file
                int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
                Drawable res = context.getResources().getDrawable(imageResource);
                holder.menu_icon.setImageDrawable(res);

            } catch (Exception e) {
                //Catch here
            }
            holder.textView.setText(mNavTitles[position - 1]); // Setting the Text with the array of our Titles
            holder.textView.setTextColor(Color.parseColor(textcolor));
            if((drawer_selected_menu_pos != mNavTitles.length) && (drawer_selected_menu_pos == position)) {
                holder.drawer_selected.setBackgroundColor(Color.parseColor(colorAccent)); // normal flow except boundry condition
                holder.textView.setTextColor(Color.parseColor(colorWhite));
            } else if ((drawer_selected_menu_pos == mNavTitles.length) && (position == 1)) {
                holder.drawer_selected.setBackgroundColor(Color.parseColor(colorAccent)); // after logout and then login, this condition will trigger
                holder.textView.setTextColor(Color.parseColor(colorWhite));
            } else if ((drawer_selected_menu_pos == 0) && position == 1) {
                holder.drawer_selected.setBackgroundColor(Color.parseColor(colorAccent)); // First time selected item will be 0;
                holder.textView.setTextColor(Color.parseColor(colorWhite));
            }
        } else {
            Glide.with(context).load(url_icon)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.profile);
            holder.org_t_name.setText(org_t_name);
            holder.email.setText(email);
        }
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length + 1; // the number of items in the list will be +1 the titles including the header view.
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}

