package com.wu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**状态返回信息类
 * @author cxj
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {
    private  long code;
    private  String message;
    private  Object object;
    /**
     * 成功返回结果
     */
    public  static RespBean success(String message){
        return  new RespBean(200,message,null);


    }

    /**
     * 成功返回·结果·
     * @param message
     * @param object
     * @return
     */
    public  static RespBean success(String message,Object object){
        return  new RespBean(200,message,object);


    }

    /**
     * 失败返回结果
     * @param message
     * @return
     */
    public  static  RespBean error(String message){
        return    new RespBean(500,message,null);
    }

    /**
     * 失败返回结果
     * @param message
     * @param object
     * @return
     */
    public  static  RespBean error(String message,Object object){
        return    new RespBean(500,message,object);
    }
}
