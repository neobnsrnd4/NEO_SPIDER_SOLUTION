package neo.spider.demo.E2E.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import neo.spider.demo.E2E.dto.ItemDto;
import neo.spider.demo.E2E.mapper.ItemMapper;

@Service
@RequiredArgsConstructor
public class TestService {
	private final ItemMapper itemMapper;
	
	public ItemDto getItemById(String id) {
		return itemMapper.findById(id);
	}
	
	public ItemDto getItemFromWrongTable(String id) {
		return itemMapper.findFromWrongTable(id);
				
	}
}
