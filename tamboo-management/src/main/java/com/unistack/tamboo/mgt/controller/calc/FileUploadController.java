//package com.unistack.tamboo.mgt.controller.calc;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.unistack.tamboo.compute.utils.spark.CalcConfig;
//import com.unistack.tamboo.mgt.common.ServerResponse;
//import com.unistack.tamboo.mgt.model.calc.CustomFilter;
//import com.unistack.tamboo.mgt.model.calc.CustomLinkFilter;
//import com.unistack.tamboo.mgt.model.calc.DataFilter;
//import com.unistack.tamboo.mgt.service.calc.CustomLinkFilterServiceImpl;
//import com.unistack.tamboo.mgt.service.calc.FileUploadServiceImpl;
//import com.unistack.tamboo.mgt.service.calc.SendCustomFilterServiceImpl;
//import com.unistack.tamboo.mgt.utils.Base64Util;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import java.io.*;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
///**
// * @ClassName FileUploadController
// * @Description TODO  文件上传类
// * @Author unistack
// * @Date 2018/7/26 14:36
// * @Version 1.0
// */
//@RestController
//@RequestMapping("/calc/uploadJar")
//@CrossOrigin(origins = "*", maxAge = 3600)
//public class FileUploadController {
//
//    @Autowired
//    private CustomLinkFilterServiceImpl customLinkFilterService;
//    @Autowired
//    private FileUploadServiceImpl fileUploadService;
//    @Autowired
//    private SendCustomFilterServiceImpl sendCustomFilterService;
//    private final String JAR_USE = ".jar";
//
//    /**
//     * @param jarDesc 信息接收
//     * @return
//     * @throws
//     * @author cyq
//     * @methodDec jar包上传
//     * @date 2018/7/27 10:55
//     */
//    @RequestMapping(value = "/fileUpLoad", method = RequestMethod.POST)
//    public ServerResponse uploadFile(@RequestBody String jarDesc) throws Exception {
//        //String findPath = "tamboo-management" + File.separator + "config" + File.separator + "application.properties";
//        JSONObject jsonObject = (JSONObject) JSON.parse(jarDesc);
//        String fileUpload = jsonObject.getString("fileUpload");
//        int location = fileUpload.indexOf(",");
//        String fileUploadFinal = fileUpload.substring(location + 1);
//        String fileName = jsonObject.getString("fileName");
//        byte[] decodeBytes = Base64Util.decode(fileUploadFinal);
//        String result = "FAIL";
//        if (!fileUploadFinal.isEmpty()) {
//            //  Class.forName("com.unistack.tamboo.commons.utils.TambooConfig");
//              String path  = CalcConfig.CALC_UPLOAD_JAR_PATH;
//            // String i = TambooConfig.CALC_MULTISQL_CHECK_POINT_PATH;
//            // System.out.println(i);
//            //String path = readProperties(findPath);
//            System.out.println(path);
//            List<File> list = getAllFile(path.trim());
//            String suffixName = fileName.substring(fileName.lastIndexOf("."));
//            if (JAR_USE.equals(suffixName)) {
//                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//                String time = df.format(new Date());
//                for (File e : list) {
//                    if (e.getName().equals(fileName)) {
//                        fileName = time + fileName;
//                    }
//                }
//                String finalPath = null;
//                if (path.endsWith("/")) {
//                    finalPath = path + fileName;
//                } else {
//                    finalPath = path + "/" + fileName;
//                }
//                if (path.endsWith("\\")) {
//                    finalPath = path + fileName;
//                } else {
//                    finalPath = path + "\\" + fileName;
//                }
//                File newFile = new File(finalPath);
//                if (!newFile.getParentFile().exists()) {
//                    newFile.getParentFile().mkdirs();
//                }
//                BufferedOutputStream out = null;
//                CustomFilter customFilter = new CustomFilter();
//                try {
//                    out = new BufferedOutputStream(new FileOutputStream(finalPath));
//                    out.write(decodeBytes);
//                    result = "SUCCESS";
//                   /* UserSession userSession = SecUtils.findLoggedUserSession();
//                    String   username = userSession.getUsername();
//                    System.out.println(username);*/
//                    SimpleDateFormat dSimple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    Date dTime = dSimple.parse(dSimple.format(new Date()));
//                    customFilter.setJarName(fileName);
//                    customFilter.setJarUser("admin");
//                    customFilter.setJarUpdatetime(dTime);
//                    String jarDesc2 = jsonObject.getString("jarDesc");
//                    customFilter.setJarDesc(jarDesc2);
//                    customFilter.setJarClasspath(path);
//                    CustomFilter customFilterId = fileUploadService.saveCustomFilter(customFilter);
//                    if (customFilterId.getJarId() != 0) {
//                        JSONArray jsonArray = jsonObject.getJSONArray("filter");
//                        for (int j = 0; j < jsonArray.size(); j++) {
//                            DataFilter dataFilter = new DataFilter();
//                            JSONObject jsonObject1 = jsonArray.getJSONObject(j);
//                            parameter(dataFilter, jsonObject1);
//                            String dataFilterVal = sendCustomFilterService.getCustomFilterMess(dataFilter.getFilterType());
//                            CustomLinkFilter customLinkFilter = new CustomLinkFilter();
//                            if (dataFilterVal == null) {
//                                DataFilter dataFilterSave = sendCustomFilterService.saveDataFilter(dataFilter);
//                                if (customFilterId.getJarId() != 0 && dataFilterSave.getFilterId() != 0) {
//                                    int jarId = customFilterId.getJarId();
//                                    int filterId = dataFilterSave.getFilterId();
//                                    customLinkFilter.setJarId(jarId);
//                                    customLinkFilter.setFilterId(filterId);
//                                    customLinkFilter.setClassPath(path);
//                                    customLinkFilterService.saveCustomLinkFilter(customLinkFilter);
//                                }
//
//                            } else {
//                                if (customFilterId.getJarId() != 0) {
//                                    int jarId = customFilterId.getJarId();
//                                    customLinkFilter.setJarId(jarId);
//                                    customLinkFilter.setFilterId(Integer.parseInt(dataFilterVal));
//                                    customLinkFilter.setClassPath(path);
//                                    customLinkFilterService.saveCustomLinkFilter(customLinkFilter);
//                                }
//                                result = "已经存在此规则！";
//                                return ServerResponse.createByErrorMsg(result);
//                            }
//
//                        }
//                    }
//                    out.flush();
//                    out.close();
//                    return ServerResponse.createBySuccess(result);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            } else {
//                result = "MUST IS JAR";
//                return ServerResponse.createByErrorMsg(result);
//            }
//
//        } else {
//            result = "上传文件为空值";
//            return ServerResponse.createByErrorMsg(result);
//        }
//        return ServerResponse.createByErrorMsg(result);
//    }
//
//    /**
//     * @param dataFilter 过滤器数据   jsonObject1   解析的json
//     * @return
//     * @throws
//     * @author cyq
//     * @methodDec 对参数进行封装
//     * @date 2018/8/2 16:08
//     */
//    public void parameter(DataFilter dataFilter, JSONObject jsonObject1) {
//        String filterName = jsonObject1.getString("filterName");
//        String filterType = jsonObject1.getString("filterType");
//        String filterDesc = jsonObject1.getString("filterDesc");
//        String paramConf = jsonObject1.getString("paramConf");
//        paramConf = paramConf.replace("\\", "");
//        String paramDesc = jsonObject1.getString("paramDesc");
//        String filterEg = jsonObject1.getString("filterEg");
//        filterEg = filterEg.replace("\\", "");
//        String interacteParamConf = jsonObject1.getString("interacteParamConf");
//        interacteParamConf = interacteParamConf.replace("\\", "");
//        dataFilter.setFilterName(filterName);
//        dataFilter.setFilterType(filterType);
//        dataFilter.setFilterDesc(filterDesc);
//        dataFilter.setParamConf(paramConf);
//        dataFilter.setParamDesc(paramDesc);
//        dataFilter.setFilterAddTime(new Timestamp(System.currentTimeMillis()));
//        dataFilter.setFilterEg(filterEg);
//        dataFilter.setInteracteParamConf(interacteParamConf);
//        dataFilter.setIsActive("2");
//        dataFilter.setRemark1("admin");
//    }
//
//    /**
//     * @param findPath 路径
//     * @return String
//     * @throws
//     * @author cyq
//     * @methodDec 读取配置文件
//     * @date 2018/8/2 16:08
//     */
//    public String readProperties(String findPath) {
//        Properties properties = new Properties();
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(findPath.trim());
//            properties.load(fis);
//            String path = properties.getProperty("calc.upload.jar.path");
//            return path;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//
//    }
//
//    /**
//     * @param path 实际路径
//     * @return List<File>   存放集合
//     * @throws
//     * @author cyq
//     * @methodDec 通过路径拿文件
//     * @date 2018/7/27 10:55
//     */
//    public static List<File> getAllFile(String path) {
//        List<File> allFileList = new ArrayList<File>();
//        return getAllFile(new File(path), allFileList);
//    }
//
//    /**
//     * @param file 文件  allFileList  存储集合
//     * @return List<File>   存储文件
//     * @throws
//     * @author cyq
//     * @methodDec
//     * @date 2018/7/27 10:55
//     */
//    public static List<File> getAllFile(File file, List<File> allFileList) {
//        if (file.exists()) {
//            //判断文件是否是文件夹，如果是，开始递归
//            if (file.isDirectory()) {
//                File[] useFile = file.listFiles();
//                for (File file2 : useFile) {
//                    getAllFile(file2, allFileList);
//                }
//            } else {
//                allFileList.add(file);
//            }
//        }
//        return allFileList;
//    }
//}
