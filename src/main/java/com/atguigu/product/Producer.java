package com.atguigu.product;

import com.atguigu.callback.MyCallBack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/confirm")
@Slf4j
public class Producer {
    public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MyCallBack myCallBack;
    //依赖注入rabbitTemplate之后再设置它的回调对象
    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(myCallBack);

        /**
         * true：
         *      交换机无法将消息进行路由时，会将该消息返回给生产者
         * false：
         *      如果发现消息无法进行路由，则直接丢弃
         */
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(myCallBack);
    }
    @GetMapping("sendMessage/{message}")
    public void sendMessage(@PathVariable String message){
        //指定消息id为1
        CorrelationData correlationData1=new CorrelationData("1");
        String routingKey="key1";
        rabbitTemplate.convertAndSend(CONFIRM_EXCHANGE_NAME,routingKey,message+routingKey,correlationData1);

        CorrelationData correlationData2=new CorrelationData("2");
        routingKey="key2";
        rabbitTemplate.convertAndSend(CONFIRM_EXCHANGE_NAME,routingKey,message+routingKey,correlationData2);
        log.info("发送消息内容:{}",message);
    }
}
