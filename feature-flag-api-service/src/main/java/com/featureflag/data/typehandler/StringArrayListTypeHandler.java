package com.featureflag.data.typehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StringArrayListTypeHandler implements TypeHandler<List<String>> {

    @SneakyThrows
    @Override
    public void setParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i,stringify(parameter));
    }

    @SneakyThrows
    @Override
    public List<String> getResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getObject(columnName));
    }

    @SneakyThrows
    @Override
    public List<String> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getObject(columnIndex));
    }

    @SneakyThrows
    @Override
    public List<String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getObject(columnIndex));
    }

    String stringify(List<String> obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    List<String> parse(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue((String)obj,new TypeReference<>() {});
    }
}
