import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Decompressor {
    private static final int BUFFER_SIZE = 1024 * 1024; // 设置缓冲区大小为1MB

    // 解压缩方法，接收输入路径、输出路径和密钥
    public void decompress(String inputPath, String outputPath, String key)
            throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        File inputFile = new File(inputPath);
        if (inputFile.isDirectory()) {
            decompressFolder(inputPath, outputPath, key); // 解压缩文件夹
        } else {
            decompressFile(inputPath, outputPath, key); // 解压缩单个文件
        }
    }

    // 解压缩文件夹
    private void decompressFolder(String inputFolder, String outputFolder, String key)
            throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        File folder = new File(inputFolder);
        File[] files = folder.listFiles(); // 获取文件列表
        if (files != null) {
            new File(outputFolder).mkdirs(); // 创建输出目录
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归解压子文件夹
                    decompressFolder(file.getAbsolutePath(), outputFolder + File.separator + file.getName(), key);
                } else {
                    String decompressedFileName = outputFolder + File.separator + file.getName().replace(".huff", ""); // 构建解压后的文件名
                    File decompressedFile = new File(decompressedFileName);

                    if (decompressedFile.exists()) {
                        if (!askUserForOverwrite(decompressedFileName)) {
                            System.out.println("跳过解压：" + file.getName()); // 输出跳过信息
                            continue; // 跳过解压
                        }
                    }

                    decompressFile(file.getAbsolutePath(), decompressedFileName, key); // 解压缩文件
                }
            }
        }
    }

    // 解压缩单个文件
    private void decompressFile(String inputFile, String outputFile, String key)
            throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFile));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {

            String storedKeyHash = (String) ois.readObject(); // 读取存储的密钥哈希值
            if (key != null && !key.isEmpty()) {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String providedKeyHash = bytesToHex(digest.digest(key.getBytes())); // 计算提供的密钥哈希值
                if (!providedKeyHash.equals(storedKeyHash)) {
                    throw new SecurityException("提供的解密密钥无效。"); // 抛出异常
                }
            } else if (storedKeyHash != null) {
                throw new SecurityException("需要解密密钥，但未提供。"); // 抛出异常
            }

            while (true) {
                try {
                    Map<Byte, String> huffmanCodes = (Map<Byte, String>) ois.readObject(); // 读取 Huffman 编码表
                    byte[] compressedData = (byte[]) ois.readObject(); // 读取压缩数据
                    HuffmanCodec codec = new HuffmanCodec();
                    byte[] dataChunk = codec.decompress(compressedData, huffmanCodes); // 解码数据块
                    bos.write(dataChunk); // 写入解压后的数据
                } catch (EOFException e) {
                    break; // 文件结束，退出循环
                }
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