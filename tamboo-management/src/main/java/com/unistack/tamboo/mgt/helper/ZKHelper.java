package com.unistack.tamboo.mgt.helper;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

/**
 * ZK工具类
 */
public class ZKHelper {

    /**
     * 获取ZK连接
     *
     * @param zkUrl
     * @return
     */
    public static ZkClient getZkClient(String zkUrl) {
         ZkClient zkClient = new ZkClient(zkUrl, 30000, 30000, new ZkSerializer() {
            @Override
            public byte[] serialize(Object data) throws ZkMarshallingError {
                return data.toString().getBytes();
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                return new String(bytes);
            }
        });
        return zkClient;
    }

    public static void close(ZkClient zkClient) {
        if (zkClient != null) {
            zkClient.close();
        }
    }


}
