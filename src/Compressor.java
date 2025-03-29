import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Compressor {
    private static final int BUFFER_SIZE = 8192; // 设置缓冲区大小为8KB

    // 压缩方法，接收输入路径、输出路径和密钥
    public void compress(String inputPath, String outputPath, String key) throws IOException, NoSuchAlgorithmException {
        File inputFile = new File(inputPath);
        if (inputFile.isDirectory()) {
            compressFolder(inputPath, outputPath, key); // 压缩文件夹
        } else {
            compressFile(inputPath, outputPath, key); // 压缩单个文件
        }
    }

    // 压缩文件夹
    private void compressFolder(String inputFolder, String outputFolder, String key)
            throws IOException, NoSuchAlgorithmException {
        File folder = new File(inputFolder);
        File[] files = folder.listFiles(); // 获取文件列表
        if (files != null) {
            new File(outputFolder).mkdirs(); // 创建输出目录
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归压缩子文件夹
                    compressFolder(file.getAbsolutePath(), outputFolder + File.separator + file.getName(), key);
                } else {
                    String compressedFileName = outputFolder + File.separator + file.getName() + ".huff"; // 构建压缩文件名
                    File compressedFile = new File(compressedFileName);

                    if (compressedFile.exists()) {
                        if (!askUserForOverwrite(compressedFileName)) {
                            System.out.println("跳过压缩：" + file.getName()); // 输出跳过信息
                            continue; // 跳过压缩
                        }
                    }

                    compressFile(file.getAbsolutePath(), compressedFileName, key); // 压缩文件
                }
            }
        }
    }

    // 压缩单个文件
    private void compressFile(String inputFile, String outputFile, String key)
            throws IOException, NoSuchAlgorithmException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
             ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile))) {

            String keyHash = null;
            if (key != null && !key.isEmpty()) {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                keyHash = bytesToHex(digest.digest(key.getBytes())); // 计算密钥的哈希值
            }

            oos.writeObject(keyHash); // 将密钥哈希写入文件

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            HuffmanCodec codec = new HuffmanCodec();

            while ((bytesRead = bis.read(buffer)) != -1) {
                byte[] dataChunk = new byte[bytesRead];
                System.arraycopy(buffer, 0, dataChunk, 0, bytesRead); // 复制数据到新的数组
                byte[] compressedData = codec.compress(dataChunk); // 压缩数据块
                Map<Byte, String> huffmanCodes = codec.getHuffmanCodes();
                oos.writeObject(huffmanCodes); // 写入编码表
                oos.writeObject(compressedData); // 写入压缩数据
            }
        }
    }

    // 提示用户是否覆盖已存在的文件
    private boolean askUserForOverwrite(String fileName) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("文件 " + fileName + " 已存在。是否覆盖？(y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("y"); // 返回用户选择
    }

    // 将字节数组转换为十六进制字符串
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b)); // 格式化为两位十六进制
        }
        return sb.toString();
    }
}