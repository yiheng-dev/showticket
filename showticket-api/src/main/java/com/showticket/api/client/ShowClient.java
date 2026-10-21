package com.showticket.api.client;

import com.showticket.api.client.fallback.ShowClientFallbackFactory;
import com.showticket.api.dto.ShowDTO;
import com.showticket.api.dto.OrderTicketDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient(value = "show-service", fallbackFactory = ShowClientFallbackFactory.class)
public interface ShowClient {
    @GetMapping("/shows")
    List<ShowDTO> queryShowByIds(@RequestParam("ids")Collection<Long> ids);

    @PutMapping("/shows/stock/deduct")
    void deductTicketStock(@RequestBody List<OrderTicketDTO> tickets);

    @PutMapping("/shows/stock/restore")
    void restoreTicketStock(@RequestBody List<OrderTicketDTO> tickets);
}
