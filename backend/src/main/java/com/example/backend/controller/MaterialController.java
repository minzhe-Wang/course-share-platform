package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.MaterialCreateDTO;
import com.example.backend.service.MaterialService;
import com.example.backend.vo.MaterialCreateVO;
import com.example.backend.vo.MaterialDetailVO;
import com.example.backend.vo.MaterialDownloadVO;
import com.example.backend.vo.MaterialListItemVO;
import com.example.backend.vo.PageResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping
    public Result<MaterialCreateVO> createMaterial(@RequestBody @Valid MaterialCreateDTO materialCreateDTO,
                                                   @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(materialService.createMaterial(materialCreateDTO, authorization));
    }

    @GetMapping
    public Result<PageResultVO<MaterialListItemVO>> listMaterials(@RequestParam(required = false) Integer pageNum,
                                                                  @RequestParam(required = false) Integer pageSize,
                                                                  @RequestParam(required = false) String keyword,
                                                                  @RequestParam(required = false) Long categoryId,
                                                                  @RequestParam(required = false) Long tagId,
                                                                  @RequestParam(required = false) String sortBy) {
        return Result.success(materialService.listMaterials(pageNum, pageSize, keyword, categoryId, tagId, sortBy));
    }

    @GetMapping("/{id}")
    public Result<MaterialDetailVO> getMaterialDetail(@PathVariable Long id) {
        return Result.success(materialService.getMaterialDetail(id));
    }

    @PostMapping("/{id}/download")
    public Result<MaterialDownloadVO> downloadMaterial(@PathVariable Long id,
                                                       @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(materialService.downloadMaterial(id, authorization));
    }

    @PostMapping("/{id}/like")
    public Result<Void> likeMaterial(@PathVariable Long id,
                                     @RequestHeader(value = "Authorization", required = false) String authorization) {
        materialService.likeMaterial(id, authorization);
        return Result.success();
    }

    @PostMapping("/{id}/favorite")
    public Result<Void> favoriteMaterial(@PathVariable Long id,
                                         @RequestHeader(value = "Authorization", required = false) String authorization) {
        materialService.favoriteMaterial(id, authorization);
        return Result.success();
    }

    @DeleteMapping("/{id}/favorite")
    public Result<Void> cancelFavoriteMaterial(@PathVariable Long id,
                                               @RequestHeader(value = "Authorization", required = false) String authorization) {
        materialService.cancelFavoriteMaterial(id, authorization);
        return Result.success();
    }
}
