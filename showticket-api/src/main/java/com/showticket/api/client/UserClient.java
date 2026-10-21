package com.showticket.api.client;

import com.showticket.api.dto.AddressDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClient {
    @PutMapping("/users/balance/deduct")
    void deductBalance(@RequestParam("pw") String pw, @RequestParam("amount") Integer amount);

    @GetMapping("/addresses/{id}")
    AddressDTO getAddressById(@PathVariable("id") Long id);
}
