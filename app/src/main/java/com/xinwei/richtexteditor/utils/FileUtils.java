package com.xinwei.richtexteditor.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Collection;

/**
 * 通用文件操作工具类
 * 不用加入 业务代码
 */
public class FileUtils {
    private static final String TAG = "Record_FileUtils";

    /**
     * 判断是文件，且文件存在
     */
    public static boolean isFileExists(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }

        File file = new File(fileName);
        return (file.exists() && file.isFile());
    }


    /**
     * 判断是文件夹，且文件夹存在
     */
    public static boolean isDirExists(String dirName) {
        if (null == dirName) {
            return false;
        }

        File dir = new File(dirName);
        return (dir.exists() && dir.isDirectory());
    }

    /**
     * 拷贝asset资源到目的路径
     *
     * @param descPath
     * @param assetPath
     * @return
     */
    public static boolean copyAssetFile(Context context, String descPath,
                                        String assetPath) {
        Log.d(TAG, "desc file = " + descPath + ", asset file = "
                + assetPath);

        boolean ret = true;
        InputStream in = null;
        FileOutputStream fos = null;

        try {
            // 读取程序包中资源
            in = context.getAssets().open(assetPath);
            int len = in.available();
            byte[] buffer = new byte[len];
            in.read(buffer);
            // 转存程序包资源
            fos = new FileOutputStream(descPath);
            fos.write(buffer, 0, len);
        } catch (IOException e) {
            ret = false;
            Log.e("", "", e);
        }

        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e1) {
            ret = false;
            Log.e("", "", e1);
        }

        try {
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e1) {
            ret = false;
            Log.e("", "", e1);
        }
        return ret;
    }

    /**
     * 从文件读取文本
     *
     * @param fileName
     * @param maxLength 长度限制，大于0时有限
     * @return
     */
    public static String readString(String fileName, int maxLength) {
        String ret = null;
        FileInputStream byteOut = null;
        try {
            File f = new File(fileName);
            byteOut = new FileInputStream(f);
            int len = byteOut.available();
            if (len > maxLength && maxLength > 0) {
                len = maxLength;
            }
            byte[] buffer = new byte[len];
            byteOut.read(buffer);
            ret = new String(buffer, "utf-8");
        } catch (IOException e) {
            Log.d(TAG, "load file failed. " + fileName);
        }

        try {
            if (null != byteOut) {
                byteOut.close();
            }
        } catch (IOException e) {
            Log.d(TAG, "", e);
        }
        return ret;
    }

    /**
     * 从文件读取文本
     *
     * @param fileName
     * @return
     */
    public static String readString(String fileName) {
        return readString(fileName, 0);
    }

    /**
     * 保存文本到文件
     *
     * @param fileName
     * @param text
     * @param isWipe   是否擦除旧内容
     * @return
     */
    public static int writeString(String fileName, String text, boolean isWipe) {
        if (TextUtils.isEmpty(fileName)) {
            return 0;
        }
        int file_len = 0;
        RandomAccessFile tmp_file = null;
        try {
            File file = new File(fileName);
            // 增加目录判断
            if (file.getParent() == null) {
                return file_len;
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            tmp_file = new RandomAccessFile(file, "rw");
            if (isWipe) {
                tmp_file.setLength(0);
            }
            tmp_file.seek(tmp_file.length());
            tmp_file.write(text.getBytes("utf-8"));
            file_len = (int) tmp_file.length();
        } catch (IOException e) {
            Log.d(TAG, "", e);
            return file_len;
        }

        try {
            if (null != tmp_file) {
                tmp_file.close();
            }
        } catch (IOException e) {
            Log.d(TAG, "", e);
        }
        return file_len;
    }

    /**
     * 检查文件长度
     *
     * @return
     */
    public static int getFileLength(String file) {
        if (null == file) {
            return 0;
        }
        return getFileLength(new File(file));
    }

    /**
     * 检查文件长度
     *
     * @return
     */
    public static int getFileLength(File file) {
        int file_len = 0;
        RandomAccessFile tmp_file = null;
        if (null == file) {
            return 0;
        }
        if (!file.exists()) {
            return 0;
        }
        try {
            tmp_file = new RandomAccessFile(file, "r");
            file_len = (int) tmp_file.length();
            Log.e("文件 lenght---", "---" + file_len);
        } catch (IOException e) {
            Log.e(TAG, "--", e);
        }

        try {
            if (null != tmp_file) {
                tmp_file.close();
            }
        } catch (IOException e) {
            Log.d(TAG, "", e);
        }
        return file_len;
    }

    /**
     * 删除指定路径的文件或者文件夹
     */
    public static boolean deleteFileFromPath(String sourceFilePath) {
        boolean isDel = true;
        if (null == sourceFilePath) {
            Log.d(TAG, "sourceFilePath is null");
            return isDel;
        }

        try {
            File file = new File(sourceFilePath);
            if (file.exists()) {
                if (file.isDirectory()) {
                    deleteDirectory(sourceFilePath);
                } else {
                    deleteFile(sourceFilePath);
                }
            }
        } catch (Exception e) {

            Log.e(TAG, "", e);
            isDel = false;
        }
        return isDel;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static boolean copyFile(String oldPath, String newPath) {
        boolean ret = false;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                File file = new File(newPath);
                // 增加目录判断
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }

                RandomAccessFile fs = new RandomAccessFile(newPath, "rw");
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
//					System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                fs.close();
                inStream.close();
                ret = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }

        return ret;
    }

    /**
     * 删除指定路径的文件
     *
     * @param filePath
     */
    private static boolean deleteFile(String filePath) {
        if (null == filePath) {
            Log.d(TAG, "filePath is null");
            return false;
        }
        boolean flag = false;
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                flag = file.delete();
            }
        } catch (Exception e) {
            Log.e("", "", e);
        }
        return flag;
    }


    /**
     * 删除指定路径的文件夹
     *
     * @param directoryPath 文件夹路径
     * @return 删除是否成功
     */
    public static boolean deleteDirectory(String directoryPath) {
        if (!directoryPath.endsWith(File.separator)) {
            directoryPath = directoryPath + File.separator;
        }

        File dirFile = new File(directoryPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        int count = 0;
        if (null != files) {
            count = files.length;
        }
        for (int i = 0; i < count; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean deleteFilesInDir(String directoryPath) {
        if (!directoryPath.endsWith(File.separator)) {
            directoryPath = directoryPath + File.separator;
        }

        File dirFile = new File(directoryPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        int count = 0;
        if (null != files) {
            count = files.length;
        }
        for (int i = 0; i < count; i++) {
            // 只删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        return flag;
    }


    /**
     * 返回byte的数据大小对应的文本
     *
     * @param size
     * @return
     */
    public static String getDataSize(long size) {
        DecimalFormat formater = new DecimalFormat("####0.0");
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            float kbsize = size / 1024f;
            return formater.format(kbsize) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            float mbsize = size / 1024f / 1024f;
            return formater.format(mbsize) + "MB";
        } else if (size < 1024 * 1024 * 1024 * 1024) {
            float gbsize = size / 1024f / 1024f / 1024f;
            return formater.format(gbsize) + "GB";
        } else {
            return "size: error";
        }
    }

    /**
     * 如果存在同名文件，重命名会返回false
     *
     * @param oldName
     * @param newName
     * @return
     */
    public static boolean rename(String oldName, String newName) {
        boolean b = false;

        try {
            File oldFile = new File(oldName);
            File newFile = new File(newName);
            b = oldFile.renameTo(newFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }


    /**
     * @param pathDir 文件所在路径
     * @param oldName
     * @param newName
     * @return
     */
    public static boolean rename(String pathDir, String oldName, String newName) {
        boolean b = false; // 文件重命名是否成功

        try {
            File oldFile = new File(pathDir, oldName);
            File newFile = new File(pathDir, newName);
            b = oldFile.renameTo(newFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }


    /**
     * 复制文件--把源文件复制到目标文件中
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     */
    @SuppressWarnings("resource")
    public static void copyFile(File sourceFile, File targetFile) {
        if (sourceFile == null || !sourceFile.exists()
                || !sourceFile.canRead() || !sourceFile.canWrite()
                || targetFile == null) {
            return;
        }
        if (!sourceFile.isDirectory()) {
            FileChannel fci = null;
            FileChannel fco = null;
            if (!targetFile.exists()) {
                targetFile = createFile(targetFile);
            }
            if (targetFile == null) {
                return;
            }
            try {
                fci = new FileInputStream(sourceFile).getChannel();
                fco = new FileOutputStream(targetFile).getChannel();
                fco.transferFrom(fci, 0, fci.size());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if (fci != null) {
                        fci.close();
                    }
                    if (fco != null) {
                        fco.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }

        }
        if (sourceFile.isDirectory()) {
            if (!targetFile.mkdirs()) {
                return;
            }
            File[] files = sourceFile.listFiles();
            if (files != null) {
                if (files.length == 0) {
                    return;
                }
                for (int i = 0; i < files.length; i++) {
                    copyFile(files[i], new File(targetFile, files[i].getName()));
                }
            }
        }
    }

    public static File createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    /**
     * 从文件中读取内容
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath, String encoding) {
        Log.d("开始读取文件:{}", filePath);
        try {
            FileInputStream fis = new FileInputStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, encoding));
            String s = "";
            StringBuffer sb = new StringBuffer();
            while ((s = br.readLine()) != null)
                sb.append(s + "\n");
            br.close();
            return sb.toString();
        } catch (Exception e) {
            Log.e("读取文件失败：", e.toString());
            return null;
        }
    }

    /**
     * 以默认的utf-8编码读取文件
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        return readFile(filePath, "utf-8");
    }

    public static boolean parentDirExists(File targetFile) {

        return targetFile.getParentFile().exists();
    }


    /**
     * 获取文件扩展名
     */
    public static String suffix(String filename) {
        int index = filename.lastIndexOf(".");

        if (index == -1) {
            return null;
        }
        String result = filename.substring(index + 1);
        return result;
    }


    public static String getFormatSize(long size) {
        String result;
        if (size >= 1024 * 1024 * 1024) {
            result = String.format("%.1f", (float) size / (1024 * 1024 * 1024));
            result = result + "GB";
        } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
            result = String.format("%.1f", (float) size / (1024 * 1024));
            result = result + "MB";
        } else if (size >= 1024 && size < 1024 * 1024) {
            result = String.format("%.1f", (float) size / 1024);
            result = result + "KB";
        } else {
            result = Long.toString(size);
            result = result + "B";
        }
        return result;
    }
}
