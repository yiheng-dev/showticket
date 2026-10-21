package com.showticket.show.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.showticket.api.dto.OrderTicketDTO;
import com.showticket.show.domain.po.Show;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 演出表 Mapper 接口
 * </p>
 */
public interface ShowMapper extends BaseMapper<Show> {

    @Update("UPDATE show_info SET stock = stock - #{num} WHERE id = #{showId}")
    void updateStock(OrderTicketDTO orderTicket);

    @Update("UPDATE show_info SET stock = stock + #{num} WHERE id = #{showId}")
    void restoreStock(OrderTicketDTO orderTicket);
}
