package com.xuxiongli.fristbitcoin.websocket;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuxiongli.fristbitcoin.bean.Block;
import com.xuxiongli.fristbitcoin.bean.MessageBean;
import com.xuxiongli.fristbitcoin.bean.NoteBook;
import com.xuxiongli.fristbitcoin.bean.Transaction;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;

/**
 * ClassName:MyClient
 * Description:
 */
public class MyClient extends WebSocketClient {

    private String name;


    public MyClient(URI serverUri,String name) {
        super(serverUri);
        this.name = name;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

        System.out.println("客户端_"+name+"_打开了连接");

    }

    @Override
    public void onMessage(String message) {

            System.out.println("客户端_"+name+"_收到消息:"+message);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MessageBean messageBean = objectMapper.readValue(message,MessageBean.class);
            NoteBook noteBook = NoteBook.getInstance();
            //判断消息类型
            if (messageBean.type == 1){
                //收到是区块链数据
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class,Block.class);
                ArrayList<Block> newList = objectMapper.readValue(messageBean.msg,javaType) ;
                //和本地的区块进行比较 如果对方的数据比较新,就用对象的数据替换本地的数据

                noteBook.comparData(newList);
            }else if (messageBean.type == 2){
                Transaction transaction = objectMapper.readValue(messageBean.msg,Transaction.class);
                if (transaction.verify()){
                    noteBook.addNote(messageBean.msg);
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("客户端_"+name+"_关闭了连接");
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("客户端_"+name+"_发生了错误");
    }
}
