package create.rwendomain.widget;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.BottomSheetDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.activeandroid.query.Update;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import create.rwendomain.R;
import create.rwendomain.activity.WebActivity;
import create.rwendomain.model.DargChildInfo;
import create.rwendomain.model.Domain;
import create.rwendomain.model.DragIconInfo;
import create.rwendomain.model.Pickers;
import create.rwendomain.util.Util;

/**
 *
 * 类: CustomGroup <p>
 * 描述: TODO <p>
 * 作者: wedcel wedcel@gmail.com<p>
 * 时间: 2015年8月25日 下午6:54:26 <p>
 */
public class CustomGroup extends ViewGroup {

	private CustomAboveView mCustomAboveView;
	private CustomBehindParent mCustomBehindParent;
	private boolean isEditModel = false;
	public static final int COLUMNUM = 2;
	public static final int COLUMNUM1 = 1;
	private Context mContext;
	private String years="1";
	private SharedPreferences sp;
	//所有以的list
	private ArrayList<DragIconInfo> allInfoList = new ArrayList<DragIconInfo>();
	/**显示的带more的list*/
	private ArrayList<DragIconInfo> homePageInfoList = new ArrayList<DragIconInfo>();
	/**可展开的list*/
	private ArrayList<DragIconInfo> expandInfoList = new ArrayList<DragIconInfo>();

	/**不可展开的list*/
	private ArrayList<DragIconInfo> onlyInfoList = new ArrayList<DragIconInfo>();

	private InfoEditModelListener editModelListener;



	public interface InfoEditModelListener {
		public void onModleChanged(boolean isEditModel);
	}

