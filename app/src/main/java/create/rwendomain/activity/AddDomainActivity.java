package create.rwendomain.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import create.rwendomain.R;
import create.rwendomain.model.Domain;
import create.rwendomain.model.Pickers;
import create.rwendomain.util.CustomDatePicker;
import create.rwendomain.util.Util;
import create.rwendomain.widget.ClearEditText;
import create.rwendomain.widget.PickerScrollView;

public class AddDomainActivity extends Activity {

    private TextView registration,tv,times;
    private Button ok;
    private ClearEditText lable,address,username,remark;
    private EditText edit_self,password;
    private LinearLayout linear;
    private PickerScrollView pickerscrlllview; // 滚动选择器
    private Button bt_yes; // 确定按钮
    private RelativeLayout picker_rel; // 选择器布局
    private List<Pickers> list_scroll; // 滚动选择器数据
    private CheckBox checkBox;
    private String[] id;
    private String[] name;
    private String choose="",time="",date="";
    private Domain domain;
    private boolean flag=true,isOK=true;
    private static final String TAG = "MainActivity";
    private static final int SUCCESS_ID = 1;
    private static final int FAIL_ID = 0;
    private  SharedPreferences sp;
    private CustomDatePicker datePicker;
    private ImageView regis_image,quxiao;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_layout);
        // 该语句必不可少
        SQLiteDatabase.loadLibs(AddDomainActivity.this);
        quxiao = (ImageView) findViewById(R.id.quxiao);
        ok = (Button) findViewById(R.id.ok);
        lable = (ClearEditText) findViewById(R.id.lable);
        address = (ClearEditText) findViewById(R.id.address);
        registration = (TextView) findViewById(R.id.registration);
        times = (TextView) findViewById(R.id.util_time);
        username = (ClearEditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        remark = (ClearEditText) findViewById(R.id.remark);
        linear=(LinearLayout)findViewById(R.id.linear);
        edit_self=(EditText)findViewById(R.id.edit_self);
        picker_rel = (RelativeLayout) findViewById(R.id.picker_rel);
        pickerscrlllview = (PickerScrollView) findViewById(R.id.pickerscrlllview);
        bt_yes = (Button) findViewById(R.id.picker_yes);
        tv = (TextView) findViewById(R.id.tv);
        checkBox = findViewById(R.id.checkbox);
        regis_image = (ImageView) findViewById(R.id.regis_image);
        ActiveAndroid.beginTransaction();
        domain=new Domain();
        sp= getSharedPreferences("db_secret", Context.MODE_PRIVATE);
        final Intent intent=getIntent();
        if (intent.getStringExtra("registration")!=null){
            lable.setText(intent.getStringExtra("domain"));
            address.setText(intent.getStringExtra("address"));
            registration.setText(intent.getStringExtra("registration"));
            username.setText(intent.getStringExtra("username"));
            password.setText(intent.getStringExtra("password"));
            remark.setText(intent.getStringExtra("remark"));
            times.setText(intent.getStringExtra("util_time"));
            Util.setImage(regis_image, intent.getStringExtra("registration"));

        }

        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddDomainActivity.this,MainActivity.class));
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里因为加密好的数据库放到了/data/data/包名/files/目录下，注意这里取得是绝对路径
                String path = AddDomainActivity.this.getFilesDir().getAbsolutePath()
                        + File.separator + "cliper.db";
                readClipherData(AddDomainActivity.this,path, "1234");

            }
        });

        ok.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!Util.isURL2(lable.getText().toString())){
                            isOK=false;
                            Toast.makeText(AddDomainActivity.this, "域名输入有误", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            isOK=true;
                        }
                        if (lable.getText().toString().equals("")||address.getText().toString().equals("")||
                                registration.getText().toString().equals("")||username.getText().toString().equals("")||
                                password.getText().toString().equals("")||remark.getText().toString().equals("")||!isOK){
                            Toast.makeText(AddDomainActivity.this, "请正确完善信息", Toast.LENGTH_SHORT).show();
                        }
                        else {
                                domain.setDomain(lable.getText().toString());
                                domain.setIp_address(address.getText().toString());
                                domain.setRegistration(registration.getText().toString());
                                domain.setUsername(username.getText().toString());
                                domain.setPassword(password.getText().toString());
                                domain.setRemark(remark.getText().toString());
                                domain.setUtil_time(times.getText().toString());

                                Select select = new Select();
                                List<Domain> list = select.from(Domain.class).execute();
                                for (int i=0;i<list.size();i++) {
                                    if (list.get(i).getDomain().equals(lable.getText().toString())&&!intent.hasExtra("change")){
                                        Toast.makeText(AddDomainActivity.this, "重复添加域名，请检查", Toast.LENGTH_SHORT).show();
                                        flag=false;
                                        break;
                                    }
                                    else {
                                        flag=true;
                                    }
                                }
                                if (flag){
                                    if (intent.hasExtra("change")){
                                        Update update = new Update(Domain.class);
                                        update.set("domain=?,ip_address=?,registration=?,username=?,password=?,remark=?,util_time=?",
                                                domain.getDomain(),domain.getIp_address(),domain.getRegistration()
                                                ,domain.getUsername(),domain.getPassword(),domain.getRemark(),domain.getUtil_time()).where("id=?",intent.getLongExtra("id",1)).execute();
                                    }
                                    else {
                                        domain.save();
                                        if (!sp.getBoolean("isSecret",false)){
                                            // 开启一个子线程来作为加密的耗时操作
                                            Thread thread = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    encrypt(AddDomainActivity.this,"cliper2.db", "cliper1.db", "1234");
                                                }
                                            });
                                            thread.start();
                                        }


                                    }

                                    ActiveAndroid.setTransactionSuccessful();
                                    ActiveAndroid.endTransaction();
                                    startActivity(new Intent(AddDomainActivity.this,MainActivity.class));
                                    finish();
                                }
                        }

                    }
                }
        );

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        times.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(date);
            }
        });
        initDate();
        initLinstener();
        initData();
    }


    private void initData() {
        list_scroll = new ArrayList<Pickers>();
        id = new String[] { "1", "2", "3", "4", "5", "6","7","8","9", "10", "11", "12", "13", "14","15","16" };
        name = new String[] { "人文网", "阿里云", "华为云", "腾讯云", "西部数码", "网易云",
                "商务中国","中国数据","纳网","西维","中资源","sun接口","新网","时代互联","世纪东方","自定义" };
        for (int i = 0; i < name.length; i++) {
            list_scroll.add(new Pickers(name[i], id[i]));
        }
        // 设置数据，默认选择第一条
        pickerscrlllview.setData(list_scroll);
        pickerscrlllview.setSelected(0);
    }

    private void initDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        time = sdf.format(new Date());
        date = time.split(" ")[0];
        /**
         * 设置年月日
         */
        datePicker = new CustomDatePicker(this, "请选择日期", new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) {
                times.setText(time.split(" ")[0]);
            }
        }, time, "2028-01-01 00:00");
        datePicker.showSpecificTime(false); //显示时和分
        datePicker.setIsLoop(false);
        datePicker.setDayIsLoop(true);
        datePicker.setMonIsLoop(true);
    }

    private void initLinstener() {
        pickerscrlllview.setOnSelectListener(pickerListener);
        bt_yes.setOnClickListener(onClickListener);
        registration.setOnClickListener(onClickListener);
        linear.setOnClickListener(onClickListener);
        lable.setOnClickListener(onClickListener);
        address.setOnClickListener(onClickListener);
        username.setOnClickListener(onClickListener);
        password.setOnClickListener(onClickListener);
    }

    // 滚动选择器选中事件
    PickerScrollView.onSelectListener pickerListener = new PickerScrollView.onSelectListener() {

        @Override
        public void onSelect(Pickers pickers) {
            edit_self.setVisibility(View.GONE);
            choose=pickers.getShowConetnt();
            Util.setImage(regis_image, choose);
            Util.setTextView(address,choose);
        }
    };

    // 点击监听事件
    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == registration) {
                picker_rel.setVisibility(View.VISIBLE);
                edit_self.setVisibility(View.GONE);

            } else  {
                if (choose.equals("")){
                    if (picker_rel.getVisibility()==View.VISIBLE) {
                        choose = "人文网";
                        registration.setText(choose);
                        Util.setImage(regis_image, choose);
                        Util.setTextView(address, choose);
                        picker_rel.setVisibility(View.GONE);
                    }
                }
                else if (choose.equals("自定义")){
                    edit_self.setVisibility(View.VISIBLE);
                    if (!edit_self.getText().toString().equals("")){
                        choose=edit_self.getText().toString();
                        registration.setText(choose);
                        Util.setImage(regis_image, choose);
                        picker_rel.setVisibility(View.GONE);
                    }
                    else {
                        Toast.makeText(AddDomainActivity.this, "请输入自定义机构", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    if (picker_rel.getVisibility()==View.VISIBLE){
                        registration.setText(choose);
                        Util.setImage(regis_image, choose);
                        Util.setTextView(address,choose);
                        picker_rel.setVisibility(View.GONE);
                    }
                }

            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (picker_rel.getVisibility()==View.VISIBLE){
                picker_rel.setVisibility(View.GONE);
                return true;
            }
            else {
                startActivity(new Intent(AddDomainActivity.this,MainActivity.class));
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS_ID:
                    Toast.makeText(AddDomainActivity.this, "加密成功", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isSecret", true);
                    editor.commit();
                    break;
                case FAIL_ID:
                    Toast.makeText(AddDomainActivity.this, "加密失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 加密数据库
     */
    private void encrypt(Context context,String encryptedName, String decryptedName, String key) {
        Message msg = new Message();
        try {
            File databaseFile = getDatabasePath(decryptedName);
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, key, null);//打开要加密的数据库

            /*String passwordString = "1234"; //只能对已加密的数据库修改密码，且无法直接修改为“”或null的密码
            database.changePassword(passwordString.toCharArray());*/

            File encrypteddatabaseFile = getDatabasePath(encryptedName);//新建加密后的数据库文件
            //deleteDatabase(SDcardPath + encryptedName);

            //连接到加密后的数据库，并设置密码
            database.rawExecSQL(String.format("ATTACH DATABASE '%s' as "+ encryptedName.split("\\.")[0] +" KEY '"+ key +"';", encrypteddatabaseFile.getAbsolutePath()));
            //输出要加密的数据库表和数据到加密后的数据库文件中
            database.rawExecSQL("SELECT sqlcipher_export('"+ encryptedName.split("\\.")[0] +"');");
            //断开同加密后的数据库的连接
            database.rawExecSQL("DETACH DATABASE "+ encryptedName.split("\\.")[0] +";");

            //打开加密后的数据库，测试数据库是否加密成功
            SQLiteDatabase encrypteddatabase = SQLiteDatabase.openOrCreateDatabase(encrypteddatabaseFile, key, null);
            //encrypteddatabase.setVersion(database.getVersion());
            encrypteddatabase.close();//关闭数据库

            database.close();
            msg.what = SUCCESS_ID;
        } catch (Exception e) {
            msg.what = FAIL_ID;
            e.printStackTrace();
        }
        finally {
            mHandler.sendMessage(msg);
        }

    }

    /*
     *读取加密后的数据
     */

    private void readClipherData(Context context, String databasePath, String key){
        try{
            SQLiteDatabase encrypteddatabase = SQLiteDatabase.openOrCreateDatabase(databasePath, key, null);
            String[] columns  = new String[]{
                    "domain"
            };
            String sections = "";     //这里填写查询条件
            Cursor cursor = encrypteddatabase.query("Domain", columns, sections, null, null, null, null);
            if (cursor == null){
                Toast.makeText(context, "未查询到数据", Toast.LENGTH_SHORT).show();
            }else {
                if (cursor.moveToFirst()) {
                    do {
                        String value = cursor.getString(cursor.getColumnIndexOrThrow("domain"));
                        Log.e(TAG, "readClipherData: value = " + value);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            encrypteddatabase.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
