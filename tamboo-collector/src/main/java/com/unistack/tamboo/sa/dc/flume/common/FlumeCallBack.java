package com.unistack.tamboo.sa.dc.flume.common;


public interface FlumeCallBack  {
    /**
     *
     * A callback method the user can implement to provide asynchronous handling of request completion. This method will
     * be called when the record sent to the server has been acknowledged. Exactly one of the arguments will be
     * non-null.
     *
     * @param key
     * @param message
     */
    public void doResult(String key, String message);

}
