package create.rwendomain.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.nikhilpanju.recyclerviewenhanced.OnActivityTouchListener;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import create.rwendomain.R;
import create.rwendomain.model.Domain;
import create.rwendomain.model.MenuItem;
import create.rwendomain.util.ToastUtil;
import create.rwendomain.util.Util;
import create.rwendomain.widget.PromptDialog;
import create.rwendomain.widget.SubPtrSwipe;
import create.rwendomain.widget.TopRightMenu;

public class MainActivity extends AppCompatActivity implements RecyclerTouchListener.RecyclerTouchListenerHelper {

    private RecyclerView domain_listView;
    private TextView del,rwen;
    private List<Domain> list;
    private Button finish,btn_add;
    private FloatingActionButton fab;
    private ImageView explore,more;
    private MainAdapter mAdapter;
    private RecyclerTouchListener onTouchListener;
    private OnActivityTouchListener touchListener;
    private LinearLayout tv_linear;
    private RelativeLayout rel_nodata;
    private SubPtrSwipe mRefreshLayout;
    private String cur_date="";//当天时间
    private StringBuffer until_domain=new StringBuffer();//即将到期域名
    private ArrayList<String> date_list=new ArrayList<>();
    public static final int UPDATE = 0x1;
    private SharedPreferences preferences;
    private TopRightMenu mTopRightMenu;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE:
                    SharedPreferences.Editor editor = preferences.edit();
                    if (date_list.size()!=0){
                        showPromptDlg();
                        Gson gson = new Gson();
                        String data = gson.toJson(date_list);
                        editor.clear();
                        editor.putString("listStr", data);
                        editor.commit();
                    }
                    else {
                        editor.remove("listStr");
                        editor.commit();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        domain_listView=(RecyclerView)findViewById(R.id.domain_listView);
        del=(TextView)findViewById(R.id.delete);
        rwen=(TextView)findViewById(R.id.rwen);
        finish=(Button)findViewById(R.id.finish);
        btn_add=(Button)findViewById(R.id.btn_add);
        explore=findViewById(R.id.explore);
        more=findViewById(R.id.more);
        tv_linear=findViewById(R.id.tv_linear);
        rel_nodata=findViewById(R.id.rel_nodata);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        cur_date = sdf.format(new Date());
        preferences = getSharedPreferences("domain", Context.MODE_PRIVATE);
        rwen.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        rwen.getPaint().setAntiAlias(true);//抗锯齿
        fab = (FloatingActionButton) findViewById(R.id.fab);
            if (getData().size()==1){
                ToastUtil.makeToast(MainActivity.this,"暂无数据");
                rel_nodata.setVisibility(View.VISIBLE);
                btn_add.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);

            }
            else {
                rel_nodata.setVisibility(View.GONE);
                btn_add.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
            }
            mAdapter = new MainAdapter(MainActivity.this, getData(),0);
            domain_listView.setAdapter(mAdapter);
            domain_listView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            SharedPreferences.Editor editor = preferences.edit();
            if (date_list.size()!=0){
                Gson gson = new Gson();
                String data = gson.toJson(date_list);
                editor.clear();
                editor.putString("listStr", data);
                editor.commit();
            }
            else {
                editor.remove("listStr");
                editor.commit();
            }

        onTouchListener = new RecyclerTouchListener(this, domain_listView);
        onTouchListener
                .setIndependentViews(R.id.rowButton,R.id.delete)
                .setViewsToFade(R.id.rowButton,R.id.delete)
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int i) {
                        //不知为什么删除第一个元素始终要执行，所以就给他初始化了一个元素并隐藏
                        if (i!=0){
                            startIntent(i);
                        }

                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int i) {
                        if (i!=0&&independentViewID==R.id.rowButton){
                           startIntent(i);
                        }
                        else if (independentViewID==R.id.delete){
                            deleteCell(domain_listView.getChildAt(i), i);
                        }

                    }
                })
                .setLongClickable(true, new RecyclerTouchListener.OnRowLongClickListener() {
                    @Override
                    public void onRowLongClicked(int position) {
                        ToastUtil.makeToast(getApplicationContext(), "Row " + (position + 1) + " long clicked!");
                    }
                })
                .setSwipeOptionViews(R.id.add, R.id.edit, R.id.change1)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int i) {
                        if (viewID == R.id.add) {
                            startIntent(i);
                        } else if (viewID == R.id.edit) {
                            Intent intent=new Intent();
                            intent.putExtra("domain",list.get(i).getDomain());
                            intent.putExtra("address",list.get(i).getIp_address());
                            intent.putExtra("registration",list.get(i).getRegistration());
                            intent.putExtra("username",list.get(i).getUsername());
                            intent.putExtra("password",list.get(i).getPassword());
                            intent.putExtra("remark",list.get(i).getRemark());
                            intent.putExtra("util_time",list.get(i).getUtil_time());
                            intent.putExtra("id",list.get(i).getId());
                            intent.putExtra("change",1);
                            intent.setClass(MainActivity.this,AddDomainActivity.class);
                            startActivity(intent);
                        } else if (viewID == R.id.change1) {
                            deleteCell(domain_listView.getChildAt(i), i);

                        }
                    }
                });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(MainActivity.this,AddDomainActivity.class));
            }
        });

        finish.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish.setVisibility(View.GONE);
                        fab.setVisibility(View.VISIBLE);
                        tv_linear.setVisibility(View.VISIBLE);
                        mAdapter = new MainAdapter(MainActivity.this, getData(),0);
                        domain_listView.setAdapter(mAdapter);
                        domain_listView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    }
                }
        );


        explore.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this,SerachActivity.class));
                    }
                }
        );

        rwen.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.putExtra("url","http://www.rwen.com");
                        intent.setClass(MainActivity.this,WebActivity.class);
                        startActivity(intent);
                    }
                }
        );

