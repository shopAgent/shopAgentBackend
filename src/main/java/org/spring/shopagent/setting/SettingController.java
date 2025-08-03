package org.spring.shopagent.setting;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.spring.shopagent.info.dto.DatabaseInfoDTO;
import org.spring.shopagent.response.ApiResponseDto;
import org.spring.shopagent.setting.dto.*;
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

    @PostMapping("/api/config/ai/connection/test")
    public ApiResponseDto<AiConnectionTestResponseDTO> aiConnectionCheck() {
        return settingService.aiConnectionCheck();
    }

    @PostMapping("/api/config/search")
    public ApiResponseDto<Void> insertSearchConfig(@RequestBody @Valid SearchRequestDTO dto) {
        return settingService.insertSearchConfig(dto);
    }

    @GetMapping("/api/config/search/{idxSearchConfig}")
    public ApiResponseDto<SearchRequestDTO> getSearchConfig(
            @PathVariable Long idxSearchConfig
    ) {
        return settingService.getSearchConfig(idxSearchConfig);
    }

    @PutMapping("/api/config/search/{idxSearchConfig}")
    public ApiResponseDto<Void> updateSearchConfig(
            @RequestBody @Valid SearchRequestDTO dto,
            @PathVariable Long idxSearchConfig) {
        dto.setIdxSearchConfig(idxSearchConfig);
        return settingService.updateSearchConfig(dto);
    }

}
