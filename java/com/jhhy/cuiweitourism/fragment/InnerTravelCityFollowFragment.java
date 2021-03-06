package com.jhhy.cuiweitourism.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jhhy.cuiweitourism.ArgumentOnClick;
import com.jhhy.cuiweitourism.R;
import com.jhhy.cuiweitourism.adapter.Tab1InnerTravelListViewAdapter;
import com.jhhy.cuiweitourism.biz.InnerTravelCityListBiz;
import com.jhhy.cuiweitourism.moudle.Travel;
import com.jhhy.cuiweitourism.ui.InnerTravelCityActivity;
import com.jhhy.cuiweitourism.ui.InnerTravelDetailActivity;
import com.jhhy.cuiweitourism.net.utils.Consts;
import com.jhhy.cuiweitourism.net.utils.LogUtil;
import com.jhhy.cuiweitourism.utils.ToastUtil;
import com.jhhy.cuiweitourism.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InnerTravelCityFollowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InnerTravelCityFollowFragment extends Fragment implements AdapterView.OnItemClickListener, ArgumentOnClick {

    private final String TAG = InnerTravelCityFollowFragment.class.getSimpleName();
    private static final String BUNDLE_TITLE = "title"; //顶部tab
    private static final String BUNDLE_CITY_ID = "cityId"; //顶部tab
    private String title;
    private String cityId;
    private int attr = 1; //跟团游

    private List<Travel> list = new ArrayList<>();

    private PullToRefreshListView listView;
    private ListView refreshView; //数据操作列表
    private Tab1InnerTravelListViewAdapter adapter;

    private int page = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 0){
                ToastUtil.show(getContext(), (String) msg.obj);
                return;
            }
            switch (msg.what){
                case Consts.MESSAGE_INNER_CITY_LIST_FOLLOW:
                    List<Travel> listFollow = (List<Travel>) msg.obj;
                    if (listFollow == null || listFollow.size() == 0){
                        ToastUtil.show(getContext(), "当前城市没有即将进行的跟团旅游项目");
                        return;
                    }
                    list = listFollow;
                    adapter.setData(listFollow);
                    break;
            }
        }
    };

    public InnerTravelCityFollowFragment() {
        // Required empty public constructor
    }

    public static InnerTravelCityFollowFragment newInstance(String title, String cityId){
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);
        bundle.putString(BUNDLE_CITY_ID, cityId);

        InnerTravelCityFollowFragment fragment = new InnerTravelCityFollowFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(BUNDLE_TITLE);
            cityId = getArguments().getString(BUNDLE_CITY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_listview, container, false);
        setupView(view);
        addListener();
        getData();
        return view;
    }

    private void getData() {
        InnerTravelCityListBiz biz = new InnerTravelCityListBiz(getContext(), handler, Consts.MESSAGE_INNER_CITY_LIST_FOLLOW);
        biz.getCityList(cityId, InnerTravelCityActivity.sort, InnerTravelCityActivity.day, InnerTravelCityActivity.price,
                InnerTravelCityActivity.earlyTime, InnerTravelCityActivity.laterTime, String.valueOf(page), String.valueOf(attr));
    }

    private void setupView(View view) {
        listView = (PullToRefreshListView) view.findViewById(R.id.listView);
        listView.getLoadingLayoutProxy().setLastUpdatedLabel(Utils.getCurrentTime());
        listView.getLoadingLayoutProxy().setPullLabel("下拉刷新");
        listView.getLoadingLayoutProxy().setRefreshingLabel("refreshingLabel");
        listView.getLoadingLayoutProxy().setReleaseLabel("松开加载更多");

        refreshView = listView.getRefreshableView();
        adapter = new Tab1InnerTravelListViewAdapter(getContext(), list, this);
        refreshView.setAdapter(adapter);
    }

    private void addListener() {
        listView.setOnItemClickListener(this);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refrehsh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMore();
            }
        });

    }


    private void loadMore() {
        listView.onRefreshComplete();
    }

    private void refrehsh() {
        listView.onRefreshComplete();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if(id < 0)  return; //此处点击的是Header或Footer
        ToastUtil.show(getContext(), "~~跟团游，详情~~");

        Travel travel = list.get((int) id);
        LogUtil.i(TAG, "position = " + position + ", id = " + id +", " + travel.getId()); //1,0  2,1

        Bundle bundle = new Bundle();
        bundle.putString("id", travel.getId());
        InnerTravelDetailActivity.actionStart(getContext(), bundle);
    }

    /**
     * 讨价还价
     * @param view
     * @param viewGroup
     * @param position
     * @param which
     */
    @Override
    public void goToArgument(View view, View viewGroup, int position, int which) {
        ToastUtil.show(getContext(), "进入讨价还价");
    }
}
