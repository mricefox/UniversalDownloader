package com.mricefox.mfdownloader.sample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mricefox.mfdownloader.lib.Download;
import com.mricefox.mfdownloader.lib.assist.L;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/4
 */
public class DownloadListAdapter extends RecyclerView.Adapter {
    private List<Download> downloadList;
    private Context context;
    private OnItemClickListener onItemClickListener;
//    private Map<Long, Integer> downloadIdMap;

    public DownloadListAdapter(List<Download> downloadList, Context context) {
        this.downloadList = downloadList;
        this.context = context;
//        downloadIdMap = new HashMap<>();
    }

//    public Map<Long, Integer> getDownloadIdMap() {
//        return downloadIdMap;
//    }
//
//    public void setDownloadIdMap(Map<Long, Integer> downloadIdMap) {
//        this.downloadIdMap = downloadIdMap;
//    }

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

//    @Override
//    public long getItemId(int position) {
////        return super.getItemId(position);
//        L.d("onItemClick getItemId#pos:" + position + "#id:" + downloadList.get(position).getId());
//        return downloadList.get(position).getId();
//    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        L.d("onBindViewHolder pos:" + position + " holder:" + holder);
        Download download = downloadList.get(position);
        ((ItemViewHolder) holder).nameTxt.setText(download.getId() + "#");
//        download.setDownloadingListener(listener);
        ((ItemViewHolder) holder).bindPositionSet.add(position);
//        Iterator<Integer> iterator = ((ItemViewHolder) holder).bindPositionSet.iterator();
//
//        while (iterator.hasNext()) {
//            int pos = iterator.next();
//            L.d(holder + "bind pos:" + pos);
//        }

    }

    @Override
    public int getItemCount() {
        return downloadList.size();
    }

//    @Override
//    public void onViewRecycled(RecyclerView.ViewHolder holder) {
//        L.d("onViewRecycled holder.pos:" + holder.getAdapterPosition());
//        super.onViewRecycled(holder);
//        if (holder.getItemId() != RecyclerView.NO_ID
//                && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
//            //holder has set downloading listener but not recycled
//            Download download = downloadList.get(holder.getAdapterPosition());
//            download.setDownloadingListener(null);
//        }
//    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt, sizeTxt;
        ProgressBar progressBar;
        Set<Integer> bindPositionSet;

        public ItemViewHolder(View itemView) {
            super(itemView);
            nameTxt = (TextView) itemView.findViewById(R.id.name_txt);
            sizeTxt = (TextView) itemView.findViewById(R.id.size_txt);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            bindPositionSet = new HashSet<>();

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
