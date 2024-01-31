package com.jupiter.smallconnect.mvc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jupiter.smallconnect.mvc.mapping.UserMapping;
import com.jupiter.smallconnect.mvc.pojo.Pass;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserMapping um;

    public boolean tocheck(String password){
//        QueryWrapper<Pass> w=new QueryWrapper<>();
//       w.eq("")
        List<Pass> userList = um.selectList(null);
        System.out.println("mm:"+userList.get(0).getPasswd());
        if(userList.get(0).getPasswd().equals(password)){
            return true;
        }
        return false;
    }

    public void update(String password){
        UpdateWrapper<Pass> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("passwd", password);

        um.update(null, updateWrapper);
    }

}
