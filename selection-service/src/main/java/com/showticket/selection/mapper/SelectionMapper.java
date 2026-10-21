package com.showticket.selection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.showticket.selection.domain.po.Selection;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 选票清单 Mapper 接口
 * </p>
 */
public interface SelectionMapper extends BaseMapper<Selection> {

    @Update("UPDATE ticket_selection SET num = num + #{num} WHERE user_id = #{userId} AND show_id = #{showId}")
    void updateNum(@Param("showId") Long showId, @Param("userId") Long userId, @Param("num") Integer num);
}
