package com.jhhy.cuiweitourism.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhhy.cuiweitourism.R;
import com.jhhy.cuiweitourism.moudle.Travel;

import org.w3c.dom.Text;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by jiahe008 on 2016/8/29.
 */
public class HotActivityListViewAdapter extends BaseAdapter {

    private Context context;
    private List<Travel> list;
    private LayoutInflater inflater;

    public HotActivityListViewAdapter(Context context, List<Travel> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        ViewHolder holder = null;
        if(view == null){
            view = inflater.inflate(R.layout.tab1_hot_activity_listview_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.iv_tab1_2_hot_activity);
            holder.tvTitle = (TextView)view.findViewById(R.id.tv_tab1_2_hot_activity_title);
            holder.tvPrice = (TextView) view.findViewById(R.id.tv_tab1_2_hot_activity_prirce);
            holder.tvAccount = (TextView) view.findViewById(R.id.tv_tab1_hot_activity_account);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        Travel travel = (Travel) getItem(i);
        if(travel != null){
            //TODO
            holder.tvPrice.setText(travel.getTravelPrice());
            holder.tvAccount.setText(travel.getAccount()+"");

        }
        return view;
    }

    class ViewHolder{
        private ImageView imageView;
        private TextView tvTitle;
        private TextView tvPrice;
        private TextView tvAccount;
    }
}
