package com.showticket.selection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.showticket.selection.domain.dto.SelectionFormDTO;
import com.showticket.selection.domain.po.Selection;
import com.showticket.selection.domain.vo.SelectionVO;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 选票清单 服务类
 * </p>
 */
public interface ISelectionService extends IService<Selection> {

    void addToSelection(SelectionFormDTO selectionFormDTO);

    List<SelectionVO> queryMySelection();

    void removeByShowIds(Collection<Long> showIds);
}
