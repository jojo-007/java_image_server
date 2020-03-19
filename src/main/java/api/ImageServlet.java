package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Image;
import dao.ImageDao;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ImageServlet extends HttpServlet {
    /**
     * 查看图片属性
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        //req对象包含了请求中的所有信息
        //resp对象要生成的结果就是放到里面去
        //考虑到查看所有图片属性和查看指定图片属性
        //存在imageId查看指定图片属性，否则就查看所有图片属性
        String imageId=req.getParameter("imageId");
        if(imageId==null||imageId.equals(" ")){
            //查看所有图片属性
            selectAll(req,resp);
        }else{
            //查看指定图片
            selectOne(imageId,resp);
        }
    }

    private void selectOne(String imageId, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        //1.创建ImageDao对象
        ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(Integer.parseInt(imageId));
        //2.使用gson把查到的数据转成json格式，并写回给响应对象
        Gson gson=new GsonBuilder().create();
        String jsonData=gson.toJson(image);
        resp.getWriter().write(jsonData);
    }

    private void selectAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        //1.创建一个ImageDao对象，并查找数据库
        ImageDao imageDao=new ImageDao();
        List<Image> images=imageDao.selectAll();
        //2.把查找到的结构转成JSON格式的字符串，并且写回给resp对象
        Gson gson=new GsonBuilder().create();
       //jsonData就是一个json格式的字符串,gson完成了大量的转换工作
        String jsonData=gson.toJson(images);
        resp.getWriter().write(jsonData);
    }

    /**
     * 上传图片
     * @param
     * @param
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         //1.获取图片的属性信息，并且存入数据库
          // a)需要创建一个factory对象和upload对象,这是为了获取到图片属性
        FileItemFactory factory=new DiskFileItemFactory();
        ServletFileUpload upload=new ServletFileUpload(factory);
         // b)通过upload对象进一步解析请求（解析Http请求中奇怪的bidy中的内容）
         //FileItem就代表一个上传的文件对象
         //理论上来说，Http支持一个请求中同事都上传多个文件
       List<FileItem> items=null;
        try {
            items= upload.parseRequest(req);
        } catch (FileUploadException e) {
            e.printStackTrace();
            //告诉客户端出现的具体的错误是啥
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write("{\"ok\":false,\"reason\":\"请求解析失败\"}");
            return;
        }
        //c)把FileItem中的属性提取出来，转换成Image对象
        FileItem fileItem=items.get(0);
        Image image=new Image();
        image.setImageName(fileItem.getName());
        image.setSize((int)fileItem.getSize());
        //手动获取一下当前时间，并转成格式化日期,yyMMdd=>20200318
        SimpleDateFormat simpleDataFormat=new SimpleDateFormat("yyyyMMdd");
        image.setUploadTime(simpleDataFormat.format(new Date()));
        image.setContentType(fileItem.getContentType());
        image.setMd5(DigestUtils.md5Hex(fileItem.get()));
        //自己构造一个路径来保存,引入时间戳是为了让路径能够唯一
        image.setPath("./image/"+image.getMd5());
        //存到数据库中
        ImageDao imageDao=new ImageDao();
        //看看数据库中是否存在相同的MD5的图片，不存在，则返回null
        Image existImage=imageDao.selectByMd5(image.getMd5());
        imageDao.insert(image);
        //2.获取图片的属性信息，并且写入磁盘文件
        if(existImage==null) {
            File file = new File(image.getPath());
            try {
                fileItem.write(file);
            } catch (Exception e) {
                e.printStackTrace();
                resp.setContentType("application/json;charset=utf-8");
                resp.getWriter().write("{\"ok\":false,\"reason\":\"写磁盘失败\"}");
                return;
            }
        }
        //3.给客户端返回一个结果数据
       // resp.setContentType("application/json;charset=utf-8");
        // resp.getWriter().write("{\"ok\":true}");
        resp.sendRedirect("index.html");
    }

    /**
     * 删除指定图片
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       resp.setContentType("application/json;charset=utf-8");
        //1.先获取请求中的imageId
        String imageId=req.getParameter("imageId");
        if(imageId==null||imageId.equals("")){
            resp.setStatus(200);
            resp.getWriter().write("{\"ok\":false,\"reason\":\"解析请求失败\"}");
            return;
        }
        //2.创建ImageDao对象，查看到该图片对象对应的相关属性
        ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(Integer.parseInt(imageId));
        if(imageId==null){
            //此时传入的id在数据库中不存在
            resp.setStatus(200);
            resp.getWriter().write("{\"ok\":false,\"reason\":\"imageId在数据库中不存在\"}");
            return;
        }
        //3.删除数据库中的记录
        imageDao.delete(Integer.parseInt(imageId));
        //4.删除本地磁盘文件
        File file=new File(image.getPath());
        file.delete();
        resp.setStatus(200);
        resp.getWriter().write("{\"ok\":true}");

    }
}
