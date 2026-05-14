package com.example.backend.service;

import com.example.backend.dto.ContentStatusDTO;
import com.example.backend.vo.AdminAnswerItemVO;
import com.example.backend.vo.AdminMaterialItemVO;
import com.example.backend.vo.AdminQuestionItemVO;
import com.example.backend.vo.AdminReplyItemVO;
import com.example.backend.vo.PageResultVO;

public interface AdminContentService {

    PageResultVO<AdminMaterialItemVO> listMaterials(Integer pageNum, Integer pageSize, String keyword,
                                                    String auditStatus, Integer status, String authorization);

    void updateMaterialStatus(Long id, ContentStatusDTO contentStatusDTO, String authorization);

    PageResultVO<AdminQuestionItemVO> listQuestions(Integer pageNum, Integer pageSize, String keyword,
                                                    String auditStatus, Integer status, String authorization);

    void updateQuestionStatus(Long id, ContentStatusDTO contentStatusDTO, String authorization);

    PageResultVO<AdminAnswerItemVO> listAnswers(Integer pageNum, Integer pageSize, String keyword,
                                                String auditStatus, Integer status, String authorization);

    void updateAnswerStatus(Long id, ContentStatusDTO contentStatusDTO, String authorization);

    PageResultVO<AdminReplyItemVO> listReplies(Integer pageNum, Integer pageSize, String keyword,
                                               String auditStatus, Integer status, String authorization);

    void updateReplyStatus(Long id, ContentStatusDTO contentStatusDTO, String authorization);
}
