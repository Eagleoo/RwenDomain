package create.rwendomain.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SubPtrSwipe extends SwipeRefreshLayout {
    private boolean mMeasured = false;
    private boolean mRefresh = false;
    public SubPtrSwipe(Context context) {
        super(context);
    }
    public SubPtrSwipe(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mDownX = ev.getX();
//                mDownY = ev.getRawY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float moveX = ev.getX();
//                float moveY = ev.getRawY();
//                float diffX = Math.abs(mDownX-moveX );
//                float diffY = Math.abs(mDownY-moveY );
//                boolean isHorizon = Math.tan(diffY / diffX)< Math.tan(90.0);
//                Log.e("滑动的值",isHorizon+"     oooo");
//                if (isHorizon) {
//                    return true;
//                }
//
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                break;
            case MotionEvent.ACTION_MOVE: {
                if (intercepted) {
                    intercepted = true;
                } else {
                    intercepted = false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                intercepted = false;
                break;
            }
        }
        return intercepted;
    }



}