//        btn_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this,AddDomainActivity.class));
//            }
//        });

        mRefreshLayout = (SubPtrSwipe) findViewById(R.id.refresh_layout);
        if (getIntent().hasExtra("refresh")){
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    //mRefreshLayout.setRefreshing(true);

                    CountDownTimer count=new CountDownTimer(1000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            if (getData().size()==1){
                                ToastUtil.makeToast(MainActivity.this,"暂无数据");
                                rel_nodata.setVisibility(View.VISIBLE);
                                btn_add.setVisibility(View.VISIBLE);
                                fab.setVisibility(View.GONE);
                            }
                            else {
                                rel_nodata.setVisibility(View.GONE);
                                btn_add.setVisibility(View.GONE);
                                fab.setVisibility(View.VISIBLE);
                            }
                            date_list.clear();
                            mAdapter = new MainAdapter(MainActivity.this, getData(),0);
                            domain_listView.setAdapter(mAdapter);
                            domain_listView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            mRefreshLayout.setRefreshing(false);
                            Message msg = new Message();
                            msg.what = UPDATE;
                            handler.sendMessage(msg);
                        }
                    };
                    count.start();
                }
            });
        }

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getData().size()==1){
                    ToastUtil.makeToast(MainActivity.this,"暂无数据");
                    rel_nodata.setVisibility(View.VISIBLE);
                    btn_add.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);
                }
                else {
                    rel_nodata.setVisibility(View.GONE);
                    btn_add.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                }
                mAdapter = new MainAdapter(MainActivity.this, getData(),0);
                domain_listView.setAdapter(mAdapter);
                domain_listView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                mRefreshLayout.setRefreshing(false);
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTopRightMenu = new TopRightMenu(MainActivity.this);
                mTopRightMenu
                          //默认高度480
                            //默认宽度wrap_content
                        .showIcon(true)     //显示菜单图标，默认为true
                        .dimBackground(true)           //背景变暗，默认为true
                        .needAnimationStyle(true)   //显示动画，默认为true
                        .setAnimationStyle(R.style.TRM_ANIM_STYLE)  //默认为R.style.TRM_ANIM_STYLE
                        .addMenuItem(new MenuItem(R.drawable.ic_border_color_black_24dp, "编辑"))
                        .addMenuItem(new MenuItem(R.drawable.ic_lock_outline_black_24dp, "管理手势密码"))
                        .addMenuItem(new MenuItem(R.drawable.ic_help_outline_black_24dp, "关于我们"))
                        .setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {
                            @Override
                            public void onMenuItemClick(int position) {
                                if (position==0) {
                                    if (getData().size()==1){
                                        Toast.makeText(MainActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        finish.setVisibility(View.VISIBLE);
                                        fab.setVisibility(View.GONE);
                                        tv_linear.setVisibility(View.GONE);
                                        mAdapter=new MainAdapter(MainActivity.this,list,1);
                                        domain_listView.setAdapter(mAdapter);
                                    }

                                }
                                else if (position==1){
                                    Intent intent=new Intent();
                                    intent.putExtra("change","change");
                                    intent.setClass(MainActivity.this, GestureLoginActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    startActivity(new Intent(MainActivity.this,AboutActivity.class));
                                }
                            }
                        })
                        .showAsDropDown(more, 0, 0);
            }
        });

    }
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.btn_add: startActivity(new Intent(MainActivity.this,AddDomainActivity.class));
                finish();break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        domain_listView.addOnItemTouchListener(onTouchListener); }

    @Override
    protected void onPause() {
        super.onPause();
        domain_listView.removeOnItemTouchListener(onTouchListener);
    }

    private List<Domain> getData() {
        Select select = new Select();
        list = select.from(Domain.class).execute();
        list.add(0,init_Domain());
        return list;
    }


    private void startIntent(int i){
            Intent intent=new Intent();
            intent.putExtra("domain",list.get(i).getDomain());
            intent.putExtra("address",list.get(i).getIp_address());
            intent.putExtra("registration",list.get(i).getRegistration());
            intent.putExtra("username",list.get(i).getUsername());
            intent.putExtra("password",list.get(i).getPassword());
            intent.putExtra("remark",list.get(i).getRemark());
            intent.putExtra("id",list.get(i).getId());
            intent.putExtra("util_time",list.get(i).getUtil_time());
            intent.setClass(MainActivity.this,LookDomainActivity.class);
            startActivity(intent);
    }

    private void deleteCell(final View v, final int index) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                Delete delete = new Delete();
                delete.from(Domain.class).where("id="+list.get(index).getId()).execute();
                list.remove(index);
