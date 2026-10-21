package com.showticket.show.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.showticket.api.dto.ShowDTO;
import com.showticket.api.dto.OrderTicketDTO;
import com.showticket.show.domain.po.Show;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 演出表 服务类
 * </p>
 */
public interface IShowService extends IService<Show> {

    void deductTicketStock(List<OrderTicketDTO> tickets);

    void restoreTicketStock(List<OrderTicketDTO> tickets);

    List<ShowDTO> queryShowByIds(Collection<Long> ids);
}