	/**
	 *
	 * 标题: 构造器 <p>
	 * 描述: TODO <p>
	 * 作者: wedcel wedcel@gmail.com <p>
	 * 时间: 2015年8月25日 下午5:29:30 <p>
	 * @param context
	 * @param attrs
	 */
	public CustomGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		LayoutParams upParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mCustomAboveView = new CustomAboveView(context, this);
		addView(mCustomAboveView, upParams);
		LayoutParams downParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mCustomBehindParent = new CustomBehindParent(mContext, this);
		addView(mCustomBehindParent, downParams);
		SQLiteDatabase.loadLibs(mContext);
		sp= mContext.getSharedPreferences("domain", Context.MODE_PRIVATE);
		initData();
	}


	public CustomGroup(Context context) {
		this(context, null);
	}

	public InfoEditModelListener getEditModelListener() {
		return editModelListener;
	}

	public void setEditModelListener(InfoEditModelListener editModelListener) {
		this.editModelListener = editModelListener;
	}

	/**
	 *
	 * 方法: initData <p>
	 * 描述: 初始化监听和数据 <p>
	 * 参数:  <p>
	 * 返回: void <p>
	 * 异常  <p>
	 * 作者: wedcel wedcel@gmail.com <p>
	 * 时间: 2015年8月25日 下午5:29:40
	 */
	private void initData() {

		setCustomViewClickListener(new CustomAboveView.CustomAboveViewClickListener() {

			@Override
			public void onSingleClicked(DragIconInfo iconInfo) {
				// TODO Auto-generated method stub
				dispatchSingle(iconInfo);
			}

			@Override
			public void onChildClicked(DargChildInfo childInfo) {
				// TODO Auto-generated method stub
				dispatchChild((childInfo));
			}
		});

		initIconInfo();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMeasure = 0;
		int heightMeasure = 0;
		if (isEditModel) {
			mCustomBehindParent.measure(widthMeasureSpec, heightMeasureSpec);
			widthMeasure = mCustomBehindParent.getMeasuredWidth();
			heightMeasure = mCustomBehindParent.getMeasuredHeight();
		} else {
			mCustomAboveView.measure(widthMeasureSpec, heightMeasureSpec);
			widthMeasure = mCustomAboveView.getMeasuredWidth();
			heightMeasure = mCustomAboveView.getMeasuredHeight();
		}
		setMeasuredDimension(widthMeasure, heightMeasure);

	}

	/**
	 * 方法: onLayout <p>
	 * 描述: TODO<p>
	 * @param changed
	 * @param l
	 * @param t
	 * @param r
	 * @param b <p>
	 * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int) <p>
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (isEditModel) {
			int behindHeight = mCustomBehindParent.getMeasuredHeight();
			mCustomBehindParent.layout(l, 0, r, behindHeight + t);
		} else {
			int aboveHeight = mCustomAboveView.getMeasuredHeight();
			mCustomAboveView.layout(l, 0, r, aboveHeight + t);
		}
	}

	/**
	 *
	 * 方法: initIconInfo <p>
	 * 描述: 初始化数据 <p>
	 * 参数:  <p>
	 * 返回: void <p>
	 * 异常  <p>
	 * 作者: wedcel wedcel@gmail.com <p>
	 * 时间: 2015年8月25日 下午5:33:14
	 */
	private void initIconInfo() {


		allInfoList.clear();
		allInfoList.addAll(initAllOriginalInfo());
		getPageInfoList();

		refreshIconInfo();
	}

	/**
	 *
	 * 方法: initAllOriginalInfo <p>
	 * 描述: 初始化Icon info <p>
	 * 参数: @return <p>
	 * 返回: ArrayList<DragIconInfo> <p>
	 * 异常  <p>
	 * 作者: wedcel wedcel@gmail.com <p>
	 * 时间: 2015年8月25日 下午5:33:48
	 */
	private ArrayList<DragIconInfo> initAllOriginalInfo() {
		ArrayList<DragIconInfo> iconInfoList = new ArrayList<DragIconInfo>();
		ArrayList<DargChildInfo> childList = initChildList();
		ArrayList<DargChildInfo> childList1 = initChildList1();
		if (!sp.getString("dialog","").equals("")){
			iconInfoList.add(new DragIconInfo(1, "自行在注册机构续费（平台手动更新到期时间）", R.drawable.pay_logo1, DragIconInfo.CATEGORY_EXPAND, childList1));
		}
		else {
			iconInfoList.add(new DragIconInfo(1, "转移域名至人文网，享受更低续费价格点击查看详情", R.drawable.pay_logo, DragIconInfo.CATEGORY_EXPAND, childList));
			iconInfoList.add(new DragIconInfo(2, "自行在注册机构续费（平台手动更新到期时间）", R.drawable.pay_logo1, DragIconInfo.CATEGORY_EXPAND, childList1));
		}

		return iconInfoList;
	}


	/**
	 *
	 * 方法: initChildList <p>
	 * 描述: 初始化child list <p>
	 * 参数: @return <p>
	 * 返回: ArrayList<DargChildInfo> <p>
	 * 异常  <p>
	 * 作者: wedcel wedcel@gmail.com <p>
	 * 时间: 2015年8月25日 下午5:36:12
	 */
	private ArrayList<DargChildInfo> initChildList() {
		ArrayList<DargChildInfo> childList = new ArrayList<DargChildInfo>();
		childList.add(new DargChildInfo(1, "QQ：802045268"));
		childList.add(new DargChildInfo(2, "电话：028-86619097"));
		childList.add(new DargChildInfo(3, "在线转入"));
		return childList;
	}
	private ArrayList<DargChildInfo> initChildList1() {
		Log.e("list*****",sp.getString("look_domain","sss"));
		Log.e("list*****", sp.getString("listStr", "qqq"));

		String data = sp.getString("listStr", "");

		Gson gson = new Gson();
		Type listType = new TypeToken<List<String>>() {
		}.getType();
		List<String> list = gson.fromJson(data, listType);
		ArrayList<DargChildInfo> childList = new ArrayList<DargChildInfo>();

		if (list!=null&&!data.equals("")){
			Log.e("list*****",list.size()+"");
			for (int i=0;i<list.size();i++){
				childList.add(new DargChildInfo(i+1, list.get(i)));
			}
		}
		else {
			childList.add(new DargChildInfo(1, sp.getString("look_domain","sss")));
        }

		return childList;
	}
	/**
	 *
	 * 方法: getPageInfoList <p>
	 * 描述: 初始化显示 <p>
	 * 参数:  <p>
	 * 返回: void <p>
	 * 异常  <p>
	 * 作者: wedcel wedcel@gmail.com <p>
	 * 时间: 2015年8月25日 下午5:37:33
	 */
	private void getPageInfoList() {
		homePageInfoList.clear();
		int count = 0;
		for (DragIconInfo info : allInfoList) {
			if (count < 9) {
				homePageInfoList.add(info);
				count++;
			} else {
				break;
			}
		}
	}

	/**
	 *
	 * 方法: refreshIconInfo <p>
	 * 描述: 刷新信息 <p>
	 * 参数:  <p>
	 * 返回: void <p>
	 * 异常  <p>
	 * 作者: wedcel wedcel@gmail.com <p>
	 * 时间: 2015年8月25日 下午5:38:11
	 */
	private void refreshIconInfo() {

		ArrayList<DragIconInfo> moreInfo = getMoreInfoList(allInfoList, homePageInfoList);
		expandInfoList = getInfoByType(moreInfo, DragIconInfo.CATEGORY_EXPAND);
		onlyInfoList = getInfoByType(moreInfo, DragIconInfo.CATEGORY_ONLY);
		setIconInfoList(homePageInfoList);
	}



	/**
	 *
	 * 方法: judeHomeInfoValid <p>
	 * 描述: 判断下显示里面是否包含更多 或者看下是否是最后一个 固定更多的位置 <p>
	 * 参数:  <p>
	 * 返回: void <p>
	 * 异常  <p>
	 * 作者: wedcel wedcel@gmail.com <p>
	 * 时间: 2015年8月25日 下午5:38:37
	 */
	private void judeHomeInfoValid() {
		boolean hasMoreInfo = false;
		int posit = 0;
		for(int index = 0;index<homePageInfoList.size();index++){
			DragIconInfo tempInfo = homePageInfoList.get(index);
			if(tempInfo.getId()==CustomAboveView.MORE){
				hasMoreInfo = true;
				posit = index;
				break;
			}
		}
	}


	/**
	 *
	 * 方法: getInfoByType <p>
	 * 描述: TODO <p>
	 * 参数: @param moreInfo
	 * 参数: @param categorySpt
	 * 参数: @return <p>
	 * 返回: ArrayList<DragIconInfo> <p>
	 * 异常  <p>
	 * 作者: wedcel wedcel@gmail.com <p>
	 * 时间: 2015年8月25日 下午6:50:25
	 */
	private ArrayList<DragIconInfo> getInfoByType(ArrayList<DragIconInfo> moreInfo, int categorySpt) {
		ArrayList<DragIconInfo> typeList = new ArrayList<DragIconInfo>();
		for (DragIconInfo info : moreInfo) {
			if (info.getCategory() == categorySpt) {
				typeList.add(info);
			}
		}
		return typeList;
	}


	public void setCustomViewClickListener(CustomAboveView.CustomAboveViewClickListener gridViewClickListener) {
		mCustomAboveView.setGridViewClickListener(gridViewClickListener);
	}

	/**
	 *
	 * 方法: setIconInfoList <p>
	 * 描述: 设置信息 <p>
	 * 参数: @param iconInfoList <p>
	 * 返回: void <p>
	 * 异常  <p>
	 * 作者: wedcel wedcel@gmail.com <p>
	 * 时间: 2015年8月25日 下午6:45:55
	 */
	public void setIconInfoList(ArrayList<DragIconInfo> iconInfoList) {
		mCustomAboveView.refreshIconInfoList(iconInfoList);
		mCustomBehindParent.refreshIconInfoList(iconInfoList);
	}


	public boolean isEditModel() {
		return isEditModel;
	}

	public void cancleEidtModel(){
		setEditModel(false, 0);
	}


	public void setEditModel(boolean isEditModel, int position) {
		this.isEditModel = isEditModel;
		if (isEditModel) {
			mCustomAboveView.setViewCollaps();
			mCustomAboveView.setVisibility(View.GONE);
			mCustomBehindParent.notifyDataSetChange(mCustomAboveView.getIconInfoList());
			mCustomBehindParent.setVisibility(View.VISIBLE);
			mCustomBehindParent.drawWindowView(position, mCustomAboveView.getFirstEvent());
		} else {
			homePageInfoList.clear();
			homePageInfoList.addAll(mCustomBehindParent.getEditList());
			mCustomAboveView.refreshIconInfoList(homePageInfoList);
			mCustomAboveView.setVisibility(View.VISIBLE);
			mCustomBehindParent.setVisibility(View.GONE);
			if(mCustomBehindParent.isModifyedOrder()){
				mCustomBehindParent.cancleModifyOrderState();
			}
			mCustomBehindParent.resetHidePosition();
		}
		if(editModelListener!=null){
			editModelListener.onModleChanged(isEditModel);
		}
	}


	public void sendEventBehind(MotionEvent ev) {
		mCustomBehindParent.childDispatchTouchEvent(ev);
	}

	/**
	 *
	 * 方法: getMoreInfoList <p>
	 * 描述: TODO <p>
	 * 参数: @param allInfoList
	 * 参数: @param homePageInfoList
	 * 参数: @return <p>
	 * 返回: ArrayList<DragIconInfo> <p>
	 * 异常  <p>
	 * 作者: wedcel wedcel@gmail.com <p>
	 * 时间: 2015年8月25日 下午6:57:06
	 */
	private ArrayList<DragIconInfo> getMoreInfoList(ArrayList<DragIconInfo> allInfoList, ArrayList<DragIconInfo> homePageInfoList) {
		ArrayList<DragIconInfo> moreInfoList = new ArrayList<DragIconInfo>();
		moreInfoList.addAll(allInfoList);
		moreInfoList.removeAll(homePageInfoList);
		return moreInfoList;
	}



	/**
	 *
	 * 方法: deletHomePageInfo <p>
	 * 描述: TODO <p>
	 * 参数: @param dragIconInfo <p>
	 * 返回: void <p>
	 * 异常  <p>
	 * 作者: wedcel wedcel@gmail.com <p>
	 * 时间: 2015年8月25日 下午6:56:19
	 */
	public void deletHomePageInfo(DragIconInfo dragIconInfo) {
		homePageInfoList.remove(dragIconInfo);
		mCustomAboveView.refreshIconInfoList(homePageInfoList);
		int category = dragIconInfo.getCategory();
		switch (category) {
			case DragIconInfo.CATEGORY_ONLY:
				onlyInfoList.add(dragIconInfo);
				break;
			case DragIconInfo.CATEGORY_EXPAND:
				expandInfoList.add(dragIconInfo);
				break;
			default:
				break;
		}
		allInfoList.remove(dragIconInfo);
		allInfoList.add(dragIconInfo);
	}




	/**
	 *
	 * 方法: dispatchChild <p>
	 * 描述: 点击child <p>
	 * 参数: @param childInfo <p>
	 * 返回: void <p>
	 * 异常  <p>
	 * 作者: wedcel wedcel@gmail.com <p>
	 * 时间: 2015年8月25日 下午5:30:58
	 */
	protected void dispatchChild(final DargChildInfo childInfo) {
		if (childInfo == null) {
			return;
		}
		if (childInfo.getName().equals("在线转入")){
		    Intent intent=new Intent();
		    intent.putExtra("url","http://sys.rwen.com/user/rw_domain_transferin.asp");
		    intent.setClass(mContext,WebActivity.class);
			mContext.startActivity(intent);
		}
		else if (childInfo.getName().contains("QQ")){
            ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            cm.setText("802045268");
            Toast.makeText(mContext, "复制成功", Toast.LENGTH_LONG).show();
		}
        else if (childInfo.getName().contains("电话")){
            Intent dialIntent =  new Intent(Intent.ACTION_DIAL, Uri.parse("tel:02886619097"));//跳转到拨号界面，同时传递电话号码
            mContext.startActivity(dialIntent);
        }
        else {
            final BottomSheetDialog dialog=new BottomSheetDialog(mContext);
            View dialogView = LayoutInflater.from(mContext).inflate(R.layout.bottom_dialog, null);
            Button btnSure = (Button) dialogView.findViewById(R.id.pay_ok);
			ImageView dialog_back = (ImageView) dialogView.findViewById(R.id.dialog_back);
			PickerScrollView pickeryears = (PickerScrollView)dialogView.findViewById(R.id.pickeryears);
            initYears(pickeryears);
            pickeryears.setOnSelectListener(pickerListener);
            btnSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

					int time=0;//还剩多少天
					int total=0;//总共
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
					String cur_date = sdf.format(new Date());
					time=Integer.valueOf(Util.getDayStr(cur_date,Util.selecttime(childInfo.getName().trim())));
					total=time+(Integer.valueOf(years)*365);
					if (total>3650){
						Toast.makeText(mContext, "国际规定续费年限不能超过10年", Toast.LENGTH_SHORT).show();
					}
					else {
						Update update = new Update(Domain.class);
						update.set("util_time=?", Util.getDate(Util.selecttime(childInfo.getName().trim()),Integer.valueOf(years)*365)).where("domain=?",childInfo.getName().trim()).execute();
						dialog.dismiss();
                        Toast.makeText(mContext, "续费成功", Toast.LENGTH_SHORT).show();
					}

                }
            });
            dialog_back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setContentView(dialogView);
            dialog.show();
        }

	}




	private void initYears(PickerScrollView pickeryears) {
		List<Pickers> list_scroll = new ArrayList<Pickers>();
		String[] id = new String[] { "1", "2", "3", "4", "5", "6","7","8","9", "10" };
		String[] name = new String[] {"1", "2", "3", "4", "5", "6","7","8","9", "10"};
		for (int i = 0; i < name.length; i++) {
			list_scroll.add(new Pickers(name[i], id[i]));
		}
		// 设置数据，默认选择第一条
		pickeryears.setData(list_scroll);
		pickeryears.setSelected(0);

	}
	// 滚动选择器选中事件
	PickerScrollView.onSelectListener pickerListener = new PickerScrollView.onSelectListener() {
		@Override
		public void onSelect(Pickers pickers) {
			years=pickers.getShowConetnt();
		}
	};

	/**
	 *
	 * 方法: dispatchSingle <p>
	 * 描述: 没child的点击 <p>
	 * 参数: @param dragInfo <p>
	 * 返回: void <p>
	 * 异常  <p>
	 * 作者: wedcel wedcel@gmail.com <p>
	 * 时间: 2015年8月25日 下午5:30:40
	 */
	public void dispatchSingle(DragIconInfo dragInfo) {
		if (dragInfo == null) {
			return;
		}
		Toast.makeText(mContext, "点击了icon"+dragInfo.getName(), Toast.LENGTH_SHORT).show();


	}




	public boolean isValideEvent(MotionEvent ev, int scrolly) {
		return mCustomBehindParent.isValideEvent(ev,scrolly);
	}


	public void clearEditDragView() {
		mCustomBehindParent.clearDragView();
	}


}