//                mAdapter = new MainAdapter(MainActivity.this, getData(),0);
//                domain_listView.setAdapter(mAdapter);
//                domain_listView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationStart(Animation animation) {}
        };

        collapse(v, al);
    }

    private void collapse(final View v, Animation.AnimationListener al) {
        final int initialHeight = v.getMeasuredHeight();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                }
                else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al!=null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(300);
        v.startAnimation(anim);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (finish.getVisibility()==View.VISIBLE){
                finish.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                tv_linear.setVisibility(View.VISIBLE);
                mAdapter=new MainAdapter(this, list,0);
                domain_listView.setAdapter(mAdapter);
                return true;
            }
            else {
                alert();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (touchListener != null) touchListener.getTouchCoordinates(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setOnActivityTouchListener(OnActivityTouchListener listener) {
        this.touchListener = listener;
    }

    private class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
        LayoutInflater inflater;
        List<Domain> modelList;
        int state=0;
        public MainAdapter(Context context, List<Domain> list,int state) {
            inflater = LayoutInflater.from(context);
            modelList = new ArrayList<>(list);
            this.state=state;
        }

        @Override
        public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.recycler_row, parent, false);
            return new MainViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MainViewHolder holder, int position) {
            if (position==0){
                holder.bindData(modelList.get(position));
                holder.setVisibility();
            }
            else {
                holder.bindData(modelList.get(position));
            }

        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        class MainViewHolder extends RecyclerView.ViewHolder {

            TextView mainText, subText,delete,date;
            ImageView icon;

            public MainViewHolder(View itemView) {
                super(itemView);
                mainText = (TextView) itemView.findViewById(R.id.mainText);
                subText = (TextView) itemView.findViewById(R.id.subText);
                delete = (TextView) itemView.findViewById(R.id.delete);
                date = (TextView) itemView.findViewById(R.id.date);
                icon = (ImageView) itemView.findViewById(R.id.icon);
            }

            public void bindData(Domain rowModel) {
                int time=0;
                time=Integer.valueOf(Util.getDayStr(cur_date,rowModel.getUtil_time()));
                mainText.setText(rowModel.getRemark());
                subText.setText(rowModel.getDomain());
                if (Util.getDateStr(cur_date,rowModel.getUtil_time()).equals("已到期")||time<=0){
                    date.setTextColor(getResources().getColor(R.color.color_type_warning));
                }
                else if (time < 10){
                    date.setTextColor(getResources().getColor(R.color.color_type_help));
                    date_list.add(rowModel.getDomain());
                }
                date.setText(Util.getDateStr(cur_date,rowModel.getUtil_time()));

                Util.setImage(icon,rowModel.getRegistration());

                if (state == 1) {
                    delete.setVisibility(View.VISIBLE);
                }
                else {
                    delete.setVisibility(View.GONE);
                }
            }

            public void setVisibility(){
//                RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
//                if (isVisible){
//                    param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    param.width = LinearLayout.LayoutParams.MATCH_PARENT;
//                    itemView.setVisibility(View.VISIBLE);
//                }else{
//                    itemView.setVisibility(View.GONE);
//                    param.height = 10;
//                    param.width = 10;
//                }
//                itemView.setLayoutParams(param);
                itemView.setVisibility(View.GONE);
                itemView.setClickable(false);
                itemView.setFocusable(false);
                itemView.setActivated(false);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }

        }
    }


    private Domain init_Domain(){
        Domain domain=new Domain();
        domain.setDomain("zyh");
        domain.setIp_address("zyh");
        domain.setRegistration("zyh");
        domain.setUsername("zyh");
        domain.setPassword("zyh");
        domain.setRemark("zyh");
        domain.setUtil_time("9999-12-12");

        return domain;
    }

    public void alert() {
        new AlertDialog.Builder(MainActivity.this).setTitle("提示：").setMessage("您确定要退出吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        }).setNegativeButton("取消", null).setIcon(R.drawable.icon_img).show();
    }

    private void showPromptDlg() {

        for (int i=0;i<date_list.size();i++){
            until_domain.append(date_list.get(i)+"\n\n");
        }

        new PromptDialog(this)
                .setDialogType(PromptDialog.DIALOG_TYPE_WARNING)
                .setAnimationEnable(true)
                .setTitleText(getString(R.string.warn_title))
                .setContentText(until_domain.toString())
                .setNegativeListener("前往续费", new PromptDialog.OnNegativeListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        startActivity(new Intent(MainActivity.this,PayGridActivity.class));
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("dialog","dialog");
                        editor.commit();
                        dialog.dismiss();
                    }
                })
                .setPositiveListener("知道了", new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
