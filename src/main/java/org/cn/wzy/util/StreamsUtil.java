package org.cn.wzy.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import sun.misc.BASE64Decoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Create by Wzy
 * on 2018/6/14 15:41
 * 不短不长八字刚好
 */
public class StreamsUtil {

    public static final void write(InputStream inputStream, OutputStream outputStream, boolean close) throws IOException {
        byte[] by = new byte[1024];
        int n;
        while ((n = inputStream.read(by)) != -1) {
            outputStream.write(by, 0, n);
        }
        if (close) {
            inputStream.close();
            outputStream.close();
        }
    }


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

    public static final boolean download(String path, String fileName, String file) {
        File parent = new File(path);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        File target = new File(parent + "/" + fileName);
        if (!target.exists()) {
            try {
                target.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(file);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(target);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static final void download(String fileName, InputStream inputStream, HttpServletResponse response) throws IOException {
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-Disposition", "attachment;filename=" + "\"" + new Date().getTime() + fileName + "\"");
        ServletOutputStream outputStream = response.getOutputStream();
        write(inputStream, outputStream, true);
    }
}
