package com.rabbitmq.rabbitmqspring.convert;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/4/29 18:02
 */
public class ConverterBody {
    private byte[] body;
    
    public ConverterBody() {
    }
    
    public ConverterBody(byte[] body) {
        this.body = body;
    }
    
    public byte[] getBody() {
        return body;
    }
    
    public void setBody(byte[] body) {
        this.body = body;
    }
}
