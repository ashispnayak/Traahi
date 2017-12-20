package com.invincix.khanam;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.huxq17.swipecardsview.BaseCardAdapter;

import com.invincix.khanam.MainPageCardModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ashis on 12/20/2017.
 */

public class MainPageCardAdapter extends BaseCardAdapter {

    private List<MainPageCardModel> modelList;
    private Context context;

    public MainPageCardAdapter(List<MainPageCardModel> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public int getCardLayoutId() {
        return R.layout.card_main_page;
    }

    @Override
    public void onBindData(int position, View cardview) {
        if(modelList==null || modelList.size()==0){
            return;
        }
        ImageView cardmainimage = (ImageView) cardview.findViewById(R.id.cardmainimage);
        cardmainimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        MainPageCardModel model = modelList.get(position);
        Picasso.with(context).load(model.getImage()).into(cardmainimage);
    }
}
