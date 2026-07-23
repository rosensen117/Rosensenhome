package com.shiguang.lostfound.auth;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserAccountMapper {
    UserAccount findByStudentId(@Param("studentId") String studentId);
    UserAccount findByAccount(@Param("account") String account);
    int countByStudentId(@Param("studentId") String studentId);
    int countByPhone(@Param("phone") String phone);
    int insert(UserAccount user);
}
