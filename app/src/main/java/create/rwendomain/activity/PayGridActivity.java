package create.rwendomain.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import create.rwendomain.R;

public class PayGridActivity extends Activity {

    @BindView(R.id.pay_back)
    Button pay_back;
    @BindView(R.id.cancel)
    ImageView back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_grid);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.pay_back,R.id.cancel})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.pay_back: startActivity(new Intent(PayGridActivity.this,MainActivity.class));
                finish();break;
            case R.id.cancel: startActivity(new Intent(PayGridActivity.this,MainActivity.class));
                finish();break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(PayGridActivity.this,MainActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
