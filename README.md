# 图文混排发帖（完美复现汽车之家论坛发帖）
![图文混排发帖](http://img.blog.csdn.net/20170719105921343?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXE1Njk2OTk5NzM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
（效果图为最终项目中的）

**本文源码已经托管在GitHub上，欢迎Fork多多star。**[地址](https://github.com/StrangerMosr/RecyclerViewItemTouchUsing/tree/master)

**CSDN**[地址](http://blog.csdn.net/qq569699973/article/details/75390091)

最近重构一个项目，增加了一个新需求，要类似汽车之家的图文混排发帖，图片文字可自由移动位置（如效果图）

功能：图文混排，自由排列文字与图片的位置，图片之间自动加入输入框，两个输入框若相邻且有一个为空，则删除一个保留另外一个，若都有内容则不删除,删除文字时，若输入框内容为空，则删除整个输入框

实现：RecyclerView + ItemTouchHelper(为什么用RecyclerView，不用ListView，这个个人觉得RecyclerView的效果要比ListView好，汽车之家应该是ListView（猜测）)

代码：
首先要改造一下ItemTouchHelper这个类，这是Android提供的拖拽帮助类，在android.support.v7.widget.helper包下。
改造的原因：
![这里写图片描述](http://img.blog.csdn.net/20170719150501204?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXE1Njk2OTk5NzM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

图中的Callback,在下面的处理过程中需要使用，但是他是protected的，所以我们要想要拿出来就要这样：
![这里写图片描述](http://img.blog.csdn.net/20170719150810549?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXE1Njk2OTk5NzM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

新建一个package 包名如图要一样，然后新建一个class 集成ItemTouchHelper这个类。

```
public class MosrItemTouchHelper extends ItemTouchHelper {
    public MosrItemTouchHelper(Callback callback) {
        super(callback);
    }

    public Callback getCallback() {
        return mCallback;
    }
}
```
加上get方法就可以了。

```
public class DefaultItemTouchHelper extends MosrItemTouchHelper {

    private DefaultItemTouchHelpCallback itemTouchHelpCallback;

    public DefaultItemTouchHelper(DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener) {
        super(new DefaultItemTouchHelpCallback(onItemTouchCallbackListener));
        itemTouchHelpCallback = (DefaultItemTouchHelpCallback) getCallback();
    }

    /**
     * 设置是否可以被拖拽
     *
     * @param canDrag 是true，否false
     */
    public void setDragEnable(boolean canDrag) {
        itemTouchHelpCallback.setDragEnable(canDrag);
    }

    /**
     * 设置是否可以被滑动
     *
     * @param canSwipe 是true，否false
     */
    public void setSwipeEnable(boolean canSwipe) {
        itemTouchHelpCallback.setSwipeEnable(canSwipe);
    }
}
```
这个类用户设置拖拽，滑动动作的可用性。

```
public class DefaultItemTouchHelpCallback extends ItemTouchHelper.Callback {

    /**
     * Item操作的回调
     */
    private OnItemTouchCallbackListener onItemTouchCallbackListener;

    /**
     * 是否可以拖拽
     */
    private boolean isCanDrag = false;
    /**
     * 是否可以被滑动
     */
    private boolean isCanSwipe = false;

    public DefaultItemTouchHelpCallback(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.onItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    /**
     * 设置Item操作的回调，去更新UI和数据源
     *
     * @param onItemTouchCallbackListener
     */
    public void setOnItemTouchCallbackListener(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.onItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    /**
     * 设置是否可以被拖拽
     *
     * @param canDrag 是true，否false
     */
    public void setDragEnable(boolean canDrag) {
        isCanDrag = canDrag;
    }

    /**
     * 设置是否可以被滑动
     *
     * @param canSwipe 是true，否false
     */
    public void setSwipeEnable(boolean canSwipe) {
        isCanSwipe = canSwipe;
    }

    /**
     * 当Item被长按的时候是否可以被拖拽
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return isCanDrag;
    }

    /**
     * Item是否可以被滑动(H：左右滑动，V：上下滑动)
     *
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return isCanSwipe;
    }

    /**
     * 当用户拖拽或者滑动Item的时候需要我们告诉系统滑动或者拖拽的方向
     *
     * @param recyclerView
     * @param viewHolder
     *
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {// GridLayoutManager
            // flag如果值是0，相当于这个功能被关闭
            int dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlag = 0;
            // create make
            return makeMovementFlags(dragFlag, swipeFlag);
        } else if (layoutManager instanceof LinearLayoutManager) {// linearLayoutManager
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int orientation = linearLayoutManager.getOrientation();

            int dragFlag = 0;
            int swipeFlag = 0;

            // 为了方便理解，相当于分为横着的ListView和竖着的ListView
            if (orientation == LinearLayoutManager.HORIZONTAL) {// 如果是横向的布局
                swipeFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            } else if (orientation == LinearLayoutManager.VERTICAL) {// 如果是竖向的布局，相当于ListView
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
            return makeMovementFlags(dragFlag, swipeFlag);
        }
        return 0;
    }

    /**
     * 当Item被拖拽的时候被回调
     *
     * @param recyclerView     recyclerView
     * @param srcViewHolder    拖拽的ViewHolder
     * @param targetViewHolder 目的地的viewHolder
     *
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder srcViewHolder, RecyclerView.ViewHolder targetViewHolder) {
        if (onItemTouchCallbackListener != null) {
            return onItemTouchCallbackListener.onMove(srcViewHolder.getAdapterPosition(), targetViewHolder.getAdapterPosition());
        }
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (onItemTouchCallbackListener != null) {
            onItemTouchCallbackListener.onSwiped(viewHolder.getAdapterPosition());
        }
    }

    public interface OnItemTouchCallbackListener {
        /**
         * 当某个Item被滑动删除的时候
         *
         * @param adapterPosition item的position
         */
        void onSwiped(int adapterPosition);

        /**
         * 当两个Item位置互换的时候被回调
         *
         * @param srcPosition    拖拽的item的position
         * @param targetPosition 目的地的Item的position
         *
         * @return 开发者处理了操作应该返回true，开发者没有处理就返回false
         */
        boolean onMove(int srcPosition, int targetPosition);
    }
}
```
这个类用户处理拖拽，滑动等动作。


接下来，创建一个RecyclerView布局，初始化，设置布局管理器，设置适配器，
加入如下代码

```

        // 把ItemTouchHelper和itemTouchHelper绑定
        itemTouchHelper = new DefaultItemTouchHelper(onItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        mainAdapter.setItemTouchHelper(itemTouchHelper);

        itemTouchHelper.setDragEnable(false);
        itemTouchHelper.setSwipeEnable(false);
```

```
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
```

Adapter代码处理  指定View  设置onLongClickListener

```
    public void setHeight(int mHeight, RecyclerView.ViewHolder mViewHolder) {
        if (this.mHeight == mHeight)
            return;
        this.mHeight = mHeight;
        if (mHeight == 400)
            try {
                for (MainContentViewHolder viewHolder : mList) {
                    viewHolder.setData();
                }
            } catch (Exception e) {
            }
        else
            notifyDataSetChanged();
        mList.clear();
        if (null != mViewHolder)
            itemTouchHelper.startDrag(mViewHolder);

    }
```
**！！！因为拖拽时需求让图片的高度减少，所以要动态设置图片View 的高度，上述代码可见变小时不是用notifyDataSetChanged 而是通过ViewHolder的SetDate方法，是因为notifyDataSetChanged 会重新绘制布局导致ItemToucHelper失去焦点，谨记！！！**

当手离开屏幕时还原图片高度

```
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

```

处理图片之间的输入框，输入框与输入框之间的关系

```
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
```
核心部分都在上面了，由于是调研时写的Demo 所以很多地方不严谨，逻辑不清晰，借鉴者多加留意，参考我在GitHub上托管的源码。
如有问题Q我1515789527
