package com.wu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页公共返回对象
 * @author cxj
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespPageBBean {
    /**
     * 总条数
     */
    private  long total;
    /**
     * 数据
     */
    private List<?> data;
}
