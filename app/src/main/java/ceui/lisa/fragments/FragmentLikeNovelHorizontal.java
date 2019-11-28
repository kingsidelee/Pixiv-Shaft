package ceui.lisa.fragments;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import ceui.lisa.R;
import ceui.lisa.activities.TemplateActivity;
import ceui.lisa.activities.ViewPagerActivity;
import ceui.lisa.adapters.LAdapter;
import ceui.lisa.adapters.NovelHorizontalAdapter;
import ceui.lisa.databinding.FragmentLikeIllustHorizontalBinding;
import ceui.lisa.http.NullCtrl;
import ceui.lisa.http.Retro;
import ceui.lisa.interfaces.OnItemClickListener;
import ceui.lisa.model.IllustsBean;
import ceui.lisa.model.ListIllustResponse;
import ceui.lisa.model.ListNovelResponse;
import ceui.lisa.model.NovelBean;
import ceui.lisa.model.UserDetailResponse;
import ceui.lisa.utils.DataChannel;
import ceui.lisa.utils.DensityUtil;
import ceui.lisa.utils.Params;
import ceui.lisa.view.LinearItemHorizontalDecoration;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static ceui.lisa.activities.Shaft.sUserModel;

public class FragmentLikeNovelHorizontal extends BaseBindFragment<FragmentLikeIllustHorizontalBinding> {

    private List<NovelBean> allItems = new ArrayList<>();
    private UserDetailResponse mUserDetailResponse;
    private NovelHorizontalAdapter mAdapter;

    public static FragmentLikeNovelHorizontal newInstance(UserDetailResponse userDetailResponse) {
        FragmentLikeNovelHorizontal fragmentLikeIllustHorizontal = new FragmentLikeNovelHorizontal();
        fragmentLikeIllustHorizontal.mUserDetailResponse = userDetailResponse;
        return fragmentLikeIllustHorizontal;
    }

    @Override
    void initLayout() {
        mLayoutID = R.layout.fragment_like_illust_horizontal;
    }

    @Override
    public void initView(View view) {
        baseBind.rootParentView.setVisibility(View.GONE);
        baseBind.recyclerView.addItemDecoration(new
                LinearItemHorizontalDecoration(DensityUtil.dp2px(8.0f)));
        FadeInLeftAnimator landingAnimator = new FadeInLeftAnimator();
        final long animateDuration = 400L;
        landingAnimator.setAddDuration(animateDuration);
        landingAnimator.setRemoveDuration(animateDuration);
        landingAnimator.setMoveDuration(animateDuration);
        landingAnimator.setChangeDuration(animateDuration);
        baseBind.recyclerView.setItemAnimator(landingAnimator);
        LinearLayoutManager manager = new LinearLayoutManager(
                mContext, LinearLayoutManager.HORIZONTAL, false);
        baseBind.recyclerView.setLayoutManager(manager);
        baseBind.recyclerView.setHasFixedSize(true);
        mAdapter = new NovelHorizontalAdapter(allItems, mContext);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, int viewType) {
                DataChannel.get().setNovelList(allItems);
                Intent intent = new Intent(mContext, TemplateActivity.class);
                intent.putExtra(Params.INDEX, position);
                intent.putExtra(TemplateActivity.EXTRA_FRAGMENT, "小说详情");
                intent.putExtra("hideStatusBar", false);
                startActivity(intent);
            }
        });
        baseBind.recyclerView.setAdapter(mAdapter);
        ViewGroup.LayoutParams layoutParams = baseBind.recyclerView.getLayoutParams();
        layoutParams.width = MATCH_PARENT;
        layoutParams.height = DensityUtil.dp2px(180.0f) + mContext.getResources()
                .getDimensionPixelSize(R.dimen.sixteen_dp);
        baseBind.recyclerView.setLayoutParams(layoutParams);
        baseBind.title.setText("小说收藏");
        baseBind.howMany.setText("查看全部");
        baseBind.howMany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, TemplateActivity.class);
                intent.putExtra(TemplateActivity.EXTRA_FRAGMENT,
                        baseBind.title.getText().toString());
                intent.putExtra(Params.USER_ID, mUserDetailResponse.getUser().getId());
                startActivity(intent);
            }
        });
    }

    @Override
    void initData() {
        Retro.getAppApi().getUserLikeNovel(sUserModel.getResponse().getAccess_token(),
                mUserDetailResponse.getUser().getId(), FragmentLikeIllust.TYPE_PUBLUC)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NullCtrl<ListNovelResponse>() {
                    @Override
                    public void success(ListNovelResponse listNovelResponse) {
                        if(listNovelResponse.getList().size() > 0) {
                            allItems.clear();
                            if (listNovelResponse.getList().size() > 10) {
                                allItems.addAll(listNovelResponse.getList().subList(0, 10));
                            } else {
                                allItems.addAll(listNovelResponse.getList());
                            }
                            mAdapter.notifyItemRangeInserted(0, allItems.size());
                            baseBind.rootParentView.setVisibility(View.VISIBLE);
                            Animation animation = new AlphaAnimation(0.0f, 1.0f);
                            animation.setDuration(400L);
                            baseBind.rootParentView.startAnimation(animation);
                        }
                    }
                });
    }
}