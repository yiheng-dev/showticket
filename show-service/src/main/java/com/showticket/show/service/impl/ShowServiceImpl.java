package com.showticket.show.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.showticket.api.dto.ShowDTO;
import com.showticket.api.dto.OrderTicketDTO;
import com.showticket.common.exception.BizIllegalException;
import com.showticket.common.utils.BeanUtils;
import com.showticket.show.domain.po.Show;
import com.showticket.show.mapper.ShowMapper;
import com.showticket.show.service.IShowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 演出表 服务实现类
 * </p>
 */
@Service
public class ShowServiceImpl extends ServiceImpl<ShowMapper, Show> implements IShowService {

    @Override
    @Transactional
    public void deductTicketStock(List<OrderTicketDTO> tickets) {
        String sqlStatement = "com.showticket.show.mapper.ShowMapper.updateStock";
        boolean r = false;
        try {
            r = executeBatch(tickets, (sqlSession, entity) -> sqlSession.update(sqlStatement, entity));
        } catch (Exception e) {
            throw new BizIllegalException("扣减余票异常，可能是余票不足", e);
        }
        if (!r) {
            throw new BizIllegalException("余票不足！");
        }
    }

    @Override
    @Transactional
    public void restoreTicketStock(List<OrderTicketDTO> tickets) {
        String sqlStatement = "com.showticket.show.mapper.ShowMapper.restoreStock";
        boolean r = false;
        try {
            r = executeBatch(tickets, (sqlSession, entity) -> sqlSession.update(sqlStatement, entity));
        } catch (Exception e) {
            throw new BizIllegalException("恢复余票异常", e);
        }
        if (!r) {
            throw new BizIllegalException("恢复余票失败！");
        }
    }

    @Override
    public List<ShowDTO> queryShowByIds(Collection<Long> ids) {
        return BeanUtils.copyList(listByIds(ids), ShowDTO.class);
    }
}
