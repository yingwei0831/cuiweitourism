package com.jhhy.cuiweitourism.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jhhy.cuiweitourism.R;
import com.jhhy.cuiweitourism.moudle.ClassifyArea;

import java.util.List;

/**
 * Created by jiahe008 on 2016/9/27.
 */
public class VisaConnection2ListAdapter extends MyBaseAdapter {

//    private int selection = 0;

    public VisaConnection2ListAdapter(Context ct, List list) {
        super(ct, list);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_visa_select_city, null);
            holder.tvName = (TextView) view.findViewById(R.id.tv_area_name);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        ClassifyArea area = (ClassifyArea) list.get(i);
        holder.tvName.setText(area.getAreaName());
//        if (selection == i){
//            holder.tvName.setBackgroundColor(Color.WHITE);
//        }else{
//            holder.tvName.setBackgroundColor(context.getResources().getColor(R.color.colorInnerTravelPopLeftBg));
//        }
        return view;
    }

    public void setData(List<ClassifyArea> area){
        this.list = area;
        notifyDataSetChanged();
    }

//    public void setSelection(int selection){
//        this.selection = selection;
//    }

    class  ViewHolder{
        private TextView tvName;
    }
}
