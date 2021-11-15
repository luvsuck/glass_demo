package com.luvsic.demo.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.luvsic.demo.R;
import com.luvsic.demo.bean.IMUImageItem;
import com.rokid.glass.imusdk.core.ui.recyclerview.BaseAdapter;
import com.rokid.glass.imusdk.core.ui.recyclerview.BaseViewHolder;

import java.io.File;

/**
 * @author zyy
 * @since 2021/10/13 10:17
 */
public class IMUImageAdapter extends BaseAdapter<IMUImageItem, IMUImageAdapter.ImageViewHolder> {
    private static String TAG = "ImageAdapter";

    public IMUImageAdapter() {
        this(R.layout.image_recycler_item);
    }

    public IMUImageAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull ImageViewHolder viewHolder, IMUImageItem item) {
        Log.d("adapter", item.getResId() + " | " + item.getTitle());
//        viewHolder.img.setImageResource(item.getResId());
        //"/storage/emulated/0/DCIM/Camera/照片_1634885495358.jpg"
        viewHolder.img.setImageURI(Uri.fromFile(new File("/storage/emulated/0/DCIM/Camera/照片_1634885495358.jpg")));
        viewHolder.itemTitle.setText(item.getTitle());
        viewHolder.tv2.setText(item.getTitle() + "测试包裹");
    }

    @Override
    protected void focusPosition(@NonNull ImageViewHolder viewHolder, IMUImageItem item) {
//        viewHolder.imgBg.setBackgroundResource(R.drawable.item_select);
        viewHolder.itemContent.setScaleX(1.1f);
        viewHolder.itemContent.setScaleY(1.1f);
        Log.e(TAG, "focusPosition = " + item);
    }

    @Override
    protected void normalPosition(@NonNull ImageViewHolder viewHolder, IMUImageItem item) {
//        viewHolder.imgBg.setBackgroundResource(R.drawable.item_normal);
        viewHolder.itemContent.setScaleX(1.0f);
        viewHolder.itemContent.setScaleY(1.0f);
        Log.e(TAG, "normalPosition = " + item);
    }

    static class ImageViewHolder extends BaseViewHolder {
        ImageView img;
        ImageView imgBg;
        TextView tv2;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.image_item);
            imgBg = itemView.findViewById(R.id.image_bg);
            tv2 = itemView.findViewById(R.id.tv_2);
        }
    }
}