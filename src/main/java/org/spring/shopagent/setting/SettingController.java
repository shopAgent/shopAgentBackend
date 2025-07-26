package org.spring.shopagent.setting;

import lombok.RequiredArgsConstructor;
import org.spring.shopagent.response.ApiResponseDto;
import org.spring.shopagent.setting.dto.DbConfigRequestDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;


    @PostMapping("/config/db")
    public ApiResponseDto<Void> setupDatabase(@RequestBody DbConfigRequestDTO dto) {
        return settingService.setupDatabase(dto);
    }

    @GetMapping("/config/db")
    public ApiResponseDto<Void> getDbConfig() {

        return settingService.getDbConnectionCheck();
    }

    @PutMapping("/config/db")
    public ApiResponseDto<Void> updateDbConfig(@RequestBody DbConfigRequestDTO dto) {
        return settingService.updateDbConfig(dto);
    }
}
