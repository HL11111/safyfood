package com.system.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
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
  @ApiModel(value = "Orders对象", description = "?????")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "order_id", type = IdType.AUTO)
      private Integer orderId;

    private Integer userId;

    private Integer foodId;

    private String orderType;

    private String orderDetails;

    private BigDecimal orderPrice;

    private String orderStatus;

    private String orderPhone;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime expectTime;

    private LocalDateTime finishTime;

    private Integer isDeleted;


}
