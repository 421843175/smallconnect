package com.jupiter.smallconnect.mvc.service;

import com.jupiter.smallconnect.mvc.pojo.LogPojo;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogService {
    public LogPojo[] parseLogFile(String logFilePath) {
        // 读取文件内容
        List<String> lines = readLogFile(logFilePath);

        // 创建LogPojo对象数组
        LogPojo[] logs = new LogPojo[lines.size()];

        // 解析每一行日志内容并填充到LogPojo对象数组中
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split("\\s+", 3); // 按空格分割，最多分割成3个部分

            String time = parts[0] + " " + parts[1];
            String info = parts[2];

            logs[i] = new LogPojo(time, info);
        }

        return logs;
    }

    private List<String> readLogFile(String logFilePath) {
        List<String> lines = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(logFilePath));
            String line = null;


            while (true) {
                try {
                    if (!((line = reader.readLine()) != null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                lines.add(line);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }
}
