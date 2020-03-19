package api;

import dao.Image;
import dao.ImageDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;

public class ImageShowServlet extends HttpServlet {
    static private HashSet<String> whiteList=new HashSet<>();
    static{
        whiteList.add("http://39.96.88.58:8080/java_image_server/index.html");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String referer=req.getHeader("Referer");
        if(!whiteList.contains(referer)){
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write("{\"ok\"：false,\"reason\":\"未授权的访问\"}");
            return;
        }
        //1.解析出imageId
        String imageId=req.getParameter("imageId");
        if(imageId==null||imageId.equals("")){
             resp.setContentType("application/json;charset=utf-8");
             resp.getWriter().write("{\"ok\"：false,\"reason\":\"imageId解析失败\"}");
             return;
        }

        //2.根据ImageId查找数据库，得到对应的图片属性信息
        ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(Integer.parseInt(imageId));
        //3.根据路径打开文件，读取其中的内容，写入到响应对象中
        resp.setContentType(image.getContentType());
        File file=new File(image.getPath());
        //由于图片是二进制的，应该使用字节流的方式读取文件
        OutputStream outputStream=resp.getOutputStream();
        FileInputStream fileInputStream=new FileInputStream(file);
        byte[] buffer=new byte[1024];
        while(true){
            int len=fileInputStream.read(buffer);
            if(len==-1){
                break;
            }
            //此时读到的数据已经放在Buffer里了,把里面的内容写到响应中
            outputStream.write(buffer);

        }
        fileInputStream.close();
        outputStream.close();
    }
}
