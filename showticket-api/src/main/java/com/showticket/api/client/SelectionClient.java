package com.showticket.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@FeignClient("selection-service")
public interface SelectionClient {
    @DeleteMapping("/selections")
    void deleteSelectionByShowIds(@RequestParam("ids") Collection<Long> ids);
}
