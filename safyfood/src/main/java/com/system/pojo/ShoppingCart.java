package com.system.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Blob;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * ???ﳵ?
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Getter
@Setter
@TableName("shopping_cart")
@ApiModel(value = "ShoppingCart对象", description = "???ﳵ?")
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "shopping_id", type = IdType.AUTO)
    private Integer shoppingId;

    private Integer userId;

    private Integer foodId;

    private String foodName;

    private String foodDescription;

    private Blob foodPhoto;

    private BigDecimal foodPrice;

    private Integer foodQuantity;

    private BigDecimal totalPrice;

    private Boolean isChecked;

    private String shoppingStatus;

    private LocalDateTime createTime;

    private Integer isDeleted;


}
