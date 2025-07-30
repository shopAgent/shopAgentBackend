package org.spring.shopagent.setting;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.spring.shopagent.info.dto.DatabaseInfoDTO;
import org.spring.shopagent.response.ApiResponseDto;
import org.spring.shopagent.setting.dto.DbConfigRequestDTO;
import org.spring.shopagent.setting.dto.DbConnectionTestRequestDTO;
import org.spring.shopagent.setting.dto.DbConnectionTestResponseDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @PostMapping("/api/config/db")
    public ApiResponseDto<DbConfigRequestDTO> insertDbConfig(@RequestBody DbConfigRequestDTO dto) {
        return settingService.insertDbConfig(dto);
    }

    @GetMapping("/api/config/db")
    public ApiResponseDto<DatabaseInfoDTO> getDbConfig() {
        return settingService.getDbConfig();
    }

    @PutMapping("/api/config/db")
    public ApiResponseDto<DbConfigRequestDTO> updateDbConfig(@RequestBody DbConfigRequestDTO dto) {
        return settingService.updateDbConfig(dto);
    }

    @DeleteMapping("/api/config/db")
    public ApiResponseDto<Void> deleteDbConfig() {
        return settingService.deleteDbConfig();
    }

    @PostMapping("/api/config/db/connection/test")
    public ApiResponseDto<DbConnectionTestResponseDTO> dbConnectionCheck(@RequestBody DbConnectionTestRequestDTO dto) {
        return settingService.dbConnectionCheck(dto);
    }

}
