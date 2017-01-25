package com.arellomobile.mvp.sample.github.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.arellomobile.mvp.sample.github.R;
import com.arellomobile.mvp.sample.github.mvp.models.Repository;
import com.arellomobile.mvp.sample.github.mvp.presenters.RepositoryLikesPresenter;
import com.arellomobile.mvp.sample.github.mvp.presenters.RepositoryPresenter;
import com.arellomobile.mvp.sample.github.mvp.views.RepositoryLikesView;
import com.arellomobile.mvp.sample.github.mvp.views.RepositoryView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date: 22.01.2016
 * Time: 17:04
 *
 * @author Yuri Shmakov
 */
public class RepositoriesAdapter extends MvpBaseAdapter implements RepositoryLikesView {
	public static final int REPOSITORY_VIEW_TYPE = 0;
	private static final int PROGRESS_VIEW_TYPE = 1;

	@InjectPresenter(type = PresenterType.WEAK, tag = RepositoryLikesPresenter.TAG)
	RepositoryLikesPresenter mRepositoryLikesPresenter;

	private int mSelection = -1;
	private List<Repository> mRepositories;
	private List<Integer> mLiked;
	private List<Integer> mLikesInProgress;
	private boolean mMaybeMore;
	private OnScrollToBottomListener mScrollToBottomListener;
	private AdapterView.OnItemClickListener mOnItemClickListener;

	public RepositoriesAdapter(MvpDelegate<?> parentDelegate, OnScrollToBottomListener scrollToBottomListener) {
		super(parentDelegate, String.valueOf(0));

		mScrollToBottomListener = scrollToBottomListener;
		mRepositories = new ArrayList<>();
		mLiked = new ArrayList<>();
		mLikesInProgress = new ArrayList<>();
	}

	public void setOnItemClickListener(final AdapterView.OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}

	public void setRepositories(List<Repository> repositories, boolean maybeMore) {
		mRepositories = new ArrayList<>(repositories);
		dataSetChanged(maybeMore);
	}

	public void addRepositories(List<Repository> repositories, boolean maybeMore) {
		mRepositories.addAll(repositories);
		dataSetChanged(maybeMore);
	}

	public void updateLikes(List<Integer> inProgress, List<Integer> likedIds) {
		mLikesInProgress = new ArrayList<>(inProgress);
		mLiked = new ArrayList<>(likedIds);

		notifyDataSetChanged();
	}

	public void setSelection(int selection) {
		mSelection = selection;

		notifyDataSetChanged();
	}

	private void dataSetChanged(boolean maybeMore) {
		mMaybeMore = maybeMore;

		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		return position == mRepositories.size() ? PROGRESS_VIEW_TYPE : REPOSITORY_VIEW_TYPE;
	}

	public int getRepositoriesCount() {
		return mRepositories.size();
	}

	@Override
	public int getItemCount() {
		return mRepositories.size() + (mMaybeMore ? 1 : 0);
	}

	public Repository getItem(int position) {
		return mRepositories.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		if (viewType == PROGRESS_VIEW_TYPE) {
			return new ProgressHolder(new ProgressBar(parent.getContext()));
		} else {
			return new RepositoryHolder(LayoutInflater.from(parent.getContext())
																								.inflate(R.layout.item_repository, parent, false));
		}
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		if (getItemViewType(position) == REPOSITORY_VIEW_TYPE) {
			((RepositoryHolder) holder).bind(position, getItem(position));
		}
	}

	public interface OnScrollToBottomListener {
		void onScrollToBottom();
	}

	public static class ProgressHolder extends RecyclerView.ViewHolder {
		public ProgressHolder(final View itemView) {
			super(itemView);
		}
	}

	public class RepositoryHolder extends RecyclerView.ViewHolder implements RepositoryView {

		@InjectPresenter
		RepositoryPresenter mRepositoryPresenter;
		@BindView(R.id.item_repository_text_view_name)
		TextView nameTextView;
		@BindView(R.id.item_repository_image_button_like)
		ImageButton likeImageButton;
		View view;
		private Repository  mRepository;
		private MvpDelegate mMvpDelegate;

		RepositoryHolder(View view) {
			super(view);
			this.view = view;
			ButterKnife.bind(this, view);
		}

		@ProvidePresenter
		RepositoryPresenter provideRepositoryPresenter() {
			return new RepositoryPresenter(mRepository);
		}

		void bind(int position, Repository repository) {
			if (getMvpDelegate() != null) {
				getMvpDelegate().onSaveInstanceState();
				getMvpDelegate().onDetach();
				getMvpDelegate().onDestroyView();
				mMvpDelegate = null;
			}

			mRepository = repository;

			getMvpDelegate().onCreate();
			getMvpDelegate().onAttach();

			view.setOnClickListener(v -> {
				mOnItemClickListener.onItemClick(null, null, position, 0);
			});

			likeImageButton.setOnClickListener(v -> mRepositoryLikesPresenter.toggleLike(repository.getId()));

			boolean isInProgress = mLikesInProgress.contains(repository.getId());

			likeImageButton.setEnabled(!isInProgress);
			likeImageButton.setSelected(mLiked.contains(repository.getId()));
		}

		@Override
		public void showRepository(Repository repository) {
			nameTextView.setText(repository.getName());
		}

		@Override
		public void updateLike(boolean isInProgress, boolean isLiked) {
			// pass
		}

		@Override
		public void setBackgroundColor(final int color) {
			view.setBackgroundColor(color);
		}

		MvpDelegate getMvpDelegate() {
			if (mRepository == null) {
				return null;
			}

			if (mMvpDelegate == null) {
				mMvpDelegate = new MvpDelegate<>(this);
				mMvpDelegate.setParentDelegate(RepositoriesAdapter.this.getMvpDelegate(), String.valueOf(mRepository.getId()));

			}
			return mMvpDelegate;
		}
	}
}
