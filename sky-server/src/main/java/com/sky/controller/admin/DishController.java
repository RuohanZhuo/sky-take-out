package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result<String> save(@RequestBody DishDTO dishDTO){
        dishService.saveWithFlavor(dishDTO);
        cleanCache("dish_" + dishDTO.getCategoryId());
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO queryDTO){
        PageResult pageResult = dishService.page(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品功能")
    public Result<String> delete(@RequestParam List<Long> ids){
        dishService.deleteBatch(ids);

        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 对菜品进行起售禁售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("停售起售菜品")
    public Result<String> startOrStop(@PathVariable Integer status, Long id){
        dishService.startOrStop(status, id);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据id查询菜品和相关联的口味数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品和相关联的口味数据")
    public Result<DishVO> getById(@PathVariable Long id){
        return Result.success(dishService.getByIdWithFlavor(id));
    }

    /**
     * 修改菜品信息
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品信息")
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavor(dishDTO);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
