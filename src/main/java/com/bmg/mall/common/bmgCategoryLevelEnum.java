package com.bmg.mall.common;
//定义常量枚举类型，一般用来表示一组相同类型的常量
public enum bmgCategoryLevelEnum {
    DEFAULT(0, "ERROR"),
    LEVEL_ONE(1, "一级分类"),
    LEVEL_TWO(2, "二级分类"),
    LEVEL_THREE(3, "三级分类");

    private int level;

    private String name;

    bmgCategoryLevelEnum(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public static bmgCategoryLevelEnum getbmgOrderStatusEnumByLevel(int level) {
        for (bmgCategoryLevelEnum bmgCategoryLevelEnum : bmgCategoryLevelEnum.values()) {
            if (bmgCategoryLevelEnum.getLevel() == level) {
                return bmgCategoryLevelEnum;
            }
        }
        return DEFAULT;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
