package org.delicious.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author huangcan
 * Date: 2025/5/7
 * Time: 11:55
 */
public class DeliciousBufferFileReader extends InputStream {

    private final FileInputStream fileInputStream;
    private final byte[] buffer = new byte[1024];
    private int index;
    private int size;

    public DeliciousBufferFileReader(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
        this.index = -1;
        this.size = -1;
    }

    @Override
    public int read() throws IOException {
        if (cantReadByBuffer()) {
            refreshBuffer();
        }
        return readByBuffer();
    }

    private int readByBuffer() {
        if (cantReadByBuffer()) {
            return -1;
        }
        return buffer[index++] & 0xFF;
    }

    private void refreshBuffer() throws IOException {
        this.size = fileInputStream.read(buffer);
        index = 0;
    }

    private boolean cantReadByBuffer() {
        if (size == -1) {
            return true;
        }
        return size == index;
    }
}
