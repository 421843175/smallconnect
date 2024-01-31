package com.jupiter.smallconnect.mvc.tools;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Tools51 {
    /**
     * 用于打开串口
     *
     * @param portName
     *            串口名
     * @param baudrate
     *            波特率
     * @return 返回串口
     */
    public static final SerialPort portParameterOpen(String portName, int baudrate) {
        SerialPort serialPort = null;
        try { // 通过端口名识别串口
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            // 打开端口并设置端口名字 serialPort和超时时间 2000ms
            CommPort commPort = portIdentifier.open(portName, 1000);
            // 进一步判断comm端口是否是串口 instanceof
            if (commPort instanceof SerialPort) {
                System.out.println("该COM端口是串口！串口名称是：" + portName);
                // 进一步强制类型转换
                serialPort = (SerialPort) commPort;
                // 设置baudrate 此处需要注意:波特率只能允许是int型 对于57600足够
                // 8位数据位
                // 1位停止位
                // 无奇偶校验
                serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                // 串口配制完成 log
                System.out.println("串口参数设置已完成，波特率为" + baudrate + ",数据位8bits,停止位1位,无奇偶校验");
            } else { // 不是串口
                System.out.println("该com端口不是串口,请检查设备!");
                // 将com端口设置为null 默认是null不需要操作
            }

        } catch (NoSuchPortException e) {
            e.printStackTrace();
        } catch (PortInUseException e) {
            e.printStackTrace();
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        }

        return serialPort;
    }

    /**
     * 发送一个字节数组到串口
     *
     * @param serialPort
     *            串口
     * @param dataPackage
     *            字节数组
     */
    public static void uartSendDatatoSerialPort(SerialPort serialPort, byte[] dataPackage) {
        OutputStream out = null;
        try {
            out = serialPort.getOutputStream(); // 和socket通信差不多，获取输出流然后调用write方法写入字节数组
            out.write(dataPackage);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭输出流 非常标准的格式！！！
            if (out != null) {
                try {
                    out.close();
                    out = null;
                    // System.out.println("数据已发送完毕!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //发送字符串数据导串口
    public static void uartSendStringtoSerialPort(SerialPort serialPort, String data) {
        OutputStream out = null;
        try {
            out = serialPort.getOutputStream();
            out.write(data.getBytes("UTF-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * 上位机接收数据 串口对象seriesPort 接收数据buffer 返回一个byte数组
     */
    public static byte[] uartReceiveDatafromSingleChipMachine(SerialPort serialPort) {
        byte[] receiveDataPackage = null;
        InputStream in = null;
        try {
            in = serialPort.getInputStream();
            // 获取data buffer数据长度
            int bufferLength = in.available();
            while (bufferLength != 0) {
                receiveDataPackage = new byte[bufferLength];
                in.read(receiveDataPackage);
                bufferLength = in.available();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiveDataPackage;
    }

    // 修改后的接收方法，持续读取串口数据
    public static String uartReceiveDatafromString(SerialPort serialPort) {
        String dataReceive = null;
        InputStream in = null;
        try {
            in = serialPort.getInputStream();
            int availableBytes = in.available();

            while (availableBytes == 0) {
                Thread.sleep(100); // 等待数据
                availableBytes = in.available();
            }

            byte[] receiveDataPackage = new byte[availableBytes];
            in.read(receiveDataPackage);

            dataReceive = new String(receiveDataPackage, "utf-8");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return dataReceive;
    }


    //发送字节数据
//    public static void main(String[] args) throws Exception {
//        // 打开串口
//        SerialPort serialPort = portParameterOpen("COM3", 4800);
//
//        String dataSend="se";
//        int i = 1;
//        while (true) {
//            // 发送数据到单片机
//            byte []datByte = dataSend.getBytes();
//            uartSendDatatoSerialPort(serialPort, datByte);
//            System.out.println("-------------------------------------------------------");
//            System.out.println((i++) + ". 发送到串口的数据：" +dataSend);
//
//            // 休眠500ms，等待单片机反应
//            Thread.sleep(1000);
//
//            // 从单片机接收到的数据，返回的是一个字节数组
//            byte[] dat = uartReceiveDatafromSingleChipMachine(serialPort);
//            if (dat != null && dat.length > 0) {
//                String dataReceive = new String(dat, "utf-8");// 字节数组以utf-8编码表编码成字符进行显示
//                System.out.println((i++) + ". 从串口接收的数据：" + dataReceive);
//            } else {
//                System.out.println("接收到的数据为空！");
//            }
//        }
//
//    }



//    public static void main(String[] args) throws InterruptedException {
//        // 打开串口
//        SerialPort serialPort = portParameterOpen("COM3", 4800);
//
//
//        String dataSend = "t:40";
//        int i = 1;
//
//            // 发送数据到单片机
//            uartSendStringtoSerialPort(serialPort, dataSend);
//            System.out.println("-------------------------------------------------------");
//            System.out.println((i++) + ". 发送到串口的数据：" + dataSend);
//
//            // 休眠500ms，等待单片机反应
//            Thread.sleep(500);
//
//            // 从单片机接收到的数据，返回的是一个字符串
//            String dataReceive = uartReceiveDatafromString(serialPort);
//            if (dataReceive != null && !dataReceive.isEmpty()) {
//                System.out.println((i++) + ". 从串口接收的数据：" + dataReceive);
//            } else {
//                System.out.println("接收到的数据为空！");
//            }
//            serialPort.close();
//    }

    public static void main(String[] args) throws InterruptedException {
        SerialPort serialPort = portParameterOpen("COM3", 4800);
            while(true){

            // 从单片机接收到的数据，返回的是一个字符串
            String dataReceive = uartReceiveDatafromString(serialPort);
            if (dataReceive != null && !dataReceive.isEmpty()) {
                System.out.println(". 从串口接收的数据：" + dataReceive);
            }
                Thread.sleep(50);
            }
    }


}
