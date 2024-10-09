package com.bmg.mall.service.impl;

import com.bmg.mall.common.Constants;
import com.bmg.mall.common.ServiceResultEnum;
import com.bmg.mall.controller.vo.bmgShoppingCartItemVO;
import com.bmg.mall.entity.bmgGoods;
import com.bmg.mall.entity.bmgShoppingCartItem;
import com.bmg.mall.service.bmgShoppingCartService;
import com.bmg.mall.util.BeanUtil;
import com.bmg.mall.dao.bmgGoodsMapper;
import com.bmg.mall.dao.bmgShoppingCartItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class bmgShoppingCartServiceImpl implements bmgShoppingCartService {

    @Autowired
    private bmgShoppingCartItemMapper BmgShoppingCartItemMapper;

    @Autowired
    private bmgGoodsMapper BmgGoodsMapper;

    //todo 修改session中购物项数量

    @Override
    public String savebmgCartItem(bmgShoppingCartItem BmgShoppingCartItem) {
        bmgShoppingCartItem temp = BmgShoppingCartItemMapper.selectByUserIdAndGoodsId(BmgShoppingCartItem.getUserId(), BmgShoppingCartItem.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            //todo count = tempCount + 1
            temp.setGoodsCount(BmgShoppingCartItem.getGoodsCount());
            return updatebmgCartItem(temp);
        }
        bmgGoods BmgGoods = BmgGoodsMapper.selectByPrimaryKey(BmgShoppingCartItem.getGoodsId());
        //商品为空
        if (BmgGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = BmgShoppingCartItemMapper.selectCountByUserId(BmgShoppingCartItem.getUserId());
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //保存记录
        if (BmgShoppingCartItemMapper.insertSelective(BmgShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updatebmgCartItem(bmgShoppingCartItem BmgShoppingCartItem) {
        bmgShoppingCartItem BmgShoppingCartItemUpdate = BmgShoppingCartItemMapper.selectByPrimaryKey(BmgShoppingCartItem.getCartItemId());
        if (BmgShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出最大数量
        if (BmgShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //todo 数量相同不会进行修改
        //todo userId不同不能修改
        BmgShoppingCartItemUpdate.setGoodsCount(BmgShoppingCartItem.getGoodsCount());
        BmgShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (BmgShoppingCartItemMapper.updateByPrimaryKeySelective(BmgShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public bmgShoppingCartItem getbmgCartItemById(Long BmgShoppingCartItemId) {
        return BmgShoppingCartItemMapper.selectByPrimaryKey(BmgShoppingCartItemId);
    }

    @Override
    public Boolean deleteById(Long BmgShoppingCartItemId) {
        //todo userId不同不能删除
        return BmgShoppingCartItemMapper.deleteByPrimaryKey(BmgShoppingCartItemId) > 0;
    }

    @Override
    public List<bmgShoppingCartItemVO> getMyShoppingCartItems(Long BmgUserId) {
        List<bmgShoppingCartItemVO> BmgShoppingCartItemVOS = new ArrayList<>();
        List<bmgShoppingCartItem> BmgShoppingCartItems = BmgShoppingCartItemMapper.selectByUserId(BmgUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(BmgShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> BmgGoodsIds = BmgShoppingCartItems.stream().map(bmgShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<bmgGoods> BmgGoods = BmgGoodsMapper.selectByPrimaryKeys(BmgGoodsIds);
            Map<Long, bmgGoods> BmgGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(BmgGoods)) {
                BmgGoodsMap = BmgGoods.stream().collect(Collectors.toMap(bmgGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (bmgShoppingCartItem BmgShoppingCartItem : BmgShoppingCartItems) {
                bmgShoppingCartItemVO BmgShoppingCartItemVO = new bmgShoppingCartItemVO();
                BeanUtil.copyProperties(BmgShoppingCartItem, BmgShoppingCartItemVO);
                if (BmgGoodsMap.containsKey(BmgShoppingCartItem.getGoodsId())) {
                    bmgGoods BmgGoodsTemp = BmgGoodsMap.get(BmgShoppingCartItem.getGoodsId());
                    BmgShoppingCartItemVO.setGoodsCoverImg(BmgGoodsTemp.getGoodsCoverImg());
                    String goodsName = BmgGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    BmgShoppingCartItemVO.setGoodsName(goodsName);
                    BmgShoppingCartItemVO.setSellingPrice(BmgGoodsTemp.getSellingPrice());
                    BmgShoppingCartItemVOS.add(BmgShoppingCartItemVO);
                }
            }
        }
        return BmgShoppingCartItemVOS;
    }
}
