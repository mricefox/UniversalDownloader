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
import com.mricefox.mfdownloader.lib.DownloadingListener;
import com.mricefox.mfdownloader.lib.assist.L;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/4
 */
public class DownloadListAdapter extends RecyclerView.Adapter {
    private List<Download> downloadList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public DownloadListAdapter(List<Download> downloadList, Context context) {
        this.downloadList = downloadList;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, long id);
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder holder = new ItemViewHolder(
                LayoutInflater.from(context).inflate(R.layout.download_item, parent, false));

        return holder;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        L.d("onBindViewHolder pos:" + position);
        Download download = downloadList.get(position);
        download.setDownloadingListener(new DownloadingListener() {
            @Override
            public void onStart(long id) {
                L.d("id:" + id + "#onStart");
            }

            @Override
            public void onComplete(long id) {
                L.d("id:" + id + "#onComplete");
            }

            @Override
            public void onFailed(long id) {
                L.d("id:" + id + "#onFailed");
            }

            @Override
            public void onCancelled(long id) {
                L.d("id:" + id + "#onCancelled");
            }

            @Override
            public void onPaused(long id) {
                L.d("id:" + id + "#onPaused");
            }

            @Override
            public void onProgressUpdate(long id, long current, long total, long bytesPerSecond) {
                L.d("id:" + id + "#onProgressUpdate" + "#current" + current + "#total" +
                        total + "#%" + String.format("%.2f", (current + 0.0f) * 100 / total));
                ((ItemViewHolder) holder).progressBar.setProgress((int) ((current + 0.0f) / total * 100));
            }
        });
    }

    @Override
    public int getItemCount() {
        return downloadList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt, sizeTxt;
        ProgressBar progressBar;

        public ItemViewHolder(View itemView) {
            super(itemView);
            nameTxt = (TextView) itemView.findViewById(R.id.name_txt);
            sizeTxt = (TextView) itemView.findViewById(R.id.size_txt);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, getAdapterPosition(), getItemId());
                    }
                }
            });
        }
    }
}
