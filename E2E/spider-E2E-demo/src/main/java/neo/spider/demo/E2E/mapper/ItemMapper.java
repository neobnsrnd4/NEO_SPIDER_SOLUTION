package neo.spider.demo.E2E.mapper;

import org.apache.ibatis.annotations.Mapper;

import neo.spider.demo.E2E.dto.ItemDto;

@Mapper
public interface ItemMapper {
	ItemDto findById(String id);
	ItemDto  findFromWrongTable(String id);
}
