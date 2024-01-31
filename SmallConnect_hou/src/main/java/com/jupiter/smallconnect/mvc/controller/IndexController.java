package com.jupiter.smallconnect.mvc.controller;

import com.alibaba.fastjson.JSONObject;
import com.jupiter.smallconnect.mvc.mapping.TMapping;
import com.jupiter.smallconnect.mvc.pojo.LogPojo;
import com.jupiter.smallconnect.mvc.pojo.TValue;
import com.jupiter.smallconnect.mvc.service.LogService;
import com.jupiter.smallconnect.mvc.service.ScheduleService;
import com.jupiter.smallconnect.mvc.service.UserService;
import com.jupiter.smallconnect.mvc.tools.AjaxResult;
import com.jupiter.smallconnect.mvc.tools.JULOG.JULog;
import com.jupiter.smallconnect.mvc.tools.Md5Tool;
import com.jupiter.smallconnect.mvc.tools.TokenUtils;
import com.jupiter.smallconnect.mvc.tools.Tools51;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;

@RestController
@CrossOrigin("*")
public class IndexController {
    @Autowired
    UserService userService;

    @Autowired
    LogService logService;

    @Autowired
    TMapping tMapping;

    @Autowired
    JULog juLog;
    @Value("${julog.logpath}")
    private String filename;

    @PostMapping("/setTH")
    public AjaxResult<String> setTH(HttpServletRequest request,@RequestBody JSONObject jsonParam) throws InterruptedException {
        String token = request.getHeader("Authorization");
        boolean result = TokenUtils.verify(token);
        if(!result)return AjaxResult.error("未授权进入!");
        boolean issetemplate=false;
        boolean issethumidity=false;
        System.out.println(jsonParam);
       int temple= jsonParam.getInteger("template");
        Integer humidity = jsonParam.getInteger("humidity");
        for(int i=0;i<5;i++){
            Tools51.uartSendStringtoSerialPort(ScheduleService.serialPort,"t:"+temple);
            String dataReceive = Tools51.uartReceiveDatafromString(ScheduleService.serialPort);
            if(dataReceive.equals("t ok")){
                issetemplate=true;
                break;
            }
            else Thread.sleep(2);
        }
        for(int i=0;i<5;i++){
            Tools51.uartSendStringtoSerialPort(ScheduleService.serialPort,"h:"+humidity);
            String dataReceive = Tools51.uartReceiveDatafromString(ScheduleService.serialPort);
            if(dataReceive.equals("h ok")){
                issethumidity=true;
                break;
            }
            else Thread.sleep(2);
        }
        if(issetemplate&&issethumidity){
            juLog.write("admin设置设备参数(模拟)温度:"+temple+",湿度:"+humidity+"操作成功");
            return  AjaxResult.success("成功");
        }

        return AjaxResult.error("设置失败");
    }


    @PostMapping("/checkPassword")
    public AjaxResult<String> checkPassword(@RequestBody String password){
        String md5pass = Md5Tool.getDoubleMd5(password);
        System.out.println("pass:"+password);
        if(userService.tocheck(md5pass)){
            return AjaxResult.success(TokenUtils.sign("admin"));
        }
        return AjaxResult.error("密码错误!");

    }

    @PostMapping("/setwork")
    public AjaxResult<String> Setwork(@RequestBody String work) throws InterruptedException {

        StringBuffer buffer = new StringBuffer(work);
        //public StringBuffer deleteCharAt(int index):删除指定位置的字符，并返回本身
        buffer.deleteCharAt(work.length() - 1);//删除最后位的元素
        int endwork = Integer.parseInt(buffer.toString());
        System.out.println(endwork);

        boolean isContorl=false;
        for(int i=0;i<5;i++){
            Tools51.uartSendStringtoSerialPort(ScheduleService.serialPort,"c:"+endwork);
            String dataReceive = Tools51.uartReceiveDatafromString(ScheduleService.serialPort);
            if(dataReceive.equals("c ok")){
                isContorl=true;
                break;
            }
            else Thread.sleep(2);
        }
        if(isContorl){
            if(endwork==1){
                juLog.write("admin运行设备操作成功");
            } else if(endwork==3){
                juLog.write("admin停止设备操作成功");
            }

            return  AjaxResult.success("成功");
        }

        return AjaxResult.error("修改状态失败");
    }

    @GetMapping("/getInfotemplate")
    public AjaxResult<TValue[]> getInfotemplate(){
        List<TValue> tValues = tMapping.selectList(null);
        TValue[] sortedTValues = tValues.stream()
                .sorted(Comparator.comparing(TValue::getId))
                .toArray(TValue[]::new);
       return AjaxResult.success(sortedTValues);
    }


    @PostMapping("/updatePassword")
    public AjaxResult<String> updatePassword(@RequestBody JSONObject passbean){
        String pass = passbean.getString("pass");
        String checkPass = passbean.getString("checkPass");
        String yuan = passbean.getString("yuan");
        String md5pass = Md5Tool.getDoubleMd5(yuan);
        if(!userService.tocheck(md5pass)){
            return new AjaxResult<>(402,"原密码错误");
        }
        if(!checkPass.equals(pass))
        {
            return new AjaxResult<>(403,"两次密码不一致");
        }
        if(pass.length()<6){
            return new AjaxResult<>(405,"密码长度过低");
        }
        userService.update(Md5Tool.getDoubleMd5(pass));
        juLog.write("admin修改密码成功");
        return AjaxResult.success("成功");
    }

    @GetMapping("/getLog")
    public AjaxResult<LogPojo[]> getlog(){
        return AjaxResult.success(logService.parseLogFile(filename));
    }
}
