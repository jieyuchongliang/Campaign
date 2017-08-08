package com.fujisoft.campaign.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.BaseActivity;
import com.fujisoft.campaign.MessageListActivity;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.bean.MessageListBean;
import com.fujisoft.campaign.utils.Constants;

import java.util.List;

/**
 * 消息列表用RecyclerAdapter
 */
public class MessageListAdapter extends RecyclerView.Adapter {
    private MessageListActivity context = null;
    private List<MessageListBean> messageDatas = null;
    /*    private static final int TYPE_ITEM = 0;
        private static final int TYPE_FOOTER = 1;*/
    private static final int TYPE_FOOTER = 3;
    private static final int invite_card = 0;
    private static final int mess_card = 1;
    private static final int null_card = 2;
    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE = 2;
    //默认状态
    private int loadMoreStatus = 3;
    public static final int PULLUP_LOAD_NULL = 3;
    private onItemClickListener mListener;
    private int messFlag;
    private int listLen;
    private int mesFlag;

    public MessageListAdapter(MessageListActivity context, List<MessageListBean> messageDatas, onItemClickListener mListener, int messFlag, int listLen, int mesFlag) {
        this.context = context;
        this.messageDatas = messageDatas;
        this.mListener = mListener;
        this.messFlag = messFlag;
        this.listLen = listLen;
        this.mesFlag = mesFlag;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == invite_card) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_company, parent, false);
            return new CompanyInviteHolder(itemView);
        } else if (viewType == mess_card) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task, parent, false);
            return new MessageItemHolder(itemView);
        } else if (viewType == null_card) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.null_invite, parent, false);
            return new MessageItemHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.load_more_footview_layout, parent, false);
            return new FooterViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MessageListAdapter.MessageItemHolder) {
            MessageListAdapter.MessageItemHolder itemViewHolder = (MessageListAdapter.MessageItemHolder) holder;
            MessageListBean messageListBean = messageDatas.get(position);
            itemViewHolder.image_title.setImageURI(Constants.PICTURE_BASE_URL + messageListBean.getPicBitmap());
            itemViewHolder.title.setText(messageListBean.getName());
            if ("1".equals(messageListBean.getRequiredFlags())) {
                itemViewHolder.select.setText(R.string.string_required_task_icon_text);
            } else {
                itemViewHolder.select.setText(R.string.string_not_required_task_icon_text);
                itemViewHolder.select.setVisibility(View.GONE);
            }

            switch (BaseActivity.getUserTaskStatus(String.valueOf(messageListBean.getCompleteFlag()), String.valueOf(messageListBean.getTaskStatus()))) {
                case 0:
                    itemViewHolder.share.setText(R.string.string_not_share_task_text);
                    break;
                case 1:
                    itemViewHolder.share.setText(R.string.string_shared_task_text);
                    break;
                case 2:
                    itemViewHolder.share.setText(R.string.finish);
                    break;
                default:
                    break;
            }

            itemViewHolder.flower_required.setText(messageListBean.getFlower());
//            itemViewHolder.explain.setText(messageListBean.getContent());

            itemViewHolder.explain.setText(messageListBean.getSubtitle());
            itemViewHolder.setId(messageListBean.getId());
            itemViewHolder.setCompleteFlag(messageListBean.getCompleteFlag());
            itemViewHolder.setTaskStatus(messageListBean.getTaskStatus());
            itemViewHolder.setTaskTitle(messageListBean.getName());
            itemViewHolder.setTaskContent(messageListBean.getContent());
            itemViewHolder.setTaskPicUrl(messageListBean.getPicUrl());
            itemViewHolder.setShareWay(messageListBean.getShareWay());

        } else if (holder instanceof MessageListAdapter.CompanyInviteHolder) {
            MessageListAdapter.CompanyInviteHolder itemViewHolder = (MessageListAdapter.CompanyInviteHolder) holder;
            final MessageListBean messageListBean = messageDatas.get(position);
            String state = messageListBean.getState();
            if (state.equals("1")) {
                itemViewHolder.btn_gone.setVisibility(View.GONE);
                itemViewHolder.btn_show.setVisibility(View.VISIBLE);
            } else if (state.equals("3")) {
                itemViewHolder.btn_show.setVisibility(View.GONE);
                itemViewHolder.btn_gone.setVisibility(View.VISIBLE);
            }
            itemViewHolder.enterpriseName.setText(messageListBean.getEnterpriseName());
            itemViewHolder.agree_mess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (context instanceof MessageListActivity) {
                            if (messageDatas.size() > 0) {
                                messageDatas.get(position).setState("2");
                                (context).checkMess(messageDatas.get(position).getState(), messageDatas.get(position).getEnterpriseId(), messageDatas.get(position).getUserId());

                                //notifyDataSetChanged();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            itemViewHolder.cancel_mess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof MessageListActivity) {
                        if (messageDatas.size() > 0) {
                            messageDatas.get(position).setState("3");
                            (context).checkMess(messageDatas.get(position).getState(), messageDatas.get(position).getEnterpriseId(), messageDatas.get(position).getUserId());
                            //notifyDataSetChanged();
                        }
                    }
                }
            });


        } else if (holder instanceof MessageListAdapter.FooterViewHolder)

        {
            MessageListAdapter.FooterViewHolder footerViewHolder = (MessageListAdapter.FooterViewHolder) holder;

            switch (loadMoreStatus) {
                case PULLUP_LOAD_NULL:
                    footerViewHolder.mPbLoad.setVisibility(View.GONE);
                    break;
                case PULLUP_LOAD_MORE:
                    footerViewHolder.mTvLoadText.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footerViewHolder.mTvLoadText.setText("正加载更多...");
                    break;
                case NO_LOAD_MORE:
                    footerViewHolder.mTvLoadText.setText("没有更多消息了");
                    footerViewHolder.mPbLoad.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (messageDatas != null) {
            return messageDatas.size() + 1;
        } else {
            return 0;
        }
    }

    /**
     * 决定元素的布局使用哪种类型
     *
     * @param position 数据源List的下标
     * @return 一个int型标志，传递给onCreateViewHolder的第二个参数
     */

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            //最后一个item设置为footerView
            return TYPE_FOOTER;
        } else {
            if (position < listLen && messFlag != 0) {
                return invite_card;
            } else if (mesFlag != 0) {
                return mess_card;
            } else {
                return null_card;
            }
        }
    }


    public class CompanyInviteHolder extends RecyclerView.ViewHolder {
        TextView enterpriseName;
        Button agree_mess;
        Button cancel_mess;
        LinearLayout btn_gone;
        LinearLayout btn_show;

        public CompanyInviteHolder(View itemView) {
            super(itemView);
            enterpriseName = (TextView) itemView.findViewById(R.id.enterpriseName);
            agree_mess = (Button) itemView.findViewById(R.id.agree_mess);
            cancel_mess = (Button) itemView.findViewById(R.id.cancel_mess);
            btn_gone = (LinearLayout) itemView.findViewById(R.id.btn_gone);
            btn_show = (LinearLayout) itemView.findViewById(R.id.btn_show);
        }
    }


    public class MessageItemHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView image_title;
        TextView title;
        TextView select;
        TextView flower_required;
        TextView explain;
        TextView share;
        int id;
        int completeFlag;
        int taskStatus;
        String taskTitle;
        String taskContent;
        String taskPicUrl;
        String shareWay;

        public MessageItemHolder(View itemView) {
            super(itemView);
            image_title = (SimpleDraweeView) itemView.findViewById(R.id.img);
            title = (TextView) itemView.findViewById(R.id.title);
            flower_required = (TextView) itemView.findViewById(R.id.integral);
            explain = (TextView) itemView.findViewById(R.id.content);
            select = (TextView) itemView.findViewById(R.id.mustFlags);
            share = (TextView) itemView.findViewById(R.id.btn_share);
            initListener(itemView);
        }

        private void initListener(View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(id, completeFlag, taskStatus, shareWay);
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onShareClick(id, completeFlag, taskStatus, taskTitle, taskContent, taskPicUrl, shareWay);
                }
            });

        }

        public void setId(int id) {
            this.id = id;
        }

        public void setCompleteFlag(int completeFlag) {
            this.completeFlag = completeFlag;
        }

        public void setTaskStatus(int taskStatus) {
            this.taskStatus = taskStatus;
        }

        public void setTaskTitle(String taskTitle) {
            this.taskTitle = taskTitle;
        }

        public void setTaskContent(String taskContent) {
            this.taskContent = taskContent;
        }

        public void setTaskPicUrl(String taskPicUrl) {
            this.taskPicUrl = taskPicUrl;
        }

        public void setShareWay(String shareWay) {
            this.shareWay = shareWay;
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mPbLoad;
        TextView mTvLoadText;
        LinearLayout mLoadLayout;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mPbLoad = (ProgressBar) itemView.findViewById(R.id.pbLoad);
            mTvLoadText = (TextView) itemView.findViewById(R.id.tvLoadText);
            mLoadLayout = (LinearLayout) itemView.findViewById(R.id.loadLayout);
        }
    }

    public void addFooterItem(List<MessageListBean> items) {
        messageDatas.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * 更新加载更多状态
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        loadMoreStatus = status;
        notifyDataSetChanged();
    }

    public void clear() {
        messageDatas.clear();
        this.notifyDataSetChanged();
    }

    public interface onItemClickListener {
        void onItemClick(int id, int completeFlag, int taskStatus, String shareWay);

        void onShareClick(int id, int completeFlag, int taskStatus, String taskTitle, String taskContent, String taskPicUrl, String shareWay);
    }
}