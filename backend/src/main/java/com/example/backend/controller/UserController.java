package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.LoginDTO;
import com.example.backend.dto.RegisterDTO;
import com.example.backend.dto.UserPasswordUpdateDTO;
import com.example.backend.dto.UserProfileUpdateDTO;
import com.example.backend.service.UserService;
import com.example.backend.vo.LoginVO;
import com.example.backend.vo.PageResultVO;
import com.example.backend.vo.UserAnswerItemVO;
import com.example.backend.vo.UserDownloadRecordVO;
import com.example.backend.vo.UserFavoriteMaterialVO;
import com.example.backend.vo.UserMaterialItemVO;
import com.example.backend.vo.UserMeVO;
import com.example.backend.vo.UserQuestionItemVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }

    @GetMapping("/me")
    public Result<UserMeVO> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(userService.getCurrentUser(authorization));
    }

    @PutMapping("/profile")
    public Result<UserMeVO> updateProfile(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody @Valid UserProfileUpdateDTO userProfileUpdateDTO) {
        return Result.success(userService.updateProfile(authorization, userProfileUpdateDTO));
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody @Valid UserPasswordUpdateDTO userPasswordUpdateDTO) {
        userService.updatePassword(authorization, userPasswordUpdateDTO);
        return Result.success();
    }

    @GetMapping("/materials")
    public Result<PageResultVO<UserMaterialItemVO>> myMaterials(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        return Result.success(userService.listMyMaterials(authorization, pageNum, pageSize));
    }

    @GetMapping("/favorites")
    public Result<PageResultVO<UserFavoriteMaterialVO>> myFavorites(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        return Result.success(userService.listMyFavorites(authorization, pageNum, pageSize));
    }

    @GetMapping("/questions")
    public Result<PageResultVO<UserQuestionItemVO>> myQuestions(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        return Result.success(userService.listMyQuestions(authorization, pageNum, pageSize));
    }

    @GetMapping("/answers")
    public Result<PageResultVO<UserAnswerItemVO>> myAnswers(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        return Result.success(userService.listMyAnswers(authorization, pageNum, pageSize));
    }

    @GetMapping("/downloads")
    public Result<PageResultVO<UserDownloadRecordVO>> myDownloads(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        return Result.success(userService.listMyDownloads(authorization, pageNum, pageSize));
    }
}
