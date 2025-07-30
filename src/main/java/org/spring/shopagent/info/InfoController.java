package org.spring.shopagent.info;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.spring.shopagent.info.dto.DatabaseInfoDTO;
import org.spring.shopagent.info.dto.DatabaseNameRequestDTO;
import org.spring.shopagent.response.ApiResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InfoController {

    private final InfoService infoService;

    @GetMapping("/info/database")
    public ApiResponseDto<List<String>> getDatabase() {
        return infoService.getDatabase();
    }

    @PostMapping("/info/database")
    public ApiResponseDto<Void> updateDatabase(
            @RequestBody @Valid DatabaseNameRequestDTO dto
    ) {
        return infoService.updateDatabase(dto);
    }

    @GetMapping("/info/setting/database")
    public ApiResponseDto<DatabaseInfoDTO> getDatabaseSetting() {
        return infoService.getDatabaseSetting();
    }
}
