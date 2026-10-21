package com.showticket.show.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.showticket.api.dto.ShowDTO;
import com.showticket.api.dto.OrderTicketDTO;
import com.showticket.common.domain.PageDTO;
import com.showticket.common.domain.PageQuery;
import com.showticket.common.utils.BeanUtils;
import com.showticket.show.domain.po.Show;
import com.showticket.show.service.IShowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "演出管理相关接口")
@RestController
@RequestMapping("/shows")
@RequiredArgsConstructor
public class ShowController {

    private final IShowService showService;

    @ApiOperation("分页查询演出")
    @GetMapping("/page")
    public PageDTO<ShowDTO> queryShowByPage(PageQuery query) {
        Page<Show> result = showService.page(query.toMpPage("update_time", false));
        return PageDTO.of(result, ShowDTO.class);
    }

    @ApiOperation("根据id批量查询演出")
    @GetMapping
    public List<ShowDTO> queryShowByIds(@RequestParam("ids") List<Long> ids){
        return showService.queryShowByIds(ids);
    }

    @ApiOperation("根据id查询演出")
    @GetMapping("{id}")
    public ShowDTO queryShowById(@PathVariable("id") Long id) {
        return BeanUtils.copyBean(showService.getById(id), ShowDTO.class);
    }

    @ApiOperation("新增演出")
    @PostMapping
    public void saveShow(@RequestBody ShowDTO show) {
        showService.save(BeanUtils.copyBean(show, Show.class));
    }

    @ApiOperation("更新演出状态")
    @PutMapping("/status/{id}/{status}")
    public void updateShowStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status){
        Show show = new Show();
        show.setId(id);
        show.setStatus(status);
        showService.updateById(show);
    }

    @ApiOperation("更新演出")
    @PutMapping
    public void updateShow(@RequestBody ShowDTO show) {
        // 状态只能通过专用接口修改
        show.setStatus(null);
        showService.updateById(BeanUtils.copyBean(show, Show.class));
    }

    @ApiOperation("根据id删除演出")
    @DeleteMapping("{id}")
    public void deleteShowById(@PathVariable("id") Long id) {
        showService.removeById(id);
    }

    @ApiOperation("批量扣减余票")
    @PutMapping("/stock/deduct")
    public void deductTicketStock(@RequestBody List<OrderTicketDTO> tickets){
        showService.deductTicketStock(tickets);
    }

    @ApiOperation("批量恢复余票")
    @PutMapping("/stock/restore")
    public void restoreTicketStock(@RequestBody List<OrderTicketDTO> tickets){
        showService.restoreTicketStock(tickets);
    }
}
