package com.unistack.tamboo.sa.dd2.constant;

/**
 * @author anning
 * @date 2018/7/20 下午5:57
 * @description: connector json key
 */
public class ConnectorConfig {

    //通用
    public static final String CONNECT_NAME = "name";
    public static final String CONNECT_CONFIG = "config";
    public static final String CONNECT_CLASS = "connector.class";
    public static final String TOPICS = "topics";
    public static final String TASKS_MAX = "tasks.max";
    private static final String TASKS_MAX_DOC = "connector的最大任务数，用户可以自行设置，但执行的最大任务数为topic的partition数" +
            "，目前没有暴露给用户此参数，type为int";

    //jdbc sink相关
    public static final String CONNECT_URL = "connection.url";
    public static final String CONNECT_USER = "connection.user";
    public static final String CONNECT_PASSWORD = "connection.password";
    public static final String TABLE_NAME_FORMAT = "table.name.format";
    private static final String TABLE_NAME_FORMAT_DOC = "定义输出到数据库的表名格式，如不设置则默认为{topic}，即把topic名称作为表名，" +
            "如设置为table1，则表名即为table1，可以在格式中添加{topic}，如kafka_{topic}，则表名为kafka_前缀加上topic的名称";
    public static final String AUTO_CREATE = "auto.create";
    private static final String AUTO_CREATE_DOC = "当表不存在时是否允许connector自行创建，type为boolean，默认为false";
    public static final String AUTO_EVOLVE = "auto.evolve";
    private static final String AUTO_EVOLVE_DOC = "当发现schema中不存在的字段时，是否允许表自动扩展字段，boolean类型，默认为false";
    public static final String PK_FIELDS = "pk.fields";
    private static final String PK_FIELDS_DOC = "pk.mode默认设置为record_value，此字段代表设置主键字段";
    public static final String PK_MODE = "pk.mode";
    private static final String PK_MODE_DOC = "主键策略，有none,kafka,record_key,record_value";
    public static final String WHITE_LIST = "fields.whitelist";
    private static final String WHITE_LIST_DOC = "字段白名单，那些字段导入表中";
    public static final String INSERT_MODE = "insert.mode";
    private static final String INSERT_MODE_DOC = "数据插入模式，分为insert、upsert、update三种，默认为insert";

    //es sink相关
    public static final String ES_CONNECTION_URL = "connection.url";
    private static final String ES_CONNECTION_URL_DOC = "连接es集群的http url";
    public static final String KEY_IGNORE = "key.ignore";
    private static final String KEY_IGNORE_DOC = "是否忽略kafka的key，如果设置为true，那么将会以数据的topic-partition-offset作为" +
            "es文档id,默认为false，表示将kafka record的key作为es文档的id";
    public static final String SCHEMA_IGNORE = "schema.ignore";
    private static final String SCHEMA_IGNORE_DOC = "是否忽略schema，如果设置为true，代表es自动匹配字段的类型，默认为false，即根据" +
            "schema registry中对应的topic schema创建mapping，建议用户自行创建index,index名称默认为topic名称，注意:topic名称必需全小写";
    public static final String TYPE_NAME = "type.name";
    private static final String TYPE_NAME_DOC = "es中Index下的type名称";

    /**
     *   file key
     */
    public static final String FILE = "file";
    private static final String FILE_DOC = "数据下发到的文件路径名称";

    /**
     * hdfs key
     */
    public static final String FLUSH_SIZE = "flush.size";
    private static final String FLUSH_SIZE_DOC = "写入hdfs文件之前需要的record数量";

    //schema相关
    public static final String KEY_CONVERTER_SCHEMAS_ENABLE = "key.converter.schemas.enable";
    private static final String KEY_CONVERTER_SCHEMAS_ENABLE_DOC = "kafka record 中的key是否使用schema，boolean类型，默认为true";
    public static final String VALUE_CONVERTER_SCHEMAS_ENABLE = "value.converter.schemas.enable";
    private static final String VALUE_CONVERTER_SCHEMAS_ENABLE_DOC = "kafka record 中的value是否使用schema，boolean类型，默认为true";
    public static final String KEY_SCHEMA_REGISTRY_URL = "key.converter.schema.registry.url";
    private static final String KEY_SCHEMA_REGISTRY_URL_DOC = "key用到的schema registry url";
    public static final String VALUE_SCHEMA_REGISTRY_URL = "value.converter.schema.registry.url";
    private static final String VALUE_SCHEMA_REGISTRY_URL_DOC = "value用到的schema registry url";
    public static final String KEY_CONVERTER = "key.converter";
    private static final String KEY_CONVERTER_DOC = "key用的转换器，默认为JSON转换器，如果要使用schema，则必须要使用AVRO转换器";
    public static final String VALUE_CONVERTER = "value.converter";
    private static final String VALUE_CONVERTER_DOC = "value用的转换器，默认为JSON转换器，如果要使用schema，则必须要使用AVRO转换器";

}
