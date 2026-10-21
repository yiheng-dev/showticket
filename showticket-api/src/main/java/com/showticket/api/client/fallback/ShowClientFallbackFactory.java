package com.showticket.api.client.fallback;

import com.showticket.api.client.ShowClient;
import com.showticket.api.dto.ShowDTO;
import com.showticket.api.dto.OrderTicketDTO;
import com.showticket.common.utils.CollUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;
import java.util.List;

@Slf4j
public class ShowClientFallbackFactory implements FallbackFactory<ShowClient> {
    @Override
    public ShowClient create(Throwable cause) {
        return new ShowClient() {
            @Override
            public List<ShowDTO> queryShowByIds(Collection<Long> ids) {
                log.error("查询演出失败！", cause);
                return CollUtils.emptyList();
            }

            @Override
            public void deductTicketStock(List<OrderTicketDTO> tickets) {
                log.error("扣减演出余票失败！", cause);
                throw new RuntimeException(cause);
            }

            @Override
            public void restoreTicketStock(List<OrderTicketDTO> tickets) {
                log.error("恢复演出余票失败！", cause);
                throw new RuntimeException(cause);
            }
        };
    }
}
