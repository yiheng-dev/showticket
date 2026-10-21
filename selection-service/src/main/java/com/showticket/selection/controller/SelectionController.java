package com.showticket.selection.controller;


import com.showticket.selection.domain.dto.SelectionFormDTO;
import com.showticket.selection.domain.po.Selection;
import com.showticket.selection.domain.vo.SelectionVO;
import com.showticket.selection.service.ISelectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "选票清单相关接口")
@RestController
@RequestMapping("/selections")
@RequiredArgsConstructor
public class SelectionController {
    private final ISelectionService selectionService;

    @ApiOperation("加入选票清单")
    @PostMapping
    public void addToSelection(@Valid @RequestBody SelectionFormDTO selectionFormDTO){
        selectionService.addToSelection(selectionFormDTO);
    }

    @ApiOperation("更新选票清单条目")
    @PutMapping
    public void updateSelection(@RequestBody Selection selection){
        selectionService.updateById(selection);
    }

    @ApiOperation("删除选票清单条目")
    @DeleteMapping("{id}")
    public void deleteSelection(@PathVariable("id") Long id){
        selectionService.removeById(id);
    }

    @ApiOperation("查询我的选票清单")
    @GetMapping
    public List<SelectionVO> queryMySelection(){
        return selectionService.queryMySelection();
    }

    @ApiOperation("按下单演出批量清理选票清单")
    @ApiImplicitParam(name = "ids", value = "演出id集合")
    @DeleteMapping
    public void deleteSelectionByShowIds(@RequestParam("ids") List<Long> ids){
        selectionService.removeByShowIds(ids);
    }
}
