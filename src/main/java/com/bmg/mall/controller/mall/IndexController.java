package com.bmg.mall.controller.mall;

import com.bmg.mall.common.Constants;
import com.bmg.mall.common.IndexConfigTypeEnum;
import com.bmg.mall.controller.vo.bmgIndexCarouselVO;
import com.bmg.mall.controller.vo.bmgIndexCategoryVO;
import com.bmg.mall.controller.vo.bmgIndexConfigGoodsVO;
import com.bmg.mall.service.bmgCarouselService;
import com.bmg.mall.service.bmgIndexConfigService;
import com.bmg.mall.service.bmgCategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Resource
    private bmgCarouselService bmgCarouselService;

    @Resource
    private bmgIndexConfigService bmgIndexConfigService;

    @Resource
    private bmgCategoryService bmgCategoryService;

    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage(HttpServletRequest request) {
        List<bmgIndexCategoryVO> categories = bmgCategoryService.getCategoriesForIndex();
        if (CollectionUtils.isEmpty(categories)) {
            return "error/error_5xx";
        }
        List<bmgIndexCarouselVO> carousels = bmgCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<bmgIndexConfigGoodsVO> hotGoodses = bmgIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<bmgIndexConfigGoodsVO> newGoodses = bmgIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<bmgIndexConfigGoodsVO> recommendGoodses = bmgIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        request.setAttribute("categories", categories);//分类数据
        request.setAttribute("carousels", carousels);//轮播图
        request.setAttribute("hotGoodses", hotGoodses);//热销商品
        request.setAttribute("newGoodses", newGoodses);//新品
        request.setAttribute("recommendGoodses", recommendGoodses);//推荐商品
        return "mall/index";
    }
}
