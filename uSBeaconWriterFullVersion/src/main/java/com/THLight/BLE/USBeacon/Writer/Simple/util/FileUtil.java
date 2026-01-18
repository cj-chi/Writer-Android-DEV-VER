package com.THLight.BLE.USBeacon.Writer.Simple.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {

    public static byte[] unZip(File file) {
        ZipInputStream zipInputStream;
        ZipEntry zipEntry;
        try {
            InputStream inputStream = new FileInputStream(file);
            zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[2048];
                    int readLen;
                    while ((-1) != (readLen = zipInputStream.read(buffer))) {
                        if (0 < readLen)
                            byteArrayOutputStream.write(buffer, 0, readLen);
                    }
                    return byteArrayOutputStream.toByteArray();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
