package com.system.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * ?û??
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Getter
@Setter
  @ApiModel(value = "Users对象", description = "?û??")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "user_id", type = IdType.AUTO)
      private Integer userId;

    private String userName;

    private String password;

    private String secondPassword;

    private String email;

    private String phone;

    private LocalDateTime registerTime;

    private LocalDate userBorn;

    private String userIdcard;

    private Blob userHealth;

    private String userScore;

    private String userAddress;

    private Blob userHead;

    private String userNickname;

    private Integer userAttention;

    private Integer userFans;

    private Integer userFavorite;

    private Integer userArticle;

    private Blob workerIcon;

    private String userType;

    private String shopName;

    private String businessScope;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Blob greenIcon;

    private Boolean isGreenCertified;

    private LocalDate certificationDate;

    private Integer adminLevel;

    private String userStatus;

    private Integer isDeleted;


}
