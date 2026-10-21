package com.showticket.show.service;

import com.showticket.api.dto.ShowDTO;
import com.showticket.common.domain.PageDTO;
import com.showticket.show.domain.query.ShowPageQuery;

public interface IShowSearchService {

    PageDTO<ShowDTO> search(ShowPageQuery query);
}
