import java.util.*;

public class HuffmanCodec {
    private Map<Byte, String> huffmanCodes; // 存储每个字节对应的 Huffman 编码

    // 压缩方法，接收原始字节数组，返回压缩后的字节数组
    public byte[] compress(byte[] data) {
        Map<Byte, Integer> frequencyMap = buildFrequencyMap(data); // 构建频率表，统计每个字节出现次数
        Node huffmanTree = buildHuffmanTree(frequencyMap); // 构建 Huffman 树
        huffmanCodes = generateCodes(huffmanTree); // 生成 Huffman 编码表
        byte[] compressedData = encodeData(data, huffmanCodes); // 对数据进行编码，得到压缩数据
        return compressedData; // 返回压缩后的数据
    }

    // 解压缩方法，接收压缩数据和编码表，返回解压后的原始数据
    public byte[] decompress(byte[] compressedData, Map<Byte, String> huffmanCodes) {
        Node huffmanTree = rebuildHuffmanTree(huffmanCodes); // 重建 Huffman 树
        byte[] originalData = decodeData(compressedData, huffmanTree); // 对压缩数据进行解码
        return originalData; // 返回解压缩后的数据
    }

    // 构建频率表，统计每个字节的出现频率
    private Map<Byte, Integer> buildFrequencyMap(byte[] data) {
        Map<Byte, Integer> frequencyMap = new HashMap<>(); // 创建频率表映射
        for (byte b : data) {
            frequencyMap.put(b, frequencyMap.getOrDefault(b, 0) + 1); // 累计字节出现次数
        }
        return frequencyMap; // 返回频率表
    }

    // 根据频率表构建 Huffman 树
    private Node buildHuffmanTree(Map<Byte, Integer> frequencyMap) {
        PriorityQueue<Node> queue = new PriorityQueue<>(); // 创建优先���列（最小堆）
        for (Map.Entry<Byte, Integer> entry : frequencyMap.entrySet()) {
            queue.add(new Node(entry.getKey(), entry.getValue())); // 创建节点并加入队列
        }
        while (queue.size() > 1) {
            Node left = queue.poll(); // 取出频率最小的节点
            Node right = queue.poll(); // 取出频率次小的节点
            Node parent = new Node(null, left.frequency + right.frequency); // 创建父节点，频率为子节点之和
            parent.left = left; // 设置左子节点
            parent.right = right; // 设置右子节点
            queue.add(parent); // 将父节点加入队列
        }
        return queue.poll(); // 返回根节点
    }

    // 生成 Huffman 编码表
    private Map<Byte, String> generateCodes(Node tree) {
        Map<Byte, String> huffmanCodes = new HashMap<>(); // 创建编码表映射
        generateCodesHelper(tree, "", huffmanCodes); // 递归生成编码
        return huffmanCodes; // 返回编码表
    }

    // 递归方法，生成编码表
    private void generateCodesHelper(Node node, String code, Map<Byte, String> huffmanCodes) {
        if (node != null) {
            if (node.data != null) {
                huffmanCodes.put(node.data, code); // 将字节和编码添加到编码表
            }
            generateCodesHelper(node.left, code + "0", huffmanCodes); // 遍历左子树，编码加 '0'
            generateCodesHelper(node.right, code + "1", huffmanCodes); // 遍历右子树，编码加 '1'
        }
    }

    // 将数据编码为二进制字符串并转换为字节数组
    private byte[] encodeData(byte[] data, Map<Byte, String> codes) {
        StringBuilder sb = new StringBuilder(); // 创建字符串构建器
        for (byte b : data) {
            sb.append(codes.get(b)); // 连接对应的编码字符串
        }
        int length = (sb.length() + 7) / 8; // 计算压缩后字节数组的长度
        byte[] compressedData = new byte[length]; // 创建压缩数据数组
        for (int i = 0; i < sb.length(); i += 8) {
            String byteString = sb.substring(i, Math.min(i + 8, sb.length())); // 每8位构成一个字节
            compressedData[i / 8] = (byte) Integer.parseInt(byteString, 2); // 将二进制字符串转换为字节
        }
        return compressedData; // 返回压缩数据
    }

    // 重建 Huffman 树，用于解码
    private Node rebuildHuffmanTree(Map<Byte, String> huffmanCodes) {
        Node root = new Node(); // 创建根节点
        for (Map.Entry<Byte, String> entry : huffmanCodes.entrySet()) {
            Node current = root; // 从根节点开始
            String code = entry.getValue();
            for (char c : code.toCharArray()) { // 遍历编码字符串
                if (c == '0') {
                    if (current.left == null) {
                        current.left = new Node(); // 创建左子节点
                    }
                    current = current.left; // 移动到左子节点
                } else {
                    if (current.right == null) {
                        current.right = new Node(); // 创建右子节点
                    }
                    current = current.right; // 移动到右子节点
                }
            }
            current.data = entry.getKey(); // 设置叶子节点的数据
        }
        return root; // 返回重建的树
    }

    // 解码压缩数据，恢复原始数据
    private byte[] decodeData(byte[] compressedData, Node tree) {
        List<Byte> dataList = new ArrayList<>(); // 创建列表存储解码后的字节
        Node current = tree; // 从根节点开始
        for (byte b : compressedData) {
            for (int i = 7; i >= 0; i--) { // 遍历字节中的每一位
                byte bit = (byte) ((b >> i) & 1); // 取出当前位
                if (bit == 0) {
                    current = current.left; // 移动到左子节点
                } else {
                    current = current.right; // 移动到右子节点
                }
                if (current.data != null) {
                    dataList.add(current.data); // 找到叶子节点，添加字节到列表
                    current = tree; // 重置到根节点
                }
            }
        }
        byte[] originalData = new byte[dataList.size()]; // 创建字节数组
        for (int i = 0; i < dataList.size(); i++) {
            originalData[i] = dataList.get(i); // 将列表中的字节复制到数组
        }
        return originalData; // 返回解码后的数据
    }

    // 获取 Huffman 编码表
    public Map<Byte, String> getHuffmanCodes() {
        return huffmanCodes;
    }

    // 内部类，表示 Huffman 树的节点
    private class Node implements Comparable<Node> {
        Byte data; // 存储的字节数据
        int frequency; // 节点的频率
        Node left; // 左子节点
        Node right; // 右子节点

        public Node(Byte data, int frequency) {
            this.data = data; // 初始化数据
            this.frequency = frequency; // 初始化频率
        }

        public Node() {
            // 无参构造函数
        }

        public int compareTo(Node other) {
            return Integer.compare(this.frequency, other.frequency); // 按频率比较节点
        }
    }
}