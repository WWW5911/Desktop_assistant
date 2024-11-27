import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class EnDecoder {
    static String indif = "resource";
    static String outdif = "resources";

    public static String Encode(String str){
        String ans = "";
        for(int i = 0; i < str.length(); ++i)
            ans += (char)( str.charAt(i) + 33 - (i * 2) % 13 );
        return ans;
    }
    public static String Decode(String str){
        String ans = "";
        for(int i = 0; i < str.length(); ++i)
            ans += (char)(str.charAt(i) - 33 + (i * 2) % 13);
        return ans;
    }

    public static void EncodeFile(String file_path){
        String name = file_path;
        if(!file_path.contains(System.getProperty("user.dir"))){
            file_path = System.getProperty("user.dir") + "\\" + indif +"\\" + file_path;
        }
        String [] tmp = file_path.split("\\\\");
        name = tmp[tmp.length-1].split("\\.")[0];
        try{
            InputStream inputStream = new FileInputStream(file_path);
            AES_CBC_PK5 enp = new AES_CBC_PK5();
            OutputStream fw = new FileOutputStream(System.getProperty("user.dir") + "\\" + outdif + "\\" + name + ".moe" ) ;
            fw.write( enp.Encrypt(inputStream.readAllBytes()) );
            fw.close();
            inputStream.close();

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("加密檔案失敗");
        }

    }
    public static InputStream ReadEncodeFile(String file_path){
        if(!file_path.contains(System.getProperty("user.dir")))
            file_path = System.getProperty("user.dir") + "\\" + outdif +"\\" + file_path;
        try{
            InputStream inputStream = new FileInputStream(file_path);
            AES_CBC_PK5 enp = new AES_CBC_PK5();
            byte [] tmp = enp.Decrypt(inputStream.readAllBytes());
            inputStream.close();
            return new ByteArrayInputStream( tmp );
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("讀取加密檔案失敗");
        }
        return null;
    }

    public static boolean checkFile(String file_path){
        if(!file_path.contains(System.getProperty("user.dir")))
            file_path = System.getProperty("user.dir") + "\\" + outdif +"\\" + file_path;
        File tmp = new File(file_path);
        return tmp.exists();
    }

}


class AES_CBC_PK5 {
    public String stringKey = "haachamahaachamahaachamahaachama";
    public byte[] iv = {4,2,0,2,4,7,2,3,6,4,1,5,7,8,5,8};
    IvParameterSpec ivp;
    SecretKey secretKey;

    public AES_CBC_PK5(){
        secretKey = generator();
        ivp = new IvParameterSpec(iv);
    }

    public byte[] Encrypt( byte [] file) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivp);
        byte[] byteCipherText = cipher.doFinal(file);
        return byteCipherText;
    }

    public byte [] Decrypt(byte [] file) throws Exception {
        SecretKey secretKey = generator();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivp);
        return cipher.doFinal(file);
    }
    public SecretKey generator(){
        byte[] decodedKey = Base64.getDecoder().decode(stringKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
    }
}



