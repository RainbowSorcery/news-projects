package com.lyra.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyra.admin.mapper.CategoryMapper;
import com.lyra.admin.service.CategoryService;
import com.lyra.pojo.Category;
import com.lyra.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisOperator redisOperator;

    private static final String CATEGORY_CACHE = "category_cache";


    @Override
    public Category queryCategoryByName(String name) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);

        return categoryMapper.selectOne(queryWrapper);
    }

    @Override
    public void insertCategory(Category category) {
        // 当分类进行修改时 需要将redis中的缓存数据删除 这样一来 用户在次请求分类列表时 查询到的是最新的数据
        // 如果不这也做的话 因为redis中的数据没有更新 所以查询到的数据还是原来的旧数据
        categoryMapper.insert(category);

        redisOperator.del(CATEGORY_CACHE);
    }

    @Override
    public void updateCategory(Category category) {
        // 当分类进行修改时 需要将redis中的缓存数据删除 这样一来 用户在次请求分类列表时 查询到的是最新的数据
        // 如果不这也做的话 因为redis中的数据没有更新 所以查询到的数据还是原来的旧数据
        categoryMapper.updateById(category);
        redisOperator.del(CATEGORY_CACHE);
    }

    @Override
    public List<Category> getArticlePageCats() {
        // 这个接口为什么不和上一个接口合并:
        // 首先是因为业务模块不同 如果在时间看中添加一个新的字段 is_delete 这个字段是否为true都需要在管理员页面中显示 而文章页面为is_delete字段为false则不用显示

        // 因为分类信息是需要频繁查询 可以将数据存储至redis中 以此来减弱数据库的压力

        // 和用户页面信息缓存一样 先从redis中查询 如果查询不到信息再从数据库中查 并把查询到的分类信息保持到redis中 若能查到 直接返回前端即可

        String jsonResult = redisOperator.get(CATEGORY_CACHE);

        if (StringUtils.isBlank(jsonResult)) {
            List<Category> categories = categoryMapper.selectList(null);

            String categoryToJson = null;
            try {
                categoryToJson = objectMapper.writeValueAsString(categories);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            redisOperator.set(CATEGORY_CACHE, categoryToJson);
            return categories;
        } else {
            String categoryToJson = redisOperator.get(CATEGORY_CACHE);

            List list = null;
            try {
                list = objectMapper.readValue(categoryToJson, List.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            return list;
        }
    }

    @Override
    public List queryCategoryByList() {
        return categoryMapper.selectList(null);
    }
}
