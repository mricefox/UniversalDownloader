package com.mricefox.mfdownloader.sample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mricefox.mfdownloader.lib.Download;
import com.mricefox.mfdownloader.lib.assist.MFLog;
import com.mricefox.mfdownloader.lib.assist.StringUtil;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/4
 */
public class DownloadListAdapter extends RecyclerView.Adapter {
    private List<Download> downloadList;
    private Context context;
    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemLongClickListener onItemLongClickListener;

    public DownloadListAdapter(List<Download> downloadList, Context context) {
        this.downloadList = downloadList;
        this.context = context;
    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AdapterView.OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder holder = new ItemViewHolder(
                LayoutInflater.from(context).inflate(R.layout.download_item, null, false));
        holder.progressBar.setMax(100);
        return holder;
    }

    @Override
    public long getItemId(int position) {
//        return super.getItemId(position);
        MFLog.d("getItemId#pos:" + position + "#id:" + downloadList.get(position).getId());
        return downloadList.get(position).getId();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        MFLog.d("onBindViewHolder pos:" + position + " holder:" + holder);
        Download download = downloadList.get(position);
        ((ItemViewHolder) holder).nameTxt.setText(download.getFileName());
        long totalBytes = download.getTotalBytes();
        long currentBytes = download.getCurrentBytes();

        ((ItemViewHolder) holder).sizeTxt.setText(
                StringUtil.displayFilesize(currentBytes) + "/" + StringUtil.displayFilesize(totalBytes));
        switch (download.getStatus()) {
            case Download.STATUS_PAUSED:
                ((ItemViewHolder) holder).speedTxt.setText("paused");
                break;
            case Download.STATUS_RUNNING:
                ((ItemViewHolder) holder).speedTxt.setText(
                        StringUtil.displayFilesize(download.getBytesPerSecondNow()) + "/s");
                break;
            case Download.STATUS_PENDING:
                ((ItemViewHolder) holder).speedTxt.setText("pending");
                break;
            case Download.STATUS_SUCCESSFUL:
                ((ItemViewHolder) holder).speedTxt.setText("success");
                break;
            case Download.STATUS_FAILED:
                ((ItemViewHolder) holder).speedTxt.setText("failed");
                break;
            case Download.STATUS_CANCELLED:
                ((ItemViewHolder) holder).speedTxt.setText("cancelled");
                break;
        }
        if (totalBytes != 0)
            ((ItemViewHolder) holder).progressBar.setProgress((int) (currentBytes * 100 / totalBytes));
        else
            ((ItemViewHolder) holder).progressBar.setProgress(0);//in case of view holder reuse
    }

    @Override
    public int getItemCount() {
        return downloadList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt, sizeTxt, speedTxt;
        ProgressBar progressBar;

        public ItemViewHolder(View itemView) {
            super(itemView);
            nameTxt = (TextView) itemView.findViewById(R.id.name_txt);
            sizeTxt = (TextView) itemView.findViewById(R.id.size_txt);
            speedTxt = (TextView) itemView.findViewById(R.id.speed_txt);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(null, v, getAdapterPosition(), getItemId());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null)
                        return onItemLongClickListener.onItemLongClick(null, v, getAdapterPosition(), getItemId());
                    return false;
                }
            });
        }
    }
}
