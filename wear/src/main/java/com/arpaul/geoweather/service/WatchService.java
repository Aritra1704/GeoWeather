package com.arpaul.geoweather.service;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by ARPaul on 17-09-2016.
 */
public class WatchService extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);

        for(DataEvent dataEvent : dataEventBuffer){
            if(dataEvent.getType() == DataEvent.TYPE_CHANGED){
                DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                String path = dataEvent.getDataItem().getUri().getPath();
                if(path.equals("/step-counter")){
                    int steps = dataMap.getInt("step-count");
                    long time = dataMap.getLong("timestamp");
                }
            }
        }
    }
}
