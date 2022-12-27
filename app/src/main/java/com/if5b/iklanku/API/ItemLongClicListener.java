package com.if5b.iklanku.API;

import android.view.View;

public interface ItemLongClicListener <T> {
    void onItemLongClick(View view, T data, int position);
}
