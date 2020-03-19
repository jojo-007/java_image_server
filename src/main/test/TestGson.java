import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sun.java2d.SurfaceDataProxy;

import java.util.HashMap;
class Hero{
    String name;
    String skill1;
    String skill2;
    String skill3;
    String skill4;
}
public class TestGson {
    public static void main(String[] args) {
//        HashMap<String,Object> hashMap=new HashMap<>();
//        hashMap.put("name","曹操");
//        hashMap.put("skill1","剑气");
//        hashMap.put("skill2","三段跳");
//        hashMap.put("skill3","加攻速");
//        hashMap.put("skill4","如来神掌");

         Hero hero=new Hero();
         hero.name="曹操";
         hero.skill1="剑气";
         hero.skill2="三段跳";
         hero.skill3="加攻速";
         hero.skill4="如来神掌";
        Gson gson=new GsonBuilder().create();
        String str=gson.toJson(hero);
        System.out.println(str);
    }
}
