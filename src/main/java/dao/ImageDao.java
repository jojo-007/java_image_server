package dao;

import com.google.gson.internal.$Gson$Preconditions;
import com.mysql.jdbc.ConnectionFeatureNotAvailableException;
import common.JavaImageServerException;
import sun.security.pkcs11.Secmod;

import javax.print.attribute.standard.PresentationDirection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImageDao {
    public void insert(Image image) {
        //把image对象插入到数据库中
        //1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2 创建相关的SQL语句
        String sql = "insert into image_table values(null,?,?,?,?,?,?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, image.getImageName());
            statement.setInt(2, image.getSize());
            statement.setString(3, image.getUploadTime());
            statement.setString(4, image.getContentType());
            statement.setString(5, image.getPath());
            statement.setString(6, image.getMd5());
            //3.执行SQL语句
            int ret = statement.executeUpdate();
            if (ret != 1) {
                //出现问题抛出异常
                throw new JavaImageServerException("插入数据库出错");
            }
        } catch (SQLException | JavaImageServerException e) {
            e.printStackTrace();
        } finally {
            //4.关闭连接和statement对象
            DBUtil.close(connection, statement, null);
        }
    }

    public List<Image> selectAll() {
        List<Image> images=new ArrayList<>();
        //查找数据库中所有图片的信息
        //1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2.创建相关的SQL语句
        String sql="select * from image_table";
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        try {
            //3.执行SQL语句
            statement=connection.prepareStatement(sql);
            resultSet=statement.executeQuery();
            //4.处理结果集
            while(resultSet.next()){
                Image image=new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("Size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("Md5"));
                images.add(image);
            }
            return images;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //5.关闭连接
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }

    public Image selectOne(int imageId) {
        //查找一个图片的信息
        //1.获取数据库连接
        Connection connection=DBUtil.getConnection();
        //2.创建SQL语句
        String sql="select * from image_table where imageId=?";
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        //3，执行SQL语句
        try {
            statement=connection.prepareStatement(sql);
            statement.setInt(1,imageId);
            resultSet=statement.executeQuery();
            //4.处理结果集
            if(resultSet.next()){
                Image image=new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("Md5"));
                return image;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //5.关闭连接
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }
  public void delete(int imageId) {
        //根据ImageId删除指定图片
      //1.连接数据库
      Connection connection=DBUtil.getConnection();
      //2.创建SQL语句
      String sql="delete from image_table where imageId=?";
      PreparedStatement statement=null;
      try {
          //3.执行SQL语句
          statement=connection.prepareStatement(sql);
          statement.setInt(1,imageId);
          int ret=statement.executeUpdate();
          if(ret!=1) {
              throw new JavaImageServerException("删除数据库操作失败");
          }
      } catch (SQLException | JavaImageServerException e) {
          e.printStackTrace();
      } finally {
          //4.关闭连接
          DBUtil.close(connection,statement,null);
      }
    }

    public static void main(String[] args) {
        //用于进行简单的测试
        //1.测试插入数据
//        Image image=new Image();
//        image.setImageName("1.png");
//        image.setSize(100);
//        image.setUploadTime("20200317");
//        image.setContentType("image/png");
//        image.setPath("./data/1.png");
//        image.setMd5("11223344");
//        ImageDao imageDao=new ImageDao();
//        imageDao.insert(image);
        //2.测试查看所有图片属性
//        ImageDao imageDao=new ImageDao();
//        List<Image> images=imageDao.selectAll();
//        System.out.println(images);
         //3.测试查找指定图片信息
//        ImageDao imageDao=new ImageDao();
//        Image image=imageDao.selectOne(1);
//        System.out.println(image);
        //4。测试删除图片信息
          ImageDao imageDao=new ImageDao();
          imageDao.delete(1);
    }
    public Image selectByMd5(String md5){
        Connection connection=DBUtil.getConnection();
        //2.创建SQL语句
        String sql="select * from image_table where md5=?";
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        //3，执行SQL语句
        try {
            statement=connection.prepareStatement(sql);
            statement.setString(1,md5);
            resultSet=statement.executeQuery();
            //4.处理结果集
            if(resultSet.next()){
                Image image=new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("Md5"));
                return image;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //5.关闭连接
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }
}