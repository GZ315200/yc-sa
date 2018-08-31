package com.unistack.tamboo.mgt.common.enums;

/**
 * @author Gyges Zean
 * @date 2018/6/25
 */
public enum EWebSocketTopic {

    TAMBOO_MANAGER_ALERT("/topic/alert"),
    TAMBOO_HOST_INFO("/topic/host"),
    TAMBOO_RUNTIME_DATA_RATE("/topic/dataRate"),
    TABOO_DATASOURCE_STATE("/topic/datasource")
    ;

    private String name;

    private EWebSocketTopic(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}


