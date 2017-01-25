package com.arellomobile.mvp.sample.github.ui.adapters;

import android.support.v7.widget.RecyclerView;

import com.arellomobile.mvp.MvpDelegate;

/**
 * Date: 26.01.2016
 * Time: 17:26
 *
 * @author Yuri Shmakov
 */
public abstract class MvpBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private MvpDelegate<? extends MvpBaseAdapter> mMvpDelegate;
	private MvpDelegate<?> mParentDelegate;
	private String mChildId;

	public MvpBaseAdapter(MvpDelegate<?> parentDelegate, String childId) {
		mParentDelegate = parentDelegate;
		mChildId = childId;

		getMvpDelegate().onCreate();
	}

	public MvpDelegate getMvpDelegate() {
		if (mMvpDelegate == null) {
			mMvpDelegate = new MvpDelegate<>(this);
			mMvpDelegate.setParentDelegate(mParentDelegate, mChildId);

		}
		return mMvpDelegate;
	}
}
