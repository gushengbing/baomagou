package com.bmg.mall.service;

import com.bmg.mall.controller.vo.bmgShoppingCartItemVO;
import com.bmg.mall.entity.bmgShoppingCartItem;

import java.util.List;

public interface bmgShoppingCartService {

    /**
     * 保存商品至购物车中
     *
     * @param bmgShoppingCartItem
     * @return
     */
    String savebmgCartItem(bmgShoppingCartItem bmgShoppingCartItem);

    /**
     * 修改购物车中的属性
     *
     * @param bmgShoppingCartItem
     * @return
     */
    String updatebmgCartItem(bmgShoppingCartItem bmgShoppingCartItem);

    /**
     * 获取购物项详情
     *
     * @param bmgShoppingCartItemId
     * @return
     */
    bmgShoppingCartItem getbmgCartItemById(Long bmgShoppingCartItemId);

    /**
     * 删除购物车中的商品
     *
     * @param bmgShoppingCartItemId
     * @return
     */
    Boolean deleteById(Long bmgShoppingCartItemId);

    /**
     * 获取我的购物车中的列表数据
     *
     * @param bmgUserId
     * @return
     */
    List<bmgShoppingCartItemVO> getMyShoppingCartItems(Long bmgUserId);
}
