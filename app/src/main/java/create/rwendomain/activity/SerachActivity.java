package create.rwendomain.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import create.rwendomain.R;
import create.rwendomain.model.Domain;

public class SerachActivity extends Activity {

    private EditText editText;
    private List<Domain> list;
    private List<Domain> addlist=new ArrayList<>();
    private ListView search_listView;
    private ImageView back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        editText=findViewById(R.id.search_edt);
        search_listView=findViewById(R.id.search_listView);
        back=findViewById(R.id.back1);
        Select select = new Select();
        list = select.from(Domain.class).execute();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SerachActivity.this,MainActivity.class));
            }
        });

        editText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!s.toString().equals("")){
                            addlist.clear();
                            for (int i=0;i<list.size();i++){
                                if (list.get(i).getRemark().contains(s.toString())){
                                    addlist.add(list.get(i));
                                }
                            }
                            if (addlist.size()!=0){
                                DomainAdapter domainAdapter=new DomainAdapter(addlist,SerachActivity.this);
                                search_listView.setAdapter(domainAdapter);
                            }
                        }

                    }
                }
        );

    }

    public class DomainAdapter extends BaseAdapter {
        private List<Domain> mData;
        private Context mContext;

        public DomainAdapter(List<Domain> mData, Context mContext) {
            this.mData = mData;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;

            if (view == null) {
                //创建缓冲布局界面，获取界面上的组件
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.domain_listview_item, viewGroup, false);
                //  Log.v("AnimalAdapter","改进后调用一次getView方法");
                holder = new ViewHolder();
                holder.domain_remark = (TextView) view.findViewById(R.id.domain_remark);
                holder.domain_lable = (TextView) view.findViewById(R.id.domain_lable);
                holder.del = (TextView) view.findViewById(R.id.delete);
                holder.domain_icon = (ImageView) view.findViewById(R.id.domain_icon);

                view.setTag(holder);
            } else {
                //用原有组件
                holder = (ViewHolder) view.getTag();
            }
            holder.domain_remark.setText(mData.get(i).getRemark());
            holder.domain_lable.setText("域名：" + mData.get(i).getDomain());

            holder.domain_icon.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra("domain", mData.get(i).getDomain());
                            intent.putExtra("address", mData.get(i).getIp_address());
                            intent.putExtra("registration", mData.get(i).getRegistration());
                            intent.putExtra("username", mData.get(i).getUsername());
                            intent.putExtra("password", mData.get(i).getPassword());
                            intent.putExtra("remark", mData.get(i).getRemark());
                            intent.putExtra("id", mData.get(i).getId());
                            intent.setClass(mContext, LookDomainActivity.class);
                            startActivity(intent);
                        }
                    }
            );

            return view;
        }

        private final class ViewHolder {

            TextView domain_remark;
            TextView domain_lable;
            TextView del;
            ImageView domain_icon;

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(SerachActivity.this,MainActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }

}

