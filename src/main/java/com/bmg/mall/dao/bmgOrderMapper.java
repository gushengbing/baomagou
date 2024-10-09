package com.bmg.mall.dao;

import com.bmg.mall.entity.bmgOrder;
import com.bmg.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface bmgOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(bmgOrder record);

    int insertSelective(bmgOrder record);

    bmgOrder selectByPrimaryKey(Long orderId);

    bmgOrder selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(bmgOrder record);

    int updateByPrimaryKey(bmgOrder record);

    List<bmgOrder> findbmgOrderList(PageQueryUtil pageUtil);

    int getTotalbmgOrders(PageQueryUtil pageUtil);

    List<bmgOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(@Param("orderIds") List<Long> asList);
}
