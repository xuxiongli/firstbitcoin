package com.xuxiongli.fristbitcoin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuxiongli.fristbitcoin.FristbitcoinApplication;
import com.xuxiongli.fristbitcoin.bean.Block;
import com.xuxiongli.fristbitcoin.bean.MessageBean;
import com.xuxiongli.fristbitcoin.bean.NoteBook;
import com.xuxiongli.fristbitcoin.bean.Transaction;
import com.xuxiongli.fristbitcoin.websocket.MyClient;
import com.xuxiongli.fristbitcoin.websocket.MyServer;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.PostConstruct;

/**
 * ClassName:BlockController
 * Description:
 */
@RestController
public class BlockController {

    private NoteBook noteBook =  NoteBook.getInstance();

    @RequestMapping(value = "/addGenesis",method = RequestMethod.POST)
    public String addGenesis(String genesis){

        try {
            noteBook.addGenesis(genesis);
            return "success";
        } catch (Exception e) {
           return "fail:"+e.getMessage();
        }

    }
    //添加记录
    @RequestMapping(value = "/addNote",method = RequestMethod.POST)
    public String addNote(Transaction transaction){

        try {
            //校验交易数据
            if(transaction.verify()){
            //将交易数据转化字符串
                ObjectMapper objectMapper = new ObjectMapper();

                String transactionString = objectMapper.writeValueAsString(transaction);

                MessageBean messageBean = new MessageBean(2,transactionString);

                String msg = objectMapper.writeValueAsString(messageBean);
                //添加数据
                server.broadcast(msg);
                noteBook.addNote(transactionString);
                return "success";
            }else{
                throw new RuntimeException("交易校验失败");
            }
        } catch (Exception e) {
            return "fail:"+e.getMessage();
        }
    }
    //展示记录
    @RequestMapping(value = "/showList",method = RequestMethod.GET)
    public ArrayList<Block> showList(){

        return noteBook.showList();

    }
    @RequestMapping(value = "/check",method = RequestMethod.GET)
    public String check(){
        String check = noteBook.check();

        if (StringUtils.isEmpty(check)){

            return "数据正常";
        }
        return check;
    }

    private MyServer server;

    @PostConstruct
    public void  init(){
        server = new MyServer(Integer.parseInt(FristbitcoinApplication.port)+1);
        server.startServer();
    }

    //节点注册
    private HashSet<String> set = new HashSet<>();

    @RequestMapping("/regist")
    public String regist(String node){
        set.add(node);
        return "添加成功";
    }

    //连接节点
    private ArrayList<MyClient> clients = new ArrayList<>();

    @RequestMapping("/conn")
    public String conn() {
        try {
            for (String s : set) {
                URI uri = new URI("ws://localhost:" + s);
                MyClient client = new MyClient(uri, s);
                client.connect();
                clients.add(client);
            }
            return "连接成功";
        } catch (URISyntaxException e) {
            return "连接失败"+e.getMessage();
        }
    }

    //广播
    @RequestMapping("/broadcast")
    public String broadcast(String msg){

        server.broadcast(msg);

        return "广播成功";
    }

    //请求同步其他节点的区块链数据
    @RequestMapping("/syncData")
    public String syncData(){

        for (MyClient client : clients){
            client.send("请把你的区块链数据给我一份,一起记录");
        }
        return "广播成功!!!";
    }



}
