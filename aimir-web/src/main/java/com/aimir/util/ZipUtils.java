package com.aimir.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

//import org.apache.commons.lang.StringUtils;

public class ZipUtils {

    private static final int COMPRESSION_LEVEL = 8;

    private static final int BUFFER_SIZE = 1024 * 2;

    public static void main(String[] args) {
        ZipUtils util = new ZipUtils();
        try {
            String fileName ="d:\\temp\\billingday20110420133131.xls"; 
//            String zipName ="d:\\temp\\billingday2.zip";
//            File sourceFile = new File(fileName);
//            String sourcePath = "d:\\temp";

            List<String> list = new ArrayList<String>();
            list.add("billingday20110420133131.xls");
            list.add("billingday20110420142209.xls");
            list.add("billingday20110420150547.xls");
            list.add("billingday20110420152840.xls");

            util.zipEntry(fileName, "d:\\temp\\billingday0001.zip");
            util.zipEntry(list, "billingday0002.zip", "d:\\temp");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 지정된 폴더를 Zip 파일로 압축한다.
     * @param sourcePath - 압축 대상 디렉토리
     * @param output - 저장 zip 파일 이름
     * @throws Exception
     */
//    public static void zip(String sourcePath, String output) throws Exception {
//
//        // 압축 대상(sourcePath)이 디렉토리나 파일이 아니면 리턴한다.
//        File sourceFile = new File(sourcePath);
//        if (!sourceFile.isFile() && !sourceFile.isDirectory()) {
//            throw new Exception("압축 대상의 파일을 찾을 수가 없습니다.");
//        }
//
//        // output 의 확장자가 zip이 아니면 리턴한다.
//        if (!(StringUtils.substringAfterLast(output, ".")).equalsIgnoreCase("zip")) {
//            throw new Exception("압축 후 저장 파일명의 확장자를 확인하세요");
//        }
//
//        FileOutputStream fos = null;
//        BufferedOutputStream bos = null;
//        ZipOutputStream zos = null;
//
//        try {
//            fos = new FileOutputStream(output); // FileOutputStream
//            bos = new BufferedOutputStream(fos); // BufferedStream
//            zos = new ZipOutputStream(bos); // ZipOutputStream
//            zos.setLevel(COMPRESSION_LEVEL); // 압축 레벨 - 최대 압축률은 9, 디폴트 8
//            zipEntry(sourceFile, sourcePath, zos); // Zip 파일 생성
//            zos.finish(); // ZipOutputStream finish
//        } finally {
//            if (zos != null) {
//                zos.close();
//            }
//            if (bos != null) {
//                bos.close();
//            }
//            if (fos != null) {
//                fos.close();
//            }
//        }
//    }

    /**
     * 압축
     * @param sourceFile
     * @param sourcePath
     * @param zos
     * @throws Exception
     */
//    private static void zipEntry(File sourceFile, String sourcePath, ZipOutputStream zos) throws Exception {
//        // sourceFile 이 디렉토리인 경우 하위 파일 리스트 가져와 재귀호출
//        if (sourceFile.isDirectory()) {
//            if (sourceFile.getName().equalsIgnoreCase(".metadata")) { // .metadata 디렉토리 return
//                return;
//            }
//            File[] fileArray = sourceFile.listFiles(); // sourceFile 의 하위 파일 리스트
//            for (int i = 0; i < fileArray.length; i++) {
//                zipEntry(fileArray[i], sourcePath, zos); // 재귀 호출
//            }
//        } else { // sourcehFile 이 디렉토리가 아닌 경우
//            BufferedInputStream bis = null;
//            try {
//                String sFilePath = sourceFile.getPath();
//                String zipEntryName = sFilePath.substring(sourcePath.length() + 1, sFilePath.length());
//
//                bis = new BufferedInputStream(new FileInputStream(sourceFile));
//                ZipEntry zentry = new ZipEntry(zipEntryName);
//                zentry.setTime(sourceFile.lastModified());
//                zos.putNextEntry(zentry);
//
//                byte[] buffer = new byte[BUFFER_SIZE];
//                int cnt = 0;
//                while ((cnt = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
//                    zos.write(buffer, 0, cnt);
//                }
//                zos.closeEntry();
//            } finally {
//                if (bis != null) {
//                    bis.close();
//                }
//            }
//
//        }
//    }

    /**
     * 압축
     * @param sourceFile
     * @param sourcePath
     * @param zos
     * @throws Exception
     */
//    private static void zipEntries(File sourceFile, String sourcePath, ZipOutputStream zos) throws Exception {
//        // sourceFile 이 디렉토리인 경우 하위 파일 리스트 가져와 재귀호출
//        if (sourceFile.isDirectory()) {
//            if (sourceFile.getName().equalsIgnoreCase(".metadata")) { // .metadata 디렉토리 return
//                return;
//            }
//            File[] fileArray = sourceFile.listFiles(); // sourceFile 의 하위 파일 리스트
//            for (int i = 0; i < fileArray.length; i++) {
//                zipEntry(fileArray[i], sourcePath, zos); // 재귀 호출
//            }
//        } else { // sourcehFile 이 디렉토리가 아닌 경우
//            BufferedInputStream bis = null;
//            try {
//                String sFilePath = sourceFile.getPath();
//                String zipEntryName = sFilePath.substring(sourcePath.length() + 1, sFilePath.length());
//
//                bis = new BufferedInputStream(new FileInputStream(sourceFile));
//                ZipEntry zentry = new ZipEntry(zipEntryName);
//                zentry.setTime(sourceFile.lastModified());
//                zos.putNextEntry(zentry);
//
//                byte[] buffer = new byte[BUFFER_SIZE];
//                int cnt = 0;
//                while ((cnt = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
//                    zos.write(buffer, 0, cnt);
//                }
//                zos.closeEntry();
//            } finally {
//                if (bis != null) {
//                    bis.close();
//                }
//            }
//        }
//    }

    
    /**
     * Zip 파일의 압축을 푼다.
     *
     * @param zipFile - 압축 풀 Zip 파일
     * @param targetDir - 압축 푼 파일이 들어간 디렉토리
     * @param fileNameToLowerCase - 파일명을 소문자로 바꿀지 여부
     * @throws Exception
     */
//    public static void unzip(File zipFile, File targetDir, boolean fileNameToLowerCase) throws Exception {
//        FileInputStream fis = null;
//        ZipInputStream zis = null;
//        ZipEntry zentry = null;
//
//        try {
//            fis = new FileInputStream(zipFile); // FileInputStream
//            zis = new ZipInputStream(fis); // ZipInputStream
//
//            while ((zentry = zis.getNextEntry()) != null) {
//                String fileNameToUnzip = zentry.getName();
//                if (fileNameToLowerCase) { // fileName toLowerCase
//                    fileNameToUnzip = fileNameToUnzip.toLowerCase();
//                }
//
//                File targetFile = new File(targetDir, fileNameToUnzip);
//
//                if (zentry.isDirectory()) {// Directory 인 경우
//                    FileUtils.makeDir(targetFile.getAbsolutePath()); // 디렉토리 생성
//                } else { // File 인 경우
//                    // parent Directory 생성
//                    FileUtils.makeDir(targetFile.getParent());
//                    unzipEntry(zis, targetFile);
//                }
//            }
//        } finally {
//            if (zis != null) {
//                zis.close();
//            }
//            if (fis != null) {
//                fis.close();
//            }
//        }
//    }

    /**
     * Zip 파일의 한 개 엔트리의 압축을 푼다.
     *
     * @param zis - Zip Input Stream
     * @param filePath - 압축 풀린 파일의 경로
     * @return
     * @throws Exception
     */
    protected static File unzipEntry(ZipInputStream zis, File targetFile) throws Exception {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);

            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = zis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        return targetFile;
    }

//    private static void zipEntry2(File sourceFile, String sourcePath, ZipOutputStream zos) throws Exception {
//        // sourceFile 이 디렉토리인 경우 하위 파일 리스트 가져와 재귀호출
//        if (sourceFile.isDirectory()) {
//            if (sourceFile.getName().equalsIgnoreCase(".metadata")) { // .metadata 디렉토리 return
//                return;
//            }
//            File[] fileArray = sourceFile.listFiles(); // sourceFile 의 하위 파일 리스트
//            for (int i = 0; i < fileArray.length; i++) {
//                zipEntry(fileArray[i], sourcePath, zos); // 재귀 호출
//            }
//        } else { // sourcehFile 이 디렉토리가 아닌 경우
//            BufferedInputStream bis = null;
//            try {
//                String sFilePath = sourceFile.getPath();
//                String zipEntryName = sFilePath.substring(sourcePath.length() + 1, sFilePath.length());
//
//                bis = new BufferedInputStream(new FileInputStream(sourceFile));
////                ZipEntry zentry = new ZipEntry(zipEntryName);
//                ZipEntry zentry = new ZipEntry(sourceFile.getName());
//                System.out.println("====================================== name : " + sourceFile.getName());
//                //zentry.setTime(sourceFile.lastModified());
//                zos.putNextEntry(zentry);
//
//                byte[] buffer = new byte[BUFFER_SIZE];
//                int cnt = 0;
////                while ((cnt = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
////                    zos.write(buffer, 0, cnt);
////                }
//                
//                while ((cnt = bis.read(buffer)) != -1) {
//                    zos.write(buffer, 0, cnt);
//                }
//                zos.closeEntry();
//            } finally {
//                if (bis != null) {
//                    bis.close();
//                }
//                if (zos != null) {
//                    zos.close();
//                }
//            }
//        }
//    }

//    private static void zipEntry3(String sourceFile, String sourcePath, String zipName) throws Exception {
//
//        FileInputStream fis = null;
//        BufferedInputStream bis = null;
//        FileOutputStream fos = null;
//        ZipOutputStream zos = null;
//        try {
////            String zipEntryName = sourceFile.substring(sourcePath.length() + 1);
//            File file = new File(sourceFile);
//            String zipEntryName = file.getName();            
//
//            fis = new FileInputStream(sourceFile);
//            bis = new BufferedInputStream(fis);
//            fos = new FileOutputStream(zipName);
//            zos = new ZipOutputStream(fos);
////                ZipEntry zentry = new ZipEntry(zipEntryName);
//            ZipEntry zentry = new ZipEntry(zipEntryName);
//            System.out.println("====================================== name1 : " + zipEntryName);
//            //zentry.setTime(sourceFile.lastModified());
//            zos.putNextEntry(zentry);
//
//            byte[] buffer = new byte[BUFFER_SIZE];
//            int cnt = 0;
////                while ((cnt = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
////                    zos.write(buffer, 0, cnt);
////                }
//                
//            while ((cnt = bis.read(buffer)) != -1) {
//                zos.write(buffer, 0, cnt);
//            }
//            zos.closeEntry();
//        } finally {
//            if (zos != null) {
//                zos.close();
//            }
//            if (fos != null) {
//                fos.close();
//            }
//            if (bis != null) {
//                bis.close();
//            }
//            if (fis != null) {
//                fis.close();
//            }
//        }
//    }

//    private static void zipEntries3(List<String> fileList, String sourcePath, String zipName) throws Exception {
//
//        FileInputStream fis = null;
//        BufferedInputStream bis = null;
//        FileOutputStream fos = null;
//        ZipOutputStream zos = null;
//        
//        String zipEntryName = null;
//        String sourceFile = null;
//        int count = 0;
//        
//        try {
//            count = fileList.size();
//
//            fos = new FileOutputStream(zipName);
//            zos = new ZipOutputStream(fos);
//            
//            for (int i = 0 ; i < count ; i++) {
//                sourceFile = fileList.get(i);
//                try {
//                    zipEntryName = sourceFile.substring(sourcePath.length() + 1);
//
//                    fis = new FileInputStream(sourceFile);
//                    bis = new BufferedInputStream(fis);
////                        ZipEntry zentry = new ZipEntry(zipEntryName);
//                    ZipEntry zentry = new ZipEntry(zipEntryName);
//                    System.out.println("====================================== name "+ i +" : " + zipEntryName);
//                    //zentry.setTime(sourceFile.lastModified());
//                    zos.putNextEntry(zentry);
//
//                    byte[] buffer = new byte[BUFFER_SIZE];
//                    int cnt = 0;
////                        while ((cnt = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
////                            zos.write(buffer, 0, cnt);
////                        }
//                        
//                    while ((cnt = bis.read(buffer)) != -1) {
//                        zos.write(buffer, 0, cnt);
//                    }
//                    zos.closeEntry();
//                } catch (Exception e) {
//                    
//                } finally {
//                    if (bis != null) {
//                        bis.close();
//                    }
//                    if (fis != null) {
//                        fis.close();
//                    }
//                }
//               
//            }
//            
//        } finally {
//            if (zos != null) {
//                zos.close();
//            }
//            if (fos != null) {
//                fos.close();
//            }
//            if (bis != null) {
//                bis.close();
//            }
//            if (fis != null) {
//                fis.close();
//            }
//        }
//    }
//    
//    public static void zipping(String fromhuge, String tozip) throws IOException {
//
//        FileInputStream in = new FileInputStream(fromhuge);
//        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(tozip));
//        byte[] buffer = new byte[4096];
//        int bytes_read;
//        int totalhuge = 0;
//        int totalgzip = 0;
//
//        while ((bytes_read = in.read(buffer)) != -1)
//            out.write(buffer, 0, bytes_read);
//
//        in.close();
//        out.close();
////        in = new FileInputStream(fromhuge);
////        FileInputStream sizeIn = new FileInputStream(tozip);
////
////        while (in.read() != -1)
////            totalhuge++;
////
////        while (sizeIn.read() != -1)
////            totalgzip++;
////
////        in.close();
////        out.close();
////
////        System.out.println("파일" + fromhuge + "성공했습니다.^^" + tozip);
////        System.out.println(totalhuge + "bytes=원래사이즈------" + "         " + totalgzip + "bytes=새 사이즈");
//    }// zipping() 닫음.

    /**
     * @param fileList
     * @param zipName
     * @param filePath
     * @throws Exception
     */
    public void zipEntry(List<String> fileList, String zipName, String filePath) throws Exception {

        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        
        String sourceFile = null;
        int count = 0;
        
        try {
            count = fileList.size();

            fos = new FileOutputStream(filePath + File.separator + zipName);
            zos = new ZipOutputStream(fos);
            
            for (int i = 0 ; i < count ; i++) {
                sourceFile = fileList.get(i);
                zipEntry(filePath + File.separator + sourceFile, zos);
            }
        } finally {
            if (zos != null) {
                zos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * @param sourceFile
     * @param zipName
     * @throws Exception
     */
    public void zipEntry(String sourceFile, String zipName) throws Exception {

        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        try {
            fos = new FileOutputStream(zipName);
            zos = new ZipOutputStream(fos);
            
            zipEntry(sourceFile, zos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (zos != null) {
                zos.close();
            }
        }
    }
    
    private void zipEntry(String sourceFile, ZipOutputStream zos) throws Exception {

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        
        String zipEntryName = null;

        try {
            File file = new File(sourceFile);
            zipEntryName = file.getName();

            fis = new FileInputStream(sourceFile);
            bis = new BufferedInputStream(fis);

            ZipEntry zentry = new ZipEntry(zipEntryName);
            zentry.setTime(file.lastModified());
//            zentry.setCompressedSize(COMPRESSION_LEVEL);
            zentry.setCompressedSize(file.length());
            zos.putNextEntry(zentry);

            byte[] buffer = new byte[BUFFER_SIZE];
            int cnt = 0;

            while ((cnt = bis.read(buffer)) != -1) {
                zos.write(buffer, 0, cnt);
            }
            zos.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (bis != null) {
                bis.close();
            }
        }
               
    }
}