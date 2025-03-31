package com.system.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * ??ַ?
 * </p>
 *
 * @author H
 * @since 2025-03-26
 */
@Getter
@Setter
  @ApiModel(value = "Address对象", description = "??ַ?")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "address_id", type = IdType.AUTO)
      private Integer addressId;

    private Integer userId;

    private String province;

    private String city;

    private String addressDetails;

    private String postalCode;

    private String contactPhone;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;


}
