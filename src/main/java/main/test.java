package main;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.*;

public class test {

    public static void main(String[] args) throws IOException, SQLException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("D:\\NewRecommentAlgorithm\\simMap.txt","rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        for (int i = 0;i < 19;i ++){
            String data = "11111\n";
            byteBuffer.clear();
            byteBuffer.put(data.getBytes());
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()){
                fileChannel.write(byteBuffer);
            }
        }
        fileChannel.close();
    }


}



