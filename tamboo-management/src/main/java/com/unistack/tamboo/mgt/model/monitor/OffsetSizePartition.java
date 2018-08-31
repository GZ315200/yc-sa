package com.unistack.tamboo.mgt.model.monitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

/**
 * @author Gyges Zean
 * @date 2018/7/31
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class OffsetSizePartition {

    private long size;

    private long offsetLag;

    private int partition;

    public OffsetSizePartition() {
    }

    public OffsetSizePartition(long size, long offsetLag, int partition) {
        this.size = size;
        this.offsetLag = offsetLag;
        this.partition = partition;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getOffsetLag() {
        return offsetLag;
    }

    public void setOffsetLag(long offsetLag) {
        this.offsetLag = offsetLag;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("size", size)
                .add("offsetLag", offsetLag)
                .add("partition", partition)
                .toString();
    }
}
