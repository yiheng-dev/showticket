package com.showticket.selection.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.showticket.api.client.ShowClient;
import com.showticket.api.dto.ShowDTO;
import com.showticket.common.exception.BizIllegalException;
import com.showticket.common.utils.BeanUtils;
import com.showticket.common.utils.CollUtils;
import com.showticket.common.utils.UserContext;
import com.showticket.selection.config.SelectionProperties;
import com.showticket.selection.domain.dto.SelectionFormDTO;
import com.showticket.selection.domain.po.Selection;
import com.showticket.selection.domain.vo.SelectionVO;
import com.showticket.selection.mapper.SelectionMapper;
import com.showticket.selection.service.ISelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 选票清单 服务实现类
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SelectionServiceImpl extends ServiceImpl<SelectionMapper, Selection> implements ISelectionService {

    private final ShowClient showClient;

    private final SelectionProperties selectionProperties;

    @Override
    public void addToSelection(SelectionFormDTO selectionFormDTO) {
        // 1.获取登录用户与购票数量
        Long userId = UserContext.getUser();
        int num = selectionFormDTO.getNum() == null || selectionFormDTO.getNum() < 1
                ? 1 : selectionFormDTO.getNum();

        // 2.判断该演出是否已在选票清单中
        if (checkShowExists(selectionFormDTO.getShowId(), userId)) {
            // 已存在则累加数量
            baseMapper.updateNum(selectionFormDTO.getShowId(), userId, num);
            return;
        }
        // 3.判断选票清单是否已满
        checkSelectionFull(userId);

        // 4.新增选票条目
        Selection selection = BeanUtils.copyBean(selectionFormDTO, Selection.class);
        selection.setUserId(userId);
        selection.setNum(num);
        save(selection);
    }

    @Override
    public List<SelectionVO> queryMySelection() {
        // 1.查询当前用户的选票清单
        List<Selection> selections = lambdaQuery()
                .eq(Selection::getUserId, UserContext.getUser())
                .list();
        if (CollUtils.isEmpty(selections)) {
            return CollUtils.emptyList();
        }

        // 2.转换VO并补充演出的最新信息
        List<SelectionVO> vos = BeanUtils.copyList(selections, SelectionVO.class);
        handleSelectionShows(vos);
        return vos;
    }

    private void handleSelectionShows(List<SelectionVO> vos) {
        // 1.获取演出id
        Set<Long> showIds = vos.stream().map(SelectionVO::getShowId).collect(Collectors.toSet());
        // 2.批量查询演出
        List<ShowDTO> shows = showClient.queryShowByIds(showIds);
        if (CollUtils.isEmpty(shows)) {
            return;
        }
        // 3.转为 id 到演出的 map
        Map<Long, ShowDTO> showMap = shows.stream()
                .collect(Collectors.toMap(ShowDTO::getId, Function.identity()));
        // 4.写入最新价格、状态与余票
        for (SelectionVO v : vos) {
            ShowDTO show = showMap.get(v.getShowId());
            if (show == null) {
                continue;
            }
            v.setNewPrice(show.getPrice());
            v.setStatus(show.getStatus());
            v.setStock(show.getStock());
        }
    }

    @Override
    @Transactional
    public void removeByShowIds(Collection<Long> showIds) {
        QueryWrapper<Selection> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Selection::getUserId, UserContext.getUser())
                .in(Selection::getShowId, showIds);
        remove(queryWrapper);
    }

    private void checkSelectionFull(Long userId) {
        int count = lambdaQuery().eq(Selection::getUserId, userId).count();
        if (count >= selectionProperties.getMaxSelections()) {
            throw new BizIllegalException(
                    StrUtil.format("选票清单最多支持{}项演出", selectionProperties.getMaxSelections()));
        }
    }

    private boolean checkShowExists(Long showId, Long userId) {
        int count = lambdaQuery()
                .eq(Selection::getUserId, userId)
                .eq(Selection::getShowId, showId)
                .count();
        return count > 0;
    }
}
