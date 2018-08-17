package com.xuxiongli.fristbitcoin.bean;

import com.xuxiongli.fristbitcoin.Util.RSAUtils;

import java.security.PublicKey;

/**
 * ClassName:Transaction
 * Description:
 */

public class Transaction {
    //付款人签名
    public String signaturedData ;

    //付款人公钥
    public String senderPublicKey;
    //收款人公钥
    public String recevierPublicKey;
    //金额
    public String content;

    public Transaction() {

    }

    @Override
    public String toString() {
        return "Transaction{" +
                "signaturedData='" + signaturedData + '\'' +
                ", senderPublicKey='" + senderPublicKey + '\'' +
                ", recevierPublicKey='" + recevierPublicKey + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public String getSignaturedData() {
        return signaturedData;
    }

    public void setSignaturedData(String signaturedData) {
        this.signaturedData = signaturedData;
    }

    public String getSenderPublicKey() {
        return senderPublicKey;
    }

    public void setSenderPublicKey(String senderPublicKey) {
        this.senderPublicKey = senderPublicKey;
    }

    public String getRecevierPublicKey() {
        return recevierPublicKey;
    }

    public void setRecevierPublicKey(String recevierPublicKey) {
        this.recevierPublicKey = recevierPublicKey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Transaction(String signaturedData, String senderPublicKey, String recevierPublicKey, String content) {

        this.signaturedData = signaturedData;
        this.senderPublicKey = senderPublicKey;
        this.recevierPublicKey = recevierPublicKey;
        this.content = content;
    }

    //校验交易的方法
    public boolean verify(){

        PublicKey publicKey = RSAUtils.getPublicKeyFromString("RSA",senderPublicKey);

        return RSAUtils.verifyDataJS("SHA256withRSA",publicKey,content,signaturedData);
    }



}
