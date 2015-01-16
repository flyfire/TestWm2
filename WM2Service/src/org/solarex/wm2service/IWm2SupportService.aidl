package org.solarex.wm2service;
import org.solarex.wm2service.IWm2Callback;
interface IWm2SupportService{
    void setPackage(String packageName);
    void setCallback(IWm2Callback callback);
}