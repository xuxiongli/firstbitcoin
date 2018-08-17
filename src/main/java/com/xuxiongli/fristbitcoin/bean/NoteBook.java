package com.xuxiongli.fristbitcoin.bean;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuxiongli.fristbitcoin.Util.HashUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * ClassName:NoteBook
 * Description:
 */
public class NoteBook {



    //将数据保存到list集合中
    private ArrayList<Block> list = new ArrayList<>();

    private NoteBook() {
            init();
    }

    private void init(){
        try {
            File file = new File("s.json");

            if (file.exists()&&file.length()>0){
                ObjectMapper om = new ObjectMapper();

                JavaType javaType = om.getTypeFactory().constructParametricType(ArrayList.class,Block.class);

                list = om.readValue(file, javaType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static  volatile NoteBook instance;

    public static NoteBook getInstance() {
        if (instance == null){
            synchronized (NoteBook.class){
                if(instance == null){
                    instance = new NoteBook();
                }
            }
        }
        return instance;
    }


    //有了保存数据就,在创建对象时就要判断里面是否有数据

    //创世区块(添加封面)
    //封面就是必须是一个新账本 里面不能有数据
    public void addGenesis(String genesis){
        if (list.size()>0){
            throw new RuntimeException("这不是一个新账本,请创建新账本");
        }
        String preHash = "0000000000000000000000000000000000000000000000000000000000000000";
        int nonce = mine(genesis);
        list.add(new Block(
                list.size()+1,
                genesis,
                HashUtils.sha256(nonce+genesis+preHash),
                nonce,
                preHash

        ));
        save2Disk();
    }
    //普通区块
    //账本里面添加数据必须是有封面的  不然则无法添加数据抛出一个运行时异常
    public void addNote(String note){

        if (list.size()<1){
            throw new RuntimeException("请先添加封面");
        }
        Block block = list.get(list.size()-1);
        String prehash = block.hash;
        int nonce = mine(note+prehash);
        list.add(new Block(
                list.size()+1,
                note,
                HashUtils.sha256(nonce+note+prehash),
                nonce,
                prehash
        ));
        save2Disk();
    }
    //展示交易记录
    //展示交易记录将他循环遍历展示就好
    public ArrayList<Block> showList(){
        return list;

    }
    private static int mine(String content){
        for (int i = 0 ;i<Integer.MAX_VALUE;i++){
            String hash = HashUtils.sha256(i+content);

            if (hash.startsWith("0000")){

                System.out.println("挖矿成功"+i);
                return i;
            }else {
                System.out.println("这是第"+i+"次尝试挖矿");
            }
        }
        throw new RuntimeException("挖矿失败");
    }
    //保存数据
    //有保存之后,就要考虑怎么方便读取,这个时候考虑使用json保存
    public void save2Disk(){
        //创建objectMapp对象,json序列化对象方法
        try {
            ObjectMapper om = new ObjectMapper();
            om.writeValue(new File("s.json"),list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String check(){

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            Block block = list.get(i);
            //获取内容
            String content= block.content;
            int nonce = block.nonce;
            int id = block.id;
            String hash = block.hash;
            String preHash = block.preHash;
            //生成hash
            //String newHash = HashUtils.sha256(nonce+content);

            if (i==0){
                String caculatedHash = HashUtils.sha256(nonce+content+preHash);
                if (!caculatedHash.equals(hash)){
                    sb.append("编号为"+block.id+"的hash数据可能被篡改,请注意<br>");
                }
            }else {
                String caculatedHash = HashUtils.sha256(nonce+content+preHash);
                if (!caculatedHash.equals(hash)){
                    sb.append("编号为"+block.id+"的hash数据可能被篡改,请注意<br>");
                }
                Block preBlock = list.get(i - 1);

                String preBlockHash = preBlock.hash;
                if (!preBlockHash.equals(preHash)){

                    sb.append("编号为"+block.id+"的prehash数据可能被篡改,请注意<br>");
                }
            }

        }
        return  sb.toString();
    }
    
    public static void main(String[] args){
        NoteBook notebook = new NoteBook();
        //notebook.addGenesis("添加第一个创世区块");
        //notebook.addNote("第二笔转账200元");
       // notebook.showList();
    }

    public void comparData(ArrayList<Block> newList) {
        if (newList.size() > list.size()){
            list = newList;
            save2Disk();
        }
    }
}
