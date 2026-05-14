package com.example.backend.service;

import com.example.backend.dto.LoginDTO;
import com.example.backend.dto.RegisterDTO;
import com.example.backend.entity.SysUser;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.impl.UserServiceImpl;
import com.example.backend.vo.LoginVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthService authService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userMapper, passwordEncoder, authService);
    }

    @Test
    void registerCreatesStudentWithEncodedPasswordAndDefaultNickname() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername(" student ");
        dto.setPassword("123456");

        when(userMapper.findByUsername("student")).thenReturn(null);
        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");

        userService.register(dto);

        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).insert(userCaptor.capture());

        SysUser user = userCaptor.getValue();
        assertThat(user.getUsername()).isEqualTo("student");
        assertThat(user.getPassword()).isEqualTo("encoded-password");
        assertThat(user.getNickname()).isEqualTo("student");
        assertThat(user.getRole()).isEqualTo("STUDENT");
        assertThat(user.getStatus()).isEqualTo(1);
    }

    @Test
    void registerRejectsDuplicateUsername() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("student");
        dto.setPassword("123456");

        when(userMapper.findByUsername("student")).thenReturn(SysUser.builder().id(1L).build());

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("用户名已存在");
        verify(userMapper, never()).insert(any());
    }

    @Test
    void loginReturnsMockTokenWhenPasswordMatches() {
        LoginDTO dto = loginDTO(" student ", "123456");
        when(userMapper.findByUsername("student")).thenReturn(SysUser.builder()
                .id(3L)
                .username("student")
                .password("encoded-password")
                .nickname("学生用户")
                .role("STUDENT")
                .status(1)
                .build());
        when(passwordEncoder.matches("123456", "encoded-password")).thenReturn(true);

        LoginVO loginVO = userService.login(dto);

        assertThat(loginVO.getUserId()).isEqualTo(3L);
        assertThat(loginVO.getUsername()).isEqualTo("student");
        assertThat(loginVO.getNickname()).isEqualTo("学生用户");
        assertThat(loginVO.getRole()).isEqualTo("STUDENT");
        assertThat(loginVO.getToken()).isEqualTo("mock-token-3");
    }

    @Test
    void loginRejectsMissingUser() {
        when(userMapper.findByUsername("ghost")).thenReturn(null);

        assertThatThrownBy(() -> userService.login(loginDTO("ghost", "123456")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("用户不存在");
    }

    @Test
    void loginRejectsDisabledUser() {
        when(userMapper.findByUsername("student")).thenReturn(SysUser.builder()
                .id(3L)
                .username("student")
                .status(0)
                .build());

        assertThatThrownBy(() -> userService.login(loginDTO("student", "123456")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("用户已被禁用");
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void loginRejectsWrongPassword() {
        when(userMapper.findByUsername("student")).thenReturn(SysUser.builder()
                .id(3L)
                .username("student")
                .password("encoded-password")
                .status(1)
                .build());
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(loginDTO("student", "wrong-password")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("密码错误");
    }

    private LoginDTO loginDTO(String username, String password) {
        LoginDTO dto = new LoginDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        return dto;
    }
}
