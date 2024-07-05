package utils;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ReflectionMapper {
    public static <T> T mapResultSetToObject(ResultSet rs, Class<T> outputClass) {
        T obj = null;
        try {
            obj = outputClass.getDeclaredConstructor().newInstance();
            Field[] fields = outputClass.getDeclaredFields();

            Map<String, Field> fieldMap = new HashMap<>();
            for (Field field : fields) {
                field.setAccessible(true);
                fieldMap.put(field.getName().toLowerCase(), field);
            }

            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                String columnName = rs.getMetaData().getColumnName(i).toLowerCase();
                if (fieldMap.containsKey(columnName)) {
                    Field field = fieldMap.get(columnName);
                    setFieldValue(field, obj, rs, i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    private static <T> void setFieldValue(Field field, T obj, ResultSet rs, int columnIndex) throws SQLException, IllegalAccessException {
        Class<?> fieldType = field.getType();
        if (fieldType == String.class) {
            field.set(obj, rs.getString(columnIndex));
        } else if (fieldType == int.class || fieldType == Integer.class) {
            field.set(obj, rs.getInt(columnIndex));
        } else if (fieldType == long.class || fieldType == Long.class) {
            field.set(obj, rs.getLong(columnIndex));
        } else if (fieldType == double.class || fieldType == Double.class) {
            field.set(obj, rs.getDouble(columnIndex));
        } else if (fieldType == BigDecimal.class) {
            field.set(obj, rs.getBigDecimal(columnIndex));
        } else if (fieldType == Timestamp.class) {
            field.set(obj, rs.getTimestamp(columnIndex));
        } // add other types as needed
    }
}
