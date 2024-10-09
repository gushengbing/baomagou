package com.bmg.mall.controller.mall;

import com.bmg.mall.common.Constants;
import com.bmg.mall.common.ServiceResultEnum;
import com.bmg.mall.controller.vo.bmgShoppingCartItemVO;
import com.bmg.mall.controller.vo.bmgUserVO;
import com.bmg.mall.entity.bmgShoppingCartItem;
import com.bmg.mall.util.Result;
import com.bmg.mall.util.ResultGenerator;
import com.bmg.mall.service.bmgShoppingCartService;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Resource
    private bmgShoppingCartService bmgShoppingCartService;

    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request,
                               HttpSession httpSession) {
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<bmgShoppingCartItemVO> myShoppingCartItems = bmgShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(bmgShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                return "error/error_5xx";
            }
            //总价
            for (bmgShoppingCartItemVO bmgShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += bmgShoppingCartItemVO.getGoodsCount() * bmgShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result savebmgShoppingCartItem(@RequestBody bmgShoppingCartItem bmgShoppingCartItem,
                                          HttpSession httpSession) {
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        bmgShoppingCartItem.setUserId(user.getUserId());
        //todo 判断数量
        String saveResult = bmgShoppingCartService.savebmgCartItem(bmgShoppingCartItem);
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updatebmgShoppingCartItem(@RequestBody bmgShoppingCartItem bmgShoppingCartItem,
                                            HttpSession httpSession) {
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        bmgShoppingCartItem.setUserId(user.getUserId());
        //todo 判断数量
        String updateResult = bmgShoppingCartService.updatebmgCartItem(bmgShoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{bmgShoppingCartItemId}")
    @ResponseBody
    public Result updatebmgShoppingCartItem(@PathVariable("bmgShoppingCartItemId") Long bmgShoppingCartItemId,
                                            HttpSession httpSession) {
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = bmgShoppingCartService.deleteById(bmgShoppingCartItemId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request,
                             HttpSession httpSession) {
        int priceTotal = 0;
        bmgUserVO user = (bmgUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<bmgShoppingCartItemVO> myShoppingCartItems = bmgShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //无数据则不跳转至结算页
            return "/shop-cart";
        } else {
            //总价
            for (bmgShoppingCartItemVO bmgShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += bmgShoppingCartItemVO.getGoodsCount() * bmgShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }
}
