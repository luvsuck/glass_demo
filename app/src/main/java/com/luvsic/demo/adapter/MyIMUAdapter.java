package com.luvsic.demo.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.luvsic.demo.R;
import com.luvsic.demo.bean.ImageEntity;
import com.rokid.glass.imusdk.core.ui.recyclerview.BaseAdapter;
import com.rokid.glass.imusdk.core.ui.recyclerview.BaseViewHolder;

import java.io.File;

/**
 * @param
 * @author zyy
 * @description
 * @return
 * @time 2021/10/22 16:09
 */
public class MyIMUAdapter extends BaseAdapter<ImageEntity, MyIMUAdapter.ImageViewHolder> {
    private static String TAG = "ImageAdapter";

    public MyIMUAdapter() {
        this(R.layout.task_recycler_item);
    }

    public MyIMUAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull ImageViewHolder viewHolder, ImageEntity item) {
        viewHolder.img.setImageURI(Uri.fromFile(new File(item.getImageUrl())));
        viewHolder.itemTitle.setText(item.getTitle());
    }

    @Override
    protected void focusPosition(@NonNull ImageViewHolder viewHolder, ImageEntity item) {
        viewHolder.imgBg.setBackgroundResource(R.drawable.item_select);
        viewHolder.itemContent.setScaleX(1.1f);
        viewHolder.itemContent.setScaleY(1.1f);
        Log.e(TAG, "focusPosition = " + item);
    }

    @Override
    protected void normalPosition(@NonNull ImageViewHolder viewHolder, ImageEntity item) {
//        viewHolder.imgBg.setBackgroundResource(R.drawable.item_normal);
        viewHolder.itemContent.setScaleX(1.0f);
        viewHolder.itemContent.setScaleY(1.0f);
        Log.e(TAG, "normalPosition = " + item);
    }

    static class ImageViewHolder extends BaseViewHolder {
        ImageView img;
        ImageView imgBg;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.image_item);
            imgBg = itemView.findViewById(R.id.image_bg);
        }
    }
}