package org.sebbas.android.memegenerator.interfaces;

public interface DataLoaderCallback {

    void onDataLoadSuccess();
    void onConnectionUnavailable();
    void onDataLoadError();
    void onFilterComplete();
    void onLineItemsComplete();
}