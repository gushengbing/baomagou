package com.bmg.mall.dao;

import com.bmg.mall.entity.bmgShoppingCartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface bmgShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(bmgShoppingCartItem record);

    int insertSelective(bmgShoppingCartItem record);

    bmgShoppingCartItem selectByPrimaryKey(Long cartItemId);

    bmgShoppingCartItem selectByUserIdAndGoodsId(@Param("bmgUserId") Long bmgUserId, @Param("goodsId") Long goodsId);

    List<bmgShoppingCartItem> selectByUserId(@Param("bmgUserId") Long bmgUserId, @Param("number") int number);

    int selectCountByUserId(Long bmgUserId);

    int updateByPrimaryKeySelective(bmgShoppingCartItem record);

    int updateByPrimaryKey(bmgShoppingCartItem record);

    int deleteBatch(List<Long> ids);
}
