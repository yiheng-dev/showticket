package com.showticket.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.showticket.user.domain.po.Address;
import com.showticket.user.mapper.AddressMapper;
import com.showticket.user.service.IAddressService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @since 2023-05-05
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

}
