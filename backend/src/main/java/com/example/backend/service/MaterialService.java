package com.example.backend.service;

import com.example.backend.dto.MaterialCreateDTO;
import com.example.backend.vo.MaterialCreateVO;
import com.example.backend.vo.MaterialDetailVO;
import com.example.backend.vo.MaterialDownloadVO;
import com.example.backend.vo.MaterialListItemVO;
import com.example.backend.vo.PageResultVO;

public interface MaterialService {

    MaterialCreateVO createMaterial(MaterialCreateDTO materialCreateDTO, String authorization);

    PageResultVO<MaterialListItemVO> listMaterials(Integer pageNum, Integer pageSize, String keyword,
                                                   Long categoryId, Long tagId, String sortBy);

    MaterialDetailVO getMaterialDetail(Long id);

    MaterialDownloadVO downloadMaterial(Long id, String authorization);

    void likeMaterial(Long id, String authorization);

    void favoriteMaterial(Long id, String authorization);

    void cancelFavoriteMaterial(Long id, String authorization);
}
