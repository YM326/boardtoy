package com.example.demo.util;

import egovframework.rte.fdl.cryptography.EgovPasswordEncoder;
import egovframework.rte.fdl.cryptography.impl.EgovARIACryptoServiceImpl;
import org.apache.commons.codec.binary.Base64;

public class ARIA256Util {

    public static final String LOGIN_KEY = "!dskajvoiqjweojdfiqjwoeipfvjksdjalkfjweqoijvfiowqevrweqruhuiweqf";

    //복호화
    public static String decrypted(String value, String key){
        String decrypted = "";
        try {
            EgovPasswordEncoder egovPasswordEncoder = new EgovPasswordEncoder();
            EgovARIACryptoServiceImpl egovARIACryptoServiceImpl = new EgovARIACryptoServiceImpl();

            String hasedPassword = egovPasswordEncoder.encryptPassword(key);
            egovPasswordEncoder.setHashedPassword(hasedPassword);
            egovPasswordEncoder.setAlgorithm("SHA-256");
            egovARIACryptoServiceImpl.setPasswordEncoder(egovPasswordEncoder);
            egovARIACryptoServiceImpl.setBlockSize(1025);

            byte[] bytedecrypted = egovARIACryptoServiceImpl.decrypt(Base64.decodeBase64(value.getBytes()), key);
            decrypted = new String(bytedecrypted, "UTF-8");
        }catch(Exception e){
            return decrypted;
        }

        return decrypted;
    }

    //암호화
    public static String encrypted(String value, String key){
        String encrypted = "";
        try {
            EgovPasswordEncoder egovPasswordEncoder = new EgovPasswordEncoder();
            EgovARIACryptoServiceImpl egovARIACryptoServiceImpl = new EgovARIACryptoServiceImpl();

            String hasedPassword = egovPasswordEncoder.encryptPassword(key);
            egovPasswordEncoder.setHashedPassword(hasedPassword);
            egovPasswordEncoder.setAlgorithm("SHA-256");
            egovARIACryptoServiceImpl.setPasswordEncoder(egovPasswordEncoder);
            egovARIACryptoServiceImpl.setBlockSize(1025);

            byte[] byteencrypted = egovARIACryptoServiceImpl.encrypt(value.getBytes("UTF-8"), key);
            encrypted = Base64.encodeBase64String(byteencrypted);
        }catch(Exception e){
            return encrypted;
        }

        return encrypted;
    }
}
