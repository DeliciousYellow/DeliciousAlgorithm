package org.delicious.io;

import java.io.*;
import java.util.Objects;

/**
 * @author huangcan
 * Date: 2025/5/7
 * Time: 11:32
 */
public class BufferFileReaderTest {

    public static void main(String[] args) {
        deliciousInputStream();
        defaultInputStream();
        compare();
    }

    private static void deliciousInputStream() {
        long start = System.currentTimeMillis();
        try (DeliciousBufferFileReader deliciousBufferFileReader = new DeliciousBufferFileReader(new FileInputStream("/Users/moka/IdeaProjects/Algorithm/src/main/resources/测试.pdf"))) {
            while (true) {
                int result = Objects.requireNonNull(deliciousBufferFileReader).read();
                if (result == -1) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("\ndelicious耗时：" + (System.currentTimeMillis() - start));
    }

    private static void defaultInputStream() {
        long start = System.currentTimeMillis();
        try (FileInputStream fileInputStream = new FileInputStream("/Users/moka/IdeaProjects/Algorithm/src/main/resources/测试.pdf")) {
            while (true) {
                int result = Objects.requireNonNull(fileInputStream).read();
                if (result == -1) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("\ndefault耗时：" + (System.currentTimeMillis() - start));
    }

    private static void compare() {
        long start = System.currentTimeMillis();
        try (FileInputStream fileInputStream = new FileInputStream("/Users/moka/IdeaProjects/Algorithm/src/main/resources/测试.pdf")) {
            DeliciousBufferFileReader bufferFileReader = new DeliciousBufferFileReader(new FileInputStream("/Users/moka/IdeaProjects/Algorithm/src/main/resources/测试.pdf"));
            while (true) {
                int result = Objects.requireNonNull(fileInputStream).read();
                if (result != bufferFileReader.read()) {
                    throw new RuntimeException("读错了");
                }
                if (result == -1) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("\ncompare耗时：" + (System.currentTimeMillis() - start));
    }

}
