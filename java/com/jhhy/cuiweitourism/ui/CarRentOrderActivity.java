package com.jhhy.cuiweitourism.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jhhy.cuiweitourism.R;
import com.jhhy.cuiweitourism.net.biz.CarRentActionBiz;
import com.jhhy.cuiweitourism.net.models.FetchModel.CarRentOrder;
import com.jhhy.cuiweitourism.net.models.ResponseModel.BasicResponseModel;
import com.jhhy.cuiweitourism.net.models.ResponseModel.CarRentOrderResponse;
import com.jhhy.cuiweitourism.net.models.ResponseModel.FetchError;
import com.jhhy.cuiweitourism.net.netcallback.BizCallback;
import com.jhhy.cuiweitourism.net.utils.LogUtil;
import com.just.sun.pricecalendar.ToastCommon;

public class CarRentOrderActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = CarRentOrderActivity.class.getSimpleName();

    private TextView tvTitle;
    private ImageView ivTitleLeft;

    private String rentTime;
    private String rentType;
    private String rentTypeText;
    private String rentDay;
    private String rentFromAddr;
    private String rentToAddr;

    private String rentPrice;
    private String rentDistance;

    private TextView tvRentType;
    private TextView tvRentDay;
    private TextView tvRentFromAddr;
    private TextView tvRentToAddr;
    private TextView tvRentTime;
    private TextView tvRentPrice;

    private EditText etLinkName;
    private EditText etLinkMobile;


    private Button btnNext;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        //获取ActionBar对象
//        ActionBar bar =  getSupportActionBar();
//        //自定义一个布局，并居中
//        bar.setDisplayShowCustomEnabled(true);
//        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.title_simple, null);
//        bar.setCustomView(v, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));

        setContentView(R.layout.activity_rent_car_order);
        getData();
        setupView();
        addListener();
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                rentTime = bundle.getString("rentTime");
                rentType = bundle.getString("rentType");
                rentTypeText = bundle.getString("rentTypeText");
                rentDay = bundle.getString("rentDay");
                rentFromAddr = bundle.getString("rentFromAddr");
                rentToAddr = bundle.getString("rentToAddr");
                rentPrice = bundle.getString("rentPrice");
                rentDistance = bundle.getString("rentDistance");
            }
        }
    }

    private void setupView() {
        tvTitle = (TextView) findViewById(R.id.tv_title_inner_travel);
        tvTitle.setText("租车");
        ivTitleLeft = (ImageView) findViewById(R.id.title_main_tv_left_location);

        tvRentType = (TextView) findViewById(R.id.tv_rent_type);
        tvRentDay = (TextView) findViewById(R.id.tv_rent_day);
        tvRentFromAddr = (TextView) findViewById(R.id.tv_rent_from_addr);
        tvRentToAddr = (TextView) findViewById(R.id.tv_rent_to_addr);
        tvRentTime = (TextView) findViewById(R.id.tv_rent_time);
        tvRentPrice = (TextView) findViewById(R.id.tv_rent_price);

        etLinkName = (EditText) findViewById(R.id.et_rent_link_name);
        etLinkMobile = (EditText) findViewById(R.id.et_rent_link_mobile);

        btnNext = (Button) findViewById(R.id.btn_car_rent_order_next);

        tvRentType.setText(rentType);
        tvRentDay.setText(rentDay+"天");
        tvRentFromAddr.setText(rentFromAddr);
        tvRentToAddr.setText(rentToAddr);
        tvRentTime.setText(rentTime);
        tvRentPrice.setText(rentPrice);
    }

    private void addListener() {
        ivTitleLeft.setOnClickListener(this);

        btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_main_tv_left_location:
                finish();
                break;
            case R.id.btn_car_rent_order_next:
                String linkName = etLinkName.getText().toString();
                String linkMobile = etLinkMobile.getText().toString();
                if (TextUtils.isEmpty(linkName) || TextUtils.isEmpty(linkMobile)){
                    ToastCommon.toastShortShow(getApplicationContext(), null, getString(R.string.empty_input));
                    return;
                }

                //租车，提交订单
                CarRentActionBiz carBiz = new CarRentActionBiz(getApplicationContext(), handler);
                CarRentOrder carOrder = new CarRentOrder(MainActivity.user.getUserId(), rentType, rentDay, rentFromAddr,
                        rentToAddr, rentTime, rentPrice, linkName, linkMobile, rentTypeText);
                carBiz.carRentSubmitOrder(carOrder, new BizCallback() {
                    @Override
                    public void onError(FetchError error) {
                        if(error.localReason != null){
                            ToastCommon.toastShortShow(getApplicationContext(), null, error.localReason);
                        }
                        LogUtil.e(TAG, " CarRentSubmitOrder :" + error.toString());
                    }

                    @Override
                    public void onCompletion(BasicResponseModel model) {
                        CarRentOrderResponse response = (CarRentOrderResponse) model.body;
                        LogUtil.e(TAG, "CarRentSubmitOrder = " + response.toString());
                        Intent intent = new Intent(getApplicationContext(), CarRentSuccessActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("carRentOrder", response);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, RENT_CARRIAGE_SUCCESS);
                    }
                });

                break;
        }
    }

    private int RENT_CARRIAGE_SUCCESS = 2908; //租车订单成功，去支付

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RENT_CARRIAGE_SUCCESS){
            if (resultCode == RESULT_OK){ //支付成功

            }
        }
    }

    public static void actionStart(Context context, Bundle bundle){
        Intent intent = new Intent(context, CarRentOrderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(bundle != null){
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }
}
