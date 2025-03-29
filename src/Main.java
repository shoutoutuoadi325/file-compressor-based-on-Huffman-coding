import java.io.File;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Compressor compressor = new Compressor(); // 创建压缩器对象
        Decompressor decompressor = new Decompressor(); // 创建解压缩器对象

        // 在事件调度线程中创建并显示图形用户界面
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI(); // 调用方法创建并显示界面
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("文件压缩/解压缩工具"); // 创建主窗口
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭操作
        frame.setSize(500, 300); // 设置窗口大小为500x300像素

        JPanel panel = new JPanel(); // 创建面板容器
        panel.setLayout(new GridLayout(5, 3, 10, 10)); // 设置网格布局，5行3列，组件间距10像素

        JLabel optionLabel = new JLabel("选择操作："); // 创建操作选项标签
        String[] options = { "Compress", "Decompress" }; // 定义操作选项数组
        JComboBox<String> optionBox = new JComboBox<>(options); // 创建操作选项下拉框
        panel.add(optionLabel); // 将标签添加到面板
        panel.add(optionBox); // 将下拉框添加到面板
        panel.add(new JLabel("")); // 添加空白占位标签

        JLabel inputLabel = new JLabel("输入路径："); // 创建输入路径标签
        JTextField inputField = new JTextField(); // 创建输入路径文本字段
        JButton inputButton = new JButton("浏览"); // 创建浏览按钮
        panel.add(inputLabel);
        panel.add(inputField);
        panel.add(inputButton);

        JLabel outputLabel = new JLabel("输出路径："); // 创建输出路径标签
        JTextField outputField = new JTextField(); // 创建输出路径文本字段
        JButton outputButton = new JButton("浏览"); // 创建浏览按钮
        panel.add(outputLabel);
        panel.add(outputField);
        panel.add(outputButton);

        JLabel keyLabel = new JLabel("加密密钥（可选）："); // 创建密钥输入标签
        JTextField keyField = new JTextField(); // 创建密钥输入文本字段
        panel.add(keyLabel);
        panel.add(keyField);
        panel.add(new JLabel("")); // 添加空白占位标签

        JButton executeButton = new JButton("执行"); // 创建执行按钮
        panel.add(new JLabel("")); // 添加空白占���标签
        panel.add(executeButton);
        panel.add(new JLabel("")); // 添加空白占位标签

        // 为输入路径的浏览按钮添加事件监听器
        inputButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(); // 创建文件选择器
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // 设置为文件和目录选择模式
            int returnVal = chooser.showOpenDialog(frame); // 显示打开对话框
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                inputField.setText(chooser.getSelectedFile().getAbsolutePath()); // 获取选定文件的绝对路径并设置到文本字段
            }
        });

        // 为输出路径的浏览按钮添加事件监听器
        outputButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(); // 创建文件选择器
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 设置为仅目录选择模式
            int returnVal = chooser.showSaveDialog(frame); // 显示保存对话框
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                outputField.setText(chooser.getSelectedFile().getAbsolutePath()); // 获取选定目录的绝对路径并设置到文本字段
            }
        });

        // 为执行按钮添加事件监听器
        executeButton.addActionListener(e -> {
            String option = (String) optionBox.getSelectedItem(); // 获取选定的操作选项
            String inputPath = inputField.getText(); // 获取输入路径文本
            String outputPath = outputField.getText(); // 获取输出路径文本
            String key = keyField.getText(); // 获取密钥文本

            try {
                if (option.equals("Compress")) { // 如果选择了压缩操作
                    Compressor compressor = new Compressor(); // 创建压缩器对象
                    compressor.compress(inputPath, outputPath, key); // 调用压缩方法
                    JOptionPane.showMessageDialog(frame, "压缩成功。"); // 弹出成功提示
                } else { // 如果选择了解压缩操作
                    Decompressor decompressor = new Decompressor(); // 创建解压缩器对象
                    decompressor.decompress(inputPath, outputPath, key); // 调用解压缩方法
                    JOptionPane.showMessageDialog(frame, "解压缩成功。"); // 弹出成功提示
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "发生错误：" + ex.getMessage()); // 弹出错误提示
            }
        });

        frame.add(panel); // 将面板添加到主窗口
        frame.setVisible(true); // 显示窗口
    }
}
