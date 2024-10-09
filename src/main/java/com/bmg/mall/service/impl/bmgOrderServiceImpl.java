package com.bmg.mall.service.impl;

import com.bmg.mall.common.*;
import com.bmg.mall.controller.vo.*;
import com.bmg.mall.entity.StockNumDTO;
import com.bmg.mall.entity.bmgGoods;
import com.bmg.mall.entity.bmgOrder;
import com.bmg.mall.entity.bmgOrderItem;
import com.bmg.mall.service.bmgOrderService;
import com.bmg.mall.util.BeanUtil;
import com.bmg.mall.util.NumberUtil;
import com.bmg.mall.util.PageQueryUtil;
import com.bmg.mall.util.PageResult;
import com.bmg.mall.dao.bmgOrderMapper;
import com.bmg.mall.dao.bmgOrderItemMapper;
import com.bmg.mall.dao.bmgShoppingCartItemMapper;
import com.bmg.mall.dao.bmgGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class bmgOrderServiceImpl implements bmgOrderService {

    @Autowired
    private bmgOrderMapper BmgOrderMapper;
    @Autowired
    private bmgOrderItemMapper BmgOrderItemMapper;
    @Autowired
    private bmgShoppingCartItemMapper BmgShoppingCartItemMapper;
    @Autowired
    private bmgGoodsMapper BmgGoodsMapper;

    @Override
    public PageResult getbmgOrdersPage(PageQueryUtil pageUtil) {
        List<bmgOrder> BmgOrders = BmgOrderMapper.findbmgOrderList(pageUtil);
        int total = BmgOrderMapper.getTotalbmgOrders(pageUtil);
        PageResult pageResult = new PageResult(BmgOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(bmgOrder BmgOrder) {
        bmgOrder temp = BmgOrderMapper.selectByPrimaryKey(BmgOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(BmgOrder.getTotalPrice());
            temp.setUserAddress(BmgOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (BmgOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<bmgOrder> orders = BmgOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (bmgOrder BmgOrder : orders) {
                if (BmgOrder.getIsDeleted() == 1) {
                    errorOrderNos += BmgOrder.getOrderNo() + " ";
                    continue;
                }
                if (BmgOrder.getOrderStatus() != 1) {
                    errorOrderNos += BmgOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (BmgOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<bmgOrder> orders = BmgOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (bmgOrder BmgOrder : orders) {
                if (BmgOrder.getIsDeleted() == 1) {
                    errorOrderNos += BmgOrder.getOrderNo() + " ";
                    continue;
                }
                if (BmgOrder.getOrderStatus() != 1 && BmgOrder.getOrderStatus() != 2) {
                    errorOrderNos += BmgOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (BmgOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<bmgOrder> orders = BmgOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (bmgOrder BmgOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (BmgOrder.getIsDeleted() == 1) {
                    errorOrderNos += BmgOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (BmgOrder.getOrderStatus() == 4 || BmgOrder.getOrderStatus() < 0) {
                    errorOrderNos += BmgOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (BmgOrderMapper.closeOrder(Arrays.asList(ids), bmgOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(bmgUserVO user, List<bmgShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(bmgShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(bmgShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<bmgGoods> BmgGoods = BmgGoodsMapper.selectByPrimaryKeys(goodsIds);
        Map<Long, bmgGoods> BmgGoodsMap = BmgGoods.stream().collect(Collectors.toMap(bmgGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (bmgShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!BmgGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                bmgException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > BmgGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                bmgException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(BmgGoods)) {
            if (BmgShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = BmgGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    bmgException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                bmgOrder BmgOrder = new bmgOrder();
                BmgOrder.setOrderNo(orderNo);
                BmgOrder.setUserId(user.getUserId());
                BmgOrder.setUserAddress(user.getAddress());
                //总价
                for (bmgShoppingCartItemVO BmgShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += BmgShoppingCartItemVO.getGoodsCount() * BmgShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    bmgException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                BmgOrder.setTotalPrice(priceTotal);
                //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                BmgOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (BmgOrderMapper.insertSelective(BmgOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<bmgOrderItem> BmgOrderItems = new ArrayList<>();
                    for (bmgShoppingCartItemVO BmgShoppingCartItemVO : myShoppingCartItems) {
                        bmgOrderItem BmgOrderItem = new bmgOrderItem();
                        //使用BeanUtil工具类将BmgShoppingCartItemVO中的属性复制到BmgOrderItem对象中
                        BeanUtil.copyProperties(BmgShoppingCartItemVO, BmgOrderItem);
                        //bmgOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        BmgOrderItem.setOrderId(BmgOrder.getOrderId());
                        BmgOrderItems.add(BmgOrderItem);
                    }
                    //保存至数据库
                    if (BmgOrderItemMapper.insertBatch(BmgOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    bmgException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                bmgException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            bmgException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        bmgException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public bmgOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        bmgOrder BmgOrder = BmgOrderMapper.selectByOrderNo(orderNo);
        if (BmgOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            List<bmgOrderItem> orderItems = BmgOrderItemMapper.selectByOrderId(BmgOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<bmgOrderItemVO> BmgOrderItemVOS = BeanUtil.copyList(orderItems, bmgOrderItemVO.class);
                bmgOrderDetailVO BmgOrderDetailVO = new bmgOrderDetailVO();
                BeanUtil.copyProperties(BmgOrder, BmgOrderDetailVO);
                BmgOrderDetailVO.setOrderStatusString(bmgOrderStatusEnum.getbmgOrderStatusEnumByStatus(BmgOrderDetailVO.getOrderStatus()).getName());
                BmgOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(BmgOrderDetailVO.getPayType()).getName());
                BmgOrderDetailVO.setBmgOrderItemVOS(BmgOrderItemVOS);
                return BmgOrderDetailVO;
            }
        }
        return null;
    }

    @Override
    public bmgOrder getbmgOrderByOrderNo(String orderNo) {
        return BmgOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = BmgOrderMapper.getTotalbmgOrders(pageUtil);
        List<bmgOrder> BmgOrders = BmgOrderMapper.findbmgOrderList(pageUtil);
        List<bmgOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(BmgOrders, bmgOrderListVO.class);
            //设置订单状态中文显示值
            for (bmgOrderListVO BmgOrderListVO : orderListVOS) {
                BmgOrderListVO.setOrderStatusString(bmgOrderStatusEnum.getbmgOrderStatusEnumByStatus(BmgOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = BmgOrders.stream().map(bmgOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<bmgOrderItem> orderItems = BmgOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<bmgOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(bmgOrderItem::getOrderId));
                for (bmgOrderListVO BmgOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(BmgOrderListVO.getOrderId())) {
                        List<bmgOrderItem> orderItemListTemp = itemByOrderIdMap.get(BmgOrderListVO.getOrderId());
                        //将bmgOrderItem对象列表转换成bmgOrderItemVO对象列表
                        List<bmgOrderItemVO> BmgOrderItemVOS = BeanUtil.copyList(orderItemListTemp, bmgOrderItemVO.class);
                        BmgOrderListVO.setBmgOrderItemVOS(BmgOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        bmgOrder BmgOrder = BmgOrderMapper.selectByOrderNo(orderNo);
        if (BmgOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            if (BmgOrderMapper.closeOrder(Collections.singletonList(BmgOrder.getOrderId()), bmgOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        bmgOrder BmgOrder = BmgOrderMapper.selectByOrderNo(orderNo);
        if (BmgOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            BmgOrder.setOrderStatus((byte) bmgOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            BmgOrder.setUpdateTime(new Date());
            if (BmgOrderMapper.updateByPrimaryKeySelective(BmgOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        bmgOrder BmgOrder = BmgOrderMapper.selectByOrderNo(orderNo);
        if (BmgOrder != null) {
            //todo 订单状态判断 非待支付状态下不进行修改操作
            BmgOrder.setOrderStatus((byte) bmgOrderStatusEnum.OREDER_PAID.getOrderStatus());
            BmgOrder.setPayType((byte) payType);
            BmgOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            BmgOrder.setPayTime(new Date());
            BmgOrder.setUpdateTime(new Date());
            if (BmgOrderMapper.updateByPrimaryKeySelective(BmgOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public List<bmgOrderItemVO> getOrderItems(Long id) {
        bmgOrder BmgOrder = BmgOrderMapper.selectByPrimaryKey(id);
        if (BmgOrder != null) {
            List<bmgOrderItem> orderItems = BmgOrderItemMapper.selectByOrderId(BmgOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<bmgOrderItemVO> BmgOrderItemVOS = BeanUtil.copyList(orderItems, bmgOrderItemVO.class);
                return BmgOrderItemVOS;
            }
        }
        return null;
    }
}
