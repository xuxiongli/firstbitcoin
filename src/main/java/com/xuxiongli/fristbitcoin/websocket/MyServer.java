package com.xuxiongli.fristbitcoin.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuxiongli.fristbitcoin.bean.Block;
import com.xuxiongli.fristbitcoin.bean.MessageBean;
import com.xuxiongli.fristbitcoin.bean.NoteBook;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * ClassName:MyServer
 * Description:
 */
public class MyServer extends WebSocketServer {

    private int port;


    public MyServer(int port){
        super(new InetSocketAddress(port));
        this.port = port;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("websocket服务器"+port+"_打开了连接");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("websocket服务器"+port+"_关闭了连接");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("websocket服务器"+port+"_收到了消息:"+message);
        try {


            if ("请把你的区块链数据给我一份,一起记录".equals(message)){
                //获取本地的区块链数据
                NoteBook noteBook = NoteBook.getInstance();
                ArrayList<Block> list = noteBook.showList();
                //发送给;链接到本服务器的所有客户端
                ObjectMapper objectMapper = new ObjectMapper();
                String blockChainData = objectMapper.writeValueAsString(list);

                MessageBean messageBean = new MessageBean(1,blockChainData);
                String msg = objectMapper.writeValueAsString(messageBean);

                //广播消息
                broadcast(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("websocket服务器"+port+"_发生错误");

    }

    @Override
    public void onStart() {
        System.out.println("websocket服务器"+port+"_启动成功");

    }

    public void startServer() {
        new Thread(this).start();
    }
}
