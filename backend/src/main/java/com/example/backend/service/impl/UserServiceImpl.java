package com.example.backend.service.impl;

import com.example.backend.dto.LoginDTO;
import com.example.backend.dto.RegisterDTO;
import com.example.backend.dto.UserPasswordUpdateDTO;
import com.example.backend.dto.UserProfileUpdateDTO;
import com.example.backend.entity.SysUser;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.AuthService;
import com.example.backend.service.UserService;
import com.example.backend.utils.MockTokenUtils;
import com.example.backend.vo.LoginVO;
import com.example.backend.vo.PageResultVO;
import com.example.backend.vo.UserAnswerItemVO;
import com.example.backend.vo.UserDownloadRecordVO;
import com.example.backend.vo.UserFavoriteMaterialVO;
import com.example.backend.vo.UserMaterialItemVO;
import com.example.backend.vo.UserMeVO;
import com.example.backend.vo.UserQuestionItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String ROLE_STUDENT = "STUDENT";
    private static final int NORMAL_STATUS = 1;
    private static final int DISABLED_STATUS = 0;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Override
    public void register(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername().trim();

        SysUser existUser = userMapper.findByUsername(username);
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        String nickname = registerDTO.getNickname();
        if (!StringUtils.hasText(nickname)) {
            nickname = username;
        }

        SysUser user = SysUser.builder()
                .username(username)
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .nickname(nickname)
                .role(ROLE_STUDENT)
                .status(NORMAL_STATUS)
                .build();

        userMapper.insert(user);
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername().trim();

        SysUser user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getStatus() == null || user.getStatus() == DISABLED_STATUS) {
            throw new RuntimeException("用户已被禁用");
        }

        boolean passwordMatch = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        if (!passwordMatch) {
            throw new RuntimeException("密码错误");
        }

        return LoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .token(MockTokenUtils.buildToken(user.getId()))
                .build();
    }

    @Override
    public UserMeVO getCurrentUser(String authorization) {
        return buildUserMeVO(authService.getEnabledUser(authorization));
    }

    @Override
    public UserMeVO updateProfile(String authorization, UserProfileUpdateDTO userProfileUpdateDTO) {
        SysUser user = authService.getEnabledUser(authorization);
        String nickname = trimToNull(userProfileUpdateDTO.getNickname());
        if (!StringUtils.hasText(nickname)) {
            nickname = user.getUsername();
        }

        userMapper.updateProfile(
                user.getId(),
                nickname,
                trimToNull(userProfileUpdateDTO.getPhone()),
                trimToNull(userProfileUpdateDTO.getEmail()),
                trimToNull(userProfileUpdateDTO.getAvatar())
        );

        return buildUserMeVO(userMapper.findById(user.getId()));
    }

    @Override
    public void updatePassword(String authorization, UserPasswordUpdateDTO userPasswordUpdateDTO) {
        SysUser user = authService.getEnabledUser(authorization);
        if (!passwordEncoder.matches(userPasswordUpdateDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        if (passwordEncoder.matches(userPasswordUpdateDTO.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("新密码不能与旧密码相同");
        }

        userMapper.updatePassword(user.getId(), passwordEncoder.encode(userPasswordUpdateDTO.getNewPassword()));
    }

    @Override
    public PageResultVO<UserMaterialItemVO> listMyMaterials(String authorization, Integer pageNum, Integer pageSize) {
        Long userId = authService.getEnabledUser(authorization).getId();
        PageQuery pageQuery = normalizePage(pageNum, pageSize);
        Long total = userMapper.countUserMaterials(userId);
        List<UserMaterialItemVO> list = userMapper.findUserMaterials(userId, pageQuery.pageSize(), pageQuery.offset());
        return PageResultVO.<UserMaterialItemVO>builder()
                .total(total)
                .list(list)
                .build();
    }

    @Override
    public PageResultVO<UserFavoriteMaterialVO> listMyFavorites(String authorization, Integer pageNum, Integer pageSize) {
        Long userId = authService.getEnabledStudent(authorization, "只有学生可以查看个人中心记录").getId();
        PageQuery pageQuery = normalizePage(pageNum, pageSize);
        Long total = userMapper.countUserFavorites(userId);
        List<UserFavoriteMaterialVO> list = userMapper.findUserFavorites(userId, pageQuery.pageSize(), pageQuery.offset());
        return PageResultVO.<UserFavoriteMaterialVO>builder()
                .total(total)
                .list(list)
                .build();
    }

    @Override
    public PageResultVO<UserQuestionItemVO> listMyQuestions(String authorization, Integer pageNum, Integer pageSize) {
        Long userId = authService.getEnabledStudent(authorization, "只有学生可以查看个人中心记录").getId();
        PageQuery pageQuery = normalizePage(pageNum, pageSize);
        Long total = userMapper.countUserQuestions(userId);
        List<UserQuestionItemVO> list = userMapper.findUserQuestions(userId, pageQuery.pageSize(), pageQuery.offset());
        return PageResultVO.<UserQuestionItemVO>builder()
                .total(total)
                .list(list)
                .build();
    }

    @Override
    public PageResultVO<UserAnswerItemVO> listMyAnswers(String authorization, Integer pageNum, Integer pageSize) {
        Long userId = authService.getEnabledStudent(authorization, "只有学生可以查看个人中心记录").getId();
        PageQuery pageQuery = normalizePage(pageNum, pageSize);
        Long total = userMapper.countUserAnswers(userId);
        List<UserAnswerItemVO> list = userMapper.findUserAnswers(userId, pageQuery.pageSize(), pageQuery.offset());
        return PageResultVO.<UserAnswerItemVO>builder()
                .total(total)
                .list(list)
                .build();
    }

    @Override
    public PageResultVO<UserDownloadRecordVO> listMyDownloads(String authorization, Integer pageNum, Integer pageSize) {
        Long userId = authService.getEnabledStudent(authorization, "只有学生可以查看个人中心记录").getId();
        PageQuery pageQuery = normalizePage(pageNum, pageSize);
        Long total = userMapper.countUserDownloads(userId);
        List<UserDownloadRecordVO> list = userMapper.findUserDownloads(userId, pageQuery.pageSize(), pageQuery.offset());
        return PageResultVO.<UserDownloadRecordVO>builder()
                .total(total)
                .list(list)
                .build();
    }

    private UserMeVO buildUserMeVO(SysUser user) {
        return UserMeVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .phone(user.getPhone())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build();
    }

    private PageQuery normalizePage(Integer pageNum, Integer pageSize) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        return new PageQuery(safePageSize, (safePageNum - 1) * safePageSize);
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private record PageQuery(int pageSize, int offset) {
    }
}
