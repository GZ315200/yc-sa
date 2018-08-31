package com.unistack.tamboo.mgt.controller.monitor;

import com.google.common.collect.Lists;
import java.util.List;

public class MonitorQueue {

    private static List<DcMonitor> dcQueue = Lists.newArrayList();

    private static List<CalcMonitor> caclQueue = Lists.newArrayList();


    public static void setDcQueue(DcMonitor dcMonitor) {
        dcQueue.add(dcMonitor);
    }

    public static List<DcMonitor> getDcQueue() {
        return dcQueue;
    }

    public static void removeDcMonitor(DcMonitor dcMonitor) {
        dcQueue.remove(dcMonitor);
    }

    public static void setCaclQueue(CalcMonitor dcMonitor) {
        caclQueue.add(dcMonitor);
    }


    public static List<CalcMonitor> getCaclQueue() {
        return caclQueue;
    }

    public static void removeCalMonitor(CalcMonitor calcMonitor) {
        caclQueue.remove(calcMonitor);
    }
}