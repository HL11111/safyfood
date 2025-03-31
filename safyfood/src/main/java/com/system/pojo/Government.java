package com.system.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * ?????
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Getter
@Setter
  @ApiModel(value = "Government对象", description = "?????")
public class Government implements Serializable {

    private static final long serialVersionUID = 1L;

      private String governmentId;

    private String governmentName;

    private String governmentCity;

    private String governmentPhone;

    private String governmentEmail;

    private LocalDateTime createTime;

    private Integer isDeleted;


}
