package ormFramework.core;

import ormFramework.annotation.Column;
import ormFramework.annotation.Entity;
import ormFramework.annotation.Id;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityManagerImpl implements EntityManager {

    private final Connection connection;

    public EntityManagerImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public <T> T findById(int id, Class<T> type) throws SQLException,
            InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String tableName = type.getAnnotation(Entity.class).tableName();
        String idColumnName = Arrays.stream(type.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow()
                .getName();

        PreparedStatement stm =
                this.connection.prepareStatement("SELECT * FROM " + tableName + " WHERE " + idColumnName + " = ?");

        stm.setInt(1, id);

        T entity = (T) type.getConstructors()[0].newInstance();

        ResultSet resultSet = stm.executeQuery();

        if (!resultSet.next()) {
            return null;
        }
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column columnInfo = field.getAnnotation(Column.class);
                String setterName = "set" + ((field.getName().charAt(0) + "").toUpperCase() + field.getName().substring(1));
                if (field.getType().equals(String.class)) {
                    String s = resultSet.getString(columnInfo.name());
                    type.getMethod(setterName, String.class).invoke(entity, s);
                } else if (field.getType().equals(LocalDate.class)) {
                    LocalDate s = LocalDate.parse(resultSet.getString(columnInfo.name()));
                    type.getMethod(setterName, LocalDate.class).invoke(entity, s);
                } else {
                    int s = resultSet.getInt(columnInfo.name());
                    type.getMethod(setterName, field.getType()).invoke(entity, s);
                }
            } else if (field.isAnnotationPresent(Id.class)) {
                String setterName = "set" + ((field.getName().charAt(0) + "").toUpperCase() + field.getName().substring(1));
                type.getMethod(setterName, int.class).invoke(entity, id);
            }
        }
        return entity;
    }

    @Override
    public <T> boolean persist(T entity) throws IllegalAccessException, SQLException {

        Field idField = getIdFieldFromEntity(entity);
        idField.setAccessible(true);
        int id = (int) idField.get(entity);

        if (id == 0) {
            return insertRecord(entity);
        } else {
            return updateRecord(id, entity);
        }

    }

    @Override
    public <T> boolean delete(T entity) throws IllegalAccessException, SQLException {
        Field fieldId = getIdFieldFromEntity(entity);
        fieldId.setAccessible(true);
        int id = (int) fieldId.get(entity);
        String tableName = getTableName(entity);
        String sqlDelete = String.format("DELETE FROM %s WHERE id = ?;", tableName);
        PreparedStatement statement = connection.prepareStatement(sqlDelete);
        statement.setInt(1, id);
        return statement.executeUpdate() > 0;
    }

    @Override
    public <T> void doAlter(T entity) throws SQLException {
        String tableName = getTableName(entity);
        String sqlAlter = String.format("ALTER TABLE %s ADD COLUMN %s;", tableName, getNewFields(entity.getClass()));
        PreparedStatement statement = connection.prepareStatement(sqlAlter);
        statement.executeUpdate();
    }

    private String getNewFields(Class<?> clazz) throws SQLException {
        Set<String> allFields = getAllFieldsFromTable();
        StringBuilder sb = new StringBuilder();
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .forEach(field -> {
                    String fieldName = field.getName();
                    if (!allFields.contains(fieldName)) {
                        sb.append(fieldName).append(" ").append(field.getAnnotation(Column.class).columnDefinition());
                    }
                });

        return sb.toString().trim();
    }

    private Set<String> getAllFieldsFromTable() throws SQLException {
        Set<String> allFields = new HashSet<>();
        String sqlFindFields = "SELECT `COLUMN_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS`\n" +
                "WHERE `TABLE_SCHEMA` = 'test_orm'\n" +
                "AND `TABLE_NAME` = 'users' AND `COLUMN_NAME` != 'id';";

        PreparedStatement statement = connection.prepareStatement(sqlFindFields);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            allFields.add(rs.getString(1));
        }

        return allFields;
    }

    private <T> boolean updateRecord(int id, T entity) throws SQLException {
        String tableName = getTableName(entity);
        String fields = Arrays.stream(entity.getClass()
                        .getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Column.class))
                .map(f -> String.format("%s = %s", f.getAnnotation(Column.class).name(), getValueToString(f, entity)))
                .collect(Collectors.joining(", "));

        String sqlUpdate = String.format("UPDATE %s\n" +
                "SET %s\n" +
                "WHERE id = ?;", tableName, fields);

        PreparedStatement statement = connection.prepareStatement(sqlUpdate);
        statement.setInt(1, id);
        return statement.executeUpdate() > 0;
    }

    private <T> boolean insertRecord(T entity) throws SQLException {
        String tableName = getTableName(entity);
        String filedNames = getFieldNamesBy(entity.getClass());
        String fieldValues = getFieldValuesAsStr(entity);
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, filedNames, fieldValues);
        System.out.println();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        return preparedStatement.executeUpdate() > 0;
    }

    private <T> String getFieldValuesAsStr(T entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Column.class))
                .map(f -> getValueToString(f, entity))
                .collect(Collectors.joining(", "));
    }

    private String getFieldNamesBy(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Column.class))
                .map(f -> f.getAnnotation(Column.class).name())
                .collect(Collectors.joining(", "));

    }

    private <T> String getTableName(T entity) {
        return entity.getClass().getAnnotation(Entity.class)
                .tableName();
    }

    private <T> Field getIdFieldFromEntity(T entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Entity doesn't have id!"));

    }

    private <T> String getValueToString(Field field, T entity) {
        String type = field.getAnnotation(Column.class).columnDefinition();
        String formattedType = "";
        field.setAccessible(true);

        try {
            if (type.equals("DATE") || type.startsWith("VARCHAR")) {
                formattedType = String.format(" '%s' ", field.get(entity));

            } else {
                formattedType = String.format(" %s ", field.get(entity));
            }

        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return formattedType;
    }

}
