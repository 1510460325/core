package org.cn.wzy.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * Create by Wzy
 * on 2018/6/14 15:41
 * 不短不长八字刚好
 */
public class StreamsUtil {
    /**
     * 输入输出读写
     * @param inputStream
     * @param outputStream
     * @param close 是否关闭输入输出流
     * @throws IOException
     */
    public static final void write(InputStream inputStream, OutputStream outputStream, boolean close) throws IOException {
        byte[] by = new byte[1024];
        int n;
        while ((n = inputStream.read(by)) != -1) {
            outputStream.write(by,0,n);
        }
        if (close) {
            inputStream.close();
            outputStream.close();
        }
    }

    /**
     * 解析文件上传的所有文件
     * @param request
     * @param tempPath 临时缓冲目录
     * @return
     * @throws FileUploadException
     */
    public static final List<FileItem> getFileList(HttpServletRequest request, String tempPath) throws FileUploadException {
        File tmpFile = new File(tempPath);
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(1024 * 100);
        factory.setRepository(tmpFile);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");
        upload.setFileSizeMax(PropertiesUtil.LongValue("fileSizeMax"));
        upload.setSizeMax(PropertiesUtil.LongValue("fileTotalSizeMax"));
        return upload.parseRequest(request);
    }

    /**
     * 向HttpServletResponse中输出文件
     * @param fileName
     * @param inputStream
     * @param response
     * @throws IOException
     */
    public static final void download(String fileName,InputStream inputStream,HttpServletResponse response) throws IOException {
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-Disposition","attachment;filename=" + "\"" + new Date().getTime() + fileName +  "\"");
        ServletOutputStream outputStream  = response.getOutputStream();
        write(inputStream,outputStream,true);
    }
}
