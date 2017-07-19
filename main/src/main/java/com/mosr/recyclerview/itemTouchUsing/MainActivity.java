package com.mosr.recyclerview.itemTouchUsing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.mosr.recyclerview.itemTouchUsing.adapter.MainAdapter;
import com.mosr.recyclerview.itemTouchUsing.entity.InvitationInfo;
import com.mosr.recyclerview.itemTouchUsing.itemtouch.DefaultItemTouchHelpCallback;
import com.mosr.recyclerview.itemTouchUsing.itemtouch.DefaultItemTouchHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private RecyclerView recyclerView;

    /**
     * 数据源
     */
    private volatile List<InvitationInfo> userInfoList = null;
    /**
     * 数据适配器
     */
    private MainAdapter mainAdapter;
    /**
     * 滑动拖拽的帮助类
     */
    private DefaultItemTouchHelper itemTouchHelper;

    private boolean isMoveing = false;
    private int mSrcPosition;
    private int mTargetPosition;
    /**
     * 选中的item位置
     */
    private int mSelectPostion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_update).setOnClickListener(onClickListener);
        findViewById(R.id.btn_add).setOnClickListener(onClickListener);
        recyclerView = (RecyclerView) findViewById(R.id.rv_main);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // 必须要设置一个布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        mainAdapter = new MainAdapter(userInfoList, this, recyclerView);
        mainAdapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(mainAdapter);
        recyclerView.setOnTouchListener(this);
        // 模拟数据
        userInfoList = new ArrayList<>();
        userInfoList.add(new InvitationInfo("", "", 1));
        userInfoList.add(new InvitationInfo("", "", 0));
        userInfoList.add(new InvitationInfo("", "", 1));
        userInfoList.add(new InvitationInfo("", "", 0));
        userInfoList.add(new InvitationInfo("", "", 1));
        mainAdapter.notifyDataSetChanged(userInfoList);


        // 把ItemTouchHelper和itemTouchHelper绑定
        itemTouchHelper = new DefaultItemTouchHelper(onItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        mainAdapter.setItemTouchHelper(itemTouchHelper);

        itemTouchHelper.setDragEnable(false);
        itemTouchHelper.setSwipeEnable(false);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_update:
                    recyclerView.scrollToPosition(3);
                    break;
                case R.id.btn_add:
                    userInfoList.add(new InvitationInfo("", "", 0));
                    userInfoList.add(new InvitationInfo("", "", 1));
                    mainAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(mainAdapter.getItemCount() - 1);
                    break;
            }
        }
    };

    private DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener = new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int adapterPosition) {
            if (userInfoList != null) {
                userInfoList.remove(adapterPosition);
                mainAdapter.notifyItemRemoved(adapterPosition);
            }
        }

        @Override
        public synchronized boolean onMove(int srcPosition, int targetPosition) {
            mSrcPosition = srcPosition;
            mTargetPosition = targetPosition;
            Log.e("mosr", "srcPosition: " + srcPosition);
            Log.e("mosr", "targetPosition: " + targetPosition);
            if (userInfoList != null) {
                // 更换数据源中的数据Item的位置
                Collections.swap(userInfoList, srcPosition, targetPosition);

                // 更新UI中的Item的位置，主要是给用户看到交互效果
                mainAdapter.notifyItemMoved(srcPosition, targetPosition);
                mSelectPostion = targetPosition;
                isMoveing = true;
                return true;
            }
            return false;
        }
    };

    /**
     * RecyclerView的Item点击监听
     */
    private MainAdapter.OnItemClickListener onItemClickListener = new MainAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Toast.makeText(MainActivity.this, "第" + position + "被点击", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                notifyItemView();
                mainAdapter.setHeight(700, null);
                break;
        }
        return false;
    }

    private void notifyItemView() {
        if (!isMoveing)
            return;
        try {
            if (userInfoList.get(0).getIsPic() != 1)//第一行
                userInfoList.add(0, new InvitationInfo("", "", 1));
            if (userInfoList.get(userInfoList.size() - 1).getIsPic() != 1) //最后一行
                userInfoList.add(new InvitationInfo("", "", 1));
            for (int i = 0; i < userInfoList.size(); i++) {
                if (i > 0 && userInfoList.get(i).getIsPic() == 0 && userInfoList.get(i).getIsPic() == userInfoList.get(i - 1).getIsPic()) {
                    userInfoList.add(i, new InvitationInfo("", "", 1));
                    mSelectPostion = mSelectPostion < i ? mSelectPostion : mSelectPostion++;
                    i++;
                }
            }

            for (int i = 0; i < userInfoList.size(); i++) {
                if (userInfoList.get(i).getIsPic() == 1 && i > 0 && userInfoList.get(i).getIsPic() == userInfoList.get(i - 1).getIsPic())
                    if (TextUtils.isEmpty(userInfoList.get(i).getText())) {
                        userInfoList.remove(i);
                        mSelectPostion = mSelectPostion < i ? mSelectPostion : mSelectPostion--;
                        i--;
                    } else if (TextUtils.isEmpty(userInfoList.get(i - 1).getText())) {
                        userInfoList.remove(i - 1);
                        mSelectPostion = mSelectPostion < i - 1 ? mSelectPostion : mSelectPostion--;
                        i--;
                    }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            mainAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(mSelectPostion);
            isMoveing = false;
        }
    }
}
