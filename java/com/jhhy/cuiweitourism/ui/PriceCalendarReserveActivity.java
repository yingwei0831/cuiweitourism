package com.jhhy.cuiweitourism.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhhy.cuiweitourism.R;
import com.jhhy.cuiweitourism.biz.CalendarLineBiz;
import com.jhhy.cuiweitourism.moudle.TravelDetail;
import com.jhhy.cuiweitourism.net.utils.Consts;
import com.jhhy.cuiweitourism.utils.LoadingIndicator;
import com.jhhy.cuiweitourism.net.utils.LogUtil;
import com.jhhy.cuiweitourism.utils.ToastUtil;
import com.just.sun.pricecalendar.GroupDeadline;
import com.just.sun.pricecalendar.KCalendar;
import com.just.sun.pricecalendar.ToastCommon;

import java.util.ArrayList;
import java.util.List;

public class PriceCalendarReserveActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = PriceCalendarReserveActivity.class.getSimpleName();

    private String id; //旅游详情id，获取日历id
    private TravelDetail detail; //旅游详情

    //    private List<GroupDeadline> calendarPrices;
    private List<GroupDeadline> calendarPricesNew = new ArrayList<>(); //价格日历集合
    private String date = null;// 设置默认选中的日期  格式为 “2015-09-18” 标准DATE格式

    private KCalendar calendar;

    private TextView tvCurrentDate; //当前的年月
    private ImageView ivLastMonth; //上个月
    private ImageView ivNextMonth; //下个月

    private ImageView ivReduceAdult; //成人减少
    private ImageView ivPlusAdult;   //成人增加
    private ImageView ivReduceChild; //儿童减少
    private ImageView ivPlusChild;   //儿童增加

    private TextView tvCountAdult; //成人数量
    private TextView tvCountChild; //儿童数量
    private int countAdult = 1;
    private int countChild;

    private Button btnReserve; //下一步，填写订单

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 0){
                ToastUtil.show(getApplicationContext(), (String) msg.obj);
            }else {
                switch (msg.what) {
                    case Consts.MESSAGE_CALENDAR_PRICE:
                        List[] ary = (List[]) msg.obj;

                        List<GroupDeadline> calendarPrices = ary[0];
                        calendarPricesNew = ary[1];

                        if (calendarPricesNew != null && calendarPricesNew.size() != 0) {
                            refreshView();
                        } else {
                            ToastUtil.show(getApplicationContext(), "价格日历获取为空");
                        }
                        break;
                }
                LoadingIndicator.cancel();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_calendar_reserve);
        getData();
        setupView();
        addListener();

    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        detail = (TravelDetail) bundle.getSerializable("detail");

        LoadingIndicator.show(PriceCalendarReserveActivity.this, getString(R.string.http_notice));
        CalendarLineBiz biz = new CalendarLineBiz(getApplicationContext(), handler);
        biz.getCalendarLine("14");
    }

    private void setupView() {
        calendar = (KCalendar) findViewById(R.id.calendar);
        tvCurrentDate = (TextView) findViewById(R.id.tv_calendar_month);
        ivLastMonth = (ImageView) findViewById(R.id.imgv_calendar_last_month);
        ivNextMonth = (ImageView) findViewById(R.id.imgv_calendar_next_month);
        ivReduceChild   = (ImageView) findViewById(R.id.tv_price_calendar_number_reduce     );
        ivPlusChild     = (ImageView) findViewById(R.id.tv_price_calendar_number_add        );
        ivReduceAdult   = (ImageView) findViewById(R.id.tv_price_calendar_number_reduce_child);
        ivPlusAdult     = (ImageView) findViewById(R.id.tv_price_calendar_number_add_child  );

        tvCountAdult = (TextView) findViewById(R.id.tv_price_calendar_number);
        tvCountChild = (TextView) findViewById(R.id.tv_price_calendar_number_child);
        tvCountAdult.setText(String.valueOf(countAdult));
        tvCountChild.setText(String.valueOf(countChild));
        btnReserve = (Button) findViewById(R.id.btn_price_calendar_reserve);
    }

    private void addListener() {
        ivLastMonth.setOnClickListener(this);
        ivNextMonth.setOnClickListener(this);
        ivReduceChild.setOnClickListener(this);
        ivPlusChild  .setOnClickListener(this);
        ivReduceAdult.setOnClickListener(this);
        ivPlusAdult  .setOnClickListener(this);
        btnReserve  .setOnClickListener(this);
    }
    private int dateYear; //第一天
    private int dateMonth; //第一天

