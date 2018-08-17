package com.xuxiongli.fristbitcoin.bean;

import com.sun.org.apache.xml.internal.security.utils.Base64;
//import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.xuxiongli.fristbitcoin.Util.RSAUtils;

import java.io.File;

import java.security.PrivateKey;
import java.security.PublicKey;


/**
 * ClassName:Wallet
 * Description:
 */
public class Wallet {


    public PrivateKey privateKey ;

    public PublicKey publicKey;

    private  String name;

    public Wallet(String name) {

        this.name = name;

        File pubFile = new File(name +".pub");
        File priFile = new File(name +".pri");

        if (priFile.length()==0||priFile.exists()||pubFile.exists()||pubFile.length()==0){

            RSAUtils.generateKeysJS("RSA",name+".pri",name + ".pub");
        }

        //privateKey= RSAUtils.getPrivateKey("RSA",name+".pri");
       // publicKey = RSAUtils.getPublicKeyFromFile("RSA",name +".pub");
    }
    //转账的代码
    public Transaction sendMoney(String recevierPublicKey,String content){

            String signature = RSAUtils.getSignature("SHA256withRSA", privateKey,content);

            String senderPublicKey = Base64.encode(publicKey.getEncoded());

            Transaction transaction = new Transaction(signature,content,recevierPublicKey,senderPublicKey);

            return transaction;
    }

    public static void main(String[] args){
        Wallet a = new Wallet("a");
        Wallet b = new Wallet("b");


    }
}
