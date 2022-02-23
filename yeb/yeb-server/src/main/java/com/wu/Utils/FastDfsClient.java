package com.wu.Utils;




import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FastDfsClient {

    private static Logger logger = LoggerFactory.getLogger(FastDfsClient.class);

    //初始化  ,读取文件，初始化属性
    static {
        try {
            String filePath = new ClassPathResource("fdfs_client.conf").getFile().getAbsolutePath();
            ClientGlobal.init(filePath);
        } catch (Exception e) {
            logger.error("FastDfsClient   init   fail", e);
        }
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    public static String[] upload(MultipartFile file) {
        String name = file.getOriginalFilename();
        logger.info("File name=", name);
        long startimeMillis = System.currentTimeMillis();
        String[] uploadResults = null;
        StorageClient storageClient = null;

        try {
            //获取storage客户端
            storageClient = getStorageClient();
            //上传
            uploadResults = storageClient.upload_file(file.getBytes(), name.substring(name.lastIndexOf(".") + 1), null);
        } catch (Exception e) {
            logger.error("上传失败", e.getMessage());
        }
        logger.info("上传时间：" + (System.currentTimeMillis() - startimeMillis) + "ms");
        //验证上传结果
        if (uploadResults == null) {
            logger.error("上传失败 ", storageClient.getErrorCode());
        }
        logger.info("上传成功");
        //上传成功返回响应的信息
        return uploadResults;
    }

    /**
     * 下载
     *
     * @param groupName
     * @param remoyeFileName
     * @return
     */
    public static InputStream downFile(String groupName, String remoyeFileName) {
        try {
            StorageClient storageClient = getStorageClient();
            byte[] fileByte = storageClient.download_file(groupName, remoyeFileName);
            InputStream ins = new ByteArrayInputStream(fileByte);
            return ins;
        } catch (Exception e) {
            logger.error("下载失败", e.getMessage());
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param groupName
     * @param remoyeFileName
     */
    public static void delteFile(String groupName, String remoyeFileName) {
        StorageClient storageClient = null;
        try {
            storageClient = getStorageClient();
            int i = storageClient.delete_file(groupName, remoyeFileName);
            logger.info("删除成功", i);
        } catch (Exception e) {
            logger.error("删除失败", e);
        }

    }

    /**
     * 查看文件信息
     *
     * @param groupName
     * @param remoyeFileName
     * @return
     */
    public static FileInfo getFile(String groupName, String remoyeFileName) {
        StorageClient storageClient = null;
        try {
            storageClient = getStorageClient();
            FileInfo fileInfo = storageClient.get_file_info(groupName, remoyeFileName);
            return fileInfo;
        } catch (Exception e) {
            logger.error("查看文件信息失败", e);
        }
        return null;

    }

    /**
     * 文件路径
     *
     * @return
     * @throws IOException
     */
    public static String getTrackerUrl() {
//          return "http://"+getTrackerServer().getInetSocketAddress().getHostString()+":8888/";
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = null;
        StorageServer  storeStorage = null;

        try {
            trackerServer = trackerClient.getTrackerServer();
             storeStorage = trackerClient.getStoreStorage(trackerServer);
        } catch (Exception e) {
            logger.error("查看文件路径失败", e);
        }
        return "http://"+storeStorage.getInetSocketAddress().getHostString()+":8888/";
    }





    /**
     * 生成Storage  客户端
     * @return
     * @throws IOException
     */
    private  static StorageClient  getStorageClient() throws IOException {
        TrackerServer trackerServer = getTrackerServer();
        StorageClient storageClient = new StorageClient(trackerServer,null);
        return storageClient;
    }




    /**
     * 生成Tracke服务器端
     * @return
     * @throws IOException
     */
    private  static TrackerServer getTrackerServer() throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        return trackerClient.getTrackerServer();
    }


}
