package db;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;


/*
 * author qianyang 1563178220@qq.com
 * Mysql操作的QueryRunner方法
 * 一个数据库操作类，别的程序直接调用即可
 */
public class MYSQLControl {

    //根据自己的数据库地址修改
    static DataSource ds = MyDataSource.getDataSource("jdbc:mysql://127.0.0.1:3306/programmableweb");
    static QueryRunner qr = new QueryRunner(ds);
    //第一类方法
    public static void executeUpdate(String sql){
        try {
            qr.update(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static <T> List<T> getListInfoBySQL (String sql, Class<T> type ){
		List<T> list = null;
		try {
			list = qr.query(sql,new BeanListHandler<T>(type));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Object> getListOneBySQL (String sql,String id){
		List<Object> list=null;
		try {
			list = (List<Object>) qr.query(sql, new ColumnListHandler(id));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}