//    private int priceAdult; //选择的成人价格
//    private int priceChild; //选择的儿童价格
    private GroupDeadline selectGroupDeadLine; //选择某天的价格日历

    private boolean selectDay;

    private void refreshView() {
        //设置团期
        calendar.setGroups(calendarPricesNew);
        calendar.showCalendar();
        if (TextUtils.isEmpty(date) && calendarPricesNew.size() > 0) {
            date = calendarPricesNew.get(0).getDate();//获取第一天的
            dateYear = Integer.parseInt(date.substring(0, date.indexOf("-")));
            dateMonth = Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")));
            tvCurrentDate.setText(dateYear + "年" + dateMonth + "月");
            calendar.showCalendar(dateYear, dateMonth);//设置初始日子
        }

        //监听所选中的日期
        calendar.setOnCalendarClickListener(new KCalendar.OnCalendarClickListener() {

            public void onCalendarClick(int row, int col, String dateFormat) {

                int month = Integer.parseInt(dateFormat.substring(
                        dateFormat.indexOf("-") + 1,
                        dateFormat.lastIndexOf("-")));

                if (calendar.getCalendarMonth() - month == 1//跨年跳转
                        || calendar.getCalendarMonth() - month == -11) {
                    calendar.lastMonth();

                } else if (month - calendar.getCalendarMonth() == 1 //跨年跳转
                        || month - calendar.getCalendarMonth() == -11) {
                    calendar.nextMonth();

                } else {
                    selectDay = true;
                    //date = dateFormat; //最后返回给全局 date
                    for (int i = 0; i < calendarPricesNew.size(); i++) {
                        String positionDate = calendarPricesNew.get(i).getDate();

//                        int stock = Integer.parseInt(calendarPrices.get(i).getStock()); //因为没有人数限制
//                        LogUtil.e("SHF", "dateFormat--->" + dateFormat + ", date--->" + calendarPrices.get(i).getDate()); // + "peopleNumCur--->" + "stock--->" + stock);
                        LogUtil.e("SHF", "dateFormat--->" + dateFormat + ", positionDate--->" + positionDate +", date = " + date); // + "peopleNumCur--->" + "stock--->" + stock);
                        if (dateFormat.equals(positionDate) ){ //当前点击日期的背景色
//                                && (stock) > 0) {
                            //设置背景色
                            calendar.removeAllBgColor();
                            calendar.setCalendarDayBgColor(dateFormat, ContextCompat.getColor(getApplicationContext(), R.color.colorCalendarSelection)); //Color.parseColor("#45BDEF")
//                            priceAdult = Integer.parseInt(calendarPricesNew.get(i).getSell_price_adult());
//                            priceChild = Integer.parseInt(calendarPricesNew.get(i).getSell_price_children());
                            selectGroupDeadLine = calendarPricesNew.get(i);
                        } else if (date.equals(positionDate)){ //如果是选择今天的日期
//                                && (stock) > 0) {
//                            ToastCommon.toastShortShow(getApplicationContext(), null, "此团期剩余空位不足，请选择其他团期或减少参团人数");
                        }
                    }

                }
            }
        });
        calendar.setOnCalendarDateChangedListener(new KCalendar.OnCalendarDateChangedListener() {
            @Override
            public void onCalendarDateChanged(int year, int month) {
                tvCurrentDate.setText(year + "年" + month + "月");
            }
        });
    }

    public static void actionStart(Context context, Bundle bundle){
        Intent intent = new Intent(context, PriceCalendarReserveActivity.class);
        if(bundle != null){
            intent.putExtras(bundle);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgv_calendar_last_month: //上个月
                calendar.lastMonth();
                break;
            case R.id.imgv_calendar_next_month: //下个月
                calendar.nextMonth();
                break;
            case R.id.tv_price_calendar_number_reduce:
                if (countAdult > 1) {
                    countAdult -= 1;
                    tvCountAdult.setText(Integer.toString(countAdult));
                }
                break;
            case R.id.tv_price_calendar_number_add:
                countAdult += 1;
                tvCountAdult.setText(Integer.toString(countAdult));
                break;
            case R.id.tv_price_calendar_number_reduce_child:
                if (countChild > 0) {
                    countChild -= 1;
                    tvCountChild.setText(Integer.toString(countChild));
                }
                break;
            case R.id.tv_price_calendar_number_add_child:
                countChild += 1;
                tvCountChild.setText(Integer.toString(countChild));
                break;
            case R.id.btn_price_calendar_reserve:
                if (selectDay) {
                    Intent intent = new Intent(getApplicationContext(), InnerTravelEditOrderActivity.class);
                    Bundle bundle = new Bundle();
//                    bundle.putInt("priceAdult", priceAdult);
//                    bundle.putInt("priceChild", priceChild);
                    bundle.putInt("countAdult", countAdult);
                    bundle.putInt("countChild", countChild);
                    bundle.putSerializable("detail", detail);
                    bundle.putSerializable("priceCalendar", selectGroupDeadLine);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, Consts.REQUEST_CODE_RESERVE_EDIT_ORDER);
                }else{
                    ToastCommon.toastShortShow(getApplicationContext(), null, "请选择出发日期");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED){

        }else{
            if (requestCode == Consts.REQUEST_CODE_RESERVE_EDIT_ORDER){ //填写订单

            }
        }
    }
}
