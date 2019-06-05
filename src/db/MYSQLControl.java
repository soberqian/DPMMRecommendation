package db;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;


/*
 * author qianyang 1563178220@qq.com
 * Mysql������QueryRunner����
 * һ�����ݿ�����࣬��ĳ���ֱ�ӵ��ü���
 */
public class MYSQLControl {

    //�����Լ������ݿ��ַ�޸�
    static DataSource ds = MyDataSource.getDataSource("jdbc:mysql://127.0.0.1:3306/programmableweb");
    static QueryRunner qr = new QueryRunner(ds);
    //��һ�෽��
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