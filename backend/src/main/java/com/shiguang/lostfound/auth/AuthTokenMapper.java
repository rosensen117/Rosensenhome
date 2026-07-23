package com.shiguang.lostfound.auth;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthTokenMapper {
    int insert(AuthToken token);
    UserAccount findUserByValidTokenHash(@Param("tokenHash") String tokenHash);
    int deleteByTokenHash(@Param("tokenHash") String tokenHash);
}
