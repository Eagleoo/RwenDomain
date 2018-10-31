package create.rwendomain.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import create.rwendomain.R;
import create.rwendomain.util.Util;

public class LookDomainActivity extends Activity {

    private TextView registration;
    private Button change,btn_web,btn_pay;
    private TextView lable,address,username,password,remark,util_time;
    private CheckBox checkBox;
    private SharedPreferences preferences;
    private ImageView regis_image,cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.look_layout);

        cancel = (ImageView) findViewById(R.id.cancel);
        change = (Button) findViewById(R.id.change);
        btn_web = (Button) findViewById(R.id.btn_web);
        btn_pay = (Button) findViewById(R.id.btn_pay);
        lable = (TextView) findViewById(R.id.lable);
        address = (TextView) findViewById(R.id.address);
        util_time = (TextView) findViewById(R.id.util_time);
        registration = (TextView) findViewById(R.id.registration);
        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);
        remark = (TextView) findViewById(R.id.remark);
        regis_image = (ImageView) findViewById(R.id.regis_image);
        checkBox = findViewById(R.id.checkbox);
        final Intent intent=getIntent();
        preferences = getSharedPreferences("domain", Context.MODE_PRIVATE);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        if (intent!=null){
            lable.setText(intent.getStringExtra("domain"));
            address.setText(Html.fromHtml("<u>"+intent.getStringExtra("address")+"</u>"));
            registration.setText(intent.getStringExtra("registration"));
            username.setText(intent.getStringExtra("username"));
            password.setText(intent.getStringExtra("password"));
            remark.setText(intent.getStringExtra("remark"));
            util_time.setText(intent.getStringExtra("util_time"));
            Util.setImage(regis_image,intent.getStringExtra("registration"));
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LookDomainActivity.this,MainActivity.class));
                finish();
            }
        });

        change.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.putExtra("change",1);
                        intent.setClass(LookDomainActivity.this,AddDomainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );

        btn_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent();
                intent1.putExtra("url",address.getText().toString());
                intent1.putExtra("username",username.getText().toString());
                intent1.putExtra("password",password.getText().toString());
                intent1.setClass(LookDomainActivity.this, WebActivity.class);
                startActivity(intent1);
            }
        });

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

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                int time=0;//还剩多少天
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                String cur_date = sdf.format(new Date());
                time=Integer.valueOf(Util.getDayStr(cur_date,Util.selecttime(lable.getText().toString())));
                if (time<=22){
                    editor.putString("dialog","dialog");
                    editor.putString("look_domain", lable.getText().toString());
                    editor.remove("listStr");
                    editor.commit();
                }
                else {
                    editor.putString("look_domain", lable.getText().toString());
                    editor.remove("listStr");
                    editor.remove("dialog");
                    editor.commit();
                }



                Intent intent1=new Intent();
                intent1.putExtra("domain",lable.getText().toString());
                intent1.setClass(LookDomainActivity.this, PayGridActivity.class);
                startActivity(intent1);}
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(LookDomainActivity.this,MainActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
