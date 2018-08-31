package com.unistack.tamboo.sa.dc.flume.common;

public enum  DcType {
    EXEC("com.unistack.tamboo.sa.dc.flume.source.ExecSource"),
    NETCAT("com.unistack.tamboo.sa.dc.flume.source.NetCatSource"),
    SYSLOG("com.unistack.tamboo.sa.dc.flume.source.SysLogSource"),
    TAILDIR("com.unistack.tamboo.sa.dc.flume.source.TailDirSource"),
    WEBSERVICE("com.unistack.tamboo.sa.dc.flume.source.WebServiceSource"),
    HTTP("com.unistack.tamboo.sa.dc.flume.source.HttpSource"),
    KAFKA("com.unistack.tamboo.sa.dc.flume.source.KafkSource");

    private String typeName;

    DcType(String typeName) {
        this.typeName = typeName;
    }

    /**
     * 根据类型的名称，返回类型的枚举实例。
     *
     * @param typeName 类型名称
     */
    public static DcType fromTypeName(String typeName) {
        for (DcType type : DcType.values()) {
            if (type.getTypeName().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

    public String getTypeName() {
        return this.typeName;
    }

}
