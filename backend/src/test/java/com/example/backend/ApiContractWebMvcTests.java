package com.example.backend;

import com.example.backend.controller.AiAuditController;
import com.example.backend.controller.HealthCheckController;
import com.example.backend.controller.TestController;
import com.example.backend.controller.UserController;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.service.AiAuditService;
import com.example.backend.service.HealthCheckService;
import com.example.backend.service.UserService;
import com.example.backend.vo.AiAuditResultVO;
import com.example.backend.vo.HealthCheckVO;
import com.example.backend.vo.HealthComponentVO;
import com.example.backend.vo.LoginVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        TestController.class,
        UserController.class,
        AiAuditController.class,
        HealthCheckController.class
})
@Import(GlobalExceptionHandler.class)
class ApiContractWebMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AiAuditService aiAuditService;

    @MockitoBean
    private HealthCheckService healthCheckService;

    @Test
    void testEndpointReturnsUnifiedResult() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value("backend dev success"));
    }

    @Test
    void runtimeExceptionReturnsUnifiedFailure() throws Exception {
        mockMvc.perform(get("/api/error-test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("这是一个测试异常"));
    }

    @Test
    void loginReturnsTokenContract() throws Exception {
        when(userService.login(any())).thenReturn(LoginVO.builder()
                .userId(3L)
                .username("student")
                .nickname("学生用户")
                .role("STUDENT")
                .token("mock-token-3")
                .build());

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "student",
                                "password", "123456"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(3))
                .andExpect(jsonPath("$.data.username").value("student"))
                .andExpect(jsonPath("$.data.role").value("STUDENT"))
                .andExpect(jsonPath("$.data.token").value("mock-token-3"));
    }

    @Test
    void loginValidationReturnsReadableMessage() throws Exception {
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "",
                                "password", "123456"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户名不能为空"));
    }

    @Test
    void aiAuditTestReturnsAuditContract() throws Exception {
        when(aiAuditService.audit(eq("QUESTION"), eq("normal content"))).thenReturn(AiAuditResultVO.builder()
                .auditResult("PASS")
                .riskScore(new BigDecimal("0.10"))
                .reason("Mock AI 审核通过")
                .build());

        mockMvc.perform(post("/api/ai-audit/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "targetType", "QUESTION",
                                "content", "normal content"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.auditResult").value("PASS"))
                .andExpect(jsonPath("$.data.riskScore").value(0.10))
                .andExpect(jsonPath("$.data.reason").value("Mock AI 审核通过"));
    }

    @Test
    void aiAuditValidationReturnsReadableMessage() throws Exception {
        mockMvc.perform(post("/api/ai-audit/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "targetType", "QUESTION",
                                "content", ""
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("审核内容不能为空"));
    }

    @Test
    void healthCheckReturnsComponentStatus() throws Exception {
        when(healthCheckService.check()).thenReturn(HealthCheckVO.builder()
                .status("UP")
                .checkTime(LocalDateTime.of(2026, 5, 5, 10, 0))
                .components(List.of(
                        HealthComponentVO.builder().name("application").status("UP").message("backend running").build(),
                        HealthComponentVO.builder().name("mysql").status("UP").message("ok").build(),
                        HealthComponentVO.builder().name("redis").status("UP").message("ok").build(),
                        HealthComponentVO.builder().name("minio").status("UP").message("bucket exists").build()
                ))
                .build());

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.components[0].name").value("application"))
                .andExpect(jsonPath("$.data.components[3].name").value("minio"))
                .andExpect(jsonPath("$.data.components[3].status").value("UP"));
    }
}
