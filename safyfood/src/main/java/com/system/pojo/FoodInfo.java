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
 * ʳƷ??Ϣ?
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Getter
@Setter
  @TableName("food_info")
@ApiModel(value = "FoodInfo对象", description = "ʳƷ??Ϣ?")
public class FoodInfo implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "food_id", type = IdType.AUTO)
      private Integer foodId;

    private Integer userId;

    private Integer orderId;

    private String foodName;

    private String foodCategory;

    private String foodBrand;

    private String foodDescription;

    private String foodIngredient;

    private Blob foodPhoto;

    private Integer monthSell;

    private Integer foodStore;

    private BigDecimal foodPrice;

    private String foodOrigin;

    private Integer favoriteStatus;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer rankType;

    private Integer isDeleted;


}
