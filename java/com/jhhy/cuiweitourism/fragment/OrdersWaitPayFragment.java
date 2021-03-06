package com.jhhy.cuiweitourism.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jhhy.cuiweitourism.ArgumentOnClick;
import com.jhhy.cuiweitourism.R;
import com.jhhy.cuiweitourism.adapter.OrderXListViewAdapter;
import com.jhhy.cuiweitourism.biz.OrderActionBiz;
import com.jhhy.cuiweitourism.biz.OrdersAllBiz;
import com.jhhy.cuiweitourism.moudle.Order;
import com.jhhy.cuiweitourism.ui.SelectPaymentActivity;
import com.jhhy.cuiweitourism.ui.Tab4OrderDetailsActivity;
import com.jhhy.cuiweitourism.net.utils.Consts;
import com.jhhy.cuiweitourism.net.utils.LogUtil;
import com.jhhy.cuiweitourism.utils.ToastUtil;
import com.just.sun.pricecalendar.ToastCommon;

import java.util.ArrayList;
import java.util.List;

public class OrdersWaitPayFragment extends Fragment implements ArgumentOnClick {

    private String TAG = getClass().getSimpleName();
    private static final String TITLE = "title";
    private static final String TYPE = "type";

    private String title;
    private String type;

//    private XListView xListView;
    private PullToRefreshListView pullListView;
    private ListView listView;

    private List<Order> lists = new ArrayList<>();
    private OrderXListViewAdapter adapter;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case Consts.MESSAGE_ORDERS_WAIT_PAY:
                    if (msg.arg1 == 0){
                        ToastUtil.show(getContext(), "获取待付款订单失败");
                        LogUtil.e(TAG, "msg.arg1 = 0");
                    } else{
                        LogUtil.e(TAG, "msg.arg1 = 1");
                        List<Order> listWaitPay = (List<Order>) msg.obj;
                        if (listWaitPay == null || listWaitPay.size() == 0){
                            ToastUtil.show(getContext(), "待付款订单为空");
                        }else{
                            lists = listWaitPay;
                            adapter.setData(lists);
                                                    }
                    }
                    break;
                case Consts.MESSAGE_ORDER_CANCEL: //取消订单
                    ToastCommon.toastShortShow(getContext(), null, String.valueOf(msg.obj));
                    if (msg.arg1 == 1){
                        getData(type);
                    }
                    break;
            }
        }
    };
    public OrdersWaitPayFragment() {
        // Required empty public constructor
    }



    public static OrdersWaitPayFragment newInstance(String title, String type) {
        OrdersWaitPayFragment fragment = new OrdersWaitPayFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
            type = getArguments().getString(TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders_wait_pay, container, false);
        getInternetData();
        setupView(view);
        addListener();
        return view;
    }

    private void getInternetData() {
        OrdersAllBiz biz = new OrdersAllBiz(getContext(), handler);
        biz.getWaitPayment("1", type); //MainActivity.user.getUserId()
    }

    public void getData(String type) {
        this.type = type;
        getInternetData();
        lists.clear();
        adapter.setData(lists);
    }

    private void addListener() {
        pullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (PullToRefreshBase.Mode.PULL_FROM_START.equals( pullListView.getCurrentMode())){ //下拉刷新
//                    mListItems.addFirst(result); //在头部增加新添内容
//                    mAdapter.notifyDataSetChanged();//通知程序数据集已经改变，如果不做通知，那么将不会刷新mListItems的集合
                    // Call onRefreshComplete when the list has been refreshed.
                    pullListView.onRefreshComplete();
                } else { //上拉加载
                    pullListView.onRefreshComplete();
                }
            }
        });
    }

    private void setupView(View view) {
//        xListView = (XListView) view.findViewById(R.id.listView_wait);
        pullListView = (PullToRefreshListView) view.findViewById(R.id.listView_wait);
        pullListView.getLoadingLayoutProxy().setLastUpdatedLabel("lastUpdateLabel");
        pullListView.getLoadingLayoutProxy().setPullLabel("PULLLABLE");
        pullListView.getLoadingLayoutProxy().setRefreshingLabel("refreshingLabel");
        pullListView.getLoadingLayoutProxy().setReleaseLabel("releaseLabel");
        pullListView.setMode(PullToRefreshBase.Mode.BOTH);

        listView = pullListView.getRefreshableView();

        adapter = new OrderXListViewAdapter(getContext(), lists, this) {
            /**
             * 订单列表item单击事件
             * @param position 此item位于列表中的位置
             */
            @Override
            public void onOrderItemClick(int position) {
                Order order = lists.get(position);
                Intent intent = new Intent(getContext(), Tab4OrderDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("orderSN", order.getOrderSN());
                bundle.putInt("type", Integer.parseInt(order.getStatus()));
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CODE_ORDER_DETAIL);
            }
        };
        pullListView.setAdapter(adapter);
    }

    private int REQUEST_CODE_ORDER_DETAIL = 1501; //订单详情
    private int REQUEST_CODE_PAY = 1502; //签约付款

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e(TAG, "============ onActivityResult ============== ");
        if (requestCode == REQUEST_CODE_ORDER_DETAIL){ //订单详情——立即付款
            if (resultCode == Activity.RESULT_OK){
                getData(type);
            }
        } else if (requestCode == REQUEST_CODE_PAY){ //签约付款
            if (resultCode == Activity.RESULT_OK){
                getData(type);
            }
        }

    }

    /**
     * @param view      layout布局
     * @param viewGroup
     * @param position  列表中的位置
     * @param which     控件的id
     */
    @Override
    public void goToArgument(View view, View viewGroup, int position, int which) {
        switch (which){
            case R.id.btn_tab3_item_sign_contact: //签约付款
                ToastUtil.show(getContext(), "签约付款");
                Intent intent = new Intent(getContext(), SelectPaymentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", lists.get(position));
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CODE_PAY);
                break;
            case R.id.btn_order_cancel: //取消订单
                OrderActionBiz biz = new OrderActionBiz(getContext(), handler);
                biz.requestCancelOrder(lists.get(position).getOrderSN());
                break;
        }
    }

}
