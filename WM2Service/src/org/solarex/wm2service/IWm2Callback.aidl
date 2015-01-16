package org.solarex.wm2service;
interface IWm2Callback{
    void onSuccess(String result);
    void onFailure(String errorMsg);
}