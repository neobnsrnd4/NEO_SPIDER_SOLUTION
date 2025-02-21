package neo.spider.admin.flow.controller;

import neo.spider.admin.flow.dto.bulkhead.BulkheadDto;
import neo.spider.admin.flow.service.BulkheadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/flow/bulkhead")
public class BulkheadController {

    private final BulkheadService bulkheadService;

    public BulkheadController(BulkheadService bulkheadService) {
        this.bulkheadService = bulkheadService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody BulkheadDto newBulkhead){
        try{
            boolean result = bulkheadService.create(newBulkhead);
            if (!result){
                return ResponseEntity.status(400).body("생성된 데이터가 없습니다.");
            }
        } catch (RuntimeException e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
        return ResponseEntity.ok("Bulkhead created");
    }

    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody BulkheadDto bh){
        try{
            boolean result = bulkheadService.update(bh);
            if (!result){
                return ResponseEntity.status(400).body("업데이트된 설정이 없습니다.");
            }
        } catch (RuntimeException e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
        return ResponseEntity.ok("Bulkhead updated");
    }

    @GetMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam long bulkheadId, @RequestParam String applicationName){
        try {
            boolean result = bulkheadService.delete(bulkheadId, applicationName);
            if (!result){
                return ResponseEntity.status(400).body("삭제된 설정이 없습니다.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        return ResponseEntity.ok("삭제 성공");
    }

    @GetMapping("/find")
    public ResponseEntity<BulkheadDto> find(@RequestParam long bulkheadId){
        BulkheadDto result = bulkheadService.findById(bulkheadId);
        return ResponseEntity.ok(result);
    }
}
