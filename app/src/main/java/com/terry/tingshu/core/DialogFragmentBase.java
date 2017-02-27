package com.terry.tingshu.core;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.terry.tingshu.R;

public abstract class DialogFragmentBase extends DialogFragment {
    protected JetApplication mApp;

    protected View layoutView;
    protected boolean isLoadData = false;

    protected abstract int getLayoutResource();

    protected abstract void init();

    public void loadData() {
    }

    public Object getReturnValue() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int resourceId = getLayoutResource();
        layoutView = inflater.inflate(resourceId, container, false);
        init();
        return layoutView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mApp = (JetApplication) getActivity().getApplicationContext();
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Fullscreen);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLoadData)
            loadData();
    }

//    @Override
//    public void dismiss() {
//        boolean isDismiss = true;
//        if (mOnDismissListener != null) {
//            Object returnValue = getReturnValue();
//            isDismiss = mOnDismissListener.beforeDismiss(returnValue);
//        }
//        if (isDismiss) {
//            super.dismiss();
//        }
//    }
//
//    public interface OnDismissListener {
//        boolean beforeDismiss(Object returnValue);
//    }
//
//    protected OnDismissListener mOnDismissListener;
//
//    public void setmOnDismissListener(OnDismissListener listener) {
//        this.mOnDismissListener = listener;
//    }


}
