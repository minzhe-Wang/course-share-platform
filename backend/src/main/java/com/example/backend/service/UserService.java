package com.example.backend.service;

import com.example.backend.dto.LoginDTO;
import com.example.backend.dto.RegisterDTO;
import com.example.backend.dto.UserPasswordUpdateDTO;
import com.example.backend.dto.UserProfileUpdateDTO;
import com.example.backend.vo.LoginVO;
import com.example.backend.vo.PageResultVO;
import com.example.backend.vo.UserAnswerItemVO;
import com.example.backend.vo.UserDownloadRecordVO;
import com.example.backend.vo.UserFavoriteMaterialVO;
import com.example.backend.vo.UserMaterialItemVO;
import com.example.backend.vo.UserMeVO;
import com.example.backend.vo.UserQuestionItemVO;

public interface UserService {

    void register(RegisterDTO registerDTO);

    LoginVO login(LoginDTO loginDTO);

    UserMeVO getCurrentUser(String authorization);

    UserMeVO updateProfile(String authorization, UserProfileUpdateDTO userProfileUpdateDTO);

    void updatePassword(String authorization, UserPasswordUpdateDTO userPasswordUpdateDTO);

    PageResultVO<UserMaterialItemVO> listMyMaterials(String authorization, Integer pageNum, Integer pageSize);

    PageResultVO<UserFavoriteMaterialVO> listMyFavorites(String authorization, Integer pageNum, Integer pageSize);

    PageResultVO<UserQuestionItemVO> listMyQuestions(String authorization, Integer pageNum, Integer pageSize);

    PageResultVO<UserAnswerItemVO> listMyAnswers(String authorization, Integer pageNum, Integer pageSize);

    PageResultVO<UserDownloadRecordVO> listMyDownloads(String authorization, Integer pageNum, Integer pageSize);
}
