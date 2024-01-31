package com.jupiter.smallconnect.mvc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("tvaluetable")
public class TValue {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String ms;
    private double tvalue;
}
