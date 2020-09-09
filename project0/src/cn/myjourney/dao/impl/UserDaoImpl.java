package cn.myjourney.dao.impl;

import cn.myjourney.dao.userDao;
import cn.myjourney.domain.User;
import cn.myjourney.util.JDBCUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDaoImpl implements userDao {
    private JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());


    @Override
    public User findUserByUsernameAndPassword(String username, String password) {
        try {
            String sql = "select * from user where username = ? and password = ?";
            User user = template.queryForObject(sql, new BeanPropertyRowMapper<User>(User.class), username, password);
            return user;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void add(User user) {
        String sql = "insert into user values (null, ?, ?, ?, ?, ?, null, null)";
        template.update(sql, user.getName(), user.getGender(), user.getAge(), user.getAddress(), user.getEmail());
    }

    @Override
    public List<User> findAll() {
        String sql = "select * from user";
        List<User> users = template.query(sql, new BeanPropertyRowMapper<User>(User.class));
        return users;
    }

    @Override
    public void remove(int id) {
        String sql = "delete from user where id = ?";
        template.update(sql, id);
    }

    @Override
    public User findUser(String id) {
        String sql = "select * from user where id = ?";
        User user = template.queryForObject(sql, new BeanPropertyRowMapper<User>(User.class), id);
        return user;
    }

    @Override
    public void update(User user) {
        String sql = "update user set name = ?, gender = ?, age = ?, address = ?, email = ? where id = ?";
        template.update(sql,user.getName(), user.getGender(),user.getAge(), user.getAddress(), user.getEmail(), user.getId());
    }

    @Override
    public int totalCount(Map<String, String[]> condition) {
        String sql = "select count(*) from user where 1 = 1";
        StringBuilder sb = new StringBuilder(sql);
        List<Object> list = new ArrayList<>();
        for (String key : condition.keySet()) {
            if ("currentPage".equals(key) || "rows".equals(key))
                continue;
            String value = condition.get(key)[0];
            if (value != null && !"".equals(value)) {
                sb.append(" and " + key + " like ? ");
                list.add("%" + value + "%");
            }
        }

        return template.queryForObject(sb.toString(), Integer.class, list.toArray());
    }

    @Override
    public List<User> findByPage(int start, int rows, Map<String, String[]> condition) {
        String sql;
        if (!condition.isEmpty()) {
            sql = "select * from user where 1 = 1";
            StringBuilder sb = new StringBuilder(sql);
            List<Object> list = new ArrayList<>();
            for (String key : condition.keySet()) {
                if ("currentPage".equals(key) || "rows".equals(key))
                    continue;
                String value = condition.get(key)[0];
                if (value != null && !"".equals(value)) {
                    sb.append(" and " + key + " like  ? ");
                    list.add("%" + value + "%");
                }
            }
            sb.append(" limit ?, ? ");
            list.add(start);
            list.add(rows);
            return template.query(sb.toString(), new BeanPropertyRowMapper<User>(User.class),list.toArray());
        }

        else {
            sql = "select * from user limit ?, ?";
            return template.query(sql, new BeanPropertyRowMapper<User>(User.class), start, rows);
        }
    }
}
