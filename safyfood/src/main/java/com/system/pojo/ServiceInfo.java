package com.system.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * ??????Ϣ?
 * </p>
 *
 * @author H
 * @since 2025-03-26
 */
@Getter
@Setter
  @TableName("service_info")
@ApiModel(value = "ServiceInfo对象", description = "??????Ϣ?")
public class ServiceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "service_id", type = IdType.AUTO)
      private Integer serviceId;

    private Integer articleId;

    private Integer userId;

    private String foodId;

    private String serviceName;

    private String serviceDescription;

    private String serviceType;

    private String serviceStatus;

    private String servicePhone;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;


}
