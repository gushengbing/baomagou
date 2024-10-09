package com.bmg.mall.dao;

import com.bmg.mall.entity.bmgOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface bmgOrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(bmgOrderItem record);

    int insertSelective(bmgOrderItem record);

    bmgOrderItem selectByPrimaryKey(Long orderItemId);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<bmgOrderItem> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<bmgOrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<bmgOrderItem> orderItems);

    int updateByPrimaryKeySelective(bmgOrderItem record);

    int updateByPrimaryKey(bmgOrderItem record);
}
