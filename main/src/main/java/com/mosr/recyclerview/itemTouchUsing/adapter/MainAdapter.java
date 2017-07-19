/*
 * Copyright © Yolanda. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mosr.recyclerview.itemTouchUsing.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mosr.recyclerview.itemTouchUsing.R;
import com.mosr.recyclerview.itemTouchUsing.entity.InvitationInfo;
import com.mosr.recyclerview.itemTouchUsing.itemtouch.DefaultItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>主页面的List的适配器</p>
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainContentViewHolder> {
    /**
     * Item点击监听
     */
    private OnItemClickListener mItemOnClickListener;
    /**
     * 数据
     */
    private List<InvitationInfo> userInfos = null;

    /**
     * Item拖拽滑动帮助
     */
    private DefaultItemTouchHelper itemTouchHelper;

    private int mHeight = 700;

    private Context mContext;

    private Drawable mDrawable;
    private int mPostion;

    private boolean isLong = false;

    private RecyclerView mRecyclerView;
    private ArrayList<MainContentViewHolder> mList;

    public MainAdapter() {
    }

    public MainAdapter(List<InvitationInfo> userInfos, Context mContext, RecyclerView mRecyclerView) {
        this.userInfos = userInfos;
        this.mContext = mContext;
        this.mRecyclerView = mRecyclerView;
        mList = new ArrayList<>();
        setEditTextDrawable(0);
    }

    public void notifyDataSetChanged(List<InvitationInfo> userInfos) {
        this.userInfos = userInfos;
        super.notifyDataSetChanged();
    }

    public void setItemTouchHelper(DefaultItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemOnClickListener = onItemClickListener;
    }

    public void setEditTextDrawable(int resId) {
        mDrawable = mContext.getResources().getDrawable(resId == 0 ? android.R.drawable.ic_menu_edit : resId);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(),
                mDrawable.getIntrinsicHeight());
    }

    @Override
    public MainContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType == 0 ? com.mosr.recyclerview.itemTouchUsing.R.layout.item_img : com.mosr.recyclerview.itemTouchUsing.R.layout.item_text, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(MainContentViewHolder holder, int position) {
        holder.setData();
        mList.add(holder);
    }

    @Override
    public int getItemViewType(int position) {
        return userInfos.get(position).getIsPic();
    }

    @Override
    public int getItemCount() {
        return userInfos == null ? 0 : userInfos.size();
    }


    public InvitationInfo getData(int position) {
        if (position < 0)
            return null;
        return userInfos.get(position);
    }


    public int getHeight() {
        return mHeight;
    }

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

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class MainContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener {
        private View itemView;
        private EditText edt_input;

        private ImageView img_pic;

        private ImageButton imb_move, imb_delete;

        private RelativeLayout rel_parent;

        private int mViewType;

        public MainContentViewHolder(View itemView, int viewType) {
            super(itemView);
            this.itemView = itemView;
            this.mViewType = viewType;
            itemView.setOnClickListener(this);
            switch (mViewType) {
                case 0:
                    rel_parent = (RelativeLayout) itemView.findViewById(com.mosr.recyclerview.itemTouchUsing.R.id.rel_parent);
                    img_pic = (ImageView) itemView.findViewById(com.mosr.recyclerview.itemTouchUsing.R.id.img_pic);
                    imb_move = (ImageButton) itemView.findViewById(com.mosr.recyclerview.itemTouchUsing.R.id.imb_move);
                    imb_delete = (ImageButton) itemView.findViewById(com.mosr.recyclerview.itemTouchUsing.R.id.imb_delete);
                    imb_move.setOnLongClickListener(this);
                    imb_delete.setOnClickListener(this);
                    break;
                default:
                    edt_input = (EditText) itemView.findViewById(com.mosr.recyclerview.itemTouchUsing.R.id.edt_input);
                    edt_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            userInfos.get(getAdapterPosition()).setText(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    edt_input.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_DEL
                                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                                if (v instanceof EditText)
                                    if (TextUtils.isEmpty(((EditText) v).getText().toString().trim()) && null != userInfos && userInfos.size() > 0) {
                                        if (getAdapterPosition() > 0 && userInfos.size() - 1 > getAdapterPosition()
                                                && (1
                                                == userInfos.get(getAdapterPosition() - 1).getIsPic()
                                                || userInfos.size() > getAdapterPosition() && 1
                                                == userInfos.get(getAdapterPosition() + 1).getIsPic())) {//中间item
                                            userInfos.remove(getAdapterPosition());
                                            notifyItemRemoved(getAdapterPosition());
                                        } else if (getAdapterPosition() == 0 && (userInfos.size() > 1 && 1
                                                == userInfos.get(getAdapterPosition() + 1).getIsPic())) {//头部item
                                            userInfos.remove(getAdapterPosition());
                                            notifyItemRemoved(getAdapterPosition());
                                        } else if (userInfos.size() - 1 == getAdapterPosition() && userInfos.size() > 1
                                                && userInfos.get(getAdapterPosition() - 1).getIsPic() == 1) {//尾部item
                                            userInfos.remove(getAdapterPosition());
                                            notifyItemRemoved(getAdapterPosition());
                                        }
                                    } else {
                                        int mLength = ((EditText) v).getText().toString().length();
                                        ((EditText) v).getText().delete(mLength, mLength);
                                    }
                            }
                            return false;
                        }
                    });
                    /**
                     * EditText得到和失去焦点时，自定义处理内容
                     */
                    edt_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (v instanceof EditText) {
                                boolean isEmpty = TextUtils.isEmpty(((EditText) v).getText().toString().trim());
                                ((EditText) v).setCompoundDrawables(null, null, !hasFocus && isEmpty ? mDrawable :
                                        null, null);
                                if (!hasFocus) {//关闭软键盘
                                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                }
                            }
                        }
                    });
                    break;

            }
        }

        /**
         * 给这个Item设置数据
         */
        public void setData() {
            switch (mViewType) {
                case 0:
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.height = mHeight;
                    rel_parent.setLayoutParams(layoutParams);
                    break;
                default:
                    InvitationInfo userInfo = getData(getAdapterPosition());
                    if (null == userInfo)
                        return;
                    if (!TextUtils.isEmpty(userInfo.getText())) {
                        edt_input.setText(userInfo.getText());
                        edt_input.setSelection(userInfo.getText().length());
                    } else
                        edt_input.setText("");

                    if (mHeight == 400) {
                        GradientDrawable mGradientDrawable = new GradientDrawable();
                        mGradientDrawable.setShape(GradientDrawable.RECTANGLE);
                        mGradientDrawable.setStroke(4, Color.BLUE, 16, 12);
                        itemView.setBackgroundDrawable(mGradientDrawable);
                    } else {
                        itemView.setBackgroundColor(Color.TRANSPARENT);
                    }
                    edt_input.setMaxLines(edt_input.getLineCount() > 2 && mHeight == 300 ? 2 : Integer.MAX_VALUE);
                    edt_input.setSelection(0);

                    edt_input.setCompoundDrawables(null, null, TextUtils.isEmpty(edt_input.getText().toString().trim()) ? mDrawable :
                            null, null);
                    break;
            }
        }

        @Override
        public void onClick(View view) {
            try {
                if (view == itemView && itemTouchHelper != null) {
                    mItemOnClickListener.onItemClick(view, getAdapterPosition());
                } else if (view.getId() == com.mosr.recyclerview.itemTouchUsing.R.id.imb_delete) {
                    if (MainAdapter.this.getItemViewType(getAdapterPosition() - 1) == 1
                            && MainAdapter.this.getItemViewType(getAdapterPosition() - 1) == MainAdapter.this.getItemViewType(getAdapterPosition() + 1)) {
                        if (TextUtils.isEmpty(userInfos.get(getAdapterPosition() - 1).getText())) {
                            userInfos.remove(getAdapterPosition() - 1);
                            notifyItemRemoved(getAdapterPosition() - 1);
                        } else if (TextUtils.isEmpty(userInfos.get(getAdapterPosition() + 1).getText())) {
                            userInfos.remove(getAdapterPosition() + 1);
                            notifyItemRemoved(getAdapterPosition() + 1);
                        }
                    }
                    userInfos.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP)
                setHeight(700, null);
            switch (view.getId()) {
                case R.id.imb_move:
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        setHeight(400, this);
                    itemTouchHelper.startDrag(this);
                    break;
//
            }
            return false;
        }

        @Override
        public boolean onLongClick(View v) {
            setHeight(400, this);
            return true;
        }
    }

}
