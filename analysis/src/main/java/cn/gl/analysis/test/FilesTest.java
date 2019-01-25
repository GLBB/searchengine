package cn.gl.analysis.test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesTest {

    public static void main(String[] args) throws IOException {
        Path path = Path.of("html_repo/2018_12_22/0a0fc5d5-ae2f-4d5a-af7c-0ff4ae0f44a9");
        FileChannel channel = FileChannel.open(path);

        CharBuffer charBuffer = CharBuffer.allocate(1024);


        ByteBuffer byteBuffer = ByteBuffer.allocate(1024*100);
        int read = channel.read(byteBuffer);



    }

}
