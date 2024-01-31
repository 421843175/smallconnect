package com.jupiter.smallconnect.mvc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jupiter.smallconnect.mvc.controller.WebSocketController;
import com.jupiter.smallconnect.mvc.mapping.TMapping;
import com.jupiter.smallconnect.mvc.pojo.TValue;
import com.jupiter.smallconnect.mvc.tools.Tools51;
import gnu.io.SerialPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScheduleService {


    @Autowired
    WebSocketController webSocketController;

    @Autowired
    TMapping tMapping;

    public static SerialPort  serialPort;

    static {
         serialPort = Tools51.portParameterOpen("COM3", 4800);
    }
    //scheduled的方法不用被调用，也不用控制器，启动springboot的时候自动开启定时任务
    @Scheduled(cron = "0/1 * * * * ?")
    //每2秒执行一次
    public void dingshihello(){
        // 从单片机接收到的数据，返回的是一个字符串
            String dataReceive = Tools51.uartReceiveDatafromString(serialPort);
            if (dataReceive != null && !dataReceive.isEmpty()) {
                System.out.println("从串口接收的数据：" + dataReceive);
                // 使用正则表达式匹配"k:"后面的数字
                Pattern p = Pattern.compile("K:(\\d+)");
                Pattern p2 = Pattern.compile("T:(\\d+\\.?\\d*)");
                Matcher m = p.matcher(dataReceive);

                while (m.find()) {
                    int kValue = Integer.parseInt(m.group(1));
                    System.out.println("处理后的k值：" + kValue);
                    //k
                    webSocketController.onMessage("k:"+kValue,webSocketController.getSessions()[0]);
                }
                m = p2.matcher(dataReceive);
                while (m.find()) {
                    double tvalue = Double.parseDouble(m.group(1));
                    System.out.println("处理后的T值：" + tvalue);

                    //t
                    Calendar calendar = Calendar.getInstance();
                    int hour=calendar.get(Calendar.HOUR);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);

                    String ms=hour+":"+minute+":"+second;
                    tMapping.insert(new TValue(null,ms,tvalue));
                    if(tMapping.selectCount(null)>=8){
                        QueryWrapper<TValue> queryWrapper = new QueryWrapper<>();
                        queryWrapper.orderByAsc("id").last("LIMIT 1");  // 按id升序排序并限制返回一条数据
                        tMapping.delete(queryWrapper);
                    }


                }
            }
    }
}
